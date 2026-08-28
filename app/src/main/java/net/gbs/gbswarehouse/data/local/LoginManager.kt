package net.gbs.gbswarehouse.data.local

interface LoginManager {
    fun getUserId():String?
    fun getUserCompanies():List<String>
    fun saveUserData(userId:String,userCompanies:List<String>)
    fun clear()
}