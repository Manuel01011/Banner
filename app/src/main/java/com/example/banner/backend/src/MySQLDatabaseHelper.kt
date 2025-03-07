import java.sql.Connection
import java.sql.DriverManager
import java.sql.CallableStatement
import java.sql.SQLException

class MySQLDatabaseHelper {

    private val dbUrl = "jdbc:mysql://localhost:3306/banner"
    private val dbUser = "root"
    private val dbPassword = "root"

    init {
        try {
            // Registrar el controlador
            Class.forName("com.mysql.cj.jdbc.Driver")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    // Función para obtener la conexión
    fun getConnection(): Connection? {
        return try {
            DriverManager.getConnection(dbUrl, dbUser, dbPassword)
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    // Ejecutar procedimiento almacenado
    fun executeStoredProcedure(procedureName: String, params: Array<Any>): Boolean {
        val connection = getConnection()
        connection?.let {
            try {
                val callableStatement: CallableStatement = it.prepareCall("{call $procedureName(?, ?)}")
                // Asumiendo que el procedimiento tiene 2 parámetros de tipo String
                callableStatement.setString(1, params[0] as String)
                callableStatement.setString(2, params[1] as String)

                val result = callableStatement.execute()
                callableStatement.close()
                return result
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
        return false
    }
}