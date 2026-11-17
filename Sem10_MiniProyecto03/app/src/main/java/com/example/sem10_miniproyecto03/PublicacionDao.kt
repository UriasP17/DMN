
package com.example.sem10_miniproyecto03

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PublicacionDao {

    @Insert
    suspend fun insertar(publicacion: Publicacion)

    @Query("SELECT * FROM publicaciones ORDER BY fecha DESC")
    fun obtenerPublicaciones(): Flow<List<Publicacion>>
}
