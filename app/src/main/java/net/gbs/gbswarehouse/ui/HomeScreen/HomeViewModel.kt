package net.gbs.gbswarehouse.ui.HomeScreen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import net.gbs.gbswarehouse.core.session.SessionManager
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val sessionManager: SessionManager
) : ViewModel()