package com.jtm.plugin.core.usecase.currency

import com.google.gson.Gson
import com.jtm.plugin.core.domain.model.Currencies
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class PriceConverter {

    private val gson = Gson()
    private val logger = LoggerFactory.getLogger(PriceConverter::class.java)
    private val client = OkHttpClient.Builder().connectionSpecs(listOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS)).build()
    var current: Currencies? = null

    @Value("\${rapidapi.key:key}")
    lateinit var rapidKey: String

    @PostConstruct
    fun init() {
        request()
    }

    private fun request() {
        val request = Request.Builder()
            .url("https://exchangerate-api.p.rapidapi.com/rapid/latest/gbp")
            .addHeader("x-rapidapi-host", "exchangerate-api.p.rapidapi.com")
            .addHeader("x-rapidapi-key", rapidKey)
            .build()

        val call = client.newCall(request)
        val response = call.execute()

        if (response.isSuccessful) {
            val body: Currencies = gson.fromJson(response.body()?.string(), Currencies::class.java)
            if (body.result.equals("success", true)) current = body
            logger.info("Successfully fetched all currencies conversions.")
            return
        }

        logger.info("Failed to fetch currencies.")
    }

    fun convert(price: Double, currency: String): Double {
        if (current == null) return price
        if (currency.equals("gbp", true)) return price
        val currencies = current ?: return price
        val value = currencies.rates[currency] ?: return price
        val rate = "%.2f".format(value).toDouble()
        return price * rate
    }
}