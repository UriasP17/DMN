
package com.example.sem9_practica11

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

private val Naranja = Color(0xFFFF8C00)
private val NaranjaClaro = Color(0xFFFFB74D)
private val Fondo = Color(0xFF101014)
private val Tarjeta = Color(0xFF1D1D24)
private val Blanco = Color(0xFFF5F5F5)
private val Gris = Color(0xFFAAAAAA)

class MainActivity : ComponentActivity() {

    private val nombreArchivo = "practica11.txt"

    // Obtiene la carpeta interna o externa de la app.
    // Si hay una SDCard extraíble, se intenta usar primero.
    private fun obtenerCarpeta(externo: Boolean): File {
        if (!externo) {
            return filesDir
        }

        val carpetas = getExternalFilesDirs(null)

        val sdCard = carpetas
            .filterNotNull()
            .firstOrNull { carpeta ->
                Environment.isExternalStorageRemovable(carpeta) &&
                        Environment.getExternalStorageState(carpeta) ==
                        Environment.MEDIA_MOUNTED
            }

        val carpeta = sdCard ?: getExternalFilesDir(null)

        if (carpeta == null ||
            Environment.getExternalStorageState(carpeta) !=
            Environment.MEDIA_MOUNTED
        ) {
            throw IllegalStateException(
                "Almacenamiento externo no disponible"
            )
        }

        return carpeta
    }

    private fun guardarArchivo(
        texto: String,
        externo: Boolean
    ): String {
        return try {
            val carpeta = obtenerCarpeta(externo)
            val archivo = File(carpeta, nombreArchivo)

            FileOutputStream(archivo, false).use { salida ->
                salida.write(texto.toByteArray(Charsets.UTF_8))
            }

            "Archivo guardado correctamente\n${archivo.absolutePath}"

        } catch (e: Exception) {
            "Error al guardar: ${e.message}"
        }
    }

    private fun leerArchivo(
        externo: Boolean
    ): Pair<String?, String> {
        return try {
            val carpeta = obtenerCarpeta(externo)
            val archivo = File(carpeta, nombreArchivo)

            if (!archivo.exists()) {
                Pair(null, "No existe un archivo guardado aquí")
            } else {
                val contenido = FileInputStream(archivo).use {
                        entrada ->
                    entrada.bufferedReader(Charsets.UTF_8)
                        .use { lector ->
                            lector.readText()
                        }
                }

                Pair(
                    contenido,
                    "Archivo leído correctamente\n${archivo.absolutePath}"
                )
            }

        } catch (e: Exception) {
            Pair(null, "Error al leer: ${e.message}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val colores = darkColorScheme(
                primary = Naranja,
                secondary = NaranjaClaro,
                background = Fondo,
                surface = Tarjeta,
                onPrimary = Color.Black,
                onBackground = Blanco,
                onSurface = Blanco
            )

            var texto by remember {
                mutableStateOf("")
            }

            var usarExterno by remember {
                mutableStateOf(false)
            }

            var mensaje by remember {
                mutableStateOf("Selecciona dónde guardar el archivo")
            }

            val permisosAntiguos = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

            val solicitarPermisos =
                rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { resultado ->

                    val concedidos = resultado.values.all { it }

                    mensaje = if (concedidos) {
                        "Permisos concedidos correctamente"
                    } else {
                        "Algún permiso fue denegado"
                    }
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

            MaterialTheme(colorScheme = colores) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Fondo)
                        .verticalScroll(rememberScrollState())
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "PRÁCTICA 11",
                        color = Naranja,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "MANEJO DE ARCHIVOS",
                        color = Blanco,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Almacenamiento interno y externo",
                        color = Gris,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Tarjeta
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            Text(
                                text = "CONTENIDO DEL ARCHIVO",
                                color = Naranja,
                                fontWeight = FontWeight.Bold
                            )

                            HorizontalDivider(
                                color = Naranja.copy(alpha = 0.35f)
                            )

                            OutlinedTextField(
                                value = texto,
                                onValueChange = { texto = it },
                                label = { Text("Escribe tu texto") },
                                placeholder = {
                                    Text("Escribe algo para guardar...")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(170.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = coloresCampo,
                                minLines = 4
                            )

                            Text(
                                text = "UBICACIÓN DE DESTINO",
                                color = Naranja,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = !usarExterno,
                                    onClick = {
                                        usarExterno = false
                                        mensaje = "Almacenamiento interno"
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Naranja
                                    )
                                )

                                Text(
                                    text = "Almacenamiento interno",
                                    color = Blanco
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = usarExterno,
                                    onClick = {
                                        usarExterno = true
                                        mensaje = "Almacenamiento externo / SDCard"
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Naranja
                                    )
                                )

                                Text(
                                    text = "Almacenamiento externo / SDCard",
                                    color = Blanco
                                )
                            }

                            HorizontalDivider(
                                color = Naranja.copy(alpha = 0.35f)
                            )

                            OutlinedButton(
                                onClick = {
                                    if (Build.VERSION.SDK_INT <= 28) {

                                        val faltantes =
                                            permisosAntiguos.filter {
                                                ContextCompat
                                                    .checkSelfPermission(
                                                        this@MainActivity,
                                                        it
                                                    ) !=
                                                        PackageManager.PERMISSION_GRANTED
                                            }

                                        if (faltantes.isEmpty()) {
                                            mensaje = "Permisos ya concedidos"
                                        } else {
                                            solicitarPermisos.launch(
                                                faltantes.toTypedArray()
                                            )
                                        }

                                    } else {
                                        mensaje =
                                            "Android actual: no se requieren " +
                                                    "permisos para archivos de la app"
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                border = BorderStroke(1.dp, Naranja),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Naranja
                                )
                            ) {
                                Text(
                                    "SOLICITAR PERMISOS",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {
                                    if (texto.isBlank()) {
                                        mensaje = "Primero escribe un texto"
                                    } else {
                                        mensaje = guardarArchivo(
                                            texto,
                                            usarExterno
                                        )
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
                                    "GUARDAR ARCHIVO",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    val resultado = leerArchivo(usarExterno)

                                    if (resultado.first != null) {
                                        texto = resultado.first!!
                                    }

                                    mensaje = resultado.second
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                border = BorderStroke(1.dp, Naranja),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Naranja
                                )
                            ) {
                                Text(
                                    "LEER ARCHIVO",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    texto = ""
                                    mensaje = "Campo de texto limpiado"
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Gris
                                )
                            ) {
                                Text("LIMPIAR CAMPO")
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Tarjeta
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Text(
                                text = "ESTADO DEL ARCHIVO",
                                color = Naranja,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = mensaje,
                                color = NaranjaClaro,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Text(
                        text = "Desarrollo Móvil Nativo • Práctica 11",
                        color = Gris,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
