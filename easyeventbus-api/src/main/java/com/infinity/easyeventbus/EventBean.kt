package com.infinity.easyeventbus

/**
 * @author wang
 * @date   2020/10/10
 * des
 */


data class EventMethodInfo(val methodName: String, val eventType: Class<*>)

data class SubscriberInfo(val subscriberClass: Class<*>, val methodList: List<EventMethodInfo>)