package com.example.sem8_practica10

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import android.graphics.BitmapFactory
import java.io.File

private val Naranja = Color(0xFFFF8C00)
private val NaranjaClaro = Color(0xFFFFB74D)
private val FondoOscuro = Color(0xFF101014)
private val FondoTarjeta = Color(0xFF1D1D24)
private val Blanco = Color(0xFFF5F5F5)
private val Gris = Color(0xFFAAAAAA)

class MainActivity : ComponentActivity() {

    private fun crearUriFoto(): Uri {
        val carpeta = File(cacheDir, "images")
        carpeta.mkdirs()

        val archivo = File.createTempFile(
            "foto_",
            ".jpg",
            carpeta
        )

        return FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",
            archivo
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val colores = darkColorScheme(
                primary = Naranja,
                secondary = NaranjaClaro,
                background = FondoOscuro,
                surface = FondoTarjeta,
                onPrimary = Color.Black,
                onBackground = Blanco,
                onSurface = Blanco
            )

            var permisoConcedido by remember {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                )
            }

            var uriFoto by rememberSaveable {
                mutableStateOf("")
            }

            var uriPendiente by rememberSaveable {
                mutableStateOf("")
            }

            var mensaje by remember {
                mutableStateOf("Solicita permiso para comenzar")
            }

            val solicitarPermiso =
                rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { concedido ->

                    permisoConcedido = concedido

                    mensaje = if (concedido) {
                        "Permiso de cámara concedido"
                    } else {
                        "Permiso de cámara denegado"
                    }
                }

            val tomarFoto =
                rememberLauncherForActivityResult(
                    ActivityResultContracts.TakePicture()
                ) { exito ->

                    if (exito) {
                        uriFoto = uriPendiente
                        mensaje = "Fotografía capturada correctamente"
                    } else {
                        mensaje = "Captura cancelada"
                    }

                    uriPendiente = ""
                }

            MaterialTheme(colorScheme = colores) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(FondoOscuro)
                        .verticalScroll(rememberScrollState())
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "PRÁCTICA 10",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Naranja,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "USO DE CÁMARA",
                        style = MaterialTheme.typography.titleMedium,
                        color = Blanco,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Captura de fotografías en Android",
                        color = Gris,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = FondoTarjeta
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            Text(
                                text = "PERMISOS DEL DISPOSITIVO",
                                color = Naranja,
                                fontWeight = FontWeight.Bold
                            )

                            HorizontalDivider(
                                color = Naranja.copy(alpha = 0.35f)
                            )

                            Text(
                                text = if (permisoConcedido) {
                                    "Permiso de cámara concedido"
                                } else {
                                    "Permiso de cámara pendiente"
                                },
                                color = if (permisoConcedido) {
                                    NaranjaClaro
                                } else {
                                    Gris
                                }
                            )

                            OutlinedButton(
                                onClick = {
                                    if (!permisoConcedido) {
                                        solicitarPermiso.launch(
                                            Manifest.permission.CAMERA
                                        )
                                    } else {
                                        mensaje = "El permiso ya está concedido"
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, Naranja),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Naranja
                                )
                            ) {
                                Text(
                                    text = "SOLICITAR PERMISO",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {

                                    if (
                                        ContextCompat.checkSelfPermission(
                                            this@MainActivity,
                                            Manifest.permission.CAMERA
                                        ) != PackageManager.PERMISSION_GRANTED
                                    ) {
                                        permisoConcedido = false
                                        mensaje = "Primero concede el permiso"
                                    } else {

                                        try {
                                            val nuevaUri = crearUriFoto()
                                            uriPendiente = nuevaUri.toString()
                                            tomarFoto.launch(nuevaUri)
                                        } catch (e: ActivityNotFoundException) {
                                            mensaje = "No hay aplicación de cámara"
                                        } catch (e: Exception) {
                                            mensaje = "Error al abrir la cámara"
                                        }
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
                                    text = "ABRIR CÁMARA",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = FondoTarjeta
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {

                            Text(
                                text = "FOTOGRAFÍA CAPTURADA",
                                color = Naranja,
                                fontWeight = FontWeight.Bold
                            )

                            HorizontalDivider(
                                color = Naranja.copy(alpha = 0.35f)
                            )

                            if (uriFoto.isNotEmpty()) {

                                val bitmap = remember(uriFoto) {
                                    try {
                                        contentResolver.openInputStream(
                                            Uri.parse(uriFoto)
                                        )?.use { stream ->
                                            BitmapFactory.decodeStream(stream)
                                        }
                                    } catch (e: Exception) {
                                        null
                                    }
                                }

                                if (bitmap != null) {
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "Fotografía tomada",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(280.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                } else {
                                    Text(
                                        text = "No se pudo mostrar la fotografía",
                                        color = Gris
                                    )
                                }

                            } else {

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp)
                                        .background(
                                            FondoOscuro,
                                            RoundedCornerShape(14.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Aquí aparecerás",
                                        color = Gris,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = FondoTarjeta
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = mensaje,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            color = NaranjaClaro,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Desarrollo Móvil Nativo • Práctica 10",
                        color = Gris,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
