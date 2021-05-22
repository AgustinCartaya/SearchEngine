package internalComunication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Clase que se encarga de hacer la comunicacion con el programa Hits
 * se crea una comunicacion por cada cliente
 * @author ujarky
 */
public class InternalComunicationClient {
	
    //indice del cliente
    private int clientIndex;

    //respuesta obtenida por el programa Hits
    private String answer;

    private String dbURL;

    /**
     * Obtiene el indice del cliente e inicia a null la respuesta
     * @param clientIndex
     */
    public InternalComunicationClient(String dbURL, int clientIndex) {
        this.clientIndex = clientIndex;
        this.answer = null;
        this.dbURL = dbURL;
    }

    /**
     * Se comunica con el programa hits enviandole el nombre
     * de la base de datos donde estan contenidos los resultados del cliente
     * para que este comienze a clasificar las paginas
     * @return boolean si la respuesta del programa Hits no es un error
     */
    public boolean comunicate() {
        boolean goodAnswer = false;
        Socket socket = null;
        BufferedWriter writer = null;
        BufferedReader reader = null;
        try {

            String serverHost = InetAddress.getLocalHost().getHostAddress();
            socket = new Socket(serverHost, 1214);

            writer = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()) );
            reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

            writer.write(this.dbURL + "\n%END%\n");
            writer.flush();

        String line;
        this.answer = "";
        while(true) {
            line = reader.readLine();
            if(line == null || line.equals("%END%"))
                break;
            this.answer += line;
        }

        socket.close();
        reader.close();
        writer.close();

        goodAnswer = !this.answer.equals("%ERROR%");

        } catch (IOException e) {
            e.printStackTrace();
            this.answer = "%INTERNAL_ERROR%";
        }finally {
            try {
                if (reader != null)
                    reader.close();
                if (writer != null)
                    writer.close();
                if (socket != null)
                    socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return goodAnswer;
    }

    /**
     * Devuelve la respuesta del programa Hits
     * @return
     */
    public String getAnswer() {
        return this.answer;
    }

}
