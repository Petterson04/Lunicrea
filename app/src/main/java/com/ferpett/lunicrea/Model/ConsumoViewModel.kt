package com.ferpett.lunicrea.Model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferpett.lunicrea.Entidad.Consumo
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ConsumoViewModel: ViewModel() {
    private val db = Firebase.firestore

    private val Lista_consumos= MutableStateFlow<List<Consumo>>(emptyList())
    val listConsumos= Lista_consumos.asStateFlow()

    private val _consumo= MutableLiveData<List<Consumo>>()
    val consumo: LiveData<List<Consumo>> = _consumo

    private val _totalCosto= MutableLiveData<Double>()
    val totalConsumo: LiveData<Double> = _totalCosto

    fun agregarConsumo(consumo: Consumo){
        consumo.consumoId= UUID.randomUUID().toString()
        viewModelScope.launch(Dispatchers.IO) {
            db.collection("Consumo")
                .document()
                .set(consumo)
        }
    }
}