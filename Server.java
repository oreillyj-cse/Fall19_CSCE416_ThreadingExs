

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server implements Runnable{
    int port = 4321;
    ServerSocket serverSock = null;
    public volatile boolean active = true;

    HelloState state = new HelloState();

    public void run(){
        try{
            this.serverSock = new ServerSocket(this.port);
            serverSock.setSoTimeout(1000);
        } 
        catch(IOException e){
            throw new RuntimeException("cannot open port " + port);
        }

        while(this.active){
            Socket clientSock =  null;
            try{
                clientSock = this.serverSock.accept();
                new Thread( new ClientConnection(clientSock,state) ).start();
            } catch(SocketTimeoutException e){
                //do nothing
            }catch(IOException e){
                System.out.println(e.getMessage());
            } 


        }

    }
}