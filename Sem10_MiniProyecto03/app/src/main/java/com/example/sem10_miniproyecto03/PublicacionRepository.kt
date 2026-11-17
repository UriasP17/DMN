
package com.example.sem10_miniproyecto03

class PublicacionRepository(
    private val dao: PublicacionDao
) {

    val publicaciones = dao.obtenerPublicaciones()

    suspend fun guardar(publicacion: Publicacion) {
        dao.insertar(publicacion)
    }
}
