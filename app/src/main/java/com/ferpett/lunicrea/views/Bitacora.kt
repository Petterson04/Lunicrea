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
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferpett.lunicrea.Model.NinoViewModel
import com.ferpett.lunicrea.ui.theme.LunicreaTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ferpett.lunicrea.Elements.BotonRegresar
import com.ferpett.lunicrea.Elements.Botones
import com.ferpett.lunicrea.Elements.SpaceBetween
import com.ferpett.lunicrea.Elements.SpaceTopBottom
import com.ferpett.lunicrea.Elements.TextosInformacion
import com.ferpett.lunicrea.Elements.TextosSimples
import com.ferpett.lunicrea.Elements.Titulo
import com.ferpett.lunicrea.R
import com.ferpett.lunicrea.ui.theme.RosaClaro
import com.ferpett.lunicrea.ui.theme.Terracota
import java.text.SimpleDateFormat
import java.util.Locale

class Bitacora : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LunicreaTheme {
                    BitacoraView()
            }
        }
    }
}
@Composable
fun BitacoraView(viewModel: NinoViewModel = viewModel()) {

    val context = LocalContext.current
    val ninosActivos by viewModel.ninosActivos.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.obtenerNinosActivos()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RosaClaro)
    ) {

        // 🔙 Botón regresar fijo
        IconButton(
            onClick = {
                context.startActivity(
                    Intent(context, PrincipalView::class.java)
                )
            },
            modifier = Modifier
                .padding(16.dp)
                .size(60.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_arrow_back_24),
                contentDescription = "Regresar"
            )
        }

        // 📜 CONTENEDOR SCROLL PRINCIPAL
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 90.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🧾 Header
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Titulo("Lista de niños activos")
                    SpaceBetween(15)
                    TextosInformacion(
                        "Total: ${ninosActivos.size}",
                        Color.White
                    )
                }
                SpaceTopBottom(20)
            }

            // 👶 Lista
            items(ninosActivos) { nino ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    colors = CardDefaults.cardColors(containerColor = Terracota),
                    elevation = CardDefaults.cardElevation(4.dp),
                    onClick = {
                        val intent = Intent(context, Infonino::class.java)
                        intent.putExtra("idNino", nino.userId)
                        context.startActivity(intent)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        TextosInformacion("Nombre:", Color.White)
                        SpaceBetween(8)
                        TextosSimples(nino.nombre, Color.White)

                        SpaceBetween(16)

                        TextosInformacion("Entrada:", Color.White)
                        SpaceBetween(8)

                        val hora = nino.horaEntrada?.toDate()
                        val horaFormateada = hora?.let {
                            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(it)
                        } ?: "--:--"

                        TextosSimples(horaFormateada, Color.White)
                    }
                }
            }

            // 🔘 Botón final
            item {
                SpaceTopBottom(25)
                Botones("Menú Principal") {
                    context.startActivity(
                        Intent(context, PrincipalView::class.java)
                    )
                }
                SpaceTopBottom(50)
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LunicreaTheme {
        BitacoraView()
    }
}