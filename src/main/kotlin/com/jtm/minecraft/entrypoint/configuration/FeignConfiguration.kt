package com.jtm.minecraft.entrypoint.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import feign.codec.Decoder
import feign.codec.Encoder
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class FeignConfiguration {

    @Bean
    open fun decoder(): Decoder {
        return JacksonDecoder()
    }

    @Bean
    open fun encoder(): Encoder {
        return JacksonEncoder()
    }

    @Bean
    open fun mapper(): ObjectMapper {
        return ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
    }
}