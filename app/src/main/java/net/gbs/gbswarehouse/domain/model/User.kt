package net.gbs.gbswarehouse.domain.model

data class User(
    val id: String,
    val userName: String,
    val email: String,
    val companyCodes: List<String>,
)
