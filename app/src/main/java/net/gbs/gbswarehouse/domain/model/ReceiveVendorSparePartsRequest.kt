package net.gbs.gbswarehouse.domain.model


data class ReceiveVendorSparePartsRequest(
    val userId: String,
    val companyCodes: List<String>,
    val poNumber: String,
    val lines: List<SparePartsLine>,
)
