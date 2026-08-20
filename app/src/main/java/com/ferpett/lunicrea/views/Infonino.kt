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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import com.ferpett.lunicrea.Elements.MiDialogoSimple
import com.ferpett.lunicrea.Entidad.Visitas
import com.ferpett.lunicrea.Model.ConsumoViewModel
import com.ferpett.lunicrea.Model.PaqueteViewModel
import com.ferpett.lunicrea.Model.VentasViewModel
import com.ferpett.lunicrea.Model.VisitasViewModel
import com.ferpett.lunicrea.R
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
    val consumoViewModel: ConsumoViewModel= viewModel()


    //Niños
    val nino by ninoViewModel.ninoSeleccionado.collectAsState(initial = null)
    //Paquetes
    val paquetes by paqueteViewModel.paquetes.observeAsState(emptyList())
    val totalCosto by paqueteViewModel.totalCosto.observeAsState(0.0)
    //Consumo
    val consumo by consumoViewModel.consumo.observeAsState(emptyList())
    val totalconsumo by consumoViewModel.totalConsumo.observeAsState(0.0)

    val totalGeneral= totalCosto + totalconsumo

    //Dialogos
    var mostrarDialogo by remember { mutableStateOf(false)}
    var dialogoPago by remember { mutableStateOf(false) }
    var dialogoBorrar by remember { mutableStateOf(false) }

    //Tiempos
    val tiempoRestante= nino?.horasTotales
    val tiempoExcedido = tiempoRestante != null && tiempoRestante < 0

    val dia= Timestamp.now()

    fun formatearTiempo(minutos: Int?): String {
        if (minutos == null) return "Sin tiempo"

        val excedido = minutos < 0
        val minutosAbsolutos = kotlin.math.abs(minutos)

        val horas = minutosAbsolutos / 60
        val minutosRestantes = minutosAbsolutos % 60

        val tiempo = when {
            horas > 0 && minutosRestantes > 0 ->
                "${horas} hr ${minutosRestantes} min"

            horas > 0 ->
                "${horas} hr"

            else ->
                "${minutosRestantes} min"
        }

        return if (excedido) {
            "$tiempo excedido"
        } else {
            tiempo
        }
    }


    LaunchedEffect(id) {
        id?.let { ninoViewModel.obtenerNinoporId(it) }
    }
    LaunchedEffect(nino) {
        nino?.let {
            Log.d("DebugPaquetes", "Obteniendo paquetes para: ${it.userId}")
            Log.d("DebugPaquetes", "Paquetes del ${it.userId} son: $paquetes")
            Log.d("DebugPaquetes", "Consumo del ${it.userId} son: $consumo")
            paqueteViewModel.obtenerPaquetesDelNino(it.userId)
            consumoViewModel.obtenerConsumopornino(it.userId)

            Log.d("ComposePaquetes", "Id: ${it.userId}")
        } ?: Log.d("DebugPaquetes", "Esperando que se cargue el niño…")
    }

    var yaCargoPaquetes by remember { mutableStateOf(false) }

    LaunchedEffect(nino) {
        nino?.let {
            if (!yaCargoPaquetes) {
                Log.d("DebugPaquetes", "Obteniendo paquetes para: ${it.userId}")
                paqueteViewModel.obtenerPaquetesDelNino(it.userId)
                consumoViewModel.obtenerConsumopornino(it.userId)
                yaCargoPaquetes = true
            }
        }
    }

    Box(
        modifier = Modifier
            .background(RosaClaro)
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        SpaceTopBottom(50)
        Row {
            IconButton(
                onClick = {
                    val homeView = Intent(context, PrincipalView::class.java)
                    context.startActivity(homeView)
                },
                modifier = Modifier
                    .size(85.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_arrow_back_24), // Usa el ícono que tengas
                    contentDescription = "Regresar",
                    modifier = Modifier.size(75.dp)

                )
            }
            SpaceBetween(800)
            IconButton(
                onClick = {
                   dialogoBorrar = true


                },
                modifier = Modifier
                    .size(100.dp)
            ) {
                Icon(
                    painter= painterResource(R.drawable.baseline_delete_24),
                    contentDescription = "Borrar Niño",
                    modifier = Modifier
                        .size(75.dp)
                )
            }
        }
        when {
            id == null -> TextosSimples("ID de niño no encontrado", Color.White)

            nino == null -> TextosSimples("Cargando datos…", Color.White)

            else ->

            Column (

                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxWidth()

                ,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center




            ){
                SpaceTopBottom(80)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,

                    ) {
                    Titulo("Informacion del niño")
                }
                SpaceTopBottom(20)
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()

                ) {

                    //Nombre y Edad
                    Row {
                        TextosInformacion("Nombre:", Terracota)
                        SpaceBetween(7)
                        TextosSimples(nino!!.nombre, Color.White)
                        SpaceBetween(50)
                        TextosInformacion("Edad:", Terracota)
                        SpaceBetween(2)
                        TextosSimples(nino!!.edad, Color.White)
                    }
                    SpaceTopBottom(5)

                        TextosInformacion("Nombre del Padre/Madre:", Terracota)
                        SpaceBetween(2)
                        TextosSimples(nino!!.nombrePadres, Color.White)
                        SpaceBetween(15)
                        TextosInformacion("Numero de emergencia:", Terracota)
                        SpaceBetween(2)
                        TextosSimples(nino!!.numeroEmergencia, Color.White)


                    SpaceTopBottom(5)
                    TextosInformacion("Personas Autorizadas para recoleccion", Terracota)
                    TextosSimples(nino!!.nombreAutorizado, Color.White)
                    SpaceTopBottom(5)
                    TextosInformacion("Horas totales:", Terracota)
                    SpaceTopBottom(5)
                    TextosSimples(formatearTiempo(tiempoRestante), if(tiempoExcedido) Color.Red else Color.White)

                    SpaceTopBottom(15)
                    Row {
                        TextosInformacion("Total a pagar de productos", Terracota)
                        SpaceBetween(2)
                        TextosSimples("$${totalconsumo.toString()}", Color.White)
                        SpaceBetween(10)
                        TextosInformacion("Total a pagar paquetes:", Terracota)
                        SpaceBetween(2)
                        TextosSimples("$${totalCosto.toString()}", Color.White)
                        SpaceBetween(2)
                    }
                    Row {
                        TextosInformacion("Total a pagar:", Terracota)
                        SpaceBetween(2)
                        TextosSimples("$${totalGeneral.toString()}", Color.White)
                        TextosInformacion("Dia de la ultima visita", Terracota)
                        val diaVisita = nino!!.horaSalida?.toDate()
                        val fechaformateada = diaVisita?.let {
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                        }
                        if (nino!!.estado == true) {
                            TextosSimples("Niño en ludoteca", Color.White)
                        } else {

                            if (diaVisita == null) {
                                TextosSimples("No hay fecha de ultima visita", Color.White)
                            } else {
                                TextosSimples(fechaformateada.toString(), Color.White)
                            }
                        }
                    }
                    SpaceTopBottom(20)
                    Row {
                        Botones("Agregar Paquetes") {
                            val AgregarHora = Intent(context, AgregarPaquetes::class.java)
                            AgregarHora.putExtra("idNino", nino!!.userId)
                            context.startActivity(AgregarHora)
                        }
                        SpaceBetween(3)
                        //Productos
                        Botones("Agregar Productos") {
                            val productos = Intent(context, AgregaraProductoNiño::class.java)
                            productos.putExtra("idNino", nino!!.userId)
                            context.startActivity(productos)
                        }
                        Botones(
                            if (nino!!.estado == false) {
                                "Entrada"
                            } else {
                                "Salida"
                            }

                        ) {
                            mostrarDialogo = true

                        }

                    }
                }

                //Botones de editar y pagar
                Row{
                    Botones("Editar Niño") {
                        val editar = Intent(context, UpdateNino::class.java)
                        editar.putExtra("idNino", nino!!.userId)
                        context.startActivity(editar)
                    }
                    //pagar carro
                    Botones("Pagar carrito"){
                        dialogoPago=true
                    }
                    Botones("Menu Principal"){
                        val menu= Intent(context, PrincipalView::class.java)
                        context.startActivity(menu)
                    }

                }
                SpaceTopBottom(50)

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
                        consumoViewModel.EliminarConsumo(nino!!.userId)
                        dialogoPago=false
                    }
                )
                MiDialogoSimple(
                    dialogoBorrar,
                    {dialogoBorrar=false},
                    {
                        ninoViewModel.borrarNIño(nino!!.userId)
                        val ListaNiño = Intent(context, NinosRegistrados::class.java)
                        context.startActivity(ListaNiño)
                        dialogoBorrar=false
                    }
                )

            }
        }
    }
}




