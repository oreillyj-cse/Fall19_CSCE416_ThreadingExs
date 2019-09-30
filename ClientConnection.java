

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientConnection implements Runnable{
    public Socket clientSock = null;
    HelloState state = null;

    public ClientConnection(Socket clientSocket,HelloState state){
        this.clientSock = clientSocket;
        this.state = state;
    }

    public void run(){
        try{
            DataInputStream input = new DataInputStream(clientSock.getInputStream());
            DataOutputStream output = new DataOutputStream(clientSock.getOutputStream());
            output.writeUTF("Hello. What is your name?\n");
            String name = input.readUTF();
            String last_visitor = state.get_last_visitor();
            state.set_last_visitor(name);
            output.writeUTF("Nice to meet you, " + name + ".\n");
            if(last_visitor.equals("")){
                output.writeUTF("You're the first person I've seen since I woke up!");
            }
            else{
                output.writeUTF("I just saw "+ last_visitor+"!");
            }
            int countdown = 10;
            for(int i = countdown;i>0;--i){
                output.writeUTF("Closing in " + i +"...");
                Thread.sleep(1000);
            }
            output.writeUTF("Good bye\n");
            output.close();
            input.close();
        }catch(EOFException e){
            System.out.println("Server closed connection");
        }
        catch (Exception e){
            System.out.println("Client"+e.getMessage());
        }
    }
}