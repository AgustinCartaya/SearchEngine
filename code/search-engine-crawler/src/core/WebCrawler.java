package core;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Clace encargada de crear los WebCrawler
 *  de cada cliente que recorreran las URL
 * @author ujarky
 */
public class WebCrawler extends Thread{
	
    //Objeto DBTriadWriter para escribir los resultados
    private TriadManager dbTriadWriter;

    //URL en la que va a comenzar este WebCrawler
    private String source;

    //profundida a la cual pude llegar el WebCrawler
    private int depth;

    /**
     * Crea el WebCrawler inciando su escritor, pagina inicial, y su profundidad
     * @param dbTriadWriter
     * @param source
     * @param depth
     */
    public WebCrawler( TriadManager dbTriadWriter, String source, int depth ) {
        this.dbTriadWriter = dbTriadWriter;
        this.source = source;
        this.depth = depth;
    }

    /**
     * Iniciar la ejecucion de WebCrawler
     */
    public void run(){
        try {
            indexSetOfURLS(this.source, this.depth);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Obtencion de todas la etiquetas a con href de la pgin demarcada por la url
     * @param url URL de la pagina
     * @return Elements grupo de etiquetas a
     * @throws IOException
     */
    public Elements getHyperlinks(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        Elements hyperlinks = document.select("a[href]");
        return hyperlinks;
    }

    /**
     * Devuelve el HTML de la pagina referida por la url
     * @param url
     * @return String HTML de la pagina
     * @throws Exception 
     */
    public String getHTML(String url) throws Exception {

        url = url.replaceAll("http://", "");
        String host = url.substring( 0 , url.indexOf("/"));
        String getq = url.replaceFirst(host, "");
        Socket socket = new Socket(host, 80);

        String answer="";
        String getRequest = "GET "+ getq+" / HTTP/1.1\n" + "Host: "+ host +"\n\n";

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        writer.write(getRequest);
        writer.flush();

        String line;
        int readTimeOut = 1000;
        long timeout = System.currentTimeMillis();
        while((line = reader.readLine()) != null && System.currentTimeMillis()- timeout < readTimeOut) {
                System.out.println(System.currentTimeMillis()- timeout );
                answer+=line;
        }
        writer.close();
        reader.close();
        socket.close();

        if (timeout >= readTimeOut)
                throw new Exception("Read time out");

        return answer;
    }

    /**
     * Metodo recursivo encargado de hacer el crawling de paginas
     * @param url URL de la pagina a hacer el crawling
     * @param depth profundidad maxima del crawling
     * @throws IOException
     */
    public void indexSetOfURLS(String url, int depth) throws IOException {
        //si aun se puede ir mas profundo
        if (depth > 0) {
            try {
                //obtengo todas las urls contenidas en la pagina y
                //llamo nuevamente al metodo por cada url disminuyende la profundidad
                for (Element element: getHyperlinks(url)) {
                    String href = element.attr("href");
                    if (href.startsWith("https")) {
                        indexSetOfURLS(href, depth - 1);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        //si no se puede ir mas profundo, se obtiene todas las urls contenidas en la pagina
        //y se guardan en la base de datos 
        } else {
            try {
                for (Element element: getHyperlinks(url)) {
                    String href = element.attr("href");
                    if (href.startsWith("https")) {
                            this.dbTriadWriter.insertTriad( new Triad(url, href, element.text() ) );
                    }
                }
            } catch (Exception e) {
                    System.out.println(e.getMessage());
            }
        }
    }
}