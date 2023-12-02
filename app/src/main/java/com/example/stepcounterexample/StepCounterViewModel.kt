package com.example.stepcounterexample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepcounterexample.data.CurrentLog
import com.example.stepcounterexample.data.StepCounterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StepCounterViewModel @Inject constructor(
    private val stepCounterRepository: StepCounterRepository
): ViewModel() {

    val currentLogUiState: StateFlow<List<CurrentLog>> = stepCounterRepository.getAllCurrentLogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = listOf()
        )

    companion object {
        const val TIMEOUT_MILLIS = 5_000L
    }
}