package com.jtm.plugin.core.domain.model

data class Currencies(val result: String = "", val base_code: String = "", val terms_of_use: String = "", val time_eol_unix: Long = 0,
                      val time_last_update_unix: Long = 0, val time_last_update_utc: String = "", val time_next_update_unix: Long = 0,
                      val time_next_update_utc: String = "", val rates: MutableMap<String, Double> = mutableMapOf())