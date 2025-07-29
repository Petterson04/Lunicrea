package com.ferpett.lunicrea.Model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferpett.lunicrea.Entidad.Paquete
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PaqueteViewModel: ViewModel() {

    private val db= Firebase.firestore

    //Lista de todos los paquetes
    private var Lista_paquetes= MutableStateFlow<List<Paquete>>(emptyList())
    val listpaquetes= Lista_paquetes.asStateFlow()

    //Paquetes
    private val _paquetes = MutableLiveData<List<Paquete>>()
    val paquetes: LiveData<List<Paquete>> = _paquetes

    //Costo total
    private val _totalCosto = MutableLiveData<Double>()
    val totalCosto: LiveData<Double> = _totalCosto

    //view niño



    init {
        ObtenerPaquetes()
    }

    fun ObtenerPaquetes() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = db.collection("Paquete").get().await()
                val paquetes = result.documents.mapNotNull {
                    it.toObject(Paquete::class.java)
                }
                Log.d("FirestoreDebug", "Se encontraron ${paquetes.size} niños")
                Lista_paquetes.value = paquetes
            } catch (e: Exception) {
                Log.e("FirestoreDebug", "Error al obtener niños", e)
            }
        }
    }

    fun agregarPaquete(paquete: Paquete){
        paquete.paqueteId= UUID.randomUUID().toString()
        viewModelScope.launch (Dispatchers.IO){
            db.collection("Paquete")
                .document()
                .set(paquete)
                .addOnSuccessListener {
                    ObtenerPaquetes()
                }
        }
    }

    fun obtenerPaquetesDelNino(ninoId: String) {
       db.collection("Paquete")
            .whereEqualTo("ninoId", ninoId.trim())
            .get()
            .addOnSuccessListener { result ->
                val lista = mutableListOf<Paquete>()
                var total = 0.0
                for (document in result) {
                    val nombre = document.getString("nombre") ?: ""
                    val costo = document.getDouble("costo") ?: 0.0
                    val duracion = document.getLong("duracion")?.toInt() ?: 0
                    val id = document.getString("ninoId") ?: ""
                    val paqueteid = document.getString("paqueteId")
                    val nombreNino= document.getString("nombreNino")

                    lista.add(Paquete(
                        costo, duracion, nombre, id,paqueteid,nombreNino
                    ))
                    total += costo
                }
                _paquetes.postValue(lista)
                _totalCosto.postValue(total)

                Log.d("DebugFirestore", "Paquetes encontrados: $lista")
                Log.d("TotalCosto", "Total de paquetes: $total")
            }
            .addOnFailureListener { error ->
                Log.e("Firestore", "Error al obtener paquetes", error)
            }
    }

    fun Eliminarpaquetes(ninoId: String){
        viewModelScope.launch(Dispatchers.IO) {
            db.collection("Paquete")
                .whereEqualTo("ninoId",ninoId.trim())
                .get()
                .addOnSuccessListener { documents->
                    for (document in documents){
                        db.collection("Paquete")
                            .document(document.id)
                            .delete()
                    }
                    val total= 0.0
                    _totalCosto.postValue(total)
                }
        }
    }
}


