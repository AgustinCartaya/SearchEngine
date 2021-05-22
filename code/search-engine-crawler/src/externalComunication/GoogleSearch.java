package externalComunication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Clase encargada de hacer una busqueda en google
 * @author ujarky
 */
public class GoogleSearch {
	
    /**
     * llama a los metodos internos encargados de hacer la busqueda y devuelve 
     * una lista con todas las urls resultantes
     * @param query String texto a buscar en google
     * @return List lista de url
     * @throws Exception
     */
    public static List<String> searchList(String query) throws Exception{
        return parseLinks( getGoogleResultPageHTML(query) );
    }
    
    public static String[] search(String query, int maxURLS) throws Exception {
        ArrayList<String>  results = (ArrayList<String>) searchList(query);
        String roots[] = new String[ (results.size()<=maxURLS)?results.size():maxURLS ];
        for (int i = 0; i<roots.length; i++) {
            roots[i] = results.get(i);
        }

        if(roots.length == 0)
            throw new Exception();

        return roots;

    }

    /**
     * Devuelve el HTML de la pagina de resultados de google, luego de la busqueda
     * de la qury 
     * @param googleSearchQuery String texto a buscar en goole
     * @return
     * @throws Exception
     */
    public static String getGoogleResultPageHTML(String query) throws Exception {
        String answer ="";

        query = query.trim();
        query = URLEncoder
             .encode(query, StandardCharsets.UTF_8.toString());
        String queryUrl = "https://www.google.com/search?q=" + query + "&num=10";
        String agent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";
        URL url = new URL(queryUrl);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        connection.setRequestProperty("User-Agent", agent);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;

        while ((inputLine = bufferedReader.readLine()) != null) {
                 answer += inputLine;
        }
        bufferedReader.close();

        return answer;
      }

    /**
     * Devuelve una lista de URL obtenidas por la busqueda en google
     * @param html String que contiene el html de la pagina resultante
     * @return List lista de urls
     * @throws Exception
     */
    public static List<String> parseLinks(String html) throws Exception {
        List<String> result = new ArrayList<String>();
        Document doc = Jsoup.parse(html);
        Elements results = doc.select("a > h3");
        for (Element link : results) {
            Elements parent = link.parent().getAllElements();
            String relHref = parent.attr("href");
            if (relHref.startsWith("/url?q=")) {
                relHref = relHref.replace("/url?q=", "");
            }
              String[] splittedString = relHref.split("&sa=");
                if (splittedString.length > 1) {
                  relHref = splittedString[0];
                }
              result.add(relHref);
        }
        return result;
      }

}
