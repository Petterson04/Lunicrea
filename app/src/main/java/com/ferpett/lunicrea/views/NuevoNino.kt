package com.ferpett.lunicrea.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
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
import com.ferpett.lunicrea.Elements.BotonRegresar
import com.ferpett.lunicrea.Elements.Botones
import com.ferpett.lunicrea.Elements.OutlinedInputs
import com.ferpett.lunicrea.Elements.SpaceTopBottom
import com.ferpett.lunicrea.Elements.Titulo
import com.ferpett.lunicrea.Entidad.Nino
import com.ferpett.lunicrea.Model.NinoViewModel
import com.ferpett.lunicrea.ui.theme.LunicreaTheme
import com.ferpett.lunicrea.ui.theme.RosaClaro

class NuevoNino : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LunicreaTheme {
                AgregarNino()
                }
            }
        }
    }


@SuppressLint("SuspiciousIndentation")
@Composable
fun AgregarNino(){
 val viewMode = NinoViewModel()
 var nombre by remember{ mutableStateOf("")}
 var edad by remember { mutableStateOf("")}
 var nombrePadre by remember { mutableStateOf("") }
 var numeroEmergencia by remember { mutableStateOf("")}
 var nombreAutorizado by remember { mutableStateOf("")}
 var horasTotales by remember { mutableStateOf("")}


 val context = LocalContext.current
    val focusManager = LocalFocusManager.current


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

    ){
        SpaceTopBottom(50)
        BotonRegresar()
        Column (   horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize())
        {
            Box(
                modifier = Modifier
                    .fillMaxWidth()

                ,
                contentAlignment = Alignment.Center
            ) {
                Titulo("Formulario para nuevo niño")
            }
            SpaceTopBottom(50)
            OutlinedInputs("Nombre del niño", nombre) { newValue -> if (newValue.all { it.isLetter() || it.isWhitespace() }) nombre = newValue }
            SpaceTopBottom(10)
            OutlinedInputs("Edad del niño",edad){edadValue -> if (edadValue.all { it.isDigit() }) edad = edadValue }
            SpaceTopBottom(10)
            OutlinedInputs("Nombre del Padre/Madre",nombrePadre) {newValue-> if(newValue.all { it.isLetter()|| it.isWhitespace() }) nombrePadre=newValue}
            SpaceTopBottom(10)
            OutlinedInputs("Numero de emergencias",numeroEmergencia){numeroValue -> if (numeroValue.all { it.isDigit() }) numeroEmergencia = numeroValue}
            SpaceTopBottom(10)
            OutlinedInputs("Nombre de persona autorizada a recoger",nombreAutorizado){newValue-> if(newValue.all { it.isLetter()|| it.isWhitespace()  }) nombreAutorizado=newValue}
            Row {


                Botones("Agregar Niño") {
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
                            horasTotales = 0,
                            estado = false,
                        )
                        viewMode.agregarNino(ninos)
                        Toast.makeText(context, "Niño registrado con exito", Toast.LENGTH_LONG)
                            .show()
                        nombre = ""
                        edad = ""
                        nombrePadre = ""
                        numeroEmergencia = ""
                        nombreAutorizado = ""
                        horasTotales = ""
                        val intent = Intent(context, NinosRegistrados::class.java)
                        context.startActivity(intent)
                    }
                }
                Botones("Menu Principal") {
                    val menu = Intent(context, PrincipalView::class.java)
                    context.startActivity(menu)
                }
            }
            SpaceTopBottom(50)
        }
    }


}



@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    LunicreaTheme {
        AgregarNino()
    }
}