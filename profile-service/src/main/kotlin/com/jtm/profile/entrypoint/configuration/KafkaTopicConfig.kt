package com.jtm.profile.entrypoint.configuration

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

@Configuration
open class KafkaTopicConfig {

    @Value("\${kafka.bootstrapAddress}")
    lateinit var bootstrapAddress: String

    @Bean
    open fun kafkaAdmin(): KafkaAdmin {
        val configs: MutableMap<String, Any> = HashMap()
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress)
        return KafkaAdmin(configs)
    }

    @Bean
    open fun notificationTopic(): NewTopic {
        return NewTopic("notification", 1, 1)
    }
}