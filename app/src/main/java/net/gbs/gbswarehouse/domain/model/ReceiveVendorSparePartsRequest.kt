package net.gbs.gbswarehouse.domain.model


data class ReceiveVendorSparePartsRequest(
    val userId: String? = null,
    val companyCodes: List<String> = listOf(),
    val poNumber: String,
    val lines: List<SparePartsLine>,
)
