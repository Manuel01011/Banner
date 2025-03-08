import java.sql.ResultSet

class GrupoController {

    fun getAllGrupos(): List<Grupo> {
        val grupos = mutableListOf<Grupo>()
        val query = "SELECT * FROM Grupo"
        val resultSet: ResultSet? = DatabaseDAO.executeQuery(query)

        resultSet?.let {
            while (it.next()) {
                val grupo = Grupo(
                    it.getInt("id"),
                    it.getInt("number_group"),
                    it.getInt("year"),
                    it.getString("horario"),
                    it.getInt("course_cod"),
                    it.getInt("teacher_id")
                )
                grupos.add(grupo)
            }
            it.close()
        }
        return grupos
    }

    fun insertGrupo(id: Int, numberGroup: Int, year: Int, horario: String, courseCod: Int, teacherId: Int): Boolean {
        val procedureName = "insert_grupo"
        return DatabaseDAO.executeStoredProcedure(procedureName, id, numberGroup, year, horario, courseCod, teacherId)
    }

    fun updateGrupo(id: Int, numberGroup: Int, year: Int, horario: String, courseCod: Int, teacherId: Int): Boolean {
        val procedureName = "update_grupo"
        return DatabaseDAO.executeStoredProcedure(procedureName, id, numberGroup, year, horario, courseCod, teacherId)
    }

    fun deleteGrupo(id: Int): Boolean {
        val procedureName = "delete_grupo"
        return DatabaseDAO.executeStoredProcedure(procedureName, id)
    }

    fun callStoredProcedure(procedureName: String, param1: Any, param2: Any): Boolean {
        return DatabaseDAO.executeStoredProcedure(procedureName, param1, param2)
    }
}
