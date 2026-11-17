
package com.example.sem9_practica12

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private val Naranja = Color(0xFFFF8C00)
private val Fondo = Color(0xFF101014)
private val Tarjeta = Color(0xFF1D1D24)
private val Blanco = Color(0xFFF5F5F5)
private val Gris = Color(0xFFAAAAAA)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val colores = darkColorScheme(
                primary = Naranja,
                background = Fondo,
                surface = Tarjeta,
                onPrimary = Color.Black,
                onBackground = Blanco,
                onSurface = Blanco
            )

            var publicaciones by remember {
                mutableStateOf<List<Publicacion>>(emptyList())
            }

            var cargando by remember {
                mutableStateOf(false)
            }

            var mensaje by remember {
                mutableStateOf("Presiona el botón para consultar la API")
            }

            val scope = rememberCoroutineScope()

            fun cargarPublicaciones() {
                scope.launch {
                    cargando = true
                    mensaje = "Consultando publicaciones..."

                    try {
                        val resultado =
                            RetrofitClient.api.obtenerPublicaciones()

                        publicaciones = resultado

                        mensaje =
                            "Se cargaron ${resultado.size} publicaciones"

                    } catch (e: Exception) {
                        mensaje = "Error de conexión: " +
                                (e.localizedMessage ?: "Error desconocido")
                    } finally {
                        cargando = false
                    }
                }
            }

            MaterialTheme(colorScheme = colores) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Fondo)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "PRÁCTICA 12",
                        color = Naranja,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "CONSUMO DE API REST",
                        color = Blanco,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Publicaciones obtenidas desde JSONPlaceholder",
                        color = Gris,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { cargarPublicaciones() },
                        enabled = !cargando,
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
                            text = if (cargando) {
                                "CARGANDO..."
                            } else {
                                "CONSULTAR API"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Tarjeta
                        )
                    ) {
                        Text(
                            text = mensaje,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            color = Naranja,
                            textAlign = TextAlign.Center
                        )
                    }

                    if (cargando) {
                        CircularProgressIndicator(
                            color = Naranja
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {

                        items(
                            items = publicaciones,
                            key = { it.id }
                        ) { publicacion ->

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Tarjeta
                                )
                            ) {

                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement =
                                        Arrangement.spacedBy(10.dp)
                                ) {

                                    Text(
                                        text = "PUBLICACIÓN #${publicacion.id}",
                                        color = Naranja,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelLarge
                                    )

                                    Text(
                                        text = publicacion.title,
                                        color = Blanco,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    HorizontalDivider(
                                        color = Naranja.copy(alpha = 0.3f)
                                    )

                                    Text(
                                        text = publicacion.body,
                                        color = Gris,
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    Text(
                                        text = "Usuario: ${publicacion.userId}",
                                        color = Naranja,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "Desarrollo Móvil Nativo • Semana 9",
                        color = Gris,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
