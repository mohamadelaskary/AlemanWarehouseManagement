package net.gbs.gbswarehouse.data.local

import net.gbs.gbswarehouse.domain.model.Permissions

interface LoginManager {
    fun getUserId(): String?
    fun getUserCompanies(): List<String>
    fun getUserPlantCodes(): List<String>
    fun getPermissions(): Permissions
    fun saveUserData(userId: String, userCompanies: List<String>) {
        saveUserData(userId, userCompanies, Permissions.default())
    }
    fun saveUserData(
        userId: String,
        userPlantCodes: List<String>,
        permissions: Permissions
    )
    fun clear()
}
