package externalComunication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.SQLException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import core.DBConnectionManager;
import core.WebCrawlingManager;
import internalComunication.DBHitsResultsReader;
import internalComunication.InternalComunicationClient;


/**
 * Esta clase se encrga de manejar a cada cliente
 * obtiene la informacion del cliente (query a buscar en google para obtener la(s) url(s))
 * hace el craweling
 * obtiene la base de datos
 * llama al hits para que la analice
 * @author ujarky
 */
public class CrawlerClient extends Thread {
	
    //Objeto Socket del cliente
    private Socket clientSocket;
    //Objeto BufferedReader para leer la informacion enviada por cliente
    private BufferedReader clientReader;
    //Objeto BufferedWriter para enviar informacion al cliente
    private BufferedWriter clientWriter;

    //Objeto DBConnectionManager para conectarse con la base de datos del cliente
    private DBConnectionManager dbConnectionManager;
    //Objeto InternalComunicationClient para hacer la comunicacion con el programa encargado de hacer el hits
//	private InternalComunicationClient internalComunicationClient;

    //Cantidad maxima de urls iniciales que pueden ser tratadas por cliente
    private int maxURLS = 1;
    //Profundidad de los WebCrawles de este cliente
    private int depth = 0;
    //index del cliente
    private int clientIndex;

    /**
     * obtiene el socket del cliente, y crea la comunicacion interna
     * @param s
     * @param clientIndex
     * @throws IOException 
     */
    CrawlerClient(Socket clientSocket, int clientIndex) throws IOException {
        this.clientSocket = clientSocket;
        this.clientIndex = clientIndex;	
        this.clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.clientWriter = new BufferedWriter( new OutputStreamWriter(clientSocket.getOutputStream()) );
//	System.out.println("1) Connected: client-" + clientIndex+ " " + clientSocket);
    }


    public void run() {
        try {
            //hace el web Crawler
            this.startCrawling(this.makeGoogleSearch(this.getClientInformation()));

            //Informa al programa encargado de hacer el hits que ya puede comenzar
            InternalComunicationClient internalComunicationClient = new InternalComunicationClient(
                this.dbConnectionManager.getPath(), this.clientIndex
            );
            if(internalComunicationClient.comunicate()) {				
                //envio de las paginas clasificadas al cliente en forma de json
                DBHitsResultsReader dbHitsResultsReader = new DBHitsResultsReader(this.dbConnectionManager);
                this.dbConnectionManager.connect();
                String packageToSend = dbHitsResultsReader.getJsonHitsTable();
                this.dbConnectionManager.close();
                if(packageToSend!= null) {
                    this.clientWriter.write( "client-"+this.clientIndex+": SUCCES\n");
                    this.clientWriter.write(packageToSend);
                    this.clientWriter.write( "\n%END%");
                    this.clientWriter.flush();
                }else {
                    sendBadResult(0);
                }
            }else
                sendBadResult(1);
            System.out.println("6) Message sent to client-" + clientIndex);
        } catch (Exception e) {
            e.printStackTrace();
            sendBadResult(0);
        }finally {
            closeClient();
        }

    }

    /**
     * Obtencion de la informacion emitida por el cliente
     * la informacion debe se emitida en formato Json con los valores "depth" (entero opcional) "query" (String)
     * depth prifundidad
     * query busqueda del cliente
     * @return String busqueda del cliente
     * @throws Exception
     */
    private String getClientInformation() throws Exception {
        String line;
        String received  = "";
        while(true) {
            line = this.clientReader.readLine();
            if(line == null || line.equals("%END%"))
                    break;
            received  += line;
        }

        JsonElement jelement = new JsonParser().parse(received );
        JsonObject  rootObject = jelement.getAsJsonObject();

        String query = rootObject.get("query").getAsString();  

        try {
            this.depth = rootObject.get("depth").getAsInt();
        }catch(NullPointerException e){
            System.out.println("profundidad no transmitida por el cliente");
        }

        System.out.println("2) client-"+ this.clientIndex+" searching for: " + query + " with depth: "+ this.depth);
        return query;
    }

    /**
     * Realiza la busqueda en google del query enviado por el cliente y devuelve las urls encontradas
     * @param query String busqueda del cliente
     * @return String[] URLS encontradas por Google
     * @throws Exception
     */
    private String[] makeGoogleSearch(String query) throws Exception {
    	System.out.println("3) searching in google " + query + " for client-" + this.clientIndex);
        return GoogleSearch.search(query, this.maxURLS);
    }

    /**
     * Comienza el crawling con las urls pasadas por parametro
     * @param roots String urls 
     * @throws SQLException
     * @throws IOException
     */
    private void startCrawling(String[] roots) throws SQLException, IOException {
//        System.out.println("4) Comenzando el crawling");

        WebCrawlingManager webCrawlingManager = new WebCrawlingManager(this.clientIndex);
        webCrawlingManager.startCrawling(roots, this.depth);
        this.dbConnectionManager = webCrawlingManager.getDBConnectionManager();

//        System.out.println("5) crawling terminado");
    }

    /**
     * cierra la coneccion con el cliente
     */
    private void closeClient() {
        try {
            if (this.clientWriter != null)
                    this.clientWriter.close();
            if (this.clientReader != null)
                    this.clientReader.close();
            if (this.clientSocket != null)
                    this.clientSocket.close();
        } catch (Exception e) {
                e.printStackTrace();
        }
        System.out.println("7) client-" + this.clientIndex + " closed");
    }

    /**
     * Envia un error al cliente en caso de tener problemas
     */
    private void sendBadResult(int error) {
        try {
            if(error == 0)
                this.clientWriter.write( "Client-"+this.clientIndex+": ERROR\nduring crawling\n");
            if(error == 1)
                this.clientWriter.write( "Client-"+this.clientIndex+": ERROR\nduring hits\n");
            this.clientWriter.write("%END%\n");
            this.clientWriter.flush();
            System.out.println("6.1) Error "+error +" sent to client-" +this.clientIndex);
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

	
}