package com.example.banner.backend.Controllers

import Usuario
import java.sql.ResultSet

class UserController {

    fun getAllUsers(): List<Usuario> {
        val usuarios = mutableListOf<Usuario>()
        val query = "SELECT * FROM Usuario"
        val resultSet: ResultSet? = DatabaseDAO.executeQuery(query)

        resultSet?.let {
            while (it.next()) {
                val usuario = Usuario(
                    it.getInt("id"),
                    it.getString("password"),
                    it.getString("role"),
                )
                usuarios.add(usuario)
            }
            it.close()
        }
        return usuarios
    }

    // Insertar usuario utilizando un procedimiento almacenado
    fun insertUser(id: Int, password: String, role: String): Boolean {
        val procedureName = "insert_user" // Nombre del procedimiento almacenado
        return DatabaseDAO.executeStoredProcedure(procedureName, id, password, role)
    }

    fun updateUser(id: Int, password: String, role: String): Boolean {
        return DatabaseDAO.executeStoredProcedure("update_user", id, password, role)
    }

    //login
    fun loginUser(id: Int, password: String): Boolean {
        val procedureName = "sp_LoginUsuario"
        val resultSet = DatabaseDAO.executeStoredProcedureWithResults(procedureName, id, password)

        return resultSet?.let {
            if (it.next()) {
                it.getBoolean("p_resultado") // Obtener el valor de salida
            } else {
                false
            }
        } ?: false
    }
}