package com.ferpett.lunicrea.views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ferpett.lunicrea.Elements.BotonRegresar
import com.ferpett.lunicrea.Elements.Botones
import com.ferpett.lunicrea.Elements.SpaceBetween
import com.ferpett.lunicrea.Elements.SpaceTopBottom
import com.ferpett.lunicrea.Elements.TextosInformacion
import com.ferpett.lunicrea.Elements.Titulo
import com.ferpett.lunicrea.Model.Login
import com.ferpett.lunicrea.ui.theme.LunicreaTheme
import com.ferpett.lunicrea.R
import com.ferpett.lunicrea.ui.theme.RosaClaro
import java.nio.file.WatchEvent

class LoginAdmin : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LunicreaTheme {
                LoginAdminview()
            }
        }
    }
}


@Composable
fun LoginAdminview() {
    var login= Login()
    var correo by remember { mutableStateOf("")}
    var contrasenia by remember { mutableStateOf("")}
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .background((RosaClaro))
            .fillMaxSize()
            .fillMaxWidth()
    ){
        BotonRegresar()
        SpaceTopBottom(15)
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
                Titulo("Login para administrados")
            }
            Spacer(modifier = Modifier.height(30.dp) )
            Text(
                text ="Correo",
                color= MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                fontSize = 25.sp
            )
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text(
                    text = "Ingresa tu correo",
                    color = Color.White,
                    fontSize = 20.sp
                ) },
                textStyle = TextStyle(color = Color.White)
            )

            Spacer(modifier = Modifier.height(20.dp) )

            Text(
                text = "Contraseña",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                fontSize = 25.sp
            )
            OutlinedTextField(
                value = contrasenia,
                onValueChange = { contrasenia = it },
                label = { Text("Ingresar contraseña", color = Color.White, fontSize = 20.sp) },
                textStyle = TextStyle(color = Color.White),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(id = if (passwordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off),
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = Color.White
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(10.dp))

            Spacer(modifier = Modifier.height(20.dp) )
            Botones("Iniciar sesion") {
                if (correo.isEmpty()||contrasenia.isEmpty()){
                    Toast.makeText(context, "Favor de rellenar los campos", Toast.LENGTH_SHORT).show()
                }else{
                login.iniciarSesion(correo,contrasenia) { success ->
                    if(success){
                        val intentHome = Intent(context, AdminView::class.java)
                        context.startActivity(intentHome)
                    }else{
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun LoginAdminviewPreview() {

}