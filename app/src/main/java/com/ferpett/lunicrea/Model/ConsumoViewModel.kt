package com.ferpett.lunicrea.Model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferpett.lunicrea.Entidad.Consumo
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
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

    fun obtenerConsumopornino(id: String){
        db.collection("Consumo")
            .whereEqualTo("ninoid",id.trim())
            .get()
            .addOnSuccessListener { result ->
                val lista= mutableListOf<Consumo>()
                var total= 0.0
                for(document in result){
                    val ninoid = document.getString("ninoId")?:""
                    val nombre = document.getString("nombreNino")?:""
                    val producto = document.getString("producto")?:""
                    val consumoId = document.getString("consumoId")?:""
                    val cantidad = document.getLong("cantidad")?.toInt()?:0
                    val precio= document.getDouble("precio")?:0.0
                    val fecha= document.getTimestamp("fecha")?: Timestamp.now()

                    lista.add(Consumo(
                        ninoid,nombre,producto,cantidad,precio,fecha,consumoId
                    ))
                    total += precio

                    _consumo.postValue(lista)
                    _totalCosto.postValue(total)
                }

            }

    }

    fun EliminarConsumo(ninoId: String){
        viewModelScope.launch(Dispatchers.IO) {
            db.collection("Consumo")
                .whereEqualTo("ninoid",ninoId.trim())
                .get()
                .addOnSuccessListener { documents->
                    for (document in documents){
                        db.collection("Consumo")
                            .document(document.id)
                            .delete()
                    }
                    val total= 0.0
                    _totalCosto.postValue(total)
                }
        }
    }
}