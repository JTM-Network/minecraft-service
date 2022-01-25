package com.jtm.minecraft.core.usecase.stripe

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.stripe.model.Event
import com.stripe.net.ApiResource


class StripeEventDeserializer(private val mapper: ObjectMapper): JsonDeserializer<Event>() {

    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Event {
        val root: ObjectNode = mapper.readTree(p)
        return ApiResource.GSON.fromJson(root.toString(), Event::class.java)
    }
}