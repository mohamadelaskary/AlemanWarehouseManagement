package net.gbs.gbswarehouse.ui.issue.issuing_direct_transfer_put_away.orders_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.domain.model.DirectTransferPutAwayDocument
import net.gbs.gbswarehouse.domain.usecase.issuing.GetDirectTransferPutAwayDocumentsUseCase
import javax.inject.Inject

@HiltViewModel
class DirectTransferPutAwayOrdersViewModel @Inject constructor(
    private val getDirectTransferPutAwayDocuments: GetDirectTransferPutAwayDocumentsUseCase
) : ViewModel() {

    private val _documents = MutableStateFlow<List<DirectTransferPutAwayDocument>>(emptyList())
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _filteredDocuments = MutableStateFlow<List<DirectTransferPutAwayDocument>>(emptyList())
    val documents: StateFlow<List<DirectTransferPutAwayDocument>> = _filteredDocuments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onSearchTextChanged(text: String) {
        _searchText.value = text
        filterDocuments()
    }

    private fun filterDocuments() {
        val query = _searchText.value.trim()
        _filteredDocuments.value = if (query.isEmpty()) {
            _documents.value
        } else {
            _documents.value.filter { it.receiptNo.contains(query, ignoreCase = true) }
        }
    }

    fun getDocuments() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val result = getDirectTransferPutAwayDocuments()) {
                is NetworkResult.Success -> {
                    _documents.value = result.data
                    filterDocuments()
                }
                is NetworkResult.BusinessError -> _errorMessage.value = result.message
                is NetworkResult.HttpError -> _errorMessage.value = result.message
                is NetworkResult.NetworkError -> _errorMessage.value = result.message
                is NetworkResult.UnknownError -> _errorMessage.value = result.message
                is NetworkResult.Loading -> _isLoading.value = true
            }
            _isLoading.value = false
        }
    }
}
