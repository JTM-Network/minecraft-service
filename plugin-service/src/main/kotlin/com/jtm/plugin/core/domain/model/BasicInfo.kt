package com.jtm.plugin.core.domain.model

data class BasicInfo(val given_name: String = "", val family_name: String = "", val username: String? = "", val email: String = "", val picture: String? = "")