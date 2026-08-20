package com.ferpett.lunicrea.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ferpett.lunicrea.Elements.Botones
import com.ferpett.lunicrea.Elements.SpaceBetween
import com.ferpett.lunicrea.Elements.SpaceTopBottom
import com.ferpett.lunicrea.R
import com.ferpett.lunicrea.ui.theme.LunicreaTheme
import com.ferpett.lunicrea.ui.theme.RosaClaro


class AdminView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LunicreaTheme {
                AdminPrincipalView()
            }
        }
    }
}

@Composable
fun AdminPrincipalView() {
    val context= LocalContext.current
    Box(
        modifier = Modifier
            .background((RosaClaro))
            .fillMaxSize()
            .fillMaxWidth()
    ){
        Column()
        {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                Image(
                    painter = painterResource(id= R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(300.dp)

                )
            }
            SpaceTopBottom(25)
            Row {
                SpaceBetween(45)
                Botones("Registro de visitas") {
                    val bitacora= Intent(context, RegistroVisitas::class.java)
                    context.startActivity(bitacora)
                }
                SpaceBetween(5)
                Botones("Productos"){
                    val productos= Intent(context, AdminProductos::class.java)
                    context.startActivity(productos)
                }
                Botones(
                    "Registro de ventas",
                    onClickAction =
                    {
                        val ninosRegistrados = Intent(context, AdminPaquetes::class.java)
                        context.startActivity(ninosRegistrados)
                    },

                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){

                Botones("Vista principal") {
                    val adminlogin= Intent(context, PrincipalView::class.java)
                    context.startActivity(adminlogin)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview6() {
    AdminPrincipalView()
}