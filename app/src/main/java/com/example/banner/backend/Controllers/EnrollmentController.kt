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
}
