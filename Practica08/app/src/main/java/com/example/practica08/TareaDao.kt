package com.example.practica08

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {

    @Query("SELECT * FROM tabla_tareas ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<TareaEntity>>

    @Insert
    suspend fun insertar(tarea: TareaEntity): Long

    @Update
    suspend fun actualizar(tarea: TareaEntity): Int

    @Delete
    suspend fun eliminar(tarea: TareaEntity): Int
}