package core;


import java.io.IOException;
import java.sql.SQLException;

/**
 * Clase encargada de gestionar los WebCrawler
 * @author ujarky
 *
 */
public class WebCrawlingManager {

    //Objeto DBConnectionManager para comunicarse con la base de datos
    private DBConnectionManager dbConnectionManager;
    //Objeto DBTriadWriter para escribir en la base de dato
    private TriadManager dbTriadWriter;
    //index del cliente
    private int clientIndex;

    /**
     * Crea la base de datos correspondiente al cliente pasado por parametro
     * crea el DBTriadWriter
     * @param clientIndex int indoce del cliente
     * @throws IOException
     * @throws SQLException
     */
    public WebCrawlingManager(int clientIndex) throws IOException, SQLException {
    	this.clientIndex = clientIndex;
        this.dbConnectionManager = new DBConnectionManager("Data-client-" + clientIndex);
        this.dbConnectionManager.connect();
        this.dbTriadWriter = new TriadManager( this.dbConnectionManager );
        this.dbConnectionManager.close();
    }

    /**
     * inicia el crawling creando los WebCrawels y poniendolos a funcionar
     * @param roots url da las paginas iniciales (se creara un WebCrawel para cada url)
     * @param depth profundidas que pueden alcanzar los WebCrawels
     * @throws SQLException
     */
    public void startCrawling( String [] roots, int depth ) throws SQLException {
    		System.out.println("4) Crawling client-" + clientIndex +" started");
            this.dbConnectionManager.connect();
            WebCrawler []wcs = new WebCrawler[roots.length];
            for (int i =0; i<roots.length; i++) {
                wcs[i] = new WebCrawler(this.dbTriadWriter, roots[i], depth);
                wcs[i].start();
            }

            for (int i =0; i<wcs.length; i++)  {
                try{
                        wcs[i].join();
                }catch(InterruptedException e){
                        e.printStackTrace();
                }
            }
            this.dbConnectionManager.close();
    		System.out.println("5) Crawling client-" + clientIndex +" finished");

    }

    public DBConnectionManager getDBConnectionManager() {
            return this.dbConnectionManager;
    }
}
