package com.infinity.easyeventbus_processor

import com.infinity.easyeventbus.EventMethodInfo
import com.infinity.easyeventbus.Subscribe
import com.infinity.easyeventbus.SubscriberInfo
import com.squareup.javapoet.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

/**
 * @author wang
 * @date   2020/10/10
 * des Subscribe注解处理器，在编译期间遍历所有注解声明的方法进行生成辅助类
 * https://blog.csdn.net/jeasonlzy/article/details/74273851
 *
 * 生成的文件在build/generated/source/kapt/debug/com/infinity/easyeventbus/EventBusInject.java路径下
 */
class EasyEventBusProcessor : AbstractProcessor() {

    companion object {
        private const val PACKAGE_NAME = "com.infinity.easyeventbus"
        private const val CLASS_NAME = "EventBusInject"
        private const val DOC = "这是自动生成的代码 by infinity"
    }


    private lateinit var elementUtils: Elements

    private val methodsByClass = LinkedHashMap<TypeElement, MutableList<ExecutableElement>>()


    override fun init(processingEnviroment: ProcessingEnvironment?) {
        super.init(processingEnviroment)
        elementUtils = processingEnv.elementUtils
        val messager = processingEnv.messager
        messager.printMessage(Diagnostic.Kind.NOTE, "init: --------------");
    }

    /***
     * 指定使用JAVA版本
     */
    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.RELEASE_8
    }

    /**
     * 这里必须指定，这个注解处理器是注册给哪个注解的。
     * 注意，它的返回值是一个字符串的集合，包含本处理器想要处理的注解类型的合法全称，即注解器所支持的注解类型集合，如果没有这样的类型，则返回一个空集合
     */
    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Subscribe::class.java.canonicalName)
    }


    override fun process(
        set: Set<TypeElement>,
        roundEnvironment: RoundEnvironment
    ): Boolean {
        val messager = processingEnv.messager
        messager.printMessage(Diagnostic.Kind.NOTE, "start: --------------");

        collectSubscribers(roundEnvironment, messager)
        if (methodsByClass.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.WARNING, "No @Event annotations found")
        } else {
            val typeSpec = TypeSpec.classBuilder(CLASS_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc(DOC)
                .addField(generateSubscriberField())
                .addMethod(generateMethodPutIndex())
                .addMethod(generateMethodGetSubscriberInfo())
            generateInitializerBlock(typeSpec)
            val javaFile = JavaFile.builder(PACKAGE_NAME, typeSpec.build())
                .build()
            try {
                javaFile.writeTo(processingEnv.filer)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        return true
    }


    private fun getClassAny(): TypeName {
        return ParameterizedTypeName.get(
            ClassName.get(Class::class.java),
            WildcardTypeName.subtypeOf(Any::class.java)
        )
    }


    /**
     * 生成 subscriberIndex 静态常量
     * private static final Map<Class<?>, SubscriberInfo> subscriberIndex = new HashMap<Class<?>, SubscriberInfo>();
     */
    private fun generateSubscriberField(): FieldSpec {
        val subscriberIndex = ParameterizedTypeName.get(
            ClassName.get(Map::class.java),
            getClassAny(),
            ClassName.get(SubscriberInfo::class.java)
        )
        return FieldSpec.builder(subscriberIndex, "subscriberIndex").addModifiers(
            Modifier.PRIVATE,
            Modifier.STATIC,
            Modifier.FINAL
        ).initializer(
            "new ${"$"}T<Class<?>, ${"$"}T>()",
            HashMap::class.java,
            SubscriberInfo::class.java
        ).build()
    }


    //生成静态方法块
    private fun generateInitializerBlock(builder: TypeSpec.Builder) {
        for (item in methodsByClass) {
            val methods = item.value
            if (methods.isEmpty()) {
                break
            }
            val codeBuilder = CodeBlock.builder()
            codeBuilder.add(
                "${"$"}T<${"$"}T> eventMethodInfoList = new ${"$"}T<${"$"}T>();",
                List::class.java,
                EventMethodInfo::class.java,
                ArrayList::class.java,
                EventMethodInfo::class.java
            )
            methods.forEach {
                val methodName = it.simpleName.toString()
                val eventType = it.parameters[0].asType()
                codeBuilder.add(
                    "eventMethodInfoList.add(new EventMethodInfo(${"$"}S, ${"$"}T.class));",
                    methodName,
                    eventType
                )
            }
            codeBuilder.add(
                "SubscriberInfo subscriberInfo = new SubscriberInfo(${"$"}T.class, eventMethodInfoList); putIndex(subscriberInfo);",
                item.key.asType()
            )
            builder.addInitializerBlock(
                codeBuilder.build()
            )
        }
    }

    //生成 putIndex 方法
    private fun generateMethodPutIndex(): MethodSpec {
        return MethodSpec.methodBuilder("putIndex")
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .returns(Void.TYPE)
            .addParameter(SubscriberInfo::class.java, "info")
            .addCode(
                CodeBlock.builder().add("subscriberIndex.put(info.getSubscriberClass() , info);")
                    .build()
            )
            .build()
    }

    //生成 getSubscriberInfo 方法
    private fun generateMethodGetSubscriberInfo(): MethodSpec {
        return MethodSpec.methodBuilder("getSubscriberInfo")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .returns(SubscriberInfo::class.java)
            .addParameter(getClassAny(), "subscriberClass")
            .addCode(
                CodeBlock.builder().add("return subscriberIndex.get(subscriberClass);")
                    .build()
            )
            .build()
    }


    private fun collectSubscribers(
        roundEnvironment: RoundEnvironment,
        messager: Messager
    ) {
        val elements = roundEnvironment.getElementsAnnotatedWith(Subscribe::class.java)
        if (elements.isNullOrEmpty()) {
            return
        }
        for (element in elements) {
            if (element is ExecutableElement) {
                if (checkHasNoErrors(element, messager)) {
                    val classElement = element.enclosingElement as TypeElement
                    var list = methodsByClass[classElement]
                    if (list == null) {
                        list = mutableListOf()
                        methodsByClass[classElement] = list
                    }
                    list.add(element)
                }
            } else {
                //@Event 只能用于修改方法
                messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "@Event is only valid for methods",
                    element
                )
            }
        }
    }

    /**
     * 校验方法签名是否合法
     */
    private fun checkHasNoErrors(element: ExecutableElement, messager: Messager): Boolean {
        //不能是静态方法
        if (element.modifiers.contains(Modifier.STATIC)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Event method must not be static", element)
            return false
        }
        //必须是 public 方法
        if (!element.modifiers.contains(Modifier.PUBLIC)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Event method must be public", element)
            return false
        }
        //方法只能且最多包含一个参数
        val parameters = element.parameters
        if (parameters.size != 1) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Event method must have exactly 1 parameter",
                element
            )
            return false
        }
        return true
    }


}