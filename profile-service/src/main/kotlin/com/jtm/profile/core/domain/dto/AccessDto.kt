package com.jtm.profile.core.domain.dto

import java.util.*

data class AccessDto(val clientId: String, val plugins: List<UUID>)
