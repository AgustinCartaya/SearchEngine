package core;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

/**
 * Clase encargada de leer en la base de dato del cliente
 * los resultados del programa crawler
 * @author ujarky
 */
public class DBCrawlerResultsReader {
	
    //Objeto DBConnectionManager que contiene la coneccion con la base de datos del cliente
    private DBConnectionManager dbConnectionManager;

    /**
     * Obtiene el objeto DBConnectionManager
     * @param dbConnectionManager
     */
    public DBCrawlerResultsReader( DBConnectionManager dbConnectionManager) throws IOException {
        this.dbConnectionManager = dbConnectionManager;

    }

    /**
     * Devuelve una lista tipo HashSet de String con todas las urls que encontrol
     * el programa crawler, busca esta infoamcacion en la tabla triasd de las
     * base de datos del cliente
     * @return HashSet de String con todas las urls no repetidas
     * @throws SQLException
     */
    public HashSet<String> getExistingURLS() throws SQLException{

        HashSet<String> urls = new HashSet<>();

        String sql = "SELECT destiny, source FROM triads";
        Statement statement  = this.dbConnectionManager.getConnection().createStatement();
        ResultSet resultSet    = statement.executeQuery(sql);

    while (resultSet.next()) {
        urls.add( resultSet.getString("destiny") );
        urls.add( resultSet.getString("source") );
    }
    return urls;
}

    /**
     * Devuelve una lista que contiene todas las urls que provienen de una 
     * pagina cuya url es source
     * @param source url pagina origen
     * @return HashSet con todas las urls provenientes de la pagina origen 
     */
    public HashSet<String> getURLSDestinyFromURLSource(String source){
        HashSet<String> urls = new HashSet<>();
        try {
            String sql = "SELECT destiny FROM triads WHERE source=? ";
            PreparedStatement preparedStatement  = this.dbConnectionManager.getConnection().prepareStatement( sql );
            preparedStatement.setString(1, source);

            ResultSet resultSet    = preparedStatement.executeQuery();

            while (resultSet.next()) {
                urls.add( resultSet.getString("destiny") );
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return urls;
    }

    /**
     * Devuelve una lista que contiene todas las urls de las paginas 
     * que contienen la url destiny
     * @param source url de la pagina destiny
     * @return HashSet con todas las urls que contiene la url destiny
     */
    public HashSet<String> getURLSSourceFromURLDestiny(String destiny){
        HashSet<String> urls = new HashSet<>();

        try {
            String sql = "SELECT source FROM triads WHERE destiny=? ";
            PreparedStatement preparedStatement  = this.dbConnectionManager.getConnection().prepareStatement( sql );
            preparedStatement.setString(1, destiny);

            ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            urls.add( resultSet.getString("source") );
        }
        }catch(SQLException e) {
            e.printStackTrace();
        }

        return urls;
    }

}
