package com.ferpett.lunicrea.Model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferpett.lunicrea.Entidad.Ventas
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.UUID
import androidx.lifecycle.viewModelScope
import com.ferpett.lunicrea.Entidad.Visitas
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await




class VentasViewModel: ViewModel() {

    private val db= Firebase.firestore



    private val _listaVentas = MutableStateFlow<List<Ventas>>(emptyList())
    val listaVentas=_listaVentas.asStateFlow()

    private val _totalVentas = MutableLiveData<Double>()
    val totalVentas: LiveData<Double> = _totalVentas

    private val _totalProductos= MutableLiveData<Double>()
    val totalProductos: LiveData<Double> = _totalProductos

    //Semana
    private val _totalVentasSemana = MutableLiveData<Double>()
    val totalVentasSemana: LiveData<Double> = _totalVentasSemana

    private val _totalProductosSemana= MutableLiveData<Double>()
    val totalProductosSemana: LiveData<Double> = _totalProductosSemana

    private val _listaVentasSemana = MutableStateFlow<List<Ventas>>(emptyList())
    val listaVentasSemana=_listaVentasSemana.asStateFlow()

    //Paquete1
    private val _conteoPaquetes = MutableLiveData<Map<String, Int>>()
    val conteoPaquetes: LiveData<Map<String, Int>> = _conteoPaquetes

    private val _conteoPaquetesSemana = MutableLiveData<Map<String, Int>>()
    val conteoPaquetesSemana: LiveData<Map<String, Int>> = _conteoPaquetesSemana

