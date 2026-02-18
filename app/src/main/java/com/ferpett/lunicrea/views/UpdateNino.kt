package com.ferpett.lunicrea.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferpett.lunicrea.Elements.BotonRegresar
import com.ferpett.lunicrea.Elements.Botones
import com.ferpett.lunicrea.Elements.OutlinedInputs
import com.ferpett.lunicrea.Elements.SpaceTopBottom
import com.ferpett.lunicrea.Elements.Titulo
import com.ferpett.lunicrea.Entidad.Nino
import com.ferpett.lunicrea.Model.NinoViewModel
import com.ferpett.lunicrea.ui.theme.LunicreaTheme
import com.ferpett.lunicrea.ui.theme.RosaClaro
import kotlin.math.log

class UpdateNino : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
            val ninoId = intent.getStringExtra("idNino")?: return
        Log.d("idNino", "Nino recibido $ninoId")

        setContent {
            LunicreaTheme {
                ActualizarNinoView(ninoId)
            }
        }
    }
}

@Composable
fun ActualizarNinoView(ninoId: String,
                       viewModel: NinoViewModel = viewModel()
                       ) {

    val context = LocalContext.current

    val nino by viewModel.ninoSeleccionado.collectAsState()

    var nombre by remember{ mutableStateOf("")}
    var edad by remember { mutableStateOf("")}
    var nombrePadre by remember { mutableStateOf("") }
    var numeroEmergencia by remember { mutableStateOf("")}
    var nombreAutorizado by remember { mutableStateOf("")}
    var horasTotales by remember { mutableStateOf("")}
    val focusManager = LocalFocusManager.current


    LaunchedEffect(Unit) {
        viewModel.obtenerNinoporId(ninoId)
    }

    LaunchedEffect(nino) {
        nino?.let{
            nombre=it.nombre
            nombreAutorizado= it.nombreAutorizado
            nombrePadre= it.nombrePadres
            edad=it.edad
            numeroEmergencia= it.numeroEmergencia
            horasTotales= it.horasTotales.toString()

        }
    }


nino?.let {
    Box(
        modifier = Modifier
            .background((RosaClaro))
            .fillMaxSize()
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
            .verticalScroll(rememberScrollState())
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
                Titulo("Formulario para modificar niño")
            }
            SpaceTopBottom(75)
            OutlinedInputs("Nombre del niño", nombre) { newValue -> if (newValue.all { it.isLetter() || it.isWhitespace() }) nombre = newValue }
            SpaceTopBottom(15)
            OutlinedInputs("Edad del niño", edad) { newValue -> if (newValue.all { it.isDigit() }) edad = newValue }
            SpaceTopBottom(15)
            OutlinedInputs("Nombre del Padre/Madre", nombrePadre) { newValue -> if (newValue.all { it.isLetter() || it.isWhitespace() }) nombrePadre = newValue }
            SpaceTopBottom(15)
            OutlinedInputs("Minutos Actuales del niño", horasTotales) { newValue -> if (newValue.all {it.isDigit() }) horasTotales = newValue}
            SpaceTopBottom(15)
            OutlinedInputs("Numero de emergencias", numeroEmergencia) {newValue -> if (newValue.all {it.isDigit()}) numeroEmergencia= newValue }
            SpaceTopBottom(15)
            OutlinedInputs(
                "Nombre de persona autorizada a recoger",
                nombreAutorizado
            ) { nombreAutorizado = it }

            Row(

            ) {


                Botones("Modificar Nino") {
                    if (nombre == "" || edad == "" || nombrePadre == "" || numeroEmergencia == "" || nombreAutorizado == "") {
                        Toast.makeText(context, "Favor de rellenar los datos", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        val minutos = horasTotales.toIntOrNull()?.times(60) ?: 0
                        val ninos = Nino(
                            nombre = nombre,
                            edad = edad,
                            nombrePadres = nombrePadre,
                            numeroEmergencia = numeroEmergencia,
                            nombreAutorizado = nombreAutorizado,
                            horasTotales = minutos,
                            estado = false,
                            userId = ninoId
                        )
                        viewModel.actualizarNino(ninoId, ninos)
                        Toast.makeText(context, "Niño Actualizado con exito", Toast.LENGTH_LONG)
                            .show()
                        val intent = Intent(context, NinosRegistrados::class.java)
                        context.startActivity(intent)
                    }
                }
                Botones("Menu Principal") {
                    val menu = Intent(context, PrincipalView::class.java)
                    context.startActivity(menu)
                }
            }
        }
    }
}?: run {
    // Mientras se carga el producto
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Cargando Niño...")
    }
}
}




@Preview(showBackground = true)
@Composable
fun UpdateNiñoprre() {

    var nombre by remember{ mutableStateOf("")}
    var edad by remember { mutableStateOf("")}
    var nombrePadre by remember { mutableStateOf("") }
    var numeroEmergencia by remember { mutableStateOf("")}
    var nombreAutorizado by remember { mutableStateOf("")}
    var horasTotales by remember { mutableStateOf("")}
    val context= LocalContext.current


    LunicreaTheme {
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
                    Titulo("Formulario para modificar niño")
                }
                SpaceTopBottom(75)
                OutlinedInputs("Nombre del niño", nombre) { nombre = it }
                SpaceTopBottom(15)
                OutlinedInputs("Edad del niño", edad) { edad = it }
                SpaceTopBottom(15)
                OutlinedInputs("Nombre del Padre/Madre", nombrePadre) { nombrePadre = it }
                SpaceTopBottom(15)
                OutlinedInputs("Horas Actuales del niño", horasTotales) { horasTotales = it }
                SpaceTopBottom(15)
                OutlinedInputs("Numero de emergencias", numeroEmergencia) { numeroEmergencia = it }
                SpaceTopBottom(15)
                OutlinedInputs(
                    "Nombre de persona autorizada a recoger",
                    nombreAutorizado
                ) { nombreAutorizado = it }
                Botones("Modificar Nino") {

                }
                Botones("Menu Principal") {
                    val menu = Intent(context, PrincipalView::class.java)
                    context.startActivity(menu)
                }
            }
        }
    }
}

