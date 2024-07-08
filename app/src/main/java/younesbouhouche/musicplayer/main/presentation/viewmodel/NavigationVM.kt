package younesbouhouche.musicplayer.main.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NavigationVM
    @Inject
    constructor() : ViewModel() {
        private val _state = MutableStateFlow(0)
        val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

        fun update(newState: Int) {
            _state.value = newState
        }
    }
