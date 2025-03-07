import java.sql.CallableStatement
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

object DatabaseDAO {

    private val dbHelper = MySQLDatabaseHelper()

    // Obtener conexión
    private fun getConnection(): Connection? {
        return dbHelper.getConnection()
    }

    // Método para ejecutar consultas SELECT
    fun executeQuery(query: String, vararg params: Any): ResultSet? {
        return try {
            val conn = getConnection() ?: return null
            val stmt = conn.prepareStatement(query)
            for ((index, param) in params.withIndex()) {
                stmt.setObject(index + 1, param)
            }
            stmt.executeQuery()
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    // Método para ejecutar procedimientos almacenados
    fun executeStoredProcedure(procedureName: String, vararg params: Any): Boolean {
        val conn = getConnection() ?: return false
        return try {
            val callableStatement: CallableStatement = conn.prepareCall("{call $procedureName(?, ?, ?)}") // Ajustar a tu procedimiento
            for ((index, param) in params.withIndex()) {
                callableStatement.setObject(index + 1, param)
            }
            callableStatement.execute()
            callableStatement.close()
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    // Método para ejecutar INSERT, UPDATE, DELETE utilizando CallableStatement
    fun executeUpdateWithProcedure(query: String, vararg params: Any): Boolean {
        val conn = getConnection() ?: return false
        return try {
            val callableStatement: CallableStatement = conn.prepareCall(query)  // Preparar la consulta de llamada
            for ((index, param) in params.withIndex()) {
                callableStatement.setObject(index + 1, param)
            }
            callableStatement.executeUpdate() > 0
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }
}