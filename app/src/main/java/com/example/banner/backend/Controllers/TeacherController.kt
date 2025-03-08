import java.sql.ResultSet

class TeacherController {

    fun getAllTeachers(): List<Teacher> {
        val teachers = mutableListOf<Teacher>()
        val query = "SELECT * FROM Teacher"
        val resultSet: ResultSet? = DatabaseDAO.executeQuery(query)

        resultSet?.let {
            while (it.next()) {
                val teacher = Teacher(
                    it.getInt("id"),
                    it.getString("name"),
                    it.getInt("tel_number"),
                    it.getString("email")
                )
                teachers.add(teacher)
            }
            it.close()
        }
        return teachers
    }

    fun insertTeacher(id: Int, name: String, telNumber: Int, email: String): Boolean {
        val procedureName = "insert_teacher"
        return DatabaseDAO.executeStoredProcedure(procedureName, id, name, telNumber, email)
    }
    fun updateTeacher(id: Int, name: String, telNumber: Int, email: String): Boolean {
        return DatabaseDAO.executeStoredProcedure("update_teacher", id, name, telNumber, email)
    }

    fun deleteTeacher(id: Int): Boolean {
        return DatabaseDAO.executeStoredProcedure("delete_teacher", id)
    }
    fun callStoredProcedure(procedureName: String, param1: Any, param2: Any): Boolean {
        return DatabaseDAO.executeStoredProcedure(procedureName, param1, param2)
    }
}