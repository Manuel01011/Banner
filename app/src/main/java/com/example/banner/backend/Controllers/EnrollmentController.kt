import java.sql.ResultSet

class EnrollmentController {

    // Obtener todas las inscripciones
    fun getAllEnrollments(): List<Enrollment> {
        val enrollments = mutableListOf<Enrollment>()
        val query = "SELECT * FROM Enrollment"
        val resultSet: ResultSet? = DatabaseDAO.executeQuery(query)

        resultSet?.let {
            while (it.next()) {
                // Crear un objeto Enrollment a partir del ResultSet
                val enrollment = Enrollment(
                    it.getInt("student_id"),
                    it.getInt("grupo_id"),
                    it.getDouble("grade")
                )
                enrollments.add(enrollment)
            }
            it.close() // Cerramos el ResultSet después de usarlo
        }
        return enrollments
    }

    // Insertar una inscripción
    fun insertEnrollment(studentId: Int, grupoId: Int, grade: Double): Boolean {
        val procedureName = "insert_enrollment"
        // Llamar al procedimiento almacenado para insertar la inscripción
        return DatabaseDAO.executeStoredProcedure(procedureName, studentId, grupoId, grade)
    }

    // Eliminar una inscripción
    fun deleteEnrollment(studentId: Int, grupoId: Int): Boolean {
        val procedureName = "delete_enrollment"
        // Llamar al procedimiento almacenado para eliminar la inscripción
        return DatabaseDAO.executeStoredProcedure(procedureName, studentId, grupoId)
    }

    // Cambiar de ciclo para el estudiante (llama al procedimiento almacenado)
    fun changeCycle(studentId: Int, newCycleId: Int): Boolean {
        val procedureName = "change_cycle"
        // Llamar al procedimiento almacenado para cambiar el ciclo del estudiante
        return DatabaseDAO.executeStoredProcedure(procedureName, studentId, newCycleId)
    }


    fun updateStudentGrade(studentId: Int, groupId: Int, newGrade: Double): Boolean {
        val procedureName = "update_student_grade"
        return DatabaseDAO.executeStoredProcedure(procedureName, studentId, groupId, newGrade)
    }

    // Obtener los cursos matriculados en el ciclo activo por un estudiante
    fun getActiveCycleCourses(studentId: Int): List<Course> {
        val courses = mutableListOf<Course>()
        val query = "CALL get_active_cycle_courses(?)" // Llamamos al procedimiento almacenado

        val resultSet: ResultSet? = DatabaseDAO.executeQuery(query, studentId)

        resultSet?.let {
            while (it.next()) {
                // Crear el objeto Course con los nuevos parámetros
                courses.add(
                    Course(
                        it.getInt("cod"),         // Código del curso
                        it.getString("name"),     // Nombre del curso
                        it.getInt("credits"),     // Créditos del curso
                        it.getInt("hours"),       // Horas del curso
                        it.getInt("year"),
                        it.getInt("ciclo_id")
                    )
                )
            }
            it.close()  // Cerramos el ResultSet después de usarlo
        }
        return courses
    }

}
