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

    // Llamar a procedimientos almacenados generales
    fun callStoredProcedure(procedureName: String, param1: String, param2: String): Boolean {
        return DatabaseDAO.executeStoredProcedure(procedureName, param1, param2)
    }

    //funcion esperada en Carrer
    fun getCareerByNameAndCode(name: String?, code: Int?): List<Triple<Int, String, String>> {
        val careers = mutableListOf<Triple<Int, String, String>>()
        val query = "buscar_carrera"  // Nombre del procedimiento almacenado

        // Llamamos al procedimiento almacenado y obtenemos el ResultSet
        val resultSet = DatabaseDAO.executeStoredProcedureWithResults(query, name, code)

        // Procesar los resultados
        resultSet?.let {
            while (it.next()) {
                val cod = it.getInt("cod")
                val name = it.getString("name")
                val title = it.getString("title")
                careers.add(Triple(cod, name, title))
            }
            it.close()
        }

        return careers
    }

    //funcion esperada
    fun editCareer(cod: Int, name: String, title: String, coursesToAdd: List<Int>, coursesToRemove: List<Int>): Boolean {
        val procedureName = "edit_career"

        // Editar la carrera
        val updated = DatabaseDAO.executeStoredProcedure(procedureName, cod, name, title)

        if (!updated) return false  // Si no se actualizó la carrera, no seguir con los cursos

        // Agregar cursos a la carrera
        for (courseId in coursesToAdd) {
            if (!addCourseToCareer(cod, courseId)) {
                println("Error al agregar curso $courseId a la carrera $cod")
                return false
            }
        }

        // Quitar cursos de la carrera
        for (courseId in coursesToRemove) {
            if (!removeCourseFromCareer(courseId)) {
                println("Error al remover curso $courseId de la carrera $cod")
                return false
            }
        }

        return true
    }

    fun addCourseToCareer(careerId: Int, courseId: Int): Boolean {
        return DatabaseDAO.executeStoredProcedure("add_course_to_career", careerId, courseId)
    }

    fun removeCourseFromCareer(courseId: Int): Boolean {
        return DatabaseDAO.executeStoredProcedure("remove_course_from_career", courseId)
    }




}
