import java.sql.ResultSet

class StudentController {

    fun getAllStudents(): List<Student> {
        val students = mutableListOf<Student>()
        val query = "SELECT * FROM Student"
        val resultSet: ResultSet? = DatabaseDAO.executeQuery(query)

        resultSet?.let {
            while (it.next()) {
                val student = Student(
                    it.getInt("id"),
                    it.getString("name"),
                    it.getInt("tel_number"),
                    it.getString("email"),
                    it.getString("born_date"),
                    it.getInt("career_cod")
                )
                students.add(student)
            }
            it.close()
        }
        return students
    }

    fun insertStudent(id: Int, name: String, telNumber: Int, email: String, bornDate: String, careerCod: Int): Boolean {
        val procedureName = "insert_student"
        return DatabaseDAO.executeStoredProcedure(procedureName, id, name, telNumber, email, bornDate, careerCod)
    }
    fun updateStudent(id: Int, name: String, telNumber: Int, email: String, bornDate: String, careerCod: Int): Boolean {
        return DatabaseDAO.executeStoredProcedure("update_student", id, name, telNumber, email, bornDate, careerCod)
    }

    fun deleteStudent(id: Int): Boolean {
        return DatabaseDAO.executeStoredProcedure("delete_student", id)
    }
    fun callStoredProcedure(procedureName: String, param1: Any, param2: Any): Boolean {
        return DatabaseDAO.executeStoredProcedure(procedureName, param1, param2)
    }

}
