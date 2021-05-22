package core;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Clase encargada de gestionar las paginas y las relaciones entre ellas
 * contiene unas series de tablas que permiten buscar las paginas de distintas maneras
 * @author ujarky
 */
public class HitsResultManager {
    //Tabla Hash que contiene todas las paginar (sin repetidos) encontradas por el programa crawler
    //la llave es la url de la pagina
    private Hashtable<String, HitsResult> pages;
    //Tabla hash que contiene todas las paginas source de las paginas destino
    private Hashtable<String, HashSet<HitsResult>> sourcePages;
    //Tabla hash que contiene todas las paginas destino de las paginas source
    private Hashtable<String, HashSet<HitsResult>>  destinyPages;
    //Objeto DBCrawlerResultsReader que permite leer los resultados del webcrawler del cliente
    private DBCrawlerResultsReader dbResult;

    /**
     * Construye un PagesManager con un DBCrawlerResultsReader y un DBHitsWriter
     * al momento de la contruccion obtiene todas las paginas en la base de dato
     * @param dbResults
     * @param dbWriterResults
     */
    public HitsResultManager(DBCrawlerResultsReader dbResults) {
        this.dbResult = dbResults;
        this.pages = new Hashtable<>();
        this.sourcePages = new Hashtable<>();
        this.destinyPages = new Hashtable<>();
        initPages();
    }

    /**
     * Obtencion de las paginas en la base de dato
     */
    private void initPages() {
        HashSet<String> urls;
        try {
            urls = dbResult.getExistingURLS();
            for (String url : urls) {
                    pages.put( url, new HitsResult(url));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Anade una url source y una lista de paginas (punteros a las paginas ya existentes)
     * a la lista destinyPages
     * @param source
     */
    private void addToDestinyHashtable(String source) {
        HashSet<String> urlsDestiny= this.dbResult.getURLSDestinyFromURLSource(source);
        HashSet<HitsResult> pagesDestiny = new HashSet<>();
        for (String url : urlsDestiny) {
                pagesDestiny.add(this.pages.get(url));
        }
        this.destinyPages.put(source, pagesDestiny);
    }

    /**
     * Anade una url destiny y una lista de paginas (punteros a las paginas ya existentes)
     * a la lista sourcePages
     * @param source
     */
    private void addToSourceHashtable(String destiny) {
        HashSet<String> urlsSource= this.dbResult.getURLSSourceFromURLDestiny(destiny);
        HashSet<HitsResult> pagesSource = new HashSet<>();
        for (String url : urlsSource) {
                pagesSource.add(this.pages.get(url));
        }
        this.sourcePages.put(destiny, pagesSource);
    }

    /**
     * Devuelve la tabla de paginas existentes
     * @return
     */
    public Hashtable<String, HitsResult> getPages(){
        return this.pages;
    }

    /**
     * Devuelve un Set con las paginas existentes
     * @return
     */
    public Set<Entry<String, HitsResult>> getPagesEntrySet(){
        return this.pages.entrySet();
    }

    /**
     * Devuelve la lista de paginas que estan contenidas en una pagina origen
     * @param p Pagina origen
     * @return HashSet de Page lista de paginas destino
     */
    public HashSet<HitsResult> getIncomingNeighbors(HitsResult p) {
        if( !this.destinyPages.containsKey(p.getUrl()))
            addToDestinyHashtable(p.getUrl());
        return this.destinyPages.get(p.getUrl());
    }

    /**
     * Devuelve la lista de paginas que contienen la pagina destino p
     * @param p Pagina destino
     * @return HashSet de Page lista de paginas origen
     */
    public HashSet<HitsResult> getOutgoingNeighbors(HitsResult p) {
        if( !this.sourcePages.containsKey(p.getUrl()))
            addToSourceHashtable(p.getUrl());
        return this.sourcePages.get(p.getUrl());
    }


}
