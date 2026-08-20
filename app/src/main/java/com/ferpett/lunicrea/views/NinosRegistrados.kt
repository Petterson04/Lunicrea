package com.ferpett.lunicrea.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import android.content.Intent
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ferpett.lunicrea.Elements.BotonRegresar
import com.ferpett.lunicrea.Elements.Botones
import com.ferpett.lunicrea.Elements.SpaceTopBottom
import com.ferpett.lunicrea.Elements.Titulo
import com.ferpett.lunicrea.Model.NinoViewModel
import com.ferpett.lunicrea.ui.theme.LunicreaTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferpett.lunicrea.Elements.TextosSimples
import com.ferpett.lunicrea.ui.theme.RosaClaro
import com.ferpett.lunicrea.ui.theme.Terracota
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.ferpett.lunicrea.Elements.SpaceBetween


class NinosRegistrados : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LunicreaTheme {
                NinosRegistradosView()
            }
        }
    }
}

@Composable
fun NinosRegistradosView() {
    val context = LocalContext.current
    val viewModel: NinoViewModel = viewModel()
    val listninos by viewModel.listninos.collectAsState()
    val ninosBuscados by viewModel.NinoName.collectAsState()
    val mostrarLista = if (ninosBuscados.isNotEmpty()) ninosBuscados else listninos

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(searchQuery) {
        viewModel.obtenerNinos()
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
            modifier = Modifier.fillMaxSize() .verticalScroll(rememberScrollState())
        )
        {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row {
                    Titulo("Lista de niños actuales ")
                    Titulo("Total ${listninos.size} niños")
                }
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null)
                },
                placeholder = { Text("Buscar Niño") },
                modifier = Modifier
                    .fillMaxWidth()
                ,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                    Botones("Buscar") {
                        viewModel.buscarNinoContiene(searchQuery.text)
                    }
                SpaceBetween(150)
                    Botones("Limpiar") {
                        searchQuery= TextFieldValue("")
                        viewModel.buscarNinoContiene(searchQuery.text)
                    }

            }

            LazyColumn (
                modifier = Modifier
                    .fillMaxWidth()
                    .size(250.dp)
            ){
                items(mostrarLista){nino->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 3.dp)

                        ,
                        colors = CardDefaults.cardColors(
                            containerColor = Terracota
                        ),
                        onClick = {
                            val intent = Intent(context, Infonino::class.java)
                            intent.putExtra("idNino",nino.userId)
                            context.startActivity(intent)
                        },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ){
                        TextosSimples("Nombre del niño: ${nino.nombre}", Color.White)
                        TextosSimples("Nombre del Padre/Madre: ${nino.nombrePadres}",Color.White)
                        TextosSimples("Numero de emergencia: ${nino.numeroEmergencia}", Color.White)
                        TextosSimples("Personas autorizadas a recoger ${nino.nombreAutorizado}", Color.White)

                    }
                }
            }

            Botones("Agregar un nuevo niño") {
                val intent = Intent(context, NuevoNino::class.java)
                context.startActivity(intent)
            }
            SpaceTopBottom(70)


        }//Column


    }
}



@Preview(showBackground = true)
@Composable
fun NinosRegistradospreview() {
    LunicreaTheme {
        NinosRegistradosView()
    }
}