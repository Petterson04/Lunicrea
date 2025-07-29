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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
fun BitacoraView(viewModel: NinoViewModel= viewModel()){
    val context = LocalContext.current
    val ninosActivos by viewModel.ninosActivos.collectAsState()


    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.obtenerNinosActivos() // ✅ Se ejecuta al volver a la vista
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
            modifier = Modifier
                .background(RosaClaro)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            SpaceTopBottom(15)
            BotonRegresar()
            SpaceTopBottom(25)

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
                    Row {
                        Titulo("Lista de niños Activos")
                        SpaceBetween(15)
                        TextosInformacion("Total: ${ninosActivos.size}",Color.White)

                    }
                }

                LazyColumn {
                    items(ninosActivos) { nino ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 3.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Terracota
                            ),
                            onClick = {
                                val intent = Intent(context, Infonino::class.java)
                                intent.putExtra("idNino", nino.userId)
                                context.startActivity(intent)
                            },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row {
                                TextosInformacion("Nombre del niño", Color.White)
                                SpaceBetween(15)
                                TextosSimples(nino.nombre, Color.White)
                                SpaceBetween(15)
                                TextosInformacion("Hora Entrada", Color.White)
                                val hora = nino.horaEntrada?.toDate()
                                val fechaString = hora?.let {
                                    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(it)
                                }
                                SpaceBetween(15)
                                TextosSimples(fechaString.toString(),Color.White)

                            }
                        }
                    }//items
                }//LazyColumn
                Botones("Menu Principal"){
                    val menu= Intent(context, PrincipalView::class.java)
                    context.startActivity(menu)
                }
            }//Column
        }//Box

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LunicreaTheme {
        BitacoraView()
    }
}