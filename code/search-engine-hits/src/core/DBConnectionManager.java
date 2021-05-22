package core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de hacer la comunicacion con la base de datos
 * cada cliente tiene su propia base de datos, por lo tanto esta
 * clase es copiada a cada cliente
 * @author ujarkyW
 */
public class DBConnectionManager {
	
    //localizacion de la base de datos
    private String path;
    //objeto conexion de la base de datos
    private Connection connection;

    /**
     * crea la base de datos en ese lugar
     * @param DBName
     * @throws IOException
     */
    public DBConnectionManager( String dbURL ) {
        this.path = dbURL;
        System.out.println("--->" +this.path);
    }

    public void connect() throws SQLException{
        this.connection = DriverManager.getConnection(this.path);
    }

    /**
     * Cierre de la base de datos
     * @throws SQLException
     */
    public void close() throws SQLException {
        this.connection.close();
    }

    /**
     * Verifica si la conexion con la base de datos esta cerrada
     * @return boolean true si la conexion esta cerrada
     * @throws SQLException
     */
    public boolean isClosed() throws SQLException {
        return this.connection.isClosed();
    }

    /**
     * Devuelve el objeto connection para comunicarse con la base de datos desde el exterior
     * @return Connection objeto de conexion
     */
    public Connection getConnection() {
        return this.connection;
    }

    /**
     * Devuelve el path de la base de datos
     * @return String que contiene el path de la base de datos
     */
    public String getPath() {
        return this.path;
    }


}
