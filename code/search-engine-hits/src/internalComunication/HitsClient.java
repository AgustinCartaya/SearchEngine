package internalComunication;

import core.HitsImplementation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;



/**
 * Clase que se encarga de manejar a 
 * @author ujarky
 */
public class HitsClient extends Thread {
	
    private Socket clientSocket;
    private BufferedReader clientReader;
    private BufferedWriter clientWriter;

    HitsClient(Socket s) {
        this.clientSocket = s;
        System.out.println("1) Connected: " + this.clientSocket);
    }
	
    public void run() {
        try {
            startHitting(getClientInformation());
            sendTableHeaders();

        } catch (Exception e) {
            e.printStackTrace();
            sendBadResult();
        }finally {
            closeClient();
        }
    }
	
    //obtener principalmente la base de datos del cliente
    private String getClientInformation() throws Exception {
        String recibido = "";

        this.clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.clientWriter = new BufferedWriter( new OutputStreamWriter(clientSocket.getOutputStream()) );
        String line;

    while(true) {
        line = this.clientReader.readLine();
        if(line == null || line.equals("%END%"))
            break;
       recibido += line;
    }

        if(recibido.length()==0)
            throw new Exception();

        System.out.println("2) client data base: " + recibido);
        return recibido;
    }

    private void startHitting(String dataBaseURL) throws Exception {
        HitsImplementation hits = new HitsImplementation(dataBaseURL);
        hits.startHitting();	
    }

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
        System.out.println("6) close internal comunicaction " +  this.clientSocket);
    }

    private void sendTableHeaders() throws IOException {
        this.clientWriter.write("%url;authority;hub%\n");
        this.clientWriter.write("%END%\n");
        this.clientWriter.flush();
        System.out.println("5) Headers sents "+  this.clientSocket);
    }

    private void sendBadResult() {
        try {
            this.clientWriter.write("%ERROR%\n");
            this.clientWriter.write("%END%\n");
            this.clientWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
