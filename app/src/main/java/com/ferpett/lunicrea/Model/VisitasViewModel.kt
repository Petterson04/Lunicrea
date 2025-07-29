package com.ferpett.lunicrea.Model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.ferpett.lunicrea.Entidad.Visitas
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.UUID

class VisitasViewModel: ViewModel() {

    private val db= Firebase.firestore

    private var _listaVisitas= MutableStateFlow<List<Visitas>>(emptyList())
    val listaVisitas= _listaVisitas.asStateFlow()




    @RequiresApi(Build.VERSION_CODES.O)
    fun ObtenerVisitasPorDia(fecha: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val zona = ZoneId.of("America/Mexico_City")
                val inicioDelDia = Timestamp(Date.from(fecha.atStartOfDay(zona).toInstant()))
                val finDelDia = Timestamp(Date.from(fecha.plusDays(1).atStartOfDay(zona).toInstant()))

                val result = db.collection("Visitas")
                    .whereGreaterThanOrEqualTo("dia", inicioDelDia)
                    .whereLessThan("dia", finDelDia)
                    .get()
                    .await()

                val visitas = result.documents.mapNotNull {
                    it.toObject(Visitas::class.java)
                }

                Log.d("Visitas", "Visitas encontradas " +
                        "${visitas}" +
                        "${inicioDelDia}"+
                        "${finDelDia}"+
                        "${visitas.size}")
                _listaVisitas.value = visitas

            } catch (e: Exception) {
                Log.e("Visitas", "Error al mostrar visitas", e)
            }
        }
    }


    fun AgregarVisitas(visitas: Visitas){
        viewModelScope.launch(Dispatchers.IO) {
                visitas.idVisita= UUID.randomUUID().toString()
                db.collection("Visitas")
                    .document(visitas.idVisita)
                    .set(visitas)
            }
        }


    @RequiresApi(Build.VERSION_CODES.O)
    fun borrarVisitasPorDia(fecha: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val zona = ZoneId.of("America/Mexico_City")
                val inicioDelDia = Timestamp(Date.from(fecha.atStartOfDay(zona).toInstant()))
                val finDelDia = Timestamp(Date.from(fecha.plusDays(1).atStartOfDay(zona).toInstant()))

                val documentos = db.collection("Visitas")
                    .whereGreaterThanOrEqualTo("dia", inicioDelDia)
                    .whereLessThan("dia", finDelDia)
                    .get()
                    .await()

                documentos.documents.forEach { doc ->
                    doc.reference.delete()
                }

                Log.d("Visitas", "Se borraron ${documentos.size()} visitas para el día $fecha")
                ObtenerVisitasPorDia(fecha)
            } catch (e: Exception) {
                Log.e("Visitas", "Error al borrar visitas por día", e)
            }
        }
    }


}