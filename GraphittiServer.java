
import java.io.*;
import java.net.*;

/**
 * Handles the server command line and handles the incoming connections
 * most logic in GraphittiClient and some in GraphittiState
 */
public class GraphittiServer{





/**
 * Plain old command line script
 */
    public static void main(String[] args){
        int port = Integer.parseInt(args[0]);
        int n_lines = Integer.parseInt(args[1]);
        
        GraphittiState gs = new GraphittiState(n_lines);

        ServerSocket welcomeSocket = null;
        BufferedReader clinput = new BufferedReader(
            new InputStreamReader(System.in)
        );


        try{
            welcomeSocket = new ServerSocket(port);
            //so the server can monitor the command line
            //without multiple threads (excepting client)
            welcomeSocket.setSoTimeout(1000); 
        } 
        catch(IOException e){
            throw new RuntimeException("Cannot open port " + port);
        }


        boolean running = true;
        while(running){
            Socket clientSock =  null;
            try{
                //.ready() lets us know something is on command line
                // ...this might not be the best way
                if(clinput.ready()){ 
                    String line = clinput.readLine();
                    if(line.equals("quit")){
                        System.out.println("Server shutting down");
                        gs.sendShutdown(); // gs can tell clients to disconnect
                        running = false;
                        Thread.sleep(5000);
                    }
                }
                clientSock = welcomeSocket.accept();
                new Thread( new GraphittiClient(clientSock,gs) ).start();
            } catch(SocketTimeoutException e){
                //do nothing
            }catch(IOException e){
                System.out.println(e.getMessage());

            } catch(InterruptedException e){
                System.out.println(e.getMessage());
            }


        }
        try{
            if(welcomeSocket!=null) welcomeSocket.close();
            if(clinput!=null) clinput.close();
        }catch(IOException e){
            System.out.println(e.getMessage());
        }

        

        
        System.out.println("Done shutting Down");
        System.exit(0);

    }

}