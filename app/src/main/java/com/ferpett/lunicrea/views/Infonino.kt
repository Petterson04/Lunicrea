package com.ferpett.lunicrea.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.ferpett.lunicrea.Elements.TextosSimples
import com.ferpett.lunicrea.Model.NinoViewModel
import com.ferpett.lunicrea.ui.theme.LunicreaTheme
import com.ferpett.lunicrea.ui.theme.RosaClaro
import androidx.compose.runtime.getValue

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferpett.lunicrea.Elements.BotonRegresar
import com.ferpett.lunicrea.Elements.Botones
import com.ferpett.lunicrea.Elements.SpaceBetween
import com.ferpett.lunicrea.Elements.SpaceTopBottom
import com.ferpett.lunicrea.Elements.TextosInformacion
import com.ferpett.lunicrea.Elements.Titulo
import com.ferpett.lunicrea.ui.theme.Terracota
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.ferpett.lunicrea.Elements.MiDialogoSimple
import com.ferpett.lunicrea.Entidad.Visitas
import com.ferpett.lunicrea.Model.PaqueteViewModel
import com.ferpett.lunicrea.Model.VentasViewModel
import com.ferpett.lunicrea.Model.VisitasViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale


class Infonino : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LunicreaTheme {
                    InfoniñoView()
            }
        }
    }
}



@Composable
fun InfoniñoView(

) {
    val context = LocalContext.current
    val id = (context as? Activity)
        ?.intent
        ?.getStringExtra("idNino")

    //ViewModels
    val ninoViewModel: NinoViewModel = viewModel()
    val paqueteViewModel: PaqueteViewModel = viewModel()
    val visitasViewModel: VisitasViewModel= viewModel()
    val ventasViewModel: VentasViewModel=viewModel()


    //Niños
    val nino by ninoViewModel.ninoSeleccionado.collectAsState(initial = null)
    //Paquetes
    val paquetes by paqueteViewModel.paquetes.observeAsState(emptyList())
    val totalCosto by paqueteViewModel.totalCosto.observeAsState(0.0)
    //ventas

    //Dialogos
    var mostrarDialogo by remember { mutableStateOf(false)}
    var dialogoPago by remember { mutableStateOf(false) }

    val dia= Timestamp.now()


    LaunchedEffect(id) {
        id?.let { ninoViewModel.obtenerNinoporId(it) }
    }
    LaunchedEffect(nino) {
        nino?.let {
            Log.d("DebugPaquetes", "Obteniendo paquetes para: ${it.userId}")
            Log.d("DebugPaquetes", "Paquetes del ${it.userId} son: $paquetes")
            paqueteViewModel.obtenerPaquetesDelNino(it.userId)

            Log.d("ComposePaquetes", "Id: ${it.userId}")
        } ?: Log.d("DebugPaquetes", "Esperando que se cargue el niño…")
    }

    var yaCargoPaquetes by remember { mutableStateOf(false) }

    LaunchedEffect(nino) {
        nino?.let {
            if (!yaCargoPaquetes) {
                Log.d("DebugPaquetes", "Obteniendo paquetes para: ${it.userId}")
                paqueteViewModel.obtenerPaquetesDelNino(it.userId)
                yaCargoPaquetes = true
            }
        }
    }

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

            nino == null -> TextosSimples("Cargando datos…", Color.White)

            else ->

            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
            ){
                SpaceTopBottom(100)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    ) {
                    Titulo("Informacion del niño")
                }
                SpaceTopBottom(15)
                Row{
                    TextosInformacion("Nombre:", Color.White)
                    SpaceBetween(5)
                    TextosSimples(nino!!.nombre,Color.White)
                    SpaceBetween(15)
                    TextosInformacion("Edad:", Color.White)
                    SpaceBetween(5)
                    TextosSimples(nino!!.edad,Color.White)
                }
                SpaceTopBottom(5)
                Row{
                    TextosInformacion("Nombre del Padre/Madre:", Color.White)
                    SpaceBetween(5)
                    TextosSimples(nino!!.nombrePadres,Color.White)
                    SpaceBetween(15)
                    TextosInformacion("Numero de emergencia:", Color.White)
                    SpaceBetween(5)
                    TextosSimples(nino!!.numeroEmergencia,Color.White)
                }
                SpaceTopBottom(10)
                TextosInformacion("Personas Autorizadas para recoleccion", Color.White)
                TextosSimples(nino!!.nombreAutorizado,Color.White)
                SpaceTopBottom(10)
                Row {
                    val tiempoRestante= nino!!.horasTotales
                    if (tiempoRestante < 0){
                        TextosInformacion("exceso de tiempo:   ${tiempoRestante} minutos",Color.Red)
                    }else{
                        TextosInformacion("Minutos por usar:   ${tiempoRestante}",Color.White)
                    }
                    SpaceBetween(15)
                    TextosInformacion("Total a pagar", Color.White)
                    SpaceBetween(7)
                    TextosSimples(totalCosto.toString(),Color.White)
                }

                TextosInformacion("Dia de la ultima visita", Terracota)
                val diaVisita=nino!!.horaSalida?.toDate()
                val fechaformateada=diaVisita?.let {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                }

                if (diaVisita==null){
                    TextosSimples("No hay fecha de ultima visita",Color.White)
                }else{
                    TextosSimples(fechaformateada.toString(),Color.White)
                }
                SpaceTopBottom(20)
                Row {
                    Botones("Agregar Paquetes") {
                        val AgregarHora= Intent(context, AgregarPaquetes::class.java)
                        AgregarHora.putExtra("idNino",nino!!.userId)
                        context.startActivity(AgregarHora)
                    }
                    SpaceBetween(3)
                    //Productos
                    Botones("Agregar Productos"){
                        val AgregarProducto= Intent(context, AgregarProducto::class.java)
                        AgregarProducto.putExtra("idNino",nino!!.userId)
                        context.startActivity(AgregarProducto)
                    }
                    //Ingresos
                    Botones(
                        if (nino!!.estado==false){
                        "Entrada"
                    }else{
                        "Salida"
                    }

                    ) {
                      mostrarDialogo=true

                    }
                    SpaceBetween(3)
                }
                Row{
                    //pagar carro
                    Botones("Pagar carrito"){
                        dialogoPago=true
                    }
                    Botones("Menu Principal"){
                        val menu= Intent(context, PrincipalView::class.java)
                        context.startActivity(menu)
                    }
                }
                //Dialogo de entrada/salida
                MiDialogoSimple(
                    mostrarDialogo = mostrarDialogo,{mostrarDialogo=false},{
                        if (nino!!.estado==false){
                            ninoViewModel.registrarEntrada(id)
                            val visitas= Visitas(
                                Dia = dia,
                                idNino = nino!!.userId,
                                nombreNino = nino!!.nombre,
                            )
                            visitasViewModel.AgregarVisitas(visitas)
                            val NinosAct = Intent(context, Bitacora::class.java)
                            context.startActivity(NinosAct)
                        }else{
                            ninoViewModel.registrarSalida(id)
                            Toast.makeText(context,"Salida Registrada", Toast.LENGTH_SHORT).show()
                        }
                        mostrarDialogo=false
                    }
                )
                //Dialoogo de pago
                MiDialogoSimple(
                    dialogoPago,{dialogoPago=false},{
                        paqueteViewModel.Eliminarpaquetes(nino!!.userId)
                        dialogoPago=false
                    }
                )

            }
        }
    }
}





@Preview(showBackground = true)
@Composable
fun GreetingPreview4() {
    InfoniñoView()
}