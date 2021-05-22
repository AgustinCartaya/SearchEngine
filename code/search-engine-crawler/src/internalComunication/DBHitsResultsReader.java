package internalComunication;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.JsonObject;

import core.DBConnectionManager;

/**
 * Clase encargada de leer en la base de dato del cliente
 * los resultados del Hits
 * @author ujarky
 */
public class DBHitsResultsReader {
	
    //Objeto DBConnectionManager que contiene la coneccion con la base de datos del cliente
    private DBConnectionManager dbConnectionManager;

    /**
     * Obtiene el objeto DBConnectionManager
     * @param dbConnectionManager
     */
    public DBHitsResultsReader(DBConnectionManager dbConnectionManager) {
        this.dbConnectionManager = dbConnectionManager;
    }

    /**
     * Devuelve la tabla que contiene los resultados del Hits en formato Json
     * @return String formato Json
     * @throws SQLException
     */
    public String getJsonHitsTable() throws SQLException {
        String tableJson = null;
        String sql = "SELECT url, authority, hub FROM hits_results";
        Statement statement;

        statement = this.dbConnectionManager.getConnection().createStatement();
        ResultSet resultSet    = statement.executeQuery(sql);
        tableJson = "[";

        while (resultSet.next()) {

            JsonObject jObject = new JsonObject();

            jObject.addProperty("url", resultSet.getString("url"));
            jObject.addProperty("authority", resultSet.getDouble("authority"));	
            jObject.addProperty("hub", resultSet.getDouble("hub"));
            tableJson += jObject.toString() + ",";

        }
        tableJson = tableJson.substring(0,tableJson.length()-1 )+"]";
        return tableJson;
    }

}
