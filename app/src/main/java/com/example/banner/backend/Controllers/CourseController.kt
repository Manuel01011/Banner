import java.sql.ResultSet

class CourseController {

    fun getAllCourses(): List<Course> {
        val courses = mutableListOf<Course>()
        val query = "SELECT * FROM Course"
        val resultSet: ResultSet? = DatabaseDAO.executeQuery(query)

        resultSet?.let {
            while (it.next()) {
                val course = Course(
                    it.getInt("cod"),
                    it.getString("name"),
                    it.getInt("credits"),
                    it.getInt("hours"),
                    it.getInt("ciclo_id"),
                    it.getInt("career_cod")
                )
                courses.add(course)
            }
            it.close()
        }
        return courses
    }

    fun insertCourse(cod: Int, name: String, credits: Int, hours: Int, cicloId: Int, careerCod: Int): Boolean {
        val procedureName = "insert_course"
        return DatabaseDAO.executeStoredProcedure(procedureName, cod, name, credits, hours, cicloId, careerCod)
    }
    fun updateCourse(cod: Int, name: String, credits: Int, hours: Int, cicloId: Int, careerCod: Int): Boolean {
        return DatabaseDAO.executeStoredProcedure("update_course", cod, name, credits, hours, cicloId, careerCod)
    }

    fun deleteCourse(cod: Int): Boolean {
        return DatabaseDAO.executeStoredProcedure("delete_course", cod)
    }
    fun callStoredProcedure(procedureName: String, param1: Any, param2: Any): Boolean {
        return DatabaseDAO.executeStoredProcedure(procedureName, param1, param2)
    }
    //funcionalidad esperada en el CourseController
    fun searchCourses(nombre: String?, codigo: Int?, carreraCod: Int?): List<Course> {
        val courses = mutableListOf<Course>()
        val procedureName = "BuscarCurso"
        val resultSet: ResultSet? = DatabaseDAO.executeStoredProcedureWithResults(
            procedureName,
            nombre,
            codigo,
            carreraCod
        )

        resultSet?.let {
            while (it.next()) {
                val course = Course(
                    it.getInt("cod"),
                    it.getString("name"),
                    it.getInt("credits"),
                    it.getInt("hours"),
                    it.getInt("ciclo_id"),
                    it.getInt("career_cod")
                )
                courses.add(course)
            }
            it.close()
        }
        return courses
    }

    fun getCoursesByCareerAndCycle(careerCod: Int, cicloId: Int): List<Course> {
        val procedureName = "get_courses_by_career_and_cycle"
        val resultSet: ResultSet? = DatabaseDAO.executeStoredProcedureWithResults(procedureName, careerCod, cicloId)

        val courses = mutableListOf<Course>()
        resultSet?.use { rs ->
            while (rs.next()) {
                val course = Course(
                    rs.getInt("cod"),
                    rs.getString("name"),
                    rs.getInt("credits"),
                    rs.getInt("hours"),
                    cicloId,
                    careerCod
                )
                courses.add(course)
            }
        }
        return courses
    }
}