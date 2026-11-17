
package com.example.sem10_miniproyecto03

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "publicaciones")
data class Publicacion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val fotoUri: String,
    val fecha: Long = System.currentTimeMillis()
)
