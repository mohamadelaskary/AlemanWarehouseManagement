package net.gbs.gbswarehouse.ui.receiving.receiving_without_order.blind_receiving

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.QualityDecision
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.request.CreateBlindReceiveRequestDto
import net.gbs.gbswarehouse.domain.model.BlindReceiveMaterial
import net.gbs.gbswarehouse.domain.model.CreateBlindReceiveResult
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.Plant
import net.gbs.gbswarehouse.domain.model.Uom
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.domain.model.WorkersGroup
import net.gbs.gbswarehouse.domain.usecase.GetPlantWarehousesUseCase
import net.gbs.gbswarehouse.domain.usecase.GetAllWorkerGroupsUseCase
import net.gbs.gbswarehouse.domain.usecase.receiving.*
import net.gbs.gbswarehouse.spinnerLabel
import javax.inject.Inject

@HiltViewModel
class BlindReceivingViewModel @Inject constructor(
    private val searchBlindReceiveMaterialsUseCase: SearchBlindReceiveMaterialsUseCase,
    private val getBlindReceiveUomsUseCase: GetBlindReceiveUomsUseCase,
    private val getBlindReceiveQualityDecisionsUseCase: GetBlindReceiveQualityDecisionsUseCase,
    private val getUserPlantsUseCase: GetUserPlantsUseCase,
    private val getPlantWarehousesUseCase: GetPlantWarehousesUseCase,
    private val getAllWorkerGroupsUseCase: GetAllWorkerGroupsUseCase,
    private val createBlindReceiveUseCase: CreateBlindReceiveUseCase
) : ViewModel() {

    var materialCodeInput by mutableStateOf("")
    var materialDescription by mutableStateOf("")
    var vendorName by mutableStateOf("")
    var quantity by mutableStateOf("")
    var purchasingOfficer by mutableStateOf("")

    var selectedMaterial by mutableStateOf<BlindReceiveMaterial?>(null)
    var searchedMaterials by mutableStateOf<List<BlindReceiveMaterial>>(emptyList())
    var materialSearchQuery by mutableStateOf("")

    var selectedPlant by mutableStateOf<Plant?>(null)
    var plantsList by mutableStateOf<List<Plant>>(emptyList())

    var selectedWarehouse by mutableStateOf<Warehouse?>(null)
    var warehousesList by mutableStateOf<List<Warehouse>>(emptyList())

    var selectedQualityDecision by mutableStateOf<QualityDecision?>(null)
    var qualityDecisionsList by mutableStateOf<List<QualityDecision>>(emptyList())

    var selectedWorkersGroup by mutableStateOf<WorkersGroup?>(null)
    var workersGroupsList by mutableStateOf<List<WorkersGroup>>(emptyList())
    var workerGroupSearchText by mutableStateOf("")

    var uomInput by mutableStateOf("")
    var uomsList by mutableStateOf<List<Uom>>(emptyList())
    var selectedUom by mutableStateOf<Uom?>(null)

    var isLoading by mutableStateOf(false)
    var isSaving by mutableStateOf(false)
    var errorMessage by mutableStateOf<Message?>(null)
    var successResult by mutableStateOf<Message?>(null)
    var isSaved by mutableStateOf(false)

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            isLoading = true
            when (val res = getUserPlantsUseCase()) {
                is NetworkResult.Success -> plantsList = res.data
                is NetworkResult.BusinessError -> errorMessage = Message.real(res.message)
                is NetworkResult.HttpError -> errorMessage = Message.real(res.message)
                is NetworkResult.NetworkError -> errorMessage = Message.real(res.message)
                is NetworkResult.UnknownError -> errorMessage = Message.real(res.message)
                is NetworkResult.Loading -> {}
            }
            when (val res = getBlindReceiveUomsUseCase()) {
                is NetworkResult.Success -> uomsList = res.data
                is NetworkResult.BusinessError -> {}
                is NetworkResult.HttpError -> {}
                is NetworkResult.NetworkError -> {}
                is NetworkResult.UnknownError -> {}
                is NetworkResult.Loading -> {}
            }
            when (val res = getBlindReceiveQualityDecisionsUseCase()) {
                is NetworkResult.Success -> qualityDecisionsList = res.data
                is NetworkResult.BusinessError -> {}
                is NetworkResult.HttpError -> {}
                is NetworkResult.NetworkError -> {}
                is NetworkResult.UnknownError -> {}
                is NetworkResult.Loading -> {}
            }
            when (val res = getAllWorkerGroupsUseCase()) {
                is NetworkResult.Success -> workersGroupsList = res.data
                is NetworkResult.BusinessError -> {}
                is NetworkResult.HttpError -> {}
                is NetworkResult.NetworkError -> {}
                is NetworkResult.UnknownError -> {}
                is NetworkResult.Loading -> {}
            }
            isLoading = false
        }
    }

    fun onMaterialCodeChanged(code: String) {
        materialCodeInput = code
        if (code.isBlank()) {
            materialDescription = ""
            selectedUom = null
            uomInput = ""
            selectedMaterial = null
            searchedMaterials = emptyList()
        } else {
            searchMaterials(code)
        }
    }

    private fun searchMaterials(filter: String) {
        viewModelScope.launch {
            when (val res = searchBlindReceiveMaterialsUseCase(filter)) {
                is NetworkResult.Success -> {
                    searchedMaterials = res.data
                }
                is NetworkResult.BusinessError,
                is NetworkResult.HttpError,
                is NetworkResult.NetworkError,
                is NetworkResult.UnknownError,
                is NetworkResult.Loading -> {
                    searchedMaterials = emptyList()
                }
            }
        }
    }

    fun selectMaterial(material: BlindReceiveMaterial) {
        selectedMaterial = material
        materialCodeInput = material.materialCode
        materialDescription = material.materialDescription
        val foundUom = uomsList.find { it.code.equals(material.uom, ignoreCase = true) || it.description.equals(material.uom, ignoreCase = true) }
        selectedUom = foundUom ?: Uom(code = material.uom, description = material.uomDescription).also {
            if (uomsList.none { u -> u.code.equals(it.code, ignoreCase = true) }) {
                uomsList = uomsList + it
            }
        }
        uomInput = selectedUom?.spinnerLabel() ?: material.uom
        searchedMaterials = emptyList()
    }

    fun clearMaterial() {
        selectedMaterial = null
        materialCodeInput = ""
        materialDescription = ""
        quantity = ""
        selectedUom = null
        uomInput = ""
        searchedMaterials = emptyList()
    }

    fun selectPlant(plant: Plant) {
        selectedPlant = plant
        selectedWarehouse = null
        warehousesList = emptyList()
        viewModelScope.launch {
            when (val res = getPlantWarehousesUseCase(plant.code)) {
                is NetworkResult.Success -> warehousesList = res.data
                is NetworkResult.BusinessError,
                is NetworkResult.HttpError,
                is NetworkResult.NetworkError,
                is NetworkResult.UnknownError,
                is NetworkResult.Loading -> warehousesList = emptyList()
            }
        }
    }

    fun selectWarehouse(warehouse: Warehouse) {
        selectedWarehouse = warehouse
    }

    fun selectQualityDecision(decision: QualityDecision) {
        selectedQualityDecision = decision
    }

    fun selectWorkersGroup(group: WorkersGroup) {
        selectedWorkersGroup = group
        workerGroupSearchText = group.spinnerLabel()
    }

    fun onWorkersGroupSearchTextChanged(text: String) {
        workerGroupSearchText = text
    }

    fun selectUom(uom: Uom) {
        selectedUom = uom
        uomInput = uom.spinnerLabel()
    }

    fun onUomInputChanged(text: String) {
        uomInput = text
        val query = text.trim()
        val matched = uomsList.find {
            it.code.equals(query, ignoreCase = true) ||
                it.description.equals(query, ignoreCase = true) ||
                it.spinnerLabel().equals(query, ignoreCase = true)
        }
        selectedUom = matched ?: query.takeIf { it.isNotEmpty() }?.let { Uom(code = it, description = it) }
    }

    val isValid: Boolean get() {
        val matCodeEmpty = materialCodeInput.isBlank()
        val descValid = if (matCodeEmpty) materialDescription.isNotBlank() else true
        val uomValid = if (matCodeEmpty) uomInput.isNotBlank() else true
        val qtyVal = quantity.toDoubleOrNull() ?: 0.0
        return descValid && uomValid && qtyVal > 0.0 && selectedPlant != null && selectedWarehouse != null
    }

    fun saveBlindReceive() {
        if (!isValid) return
        val qtyVal = quantity.toDoubleOrNull() ?: 0.0
        val qualityId = selectedQualityDecision?.id?.toIntOrNull()

        val request = CreateBlindReceiveRequestDto(
            materialCode = materialCodeInput.takeIf { it.isNotBlank() },
            materialDescription = materialDescription,
            vendorName = vendorName.takeIf { it.isNotBlank() },
            workerGroupCode = selectedWorkersGroup?.workerGroupCode,
            quantity = qtyVal,
            uom = selectedUom?.code ?: selectedMaterial?.uom ?: "",
            qualityDecisionId = qualityId,
            purchasingOfficer = purchasingOfficer.takeIf { it.isNotBlank() },
            plantCode = selectedPlant?.code,
            warehouseCode = selectedWarehouse?.warehouseCode
        )

        viewModelScope.launch {
            isSaving = true
            errorMessage = null
            when (val res = createBlindReceiveUseCase(request)) {
                is NetworkResult.Success -> {
                    successResult = Message.of(res.message, R.string.success_save)
                    isSaved = true
                    clearAllData()
                }
                is NetworkResult.BusinessError -> errorMessage = Message.real(res.message)
                is NetworkResult.HttpError -> errorMessage = Message.real(res.message)
                is NetworkResult.NetworkError -> errorMessage = Message.real(res.message)
                is NetworkResult.UnknownError -> errorMessage = Message.real(res.message)
                is NetworkResult.Loading -> {}
            }
            isSaving = false
        }
    }

    fun clearAllData() {
        materialCodeInput = ""
        materialDescription = ""
        vendorName = ""
        quantity = ""
        purchasingOfficer = ""
        selectedMaterial = null
        selectedPlant = null
        selectedWarehouse = null
        selectedQualityDecision = null
        selectedWorkersGroup = null
        workerGroupSearchText = ""
        selectedUom = null
        uomInput = ""
        warehousesList = emptyList()
        searchedMaterials = emptyList()
    }
}
