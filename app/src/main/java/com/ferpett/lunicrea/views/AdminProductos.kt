package com.ferpett.lunicrea.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferpett.lunicrea.Elements.BotonRegresar
import com.ferpett.lunicrea.Elements.Botones
import com.ferpett.lunicrea.Elements.SpaceTopBottom
import com.ferpett.lunicrea.Elements.TextosSimples
import com.ferpett.lunicrea.Elements.Titulo
import com.ferpett.lunicrea.Model.NinoViewModel
import com.ferpett.lunicrea.Model.ProductoViewModel
import com.ferpett.lunicrea.ui.theme.LunicreaTheme
import com.ferpett.lunicrea.ui.theme.RosaClaro
import com.ferpett.lunicrea.ui.theme.Terracota

class AdminProductos : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LunicreaTheme {
                AdminProductosView()
            }
        }
    }
}

@Composable
fun AdminProductosView() {
    val context = LocalContext.current
    val viewModel: ProductoViewModel = viewModel()
    val listproductos by viewModel.listproductos.collectAsState()
    val productoBuscados by viewModel.ProductoName.collectAsState()
    val mostrarLista = if (productoBuscados.isNotEmpty()) productoBuscados else listproductos

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        viewModel.ObtenerProductos()
    }
    Box(
        modifier = Modifier
            .background((RosaClaro))
            .fillMaxSize()
            .fillMaxWidth()
    ) {
        SpaceTopBottom(50)
        BotonRegresar()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        )
        {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row {
                    Titulo("Productos")
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = null)
                    },
                    placeholder = { Text("Buscar Producto") },
                    modifier = Modifier
                        .weight(1f)
                    ,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White
                    )
                )
                Botones("Buscar") {
                    viewModel.buscarProductoContiene(searchQuery.text)
                }
                Botones("Limpiar") {
                    searchQuery= TextFieldValue("")
                    viewModel.buscarProductoContiene(searchQuery.text)
                }

            }

            LazyColumn (
                modifier = Modifier
                    .fillMaxWidth()
                    .size(300.dp)
            ){
                items(mostrarLista){producto->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 3.dp)

                        ,
                        colors = CardDefaults.cardColors(
                            containerColor = Terracota
                        ),
                        onClick = {
                            val intent = Intent(context, InfoProducto::class.java)
                            intent.putExtra("idproducto",producto.productoId)
                            context.startActivity(intent)
                        },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ){
                        TextosSimples("Nombre del producto: ${producto.nombreProducto}", Color.White)
                        TextosSimples("Cantidad: ${producto.cantidad}",Color.White)
                        TextosSimples("Categoria: ${producto.categoria}", Color.White)
                        TextosSimples("Precio: ${producto.precio}", Color.White)

                    }
                }
            }

            Botones("Agregar un nuevo producto") {
                val intent = Intent(context, AgregarProducto::class.java)
                context.startActivity(intent)
            }


        }//Column


    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview10() {
    LunicreaTheme {
        AdminProductosView()
    }
}