    fun agregarVenta(ventas: Ventas){
        viewModelScope.launch(Dispatchers.IO) {
            ventas.ventaId= UUID.randomUUID().toString()
            db.collection("Ventas")
                .document(ventas.ventaId)
                .set(ventas)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun ObtenerVentasPorDia(fecha: LocalDate) {
        viewModelScope.launch {
            try {
                val zona = ZoneId.of("America/Mexico_City")
                val inicioDelDia = Timestamp(Date.from(fecha.atStartOfDay(zona).toInstant()))
                val finDelDia = Timestamp(Date.from(fecha.plusDays(1).atStartOfDay(zona).toInstant()))

                db.collection("Ventas")
                    .whereGreaterThanOrEqualTo("fecha", inicioDelDia)
                    .whereLessThan("fecha", finDelDia)
                    .get()
                    .addOnSuccessListener { result ->
                        val lista = mutableListOf<Ventas>()
                        var total = 0.0
                        var totalProductos=0.0
                        val totalGeneral= totalProductos + total
                        val conteoPaquetes= mutableMapOf<String, Int>()

                        for (document in result) {
                            val ventaId = document.getString("ventaId") ?: ""
                            val fecha = document.getTimestamp("fecha")
                            val venta: Double = document.getDouble("total") ?: 0.0
                            val ventaProducto= document.getDouble("productosTotal")?:0.0
                            val nombrePaquete = document.getString("nombrePaquete") ?: ""
                            val nombreNino = document.getString("nombreNino") ?: ""
                            val categoria= document.getString("categoria") ?:""

                            lista.add(
                                Ventas(
                                    ventaId = ventaId,
                                    fecha = fecha,
                                    total = venta,
                                    productosTotal = ventaProducto,
                                    nombrePaquete = nombrePaquete,
                                    nombreNino = nombreNino,
                                    categoria = categoria
                                )
                            )
                            total += venta
                            totalProductos += ventaProducto
                            conteoPaquetes[categoria]=conteoPaquetes.getOrDefault(categoria,0)+1
                        }

                        // Aquí puedes actualizar tus LiveData o State
                        _totalProductos.value= totalProductos

                        _listaVentas.value = lista
                        _totalVentas.value = total
                        _conteoPaquetes.value=conteoPaquetes
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error al obtener ventas", e)
                    }

            } catch (e: Exception) {
                Log.e("Firestore", "Error en ObtenerVentasPorDia", e)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun BorrarVentasporDia(fecha: LocalDate){
        viewModelScope.launch {
            try {
                val zona = ZoneId.of("America/Mexico_City")
                val inicioDelDia = Timestamp(Date.from(fecha.atStartOfDay(zona).toInstant()))
                val finDelDia = Timestamp(Date.from(fecha.plusDays(1).atStartOfDay(zona).toInstant()))

                val documentos= db.collection("Ventas")
                    .whereGreaterThanOrEqualTo("fecha", inicioDelDia)
                    .whereLessThan("fecha", finDelDia)
                    .get()
                    .await()

                documentos.documents.forEach { doc->
                    doc.reference.delete()
                }
                Log.d("ventas","Se borraron ${documentos.size()} para el fecha ${fecha}")
                ObtenerVentasPorDia(fecha)
            }catch (e: Exception){
                Log.e("Ventas","Error al obtener ventas",e)
            }
        }}

    @RequiresApi(Build.VERSION_CODES.O)
    fun ObtenerVentasPorSemana(fecha: LocalDate) {
        viewModelScope.launch {
            try {
                val zona = ZoneId.of("America/Mexico_City")
                val inicioDelDia = Timestamp(Date.from(fecha.atStartOfDay(zona).toInstant()))
                val finDelDia = Timestamp(Date.from(fecha.plusDays(7).atStartOfDay(zona).toInstant()))

                db.collection("Ventas")
                    .whereGreaterThanOrEqualTo("fecha", inicioDelDia)
                    .whereLessThan("fecha", finDelDia)
                    .get()
                    .addOnSuccessListener { result ->
                        val lista = mutableListOf<Ventas>()
                        var totalSemana = 0.0
                        var totalProductosSemana=0.0
                        val totalGeneral= totalProductosSemana + totalSemana
                        val conteoPaquetes= mutableMapOf<String, Int>()

                        for (document in result) {
                            val ventaId = document.getString("ventaId") ?: ""
                            val fecha = document.getTimestamp("fecha")
                            val venta: Double = document.getDouble("total") ?: 0.0
                            val ventaProducto= document.getDouble("productosTotal")?:0.0
                            val nombrePaquete = document.getString("nombrePaquete") ?: ""
                            val nombreNino = document.getString("nombreNino") ?: ""
                            val categoria= document.getString("categoria") ?:""

                            lista.add(
                                Ventas(
                                    ventaId = ventaId,
                                    fecha = fecha,
                                    total = venta,
                                    productosTotal = ventaProducto,
                                    nombrePaquete = nombrePaquete,
                                    nombreNino = nombreNino,
                                    categoria = categoria
                                )
                            )
                            totalSemana += venta
                            totalProductosSemana += ventaProducto
                            conteoPaquetes[categoria]=conteoPaquetes.getOrDefault(categoria,0)+1
                        }

                        // Aquí puedes actualizar tus LiveData o State
                        _totalProductosSemana.value= totalProductosSemana

                        _listaVentasSemana.value = lista
                        _totalVentasSemana.value = totalSemana
                        _conteoPaquetesSemana.value=conteoPaquetes
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error al obtener ventas", e)
                    }

            } catch (e: Exception) {
                Log.e("Firestore", "Error en ObtenerVentasPorDia", e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun BorrarVentasporSemana(fecha: LocalDate){
        viewModelScope.launch {
            try {
                val zona = ZoneId.of("America/Mexico_City")
                val inicioDelDia = Timestamp(Date.from(fecha.atStartOfDay(zona).toInstant()))
                val finDelDia = Timestamp(Date.from(fecha.plusDays(7).atStartOfDay(zona).toInstant()))

                val documentos= db.collection("Ventas")
                    .whereGreaterThanOrEqualTo("fecha", inicioDelDia)
                    .whereLessThan("fecha", finDelDia)
                    .get()
                    .await()

                documentos.documents.forEach { doc->
                    doc.reference.delete()
                }
                Log.d("ventas","Se borraron ${documentos.size()} para el fecha ${fecha}")
                ObtenerVentasPorSemana(fecha)
            }catch (e: Exception){
                Log.e("Ventas","Error al obtener ventas",e)
            }
        }}





}