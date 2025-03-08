import java.sql.ResultSet

class CareerController {

    fun getAllCareers(): List<Triple<Int, String, String>> {  // Cambié Pair por Triple para incluir el título
        val careers = mutableListOf<Triple<Int, String, String>>()  // Usamos Triple para almacenar los tres valores
        val query = "SELECT cod, name, title FROM Career"  // Agregamos 'title' a la consulta
        val resultSet: ResultSet? = DatabaseDAO.executeQuery(query)

        resultSet?.let {
            while (it.next()) {
                val cod = it.getInt("cod")
                val name = it.getString("name")
                val title = it.getString("title")  // Obtenemos el valor de 'title'
                careers.add(Triple(cod, name, title))  // Usamos Triple para almacenar los tres valores
            }
            it.close()
        }
        return careers
    }


    // Insertar carrera utilizando un procedimiento almacenado
    fun insertCareer(cod: Int, name: String, title: String): Boolean {
        val procedureName = "insert_career" // Nombre del procedimiento almacenado
        return DatabaseDAO.executeStoredProcedure(procedureName, cod, name, title)
    }

    fun deleteCareer(cod: Int): Boolean {
        val procedureName = "delete_career"  // Nombre del procedimiento almacenado
        return DatabaseDAO.executeStoredProcedure(procedureName, cod)
    }
    // Insertar usuario utilizando un procedimiento almacenado
    fun insertUser(id: Int, password: String, role: String): Boolean {
        val procedureName = "insert_user" // Nombre del procedimiento almacenado
        return DatabaseDAO.executeStoredProcedure(procedureName, id, password, role)
    }

    // Llamar a procedimientos almacenados generales
    fun callStoredProcedure(procedureName: String, param1: String, param2: String): Boolean {
        return DatabaseDAO.executeStoredProcedure(procedureName, param1, param2)
    }
}
