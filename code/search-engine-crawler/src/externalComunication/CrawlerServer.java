package externalComunication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Clase encargarda de crear el servidor del crawler 
 * y ponerlo a la escucha de los clientes
 * @author ujarky
 */
public class CrawlerServer {
	
    //Objeto Servidor
    private ServerSocket server;
    //Cantidad de clientes que han hecho demandas (utilizado para indexar los clientes)
    private static int clientIndex = 0;


    /**
     * Inicia el servidor y se pone a la escucha de los clientes
     * @throws IOException
     */
    public CrawlerServer() throws IOException {

        this.server = new ServerSocket(1213);
        System.out.println("CrawlerServer Encendido");
        Socket client;
        while (true) {
            client = this.server.accept();
            Thread crawlerClient = new CrawlerClient(client, this.clientIndex);
            crawlerClient.start();
        	System.out.println("1) Connected: client-" + clientIndex+ " " + clientIndex);

            this.clientIndex++;
        }
    }
}

