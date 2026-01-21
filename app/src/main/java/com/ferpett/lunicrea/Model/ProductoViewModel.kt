package com.ferpett.lunicrea.Model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferpett.lunicrea.Entidad.Producto
import com.google.firebase.Firebase
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

class ProductoViewModel: ViewModel() {

    private val db= Firebase.firestore

    private val Lista_productos= MutableStateFlow<List<Producto>>(emptyList())
    val listproductos= Lista_productos.asStateFlow()

    private val _productoSeleccionado=MutableStateFlow<Producto?>(null)
    val productoSeleccionado: StateFlow<Producto?> = _productoSeleccionado.asStateFlow()

    private val _ProductoName = MutableStateFlow<List<Producto>>(emptyList())
    val ProductoName: StateFlow<List<Producto>> = _ProductoName.asStateFlow()

    init {
        ObtenerProductos()
    }

    fun ObtenerProductos(){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val result= db.collection("Productos")
                    .get()
                    .await()
                val productos= result.documents.mapNotNull {
                    it.toObject(Producto::class.java)
                }
                Log.d("Productos", "Se encontraro ${productos.size}")
                Lista_productos.value=productos
            }catch (e: Exception){
                Log.e("Productos","Erro al obtener productos", e)
            }
        }
    }

    fun agregarProductos(producto: Producto){
        producto.productoId= UUID.randomUUID().toString()
        viewModelScope.launch {
            db.collection("Productos")
                .document(producto.productoId)
                .set(producto)
                .addOnSuccessListener {
                    ObtenerProductos()
                }
        }
    }

    fun actualizarProducto(id: String, producto: Producto){
        viewModelScope.launch(Dispatchers.IO) {
            db.collection("Productos")
                .document(id)
                .update(producto.toMap())
        }
    }

    fun elimarProducto(id: String){
        viewModelScope.launch {
            db.collection("Productos")
                .document(id)
                .delete()
                .addOnSuccessListener {
                    Lista_productos.value=listproductos.value.filter {
                        it.productoId !=id
                    }
                }
        }
    }

    fun obtenerproductoPorId(id: String){
        viewModelScope.launch {
            try {
                val snapshot= db.collection("Productos").document(id).get().await()
                if(!snapshot.exists()){
                    withContext(Dispatchers.Main){
                        _productoSeleccionado.value=null
                    }
                    return@launch
                }
                val producto= snapshot.toObject(Producto::class.java)
                withContext(Dispatchers.Main) {
                    _productoSeleccionado.value = producto         // puede ser null si falla el mapeo
                }

            }catch (e: Exception){
                Log.e("Producto", "Error al obtener el producto por Id", e)
                withContext(Dispatchers.Main) {
                    _productoSeleccionado.value = null         // para que la UI muestre el error
                }
            }
        }
    }
    fun buscarProductoContiene(search: String){
        viewModelScope.launch {
            try {
                val result= db.collection("Productos").get().await()
                val listaFiltrada= result.documents.mapNotNull {
                    it.toObject(Producto::class.java)
                }.filter {
                    it.categoria.contains(search, ignoreCase = true) or
                            it.nombreProducto.contains(search, ignoreCase = true)
                }
                _ProductoName.value=listaFiltrada
                Log.d("Productos","Se encontraron ${listaFiltrada.size}")
            }catch (e: Exception){
                Log.e("Productos","Erro",e)
            }
        }
    }

    fun agregarInventario(
        productoId: String,
        cantidad: Int,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        db.collection("Productos")
            .document(productoId)
            .update("cantidad", FieldValue.increment(cantidad.toLong()))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun eliminarInventario(
        productoId: String,
        cantidad: Int,
        onSuccess: () -> Unit={},
        onFailure: (Exception) -> Unit={}
    ){
        db.collection("Productos")
            .document(productoId)
            .update("cantidad", FieldValue.increment(-cantidad.toLong()))
            .addOnSuccessListener { onSuccess }
            .addOnFailureListener { onFailure }
    }
}