package com.ferpett.lunicrea.views

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.ferpett.lunicrea.Elements.BotonRegresar
import com.ferpett.lunicrea.Elements.Botones
import com.ferpett.lunicrea.Elements.OutlinedInputs
import com.ferpett.lunicrea.Elements.SpaceTopBottom
import com.ferpett.lunicrea.Elements.Titulo
import com.ferpett.lunicrea.Entidad.Nino
import com.ferpett.lunicrea.Entidad.Paquete
import com.ferpett.lunicrea.Model.NinoViewModel
import com.ferpett.lunicrea.ui.theme.LunicreaTheme
import com.ferpett.lunicrea.ui.theme.RosaClaro
import androidx.compose.material3.ExperimentalMaterial3Api
import com.ferpett.lunicrea.Entidad.Producto
import com.ferpett.lunicrea.Model.ProductoViewModel

class AgregarProducto : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LunicreaTheme {
                AgregarProductosView()
            }
        }
    }
}

@Composable
fun AgregarProductosView() {
    val viewMode = ProductoViewModel()
    var nombreProducto by remember{ mutableStateOf("")}
    var cantidad by remember { mutableStateOf("")}
    var precio by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Bebidas") }
    val context = LocalContext.current


    Box(
        modifier = Modifier
            .background((RosaClaro))
            .fillMaxSize()
            .fillMaxWidth()
    ){
        SpaceTopBottom(50)
        BotonRegresar()
        Column (   horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize())
        {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Titulo("Formulario para nuevo Producto")
            }
            SpaceTopBottom(75)
            OutlinedInputs("Nombre del producto", nombreProducto) { nombreProducto = it }
            SpaceTopBottom(15)
            OutlinedInputs("Cantidad inicial",cantidad){cantidad=it}
            SpaceTopBottom(15)
            OutlinedInputs("Precio",precio) {precio=it}
            SpaceTopBottom(15)
            CategoriaSelector(selectedCategoria = categoria){categoria=it}
            SpaceTopBottom(15)
            Botones("Agregar Producto") {
                if (nombreProducto=="" || cantidad=="" || precio=="" ){
                    Toast.makeText(context,"Favor de rellenar los datos", Toast.LENGTH_SHORT).show()
                }else{
                    val producto= Producto(
                        categoria = categoria,
                        cantidad = cantidad.toInt(),
                        precio = precio.toDouble(),
                        nombreProducto = nombreProducto,
                    )
                    viewMode.agregarProductos(producto)
                    Toast.makeText(context,"Producto registrado con exito", Toast.LENGTH_LONG).show()
                    val intent = Intent(context, AdminProductos::class.java)
                    context.startActivity(intent)
                }
            }
            Botones("Menu Principal"){
                val menu= Intent(context, AdminView::class.java)
                context.startActivity(menu)
            }
        }
    }
}

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CategoriaSelector(
        selectedCategoria: String,
        onCategoriaSelected: (String) -> Unit
    ) {
        val categorias = listOf("Bebidas", "Alimentos", "Consumibles")
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedCategoria,
                onValueChange = {},
                readOnly = true,
                label = { Text("Selecciona categoría") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categorias.forEach { categoria ->
                    DropdownMenuItem(
                        text = { Text(categoria) },
                        onClick = {
                            onCategoriaSelected(categoria)
                            expanded = false
                        }
                    )
                }
            }
        }
    }

@Preview(showBackground = true)
@Composable
fun GreetingPreview9() {
    LunicreaTheme {
        AgregarProductosView()
    }
}