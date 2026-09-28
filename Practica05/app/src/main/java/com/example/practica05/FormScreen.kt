package com.example.practica05
import androidx.compose.material3.darkColorScheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practica05.data.PreferencesManager
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.LocalTextStyle
@Composable
fun FormScreen() {
    val context = LocalContext.current
    val preferencesManager = remember {
        PreferencesManager(context)
    }

    var nombre by remember {
        mutableStateOf("")
    }

    var modoOscuro by remember {
        mutableStateOf(false)
    }

    var notificaciones by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        nombre = preferencesManager.obtenerNombre()
        modoOscuro = preferencesManager.obtenerModoOscuro()
        notificaciones = preferencesManager.obtenerNotificaciones()
    }

    val colores = if (modoOscuro) {
        darkColorScheme(
            primary = Color(0xFFBB86FC),
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E),
            onPrimary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White
        )
    } else {
        lightColorScheme()
    }

    MaterialTheme(colorScheme = colores) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Configuración de usuario",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Nombre")
                },
                textStyle = LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Modo oscuro",
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Switch(
                    checked = modoOscuro,
                    onCheckedChange = {
                        modoOscuro = it
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notificaciones activas",
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Switch(
                    checked = notificaciones,
                    onCheckedChange = {
                        notificaciones = it
                    }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    preferencesManager.guardarNombre(nombre)
                    preferencesManager.guardarModoOscuro(modoOscuro)
                    preferencesManager.guardarNotificaciones(notificaciones)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar configuración")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Los datos se guardan con SharedPreferences.",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp
            )
        }
    }
}