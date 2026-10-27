package com.example.miniproyecto02

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tareas")
data class TareaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val completada: Boolean = false,
    val fechaCreacion: Long = System.currentTimeMillis()
)