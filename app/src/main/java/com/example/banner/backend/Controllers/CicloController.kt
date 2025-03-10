import java.sql.ResultSet

class CicloController {

    fun getAllCiclos(): List<Ciclo> {
        val ciclos = mutableListOf<Ciclo>()
        val query = "SELECT * FROM Ciclo"
        val resultSet: ResultSet? = DatabaseDAO.executeQuery(query)

        resultSet?.let {
            while (it.next()) {
                val ciclo = Ciclo(
                    it.getInt("id"),
                    it.getInt("year"),
                    it.getInt("number"),
                    it.getString("date_start"),
                    it.getString("date_finish"),
                    it.getBoolean("is_active")
                )
                ciclos.add(ciclo)
            }
            it.close()
        }
        return ciclos
    }

    fun insertCiclo(id: Int, year: Int, number: Int, dateStart: String, dateFinish: String): Boolean {
        val procedureName = "insert_ciclo"
        return DatabaseDAO.executeStoredProcedure(procedureName, id, year, number, dateStart, dateFinish)
    }
    fun updateCiclo(id: Int, year: Int, number: Int, dateStart: String, dateFinish: String): Boolean {
        return DatabaseDAO.executeStoredProcedure("update_ciclo", id, year, number, dateStart, dateFinish)
    }

    fun deleteCiclo(id: Int): Boolean {
        return DatabaseDAO.executeStoredProcedure("delete_ciclo", id)
    }
    fun callStoredProcedure(procedureName: String, param1: Any, param2: Any): Boolean {
        return DatabaseDAO.executeStoredProcedure(procedureName, param1, param2)
    }

    fun ciclo_anio(year: Int?): List<Ciclo> {
        val ciclos = mutableListOf<Ciclo>()
        val procedureName = "ciclo_anio"
        val resultSet: ResultSet? = DatabaseDAO.executeStoredProcedureWithResults(
            procedureName,
            year
        )

        resultSet?.let {
            while (it.next()) {
                val ciclo = Ciclo(
                    it.getInt("id"),
                    it.getInt("year"),
                    it.getInt("number"),
                    it.getString("date_start"),
                    it.getString("date_finish"),
                    it.getBoolean("is_active"),
                )
                ciclos.add(ciclo)
            }
            it.close()
        }
        return ciclos
    }

    fun getActiveCiclo(): Ciclo? {
        val procedureName = "get_active_ciclo"
        val resultSet: ResultSet? = DatabaseDAO.executeStoredProcedureForSingleResult(procedureName)

        resultSet?.use { rs ->
            if (rs.next()) {
                return Ciclo(
                    rs.getInt("id"),
                    rs.getInt("year"),
                    rs.getInt("number"),
                    rs.getString("date_start"),
                    rs.getString("date_finish"),
                    rs.getBoolean("is_active")
                )
            }
        }
        return null
    }

    fun setActiveCiclo(id: Int): Boolean {
        return DatabaseDAO.executeStoredProcedure("set_active_ciclo", id)
    }

    fun setDisActiveCiclo(id: Int): Boolean {
        return DatabaseDAO.executeStoredProcedure("set_disactive_ciclo", id)
    }


}
