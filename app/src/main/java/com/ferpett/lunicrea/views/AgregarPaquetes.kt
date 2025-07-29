package com.ferpett.lunicrea.views

import android.app.Activity
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferpett.lunicrea.Elements.BotonRegresar
import com.ferpett.lunicrea.Elements.Botones
import com.ferpett.lunicrea.Elements.SpaceTopBottom
import com.ferpett.lunicrea.Elements.TextosInformacion
import com.ferpett.lunicrea.Elements.TextosSimples
import com.ferpett.lunicrea.Elements.Titulo
import com.ferpett.lunicrea.Entidad.Nino
import com.ferpett.lunicrea.Model.NinoViewModel
import com.ferpett.lunicrea.ui.theme.RosaClaro
import com.ferpett.lunicrea.ui.theme.LunicreaTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import com.ferpett.lunicrea.Elements.SpaceBetween
import com.ferpett.lunicrea.Entidad.Paquete
import com.ferpett.lunicrea.Entidad.Ventas
import com.ferpett.lunicrea.Model.PaqueteViewModel
import com.ferpett.lunicrea.Model.VentasViewModel
import com.google.firebase.Timestamp

class AgregarPaquetes : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LunicreaTheme {
                AgregarPaquetesView()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarPaquetesView() {

    val ninoViewModel: NinoViewModel = viewModel()
    val paqueteViewModel: PaqueteViewModel = viewModel()
    val ventasViewModel: VentasViewModel=viewModel()
    val dia= Timestamp.now()
    val context = LocalContext.current
    val id = (context as? Activity)?.intent?.getStringExtra("idNino")
    val nino by ninoViewModel.ninoSeleccionado.collectAsState(initial = null)

    LaunchedEffect(id) {
        id?.let { ninoViewModel.obtenerNinoporId(it) }
    }
    Box(
        modifier = Modifier
            .background(RosaClaro)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            id == null -> {
                TextosSimples("ID de niño no encontrado", Color.White)
            }

            nino == null -> {
                TextosSimples("Cargando datos…", Color.White)
            }

            else -> {
                var expanded by remember { mutableStateOf(false) }
                val opciones = listOf(
                    Paquete(100.00, 60, "Paquete 1 Hora", nino!!.userId,"1",nino!!.nombre),
                    Paquete(70.0, 30, "Paquete 30 Minutos",nino!!.userId,"2",nino!!.nombre),

                )
                var seleccion by remember { mutableStateOf(opciones[0]) }
                // SOLO cuando nino no es null se muestra la interfaz
                SpaceTopBottom(10)
                BotonRegresar()
                SpaceTopBottom(10)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Titulo("Agregar Horas")
                    }
                    SpaceTopBottom(25)
                    Row {
                        TextosInformacion("Nombre del niño", Color.White)
                        SpaceBetween(10)
                        TextosSimples(nino!!.nombre, Color.White)
                    }
                    //Funcion para agregar paquetes
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                    ) {
                        TextField(
                            value = seleccion.nombre,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Selecciona una opción") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            opciones.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion.nombre) },
                                    onClick = {
                                        seleccion = opcion
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }   //selector de paqueetes

                    Botones("Agregar Paquete") {
                        val horas_actuales = nino!!.horasTotales
                        val nuevas_horas = horas_actuales + seleccion.duracion
                        val ninos = Nino(
                            nombre = nino!!.nombre,
                            edad = nino!!.edad,
                            nombrePadres = nino!!.nombrePadres,
                            numeroEmergencia = nino!!.numeroEmergencia,
                            nombreAutorizado = nino!!.nombreAutorizado,
                            horasTotales = nuevas_horas,
                            estado = false,
                            userId = nino!!.userId
                        )
                        val venta= Ventas(
                            fecha = dia,
                            total = seleccion.costo,
                            nombrePaquete = seleccion.nombre,
                            nombreNino = nino!!.nombre
                        )

                        ninoViewModel.actualizarNino(id, ninos)
                        paqueteViewModel.agregarPaquete(seleccion)
                        ventasViewModel.agregarVenta(venta)
                        val regresar = Intent(context, Infonino::class.java)
                        regresar.putExtra("idNino", nino!!.userId)
                        context.startActivity(regresar)
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview5() {
    LunicreaTheme {
        AgregarPaquetesView()
    }
}