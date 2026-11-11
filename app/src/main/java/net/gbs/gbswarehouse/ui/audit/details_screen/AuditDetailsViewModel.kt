package net.gbs.gbswarehouse.ui.audit.details_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.response.ScanInventoryItemBodyDto
import net.gbs.gbswarehouse.domain.model.InventoryAssignment
import net.gbs.gbswarehouse.domain.model.InventoryOrder
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.StorageBinLocation
import net.gbs.gbswarehouse.domain.repository.AuditRepository
import net.gbs.gbswarehouse.domain.usecase.issuing.GetStorageBinLocationIssuingUseCase
import javax.inject.Inject

@HiltViewModel
class AuditDetailsViewModel @Inject constructor(
    private val auditRepository: AuditRepository,
    private val getStorageBinLocationIssuingUseCase: GetStorageBinLocationIssuingUseCase
) : ViewModel() {

    var orderId by mutableStateOf(0)
    var order by mutableStateOf<InventoryOrder?>(null)
    var assignments by mutableStateOf<List<InventoryAssignment>>(emptyList())
    var filteredMaterials by mutableStateOf<List<InventoryAssignment>>(emptyList())

    var isLoading by mutableStateOf(false)
    var isBinLoading by mutableStateOf(false)
    var saving by mutableStateOf(false)
    var saved by mutableStateOf(false)
    var materialErrorMessage by mutableStateOf<Message?>(null)
    var saveErrorMessage by mutableStateOf<Message?>(null)
    var binErrorMessage by mutableStateOf<Message?>(null)
    var successMessage by mutableStateOf<Message?>(null)

    // Bin selection
    var binInput by mutableStateOf("")
    var binDlg by mutableStateOf(false)
    var selBin by mutableStateOf<StorageBinLocation?>(null)

    // Material selection
    var matInput by mutableStateOf("")
    var matDlg by mutableStateOf(false)
    var matSearch by mutableStateOf("")
    var selAssignment by mutableStateOf<InventoryAssignment?>(null)

    // Input fields
    var qty by mutableStateOf("")
    var selectedBatch by mutableStateOf<String?>(null)
    var selectedValuationType by mutableStateOf<String?>(null)

    // Pagination
    var currentPage = 1
    var pageSize = 50
    var hasMore = false

    fun initOrder(id: Int) {
        orderId = id
        loadOrderHeader()
        loadAssignments(1, clear = true)
    }

    private fun loadOrderHeader() {
        viewModelScope.launch {
            when (val result = auditRepository.getMyInventoryOrders()) {
                is NetworkResult.Success -> {
                    order = result.data.find { it.inventoryOrderId == orderId }
                }
                else -> {}
            }
        }
    }

    fun loadAssignments(page: Int, clear: Boolean = false, filter: String? = null) {
        viewModelScope.launch {
            isLoading = true
            materialErrorMessage = null
            when (val result = auditRepository.getMyAssignments(orderId, filter, page, pageSize)) {
                is NetworkResult.Success -> {
                    val pageData = result.data
                    val items = pageData.items
                    assignments = if (clear) items else assignments + items
                    filteredMaterials = assignments
                    currentPage = page
                    hasMore = pageData.hasMore
                }
                is NetworkResult.BusinessError -> materialErrorMessage = Message.Real(result.message)
                is NetworkResult.HttpError -> materialErrorMessage = Message.res(R.string.error_in_connection)
                is NetworkResult.NetworkError -> materialErrorMessage = Message.res(R.string.error_in_connection)
                is NetworkResult.UnknownError -> materialErrorMessage = Message.res(R.string.error_in_connection)
                else -> materialErrorMessage = Message.res(R.string.error_in_connection)
            }
            isLoading = false
        }
    }

    fun loadMoreAssignments() {
        if (!isLoading && hasMore) {
            loadAssignments(currentPage + 1)
        }
    }

    fun searchMaterials(query: String) {
        matSearch = query
        filteredMaterials = if (query.isBlank()) assignments
        else assignments.filter {
            it.itemCode?.contains(query, ignoreCase = true) == true ||
            it.itemName?.contains(query, ignoreCase = true) == true
        }
    }

    fun scanOrEnterBin(binCode: String) {
        if (binCode.isBlank()) return
        viewModelScope.launch {
            isBinLoading = true
            binErrorMessage = null
            when (val result = getStorageBinLocationIssuingUseCase(binCode)) {
                is NetworkResult.Success -> {
                    selBin = result.data
                    binInput = result.data.storageBinCode
                }
                is NetworkResult.BusinessError -> binErrorMessage = Message.Real(result.message)
                is NetworkResult.HttpError -> binErrorMessage = Message.res(R.string.error_in_connection)
                is NetworkResult.NetworkError -> binErrorMessage = Message.res(R.string.error_in_connection)
                is NetworkResult.UnknownError -> binErrorMessage = Message.res(R.string.error_in_connection)
                else -> binErrorMessage = Message.res(R.string.error_in_connection)
            }
            isBinLoading = false
        }
    }

    fun selectAssignment(assignment: InventoryAssignment) {
        selAssignment = assignment
        matInput = assignment.itemCode ?: ""
        matDlg = false
        matSearch = ""
        qty = assignment.countedQty?.toString() ?: ""
        selectedBatch = assignment.batch
        selectedValuationType = assignment.valuationType
    }

    fun clearMaterial() {
        selAssignment = null
        matInput = ""
        qty = ""
        selectedBatch = null
        selectedValuationType = null
        saved = false
    }

    fun clearBin() {
        selBin = null
        binInput = ""
    }

    fun onBarcodeScanned(barcode: String) {
        if (barcode.isBlank()) return
        if (selBin==null) {
            scanOrEnterBin(barcode)
        } else {
            val matched = assignments.find { it.itemCode == barcode }
            if (matched != null) {
                selectAssignment(matched)
            } else {
                viewModelScope.launch {
                    when (val result = auditRepository.getMyAssignments(orderId, barcode, 1, 10)) {
                        is NetworkResult.Success -> {
                            val item = result.data.items.firstOrNull()
                            if (item != null) {
                                if (!assignments.any { it.assignmentId == item.assignmentId }) {
                                    assignments = assignments + item
                                }
                                selectAssignment(item)
                            } else {
                                materialErrorMessage = Message.res(R.string.wrong_material_code)
                            }
                        }

                        else -> materialErrorMessage = Message.res(R.string.wrong_material_code)
                    }
                }
            }
        }
    }

    fun saveScan() {
        val assignment = selAssignment ?: return
        val bin = selBin ?: return
        val countQty = qty.toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            saving = true
            saveErrorMessage = null
            successMessage = null
            val body = ScanInventoryItemBodyDto(
                inventoryOrderId = orderId,
                storageBinCode = bin.storageBinCode,
                itemCode = assignment.itemCode ?: "",
                batch = selectedBatch ?: assignment.batch,
                valuationType = selectedValuationType ?: assignment.valuationType,
                countedQty = countQty
            )
            when (val result = auditRepository.scanInventoryItem(body)) {
                is NetworkResult.Success -> {
                    saved = true
                    successMessage = Message.res(R.string.success_save)
                    // Clear material data and keep bin data!
                    clearMaterial()
                    // Refresh assignments and order header
                    loadAssignments(1, clear = true)
                    loadOrderHeader()
                }
                is NetworkResult.BusinessError -> saveErrorMessage = Message.Real(result.message)
                is NetworkResult.HttpError -> saveErrorMessage = Message.res(R.string.error_in_connection)
                is NetworkResult.NetworkError -> saveErrorMessage = Message.res(R.string.error_in_connection)
                is NetworkResult.UnknownError -> saveErrorMessage = Message.res(R.string.error_in_connection)
                else -> saveErrorMessage = Message.res(R.string.error_in_connection)
            }
            saving = false
        }
    }
}
