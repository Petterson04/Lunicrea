package com.ferpett.lunicrea.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferpett.lunicrea.Elements.BotonRegresar
import com.ferpett.lunicrea.Elements.Botones
import com.ferpett.lunicrea.Elements.SpaceBetween
import com.ferpett.lunicrea.Elements.SpaceTopBottom
import com.ferpett.lunicrea.Elements.TextosInformacion
import com.ferpett.lunicrea.Elements.TextosSimples
import com.ferpett.lunicrea.Elements.Titulo
import com.ferpett.lunicrea.Model.ProductoViewModel
import com.ferpett.lunicrea.ui.theme.LunicreaTheme
import com.ferpett.lunicrea.ui.theme.RosaClaro

class InfoProducto : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LunicreaTheme {
              InfoProductoView()
            }
        }
    }
}

@Composable
fun InfoProductoView() {
    val context= LocalContext.current
    val id = (context as? Activity)
        ?.intent
        ?.getStringExtra("idproducto")

    val viewModel: ProductoViewModel= viewModel()

    LaunchedEffect(id) {
        id?.let { viewModel.obtenerproductoPorId(it) }
    }



    var mostrarDialogo by remember { mutableStateOf(false) }
    val producto by viewModel.productoSeleccionado.collectAsState(initial = null)
    Box(
        modifier = Modifier
            .background(RosaClaro)
            .fillMaxSize()
            .padding(16.dp)
    ) {

        SpaceTopBottom(50)
        BotonRegresar()
        when {
            id == null -> TextosSimples("ID de niño no encontrado", Color.White)

            producto == null -> TextosSimples("Cargando datos…", Color.White)

            else ->

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    SpaceTopBottom(100)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Titulo("Informacion del Producto")
                    }
                    SpaceTopBottom(15)

                    TextosInformacion("Nombre:", Color.White)
                    SpaceTopBottom(5)
                    TextosSimples(producto!!.nombreProducto, Color.White)
                    SpaceTopBottom(15)
                    TextosInformacion("Cantidad", Color.White)
                    SpaceTopBottom(5)
                    TextosSimples(producto!!.cantidad.toString(), Color.White)
                    SpaceTopBottom(5)
                    TextosInformacion("Precio:", Color.White)
                    SpaceTopBottom(5)
                    TextosSimples(producto!!.precio.toString(), Color.White)
                    SpaceTopBottom(15)
                    TextosInformacion("Categoria:", Color.White)
                    SpaceBetween(5)
                    TextosSimples(producto!!.categoria, Color.White)
                    SpaceTopBottom(10)
                    if (mostrarDialogo) {
                        DialogoAgregarInventario(
                            productoId = producto!!.productoId, // o el ID correspondiente
                            onDismiss = { mostrarDialogo = false },
                            onInventarioAgregado = {
                                viewModel.obtenerproductoPorId(producto!!.productoId)
                            }
                        )
                    }
                    Row {
                        Botones("Agregar Inventario") {
                            mostrarDialogo=true
                        }
                        SpaceBetween(3)
                        Botones("Eliminar producto") {
                            viewModel.elimarProducto(id)
                            val productos= Intent(context, AdminProductos::class.java)
                            context.startActivity(productos)
                        }
                        SpaceBetween(3)
                        Botones("Editar Producto"){
                            val edit= Intent(context, UpdateProduct::class.java)
                            edit.putExtra("id",id)
                            context.startActivity(edit)
                        }
                    }
                }
        }
    }
}
@Composable
fun DialogoAgregarInventario(
    productoId: String,
    onDismiss: () -> Unit,
    onInventarioAgregado: () -> Unit
) {
    var cantidadTexto by remember { mutableStateOf("") }
    var mostrandoError by remember { mutableStateOf(false) }
    val viewModel: ProductoViewModel= viewModel()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar inventario") },
        text = {
            Column {
                OutlinedTextField(
                    value = cantidadTexto,
                    onValueChange = { cantidadTexto = it },
                    label = { Text("Cantidad a agregar") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                if (mostrandoError) {
                    Text("Ingresa una cantidad válida", color = Color.Red, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val cantidad = cantidadTexto.toIntOrNull()
                if (cantidad != null && cantidad > 0) {
                    viewModel.agregarInventario(
                        productoId = productoId,
                        cantidad = cantidad,
                        onSuccess = {
                            onInventarioAgregado()
                            onDismiss()
                        },
                        onFailure = {
                            mostrandoError = true
                        }
                    )
                } else {
                    mostrandoError = true
                }
            }) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview11() {
    LunicreaTheme {
        InfoProductoView()
    }
}