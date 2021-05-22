package core;

import java.io.File;
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
     * Crea una carpeta en "..." si eta no existe
     * crea la base de datos en ese lugar
     * @param DBName
     * @throws IOException
     */
    public DBConnectionManager( String DBName ) throws IOException {
        File dir = new File("DDBB");
        if(!dir.exists())
                dir.mkdir();
        this.path = buildPathDatabaseURL(DBName);
    }

    /**
     * Devuelve el path de la base de datos  
     * @param DDBBName nombre de la base de datos
     * @return String con el path de la bas de datos
     * @throws IOException
     */
    private String buildPathDatabaseURL(String DBName) throws IOException{
        if(!DBName.endsWith(".db"))
            DBName += ".db";
        return ("jdbc:sqlite:" +  new File("DDBB").getCanonicalPath()  +"/"+ DBName);
    }

    /**
     * conexion con la base de datos
     * @throws SQLException
     */
    public void connect() throws SQLException{
        this.connection = DriverManager.getConnection(path);
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
