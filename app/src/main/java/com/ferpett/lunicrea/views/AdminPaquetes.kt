package com.ferpett.lunicrea.views

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferpett.lunicrea.Elements.BotonRegresar
import com.ferpett.lunicrea.Elements.Botones
import com.ferpett.lunicrea.Elements.SelectorFecha
import com.ferpett.lunicrea.Elements.SpaceBetween
import com.ferpett.lunicrea.Elements.SpaceTopBottom
import com.ferpett.lunicrea.Elements.TextosInformacion
import com.ferpett.lunicrea.Elements.TextosSimples
import com.ferpett.lunicrea.Elements.Titulo
import com.ferpett.lunicrea.Model.VentasViewModel
import com.ferpett.lunicrea.ui.theme.LunicreaTheme
import com.ferpett.lunicrea.ui.theme.RosaClaro
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class AdminPaquetes : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LunicreaTheme {
                AdminPaquetesView()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdminPaquetesView() {
    var showDialog by remember {
        mutableStateOf(false)
    }
    val viewModel: VentasViewModel = viewModel()

    val context= LocalContext.current

    var selectDay by remember { mutableStateOf("") }
    val listVentas by viewModel.listaVentas.collectAsState()

    val totalCosto by viewModel.totalVentas.observeAsState(0.0)

    val paquetes by viewModel.conteoPaquetes.observeAsState()

    val paquetesSemana by viewModel.conteoPaquetesSemana.observeAsState()

    val totalProductos by viewModel.totalProductos.observeAsState(0.0)

    val totalCostoSemana by viewModel.totalVentasSemana.observeAsState(0.0)
    val totalProductosemana by viewModel.totalProductosSemana.observeAsState(0.0)
    val listventasSemana by viewModel.listaVentasSemana.collectAsState()

    val totalGeneralSemana= totalProductosemana + totalCostoSemana

    val totalGeneral = totalProductos + totalCosto
    val scrollState = rememberScrollState()
    val scrollStateDia = rememberScrollState()
    Box(
        modifier = Modifier
            .background((RosaClaro))
            .fillMaxSize()
            .fillMaxWidth(),

        ) {
        SpaceTopBottom(50)
        BotonRegresar()
        SpaceTopBottom(52)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Titulo("Ventas por dia")
            }
            var finStr by remember { mutableStateOf("") }
            Row {
                TextosSimples("Favor de seleccionar una fecha", Color.White)
                SpaceBetween(15)
                SelectorFecha(selectDay) { nuevaFecha ->
                    selectDay = nuevaFecha
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    val fechaLocalDate = LocalDate.parse(nuevaFecha, formatter)

                    val finRango = fechaLocalDate.plusDays(7)

                    // Formatear para mostrar
                    val inicioStr = fechaLocalDate.format(formatter)
                    finStr = finRango.format(formatter)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // Llamada original
                        viewModel.ObtenerVentasPorDia(fechaLocalDate)
                        viewModel.ObtenerVentasPorSemana(fechaLocalDate)
                    }
                }
            }
            SpaceTopBottom(2)
            if (listventasSemana.isEmpty()){
                TextosSimples("No hay ninguna venta",Color.White)
            }else{

                //DIAS
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                    contentAlignment = Alignment.Center,){

                Column(modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollStateDia)
                    .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
                    Row {
                        TextosSimples(
                            "Ventas de el dia ${selectDay} total ${listVentas.size}",
                            Color.White
                        )
                        SpaceBetween(15)
                        TextosSimples("Total vendido del dia ${totalGeneral}", Color.White)
                    }
                    Row {
                        TextosInformacion("Total de paquetes ${totalCosto}", Color.White)
                        SpaceBetween(15)
                        TextosInformacion("Total de productos ${totalProductos}", Color.White)
                    }
                    if (!paquetes.isNullOrEmpty()) {
                        TextosSimples("Resumen de ventas:", Color.White)
                        paquetes!!.forEach { (nombrePaquete, cantidad) ->
                            Row {
                                TextosSimples("$nombrePaquete:", Color.White)
                                SpaceBetween(10)
                                TextosSimples("$cantidad veces", Color.White)
                            }
                        }
                    }
                }
                }
                SpaceTopBottom(25)
                Box (modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                    contentAlignment = Alignment.Center,
                ){
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center

                    ) {

                        //Semana
                        Row {

                            TextosSimples(
                                "Ventas de la semana del dia ${selectDay} al ${finStr} total ${listventasSemana.size}",
                                Color.White
                            )
                            SpaceBetween(15)
                            TextosSimples("Total vendido del dia ${totalGeneralSemana}",Color.White)
                        }
                        Row {
                            TextosInformacion("Total de paquetes ${totalCostoSemana}",Color.White)
                            SpaceBetween(15)
                            TextosInformacion("Total de productos ${totalProductosemana}",Color.White)
                        }
                        if (!paquetesSemana.isNullOrEmpty()) {
                            TextosSimples("Resumen de ventas:", Color.White)
                            paquetesSemana!!.forEach { (nombrePaquete, cantidad,) ->
                                Row {
                                    TextosSimples("$nombrePaquete:", Color.White)
                                    SpaceBetween(10)
                                    TextosSimples("$cantidad veces", Color.White)
                                }
                            }
                        }
                    }

                }
            }
            SpaceTopBottom(3)
            Row {
                Botones("Borrar Ventas por dia") {
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    try {
                        val fechaLocalDate = LocalDate.parse(selectDay, formatter)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            viewModel.BorrarVentasporDia(fechaLocalDate)
                        }
                    } catch (e: Exception) {
                        Log.e("FormatoFecha", "Fecha inválida: $selectDay", e)
                    }
                }
                Botones("Borrar ventas por Semana") {
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    try {
                        val fechaLocalDate=LocalDate.parse(selectDay,formatter)
                        viewModel.BorrarVentasporSemana(fechaLocalDate)
                    }catch (e: Exception){

                    }
                }
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
fun GreetingPreview8() {
    LunicreaTheme {

    }
}