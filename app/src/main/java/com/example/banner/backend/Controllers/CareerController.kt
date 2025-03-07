import java.sql.ResultSet

class CareerController {

    fun getAllCareers(): List<Pair<Int, String>> {
        val careers = mutableListOf<Pair<Int, String>>()
        val query = "SELECT cod, name FROM Career"
        val resultSet: ResultSet? = DatabaseDAO.executeQuery(query)

        resultSet?.let {
            while (it.next()) {
                val cod = it.getInt("cod")
                val name = it.getString("name")
                careers.add(Pair(cod, name))
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
