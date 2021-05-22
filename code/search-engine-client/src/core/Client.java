package core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {
    
    public String serverIP;
    public int serverPort;
    public ServerAnswer lastAnswer;
    
    public Client(){
        this(null, -1);
    }
    
    public Client(String serverIP, int serverPort){
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.lastAnswer = new ServerAnswer(null, null);
    }
    
    public ServerAnswer getAnswerByQuery(String query){
        return getAnswerByQuery(query, 0);
    }
    
    public ServerAnswer getAnswerByQuery(String query, int depth) {
        
        if(this.serverIP == null || this.serverPort == -1 || query == null)
            return null;
        
        Gson gson = new Gson();
        Socket socket = null;
        BufferedWriter writer = null;
        BufferedReader reader = null;
        try {

            socket = new Socket(this.serverIP, this.serverPort );

            writer = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()) );
            reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

            writer.write(prepareMessage(query, depth) + "\n%END%\n");
            writer.flush();

            String line;
            String receaved = "";
            while(true) {
                line = reader.readLine();
                if(line == null || line.equals("%END%"))
                    break;
               receaved += line+'\n';
            }
            
            String [] receavedSplited = receaved.split("\n");
            lastAnswer.setAnswer(receavedSplited[0], receavedSplited[1]);

        } catch (IOException e) {
            System.out.println("Connection Error");
        }finally {
            try {
                if (reader != null)
                        reader.close();
                if (writer != null)
                        writer.close();
                if (socket != null)
                        socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lastAnswer;
    }
    
    private String prepareMessage(String query, int depth){
        JsonObject jObject = new JsonObject();
        jObject.addProperty("query", query );
        jObject.addProperty("depth", depth );
        return jObject.toString();
    }
 
    public String getServerIP() {
        return this.serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
    
    public ServerAnswer getLastAnswer(){
        return this.lastAnswer;
    }
    
}
