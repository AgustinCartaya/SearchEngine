package main;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;

import externalComunication.CrawlerServer;

/**
 *
 * @author ujarky
 */
public class Main {
	
	//Start Crawler server for getting clients communication
	public static void main(String[] args) {
            
            try {
                CrawlerServer crawlerServer = new CrawlerServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}