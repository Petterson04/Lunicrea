package com.ferpett.lunicrea.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.ferpett.lunicrea.Elements.*
import com.ferpett.lunicrea.Entidad.Producto
import com.ferpett.lunicrea.Model.ProductoViewModel
import com.ferpett.lunicrea.ui.theme.LunicreaTheme
import com.ferpett.lunicrea.ui.theme.RosaClaro



class UpdateProduct : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val productoId = intent.getStringExtra("id") ?: return
        Log.d("Producto", "Id recibido ${productoId}")

        setContent {
            LunicreaTheme {
                ActualizarProductoView(productoId)
            }
        }
    }
}

@Composable
fun ActualizarProductoView(productoId: String) {
    val viewModel = remember { ProductoViewModel() }
    val context = LocalContext.current

    val producto by viewModel.productoSeleccionado.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Bebidas") }

    // Cargar una sola vez
    LaunchedEffect(Unit) {
        viewModel.obtenerproductoPorId(productoId)
    }

    // Cuando llega el producto desde la base de datos
    LaunchedEffect(producto) {
        producto?.let {
            nombre = it.nombreProducto
            cantidad = it.cantidad.toString()
            precio = it.precio.toString()
            categoria = it.categoria
        }
    }

    producto?.let {
        Box(
            modifier = Modifier
                .background(RosaClaro)
                .fillMaxSize()
        ) {
            SpaceTopBottom(50)
            BotonRegresar()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Titulo("Editar Producto")
                SpaceTopBottom(75)

                OutlinedInputs("Nombre del producto", nombre) { nombre = it }
                SpaceTopBottom(15)

                OutlinedInputs("Cantidad", cantidad) { cantidad = it }
                SpaceTopBottom(15)

                OutlinedInputs("Precio", precio) { precio = it }
                SpaceTopBottom(15)

                CategoriaSelector(selectedCategoria = categoria) { categoria = it }
                SpaceTopBottom(15)

                Botones("Actualizar Producto") {
                    if (nombre.isBlank() || cantidad.isBlank() || precio.isBlank()) {
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    } else {
                        val productoActualizado = Producto(
                            nombreProducto = nombre,
                            cantidad = cantidad.toInt(),
                            precio = precio.toDouble(),
                            categoria = categoria
                        )
                        viewModel.actualizarProducto(id = productoId,productoActualizado)
                        Toast.makeText(context, "Producto actualizado", Toast.LENGTH_SHORT).show()
                        context.startActivity(Intent(context, AdminProductos::class.java))
                    }
                }
            }
        }
    } ?: run {
        // Mientras se carga el producto
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Cargando producto...")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdatePreview() {
    LunicreaTheme {
        ActualizarProductoView("1")
    }
}

