package net.gbs.gbswarehouse.domain.model

import com.google.gson.Gson

data class PurchaseOrder(
    val poNumber: String,
    val vendorCode: String,
    val vendorName: String,
    val companyCode: String,
    val purchaseOrderItems: List<SparePartsMaterial>,
){
    fun toJson(): String {
        return Gson().toJson(this)
    }
    companion object{
        fun fromJson(json: String): PurchaseOrder {
            return Gson().fromJson(json, PurchaseOrder::class.java)
        }
    }
}
