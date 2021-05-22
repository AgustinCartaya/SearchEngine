package main;

import java.io.IOException;

import internalComunication.InternalComunicationServer;

//Start Hits server for getting crawler progam communication
public class Main {
	public static void main(String[] args) {
            try {
                InternalComunicationServer crawlerServer = new InternalComunicationServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
