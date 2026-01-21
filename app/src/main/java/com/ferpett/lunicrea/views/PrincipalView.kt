package com.ferpett.lunicrea.views

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
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ferpett.lunicrea.ui.theme.LunicreaTheme
import com.ferpett.lunicrea.R
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.ferpett.lunicrea.Elements.*
import com.ferpett.lunicrea.ui.theme.RosaClaro

class PrincipalView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LunicreaTheme {
                    Homeview()
            }
        }
    }
}

@Composable
fun Homeview(){

    val context= LocalContext.current
    Box(
        modifier = Modifier
            .background((RosaClaro))

    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        )
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
            SpaceTopBottom(7)
            Row(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                SpaceBetween(45)
                    Botones("Bitacora de niños") {
                        val bitacora= Intent(context, Bitacora::class.java)
                        context.startActivity(bitacora)
                    }
                SpaceBetween(30)
                Botones(
                    "Lista de niños",
                    {
                        val ninosRegistrados = Intent(context, NinosRegistrados::class.java)
                        context.startActivity(ninosRegistrados)
                    }

                    )
                SpaceBetween(150)

                }
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){

                Botones("Administrador") {
                    val adminlogin= Intent(context, LoginAdmin()::class.java)
                    context.startActivity(adminlogin)
                }
            }
            }
        }
    }


@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    LunicreaTheme {
        Homeview()
    }
}