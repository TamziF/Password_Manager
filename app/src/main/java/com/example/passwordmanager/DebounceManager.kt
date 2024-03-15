package com.example.passwordmanager

import kotlinx.coroutines.Job

class DebounceManager {

    private var job: Job? = null

    fun debounceRequest(request: () -> Unit){
        job?.cancel()
        
    }

}