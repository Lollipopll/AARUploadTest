package com.infinity.easyeventbus

/**
 * @author wang
 * @date   2020/10/10
 * des
 */
object EasyEventBus {
    private val subscriptions = mutableSetOf<Any>()

    private const val PACKAGE_NAME = "com.infinity.easyeventbus"

    private const val CLASS_NAME = "EventBusInject"

    private const val CLASS_PATH = "$PACKAGE_NAME.$CLASS_NAME"

    private val clazz = Class.forName(CLASS_PATH)


    // 通过反射生成EventBusInject对象
    private val instance = clazz.newInstance()


    @Synchronized
    fun register(subscriber: Any) {
        subscriptions.add(subscriber)
    }


    @Synchronized
    fun unregister(subscriber: Any) {
        subscriptions.remove(subscriber)
    }

    @Synchronized
    fun post(event: Any) {
        subscriptions.forEach { subscriber ->
            val subscriberInfo = getSubscriberInfo(subscriber.javaClass)

            if (subscriberInfo != null) {
                val methodList = subscriberInfo.methodList

                methodList.forEach { method ->
                    if (method.eventType == event.javaClass) {
                        val declareMethod = subscriber.javaClass.getDeclaredMethod(
                            method.methodName,
                            method.eventType
                        )
                        declareMethod.invoke(subscriber, event)
                    }
                }
            }
        }
    }


    /**
     * 通过反射调用EventBusInject的getSubscriberInfo方法
     */
    private fun getSubscriberInfo(subscriberClass: Class<*>): SubscriberInfo? {
        val method = clazz.getMethod("getSubscriberInfo", Class::class.java)
        return method.invoke(instance, subscriberClass) as? SubscriberInfo
    }
}