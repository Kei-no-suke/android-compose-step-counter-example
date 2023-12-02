package com.example.stepcounterexample.common

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StepCounter @Inject constructor(){
    var currentSteps: Float = 0f
        private set

    var initialLogFlag: Boolean = false
        private set

    fun setCurrentSteps(steps: Float){
        currentSteps = steps
    }

    fun setInitialLogFlag(flag: Boolean){
        initialLogFlag = flag
    }
}