
package com.example.sem5_practica05

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private val Naranja = Color(0xFFFF8C00)
private val NaranjaClaro = Color(0xFFFFB74D)
private val FondoOscuro = Color(0xFF101014)
private val FondoTarjeta = Color(0xFF1D1D24)
private val Blanco = Color(0xFFF5F5F5)
private val Gris = Color(0xFFAAAAAA)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferencias = getSharedPreferences(
            "DatosUsuario",
            Context.MODE_PRIVATE
        )

        setContent {

            val colores = darkColorScheme(
                primary = Naranja,
                secondary = NaranjaClaro,
                background = FondoOscuro,
                surface = FondoTarjeta,
                onPrimary = Color.Black,
                onSecondary = Color.Black,
                onBackground = Blanco,
                onSurface = Blanco
            )

            MaterialTheme(colorScheme = colores) {

                var nombre by remember {
                    mutableStateOf(
                        preferencias.getString("nombre", "") ?: ""
                    )
                }

                var correo by remember {
                    mutableStateOf(
                        preferencias.getString("correo", "") ?: ""
                    )
                }

                var telefono by remember {
                    mutableStateOf(
                        preferencias.getString("telefono", "") ?: ""
                    )
                }

                var mensaje by remember {
                    mutableStateOf("Ingresa tus datos para comenzar")
                }

                val coloresCampo = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Naranja,
                    unfocusedBorderColor = Gris,
                    focusedLabelColor = Naranja,
                    unfocusedLabelColor = Gris,
                    cursorColor = Naranja,
                    focusedTextColor = Blanco,
                    unfocusedTextColor = Blanco
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(FondoOscuro)
                        .verticalScroll(rememberScrollState())
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Spacer(modifier = Modifier.height(25.dp))

                    Text(
                        text = "PRÁCTICA 05",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Naranja
                    )

                    Text(
                        text = "ALMACENAMIENTO LOCAL",
                        style = MaterialTheme.typography.titleMedium,
                        color = Blanco,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Persistencia de datos con SharedPreferences",
                        color = Gris,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = FondoTarjeta
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {

                            Text(
                                text = "INFORMACIÓN PERSONAL",
                                color = Naranja,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )

                            HorizontalDivider(
                                color = Naranja.copy(alpha = 0.35f)
                            )

                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                label = { Text("Nombre completo") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = coloresCampo
                            )

                            OutlinedTextField(
                                value = correo,
                                onValueChange = { correo = it },
                                label = { Text("Correo electrónico") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = coloresCampo
                            )

                            OutlinedTextField(
                                value = telefono,
                                onValueChange = { telefono = it },
                                label = { Text("Teléfono") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = coloresCampo
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Button(
                                onClick = {

                                    if (
                                        nombre.isBlank() ||
                                        correo.isBlank() ||
                                        telefono.isBlank()
                                    ) {
                                        mensaje = "Completa todos los campos"
                                    } else {

                                        preferencias.edit()
                                            .putString("nombre", nombre)
                                            .putString("correo", correo)
                                            .putString("telefono", telefono)
                                            .apply()

                                        mensaje = "Datos guardados correctamente"
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Naranja,
                                    contentColor = Color.Black
                                )
                            ) {
                                Text(
                                    text = "GUARDAR DATOS",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                OutlinedButton(
                                    onClick = {

                                        nombre = preferencias.getString(
                                            "nombre", ""
                                        ) ?: ""

                                        correo = preferencias.getString(
                                            "correo", ""
                                        ) ?: ""

                                        telefono = preferencias.getString(
                                            "telefono", ""
                                        ) ?: ""

                                        mensaje = if (
                                            preferencias.contains("nombre")
                                        ) {
                                            "Datos cargados correctamente"
                                        } else {
                                            "No hay datos guardados"
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, Naranja),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Naranja
                                    )
                                ) {
                                    Text(
                                        "CARGAR",
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                OutlinedButton(
                                    onClick = {

                                        preferencias.edit()
                                            .clear()
                                            .apply()

                                        nombre = ""
                                        correo = ""
                                        telefono = ""

                                        mensaje = "Datos eliminados correctamente"
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, Naranja),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Naranja
                                    )
                                ) {
                                    Text(
                                        "BORRAR",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = FondoTarjeta
                        )
                    ) {
                        Text(
                            text = mensaje,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            color = NaranjaClaro,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Desarrollo Móvil Nativo • Práctica 05",
                        color = Gris,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
