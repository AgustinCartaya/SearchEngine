package core;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map.Entry;

import java.util.Set;

/**
 * Clase con los metodos necesarios para implementar el metodo hits
 * y obtener la informacion resultante luego de haberlo implementado 
 * @author ujarky
 */
public class HitsImplementation {
	
    //Objeto DBConnectionManager para comunicarse con la base de datos
    private DBConnectionManager dbConnectionManager;
    //Objeto DBCrawlerResults para obtener los datos del crawler de la base datos
    private DBCrawlerResultsReader dbCrawlerResultsReader;
    //Objeto DBCrawlerResults para escribir los resultados luego del hits
    private DBHitsWriter dbHitsWriter;
    //Objeto PagesManager para gestionar la relacion entre paginas
    private HitsResultManager pagesManager;

    /**
     * Obtiene el nombre de la base de datos del cliente
     * inicializa los Objetos para parer el Hits
     * @param dataBaseName
     * @throws SQLException
     * @throws IOException
     */
    public HitsImplementation(String dataBaseURL) throws Exception {
    	System.out.println("3) Hits started " + dataBaseURL);
        this.dbConnectionManager = new DBConnectionManager(dataBaseURL);
        this.dbConnectionManager.connect();
        this.dbCrawlerResultsReader = new DBCrawlerResultsReader(this.dbConnectionManager);
        this.dbHitsWriter = new DBHitsWriter(this.dbConnectionManager);
        this.pagesManager = new HitsResultManager(this.dbCrawlerResultsReader);
        this.dbConnectionManager.close();
    	System.out.println("4) Hits finished " + dataBaseURL);

    }

    /**
     * Comienza el Hits
     * @throws SQLException
     */
    public void startHitting() throws SQLException {
        this.dbConnectionManager.connect();
        hubsAndAuthorities();
        writeResults();
        this.dbConnectionManager.close();
    }

    /**
     * Metodo que se encarga de ejecutar el Algoritmo Hits
     * para clasificar las paginas
     * @throws SQLException
     */
    private void hubsAndAuthorities() throws SQLException{
        Set<Entry<String, HitsResult>> pages = this.pagesManager.getPagesEntrySet();
        for (int i = 0; i < 2; i++) {
            double norm = 0;
            for (Entry<String, HitsResult> entry : pages) {
                HitsResult p = entry.getValue();
                p.setAuthority(0);
                HashSet<HitsResult> incomingNeighbors = this.pagesManager.getIncomingNeighbors(p);
                for (HitsResult q : incomingNeighbors) 
                        p.setAuthority( p.getAuthority() + q.getHub()); 

                norm += Math.pow(p.getAuthority(), 2);
            }
            norm = Math.sqrt(norm);

            for (Entry<String, HitsResult> entry : pages) {
                HitsResult p = entry.getValue();
                p.setAuthority(p.getAuthority() / norm);
            }

            for (Entry<String, HitsResult> entry : pages) {
                HitsResult p = entry.getValue();
                p.setHub(0);
                HashSet<HitsResult> outgoinggNeighbors = this.pagesManager.getOutgoingNeighbors(p);
                for (HitsResult r : outgoinggNeighbors) 
                    p.setHub( p.getHub() + r.getAuthority()); 

                norm += Math.pow(p.getHub(), 2);
            }
            norm = Math.sqrt(norm);

            for (Entry<String, HitsResult> entry : pages) {
                HitsResult p = entry.getValue();
                p.setHub(p.getHub() / norm);
            }
        }	
    }

    /**
     * Escribe los resultados en en la base de datos
     */
    public void writeResults() {
        Set<Entry<String, HitsResult>> pages = this.pagesManager.getPagesEntrySet();
        for (Entry<String, HitsResult> entry : pages) {
                this.dbHitsWriter.insertPage(entry.getValue());
        }
    }

}
