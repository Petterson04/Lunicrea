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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferpett.lunicrea.Elements.BotonRegresar
import com.ferpett.lunicrea.Elements.Botones
import com.ferpett.lunicrea.Elements.SelectorFecha
import com.ferpett.lunicrea.Elements.SpaceBetween
import com.ferpett.lunicrea.Elements.SpaceTopBottom
import com.ferpett.lunicrea.Elements.TextosSimples
import com.ferpett.lunicrea.Elements.Titulo
import com.ferpett.lunicrea.Model.VentasViewModel
import com.ferpett.lunicrea.ui.theme.LunicreaTheme
import com.ferpett.lunicrea.ui.theme.RosaClaro
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
                Titulo("Visitas por dia")
            }
            Row {
                TextosSimples("Favor de seleccionar una fecha", Color.White)
                SpaceBetween(15)
                SelectorFecha(selectDay) { nuevaFecha ->
                    selectDay = nuevaFecha
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    val fechaLocalDate = LocalDate.parse(nuevaFecha, formatter)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        viewModel.ObtenerVentasPorDia(fechaLocalDate)
                    }
                }
            }
            SpaceTopBottom(10)
            if (listVentas.isEmpty()){
                TextosSimples("No hay ninguna venta",Color.White)
            }else{
                Row {
                    TextosSimples(
                        "Paquetes vendidos el dia ${selectDay} total ${listVentas.size}",
                        Color.White
                    )
                    SpaceBetween(15)
                    TextosSimples("Total vendido del dia ${totalCosto}",Color.White)
                }
                if (!paquetes.isNullOrEmpty()) {
                    TextosSimples("Resumen de paquetes vendidos:", Color.White)
                    paquetes!!.forEach { (nombrePaquete, cantidad) ->
                        Row {
                            TextosSimples("$nombrePaquete:", Color.White)
                            SpaceBetween(10)
                            TextosSimples("$cantidad veces", Color.White)
                        }
                    }
                }
                listVentas.forEach { ventas ->
                    TextosSimples("Nombre ${ventas.nombreNino} paquete comprado ${ventas.nombrePaquete}",Color.White)
                }
            }
            Row {
                Botones("Borrar visitas por dia") {
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