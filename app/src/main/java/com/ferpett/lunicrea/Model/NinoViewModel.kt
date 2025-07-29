package com.ferpett.lunicrea.Model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferpett.lunicrea.Entidad.Nino
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class NinoViewModel: ViewModel() {
    private val db= Firebase.firestore

    private var Lista_ninos= MutableStateFlow<List<Nino>>(emptyList())
    val listninos= Lista_ninos.asStateFlow()

    private val ListaActivos = MutableStateFlow<List<Nino>>(emptyList())
    val ninosActivos: StateFlow<List<Nino>> = ListaActivos.asStateFlow()

    private val _ninoSeleccionado = MutableStateFlow<Nino?>(null)
    val ninoSeleccionado: StateFlow<Nino?> = _ninoSeleccionado.asStateFlow()

    private val _NinoName = MutableStateFlow<List<Nino>>(emptyList())
    val NinoName: StateFlow<List<Nino>> = _NinoName.asStateFlow()



    init {
        obtenerNinos()
    }
    fun obtenerNinos() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = db.collection("Nino").get().await()
                val ninos = result.documents.mapNotNull {
                    it.toObject(Nino::class.java)
                }
                Log.d("FirestoreDebug", "Se encontraron ${ninos.size} niños")
                Lista_ninos.value = ninos
            } catch (e: Exception) {
                Log.e("FirestoreDebug", "Error al obtener niños", e)
            }
        }
    }


    fun agregarNino(nino: Nino){
        nino.userId= UUID.randomUUID().toString()
        viewModelScope.launch (Dispatchers.IO){
            db.collection("Nino").document(nino.userId).set(nino)
                .addOnSuccessListener {
                    obtenerNinos()
                }
        }
    }

    fun actualizarNino(id: String,nino: Nino){
        viewModelScope.launch(Dispatchers.IO){
            db.collection("Nino").document(id).update(nino.toMap())
                .addOnSuccessListener {
                    obtenerNinos()
                }
        }
    }

    fun borrarNIño(id: String){
        viewModelScope.launch (Dispatchers.IO){
            db.collection("Nino").document(id).delete()
                .addOnSuccessListener {
                    Lista_ninos.value=listninos.value.filter {
                        it.userId != id
                    }
                }
        }
    }

    fun obtenerNinoporId(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val snapshot = db.collection("Nino").document(id).get().await()

                if (!snapshot.exists()) {
                    // Documento no encontrado
                    withContext(Dispatchers.Main) {
                        _ninoSeleccionado.value = null      // manejarás el “no existe” en la UI
                    }
                    return@launch
                }

                val nino = snapshot.toObject(Nino::class.java)

                withContext(Dispatchers.Main) {
                    _ninoSeleccionado.value = nino         // puede ser null si falla el mapeo
                }
            } catch (e: Exception) {
                Log.e("NinoViewModel", "Error al obtener el niño por Id", e)
                withContext(Dispatchers.Main) {
                    _ninoSeleccionado.value = null         // para que la UI muestre el error
                }
            }
        }
    }


    fun buscarNinoContiene(nombreParcial: String) {
        viewModelScope.launch {
            try {
                val result = db.collection("Nino").get().await()
                val listaFiltrada = result.documents.mapNotNull {
                    it.toObject(Nino::class.java)
                }.filter {
                    it.nombre.contains(nombreParcial, ignoreCase = true)
                }

                _NinoName.value = listaFiltrada
                Log.d("FirestoreDebug", "Se encontraron ${listaFiltrada.size} niños que contienen '$nombreParcial'")
            } catch (e: Exception) {
                Log.e("FirestoreDebug", "Error en búsqueda parcial", e)
            }
        }
    }

    fun obtenerNinosActivos(){
        viewModelScope.launch {
            try {
                val lista =
                    db.collection("Nino")
                    .whereEqualTo("estado", true)
                    .orderBy("horaEntrada")                    // opcional
                    .get()
                    .await()
                val Activos = lista.documents.mapNotNull {
                    it.toObject(Nino::class.java)
                }
                    Log.d("FirestoreDebug", "se encontraron ${Activos.size}")
                    ListaActivos.value=Activos
            } catch (e: Exception) {
                // log o state de error
                Log.e("FirestoreDebug","Error al encontrar niños", e)
            }

        }
    }

    fun registrarEntrada(ninoId: String) = viewModelScope.launch(Dispatchers.IO) {
        val ref = db.collection("Nino").document(ninoId)
        try {
            ref.update(
                mapOf(
                    "estado"      to true,
                    "horaEntrada" to FieldValue.serverTimestamp(),
                    "horaSalida"  to null        // limpia salida anterior
                )
            ).await()
        } catch (e: Exception) {
            // manejar error
        }
    }

    fun registrarSalida(ninoId: String) = viewModelScope.launch(Dispatchers.IO) {
        val ref = db.collection("Nino").document(ninoId)
        db.runTransaction { tx ->
            val snap = tx.get(ref)
            val horaEntrada        = snap.getTimestamp("horaEntrada") ?: return@runTransaction
            val minutosDisponibles = snap.getLong("horasTotales") ?: 0L

            val ahora         = Timestamp.now()
            val minutosUsados = ((ahora.seconds - horaEntrada.seconds) / 60).toLong()
            val nuevosMinutos = (minutosDisponibles - minutosUsados)


            tx.update(ref, mapOf(
                "estado"             to false,
                "horaSalida"         to ahora,
                "horaEntrada"        to null,
                "horasTotales" to nuevosMinutos
            ))
        }.await()

        obtenerNinoporId(ninoId)
    }



}





