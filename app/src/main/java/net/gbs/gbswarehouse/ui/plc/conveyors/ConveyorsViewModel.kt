package net.gbs.gbswarehouse.ui.plc.conveyors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.Conveyor
import net.gbs.gbswarehouse.domain.model.Message
import net.gbs.gbswarehouse.domain.model.StreamStatus
import net.gbs.gbswarehouse.domain.usecase.loading.LoadingUseCases
import javax.inject.Inject

@HiltViewModel
class ConveyorsViewModel @Inject constructor(
    private val useCases: LoadingUseCases
) : ViewModel() {

    private val _conveyors = MutableStateFlow<List<Conveyor>>(emptyList())
    val conveyors: StateFlow<List<Conveyor>> = _conveyors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<Message?>(null)
    val errorMessage: StateFlow<Message?> = _errorMessage.asStateFlow()

    /** Live connection state of the api/Conveyors/Stream push channel. */
    val streamStatus: StateFlow<StreamStatus> = useCases.observeStreamStatus()

    private var initialLoadFailed = false

    init {
        loadConveyors()
        viewModelScope.launch {
            useCases.observeConveyors().collect {
                if (it.isNotEmpty()) {
                    _conveyors.value = it
                    _isLoading.value = false
                    if (initialLoadFailed) {
                        initialLoadFailed = false
                        _errorMessage.value = null
                    }
                }
            }
        }
    }

    fun loadConveyors() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val result = useCases.getConveyors()) {
                is NetworkResult.Success -> {
                    _conveyors.value = result.data
                    _isLoading.value = false
                }
                is NetworkResult.Loading -> _isLoading.value = true
                else -> {
                    _isLoading.value = false
                    initialLoadFailed = true
                    _errorMessage.value = result.errorMessageOrDefault()
                }
            }
        }
    }

    fun manualStart(conveyor: Conveyor) {
        viewModelScope.launch {
            when (val result = useCases.manualStartConveyor(conveyor.id)) {
                is NetworkResult.Success -> _errorMessage.value = null
                is NetworkResult.Loading -> Unit
                else -> _errorMessage.value = result.errorMessageOrDefault()
            }
        }
    }

    fun manualStop(conveyor: Conveyor) {
        viewModelScope.launch {
            when (val result = useCases.manualStopConveyor(conveyor.id)) {
                is NetworkResult.Success -> _errorMessage.value = null
                is NetworkResult.Loading -> Unit
                else -> _errorMessage.value = result.errorMessageOrDefault()
            }
        }
    }

    fun resetFault(conveyor: Conveyor) {
        viewModelScope.launch {
            when (val result = useCases.resetConveyorFault(conveyor.conveyorId)) {
                is NetworkResult.Success -> _errorMessage.value = null
                is NetworkResult.Loading -> Unit
                else -> _errorMessage.value = result.errorMessageOrDefault()
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun NetworkResult<*>.errorMessageOrDefault(): Message = when (this) {
        is NetworkResult.BusinessError -> Message.real(message)
        is NetworkResult.HttpError -> Message.real(message)
        is NetworkResult.NetworkError -> Message.real(message)
        is NetworkResult.UnknownError -> Message.real(message)
        else -> Message.Resource(R.string.unknown_error)
    }
}
