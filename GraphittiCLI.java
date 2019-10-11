import java.io.*;
import java.net.*;

/**
 * Client side of connection, the client command line program
 * @param args command line args in IP-address port order
 * CLI is Command Line Interface
 */
public class GraphittiCLI{
    public static void main(String[] args){
        if(args.length!=2 ){
            System.out.println("format is \"java GraphittiCLI <IP address> <port Number>\"");
            System.exit(1);
        }
        String ip = args[0];
        int port = Integer.parseInt(args[1]);

        Socket sock = null;
        DataInputStream serverInput = null;
        DataOutputStream serverOutput = null;
        BufferedReader cliInput =null;

        boolean running = true;
        try{
            sock = new Socket(ip,port);
            serverInput = new DataInputStream(sock.getInputStream());
            serverOutput = new DataOutputStream(sock.getOutputStream());
            cliInput = new BufferedReader( new InputStreamReader(System.in) );

            //This really is overkill for the client's application
            //...but this is an example of how to avoid blocking
            // for the client having two separate threads that block on 
            // command line input and output
            // would be fine. Those threads would be relatively simple.

            while(running){
                if(serverInput.available()>0){
                    String line = serverInput.readUTF();
                    if (line.equals("shutdown")) {
                        running=false;
                    }
                    System.out.println(line);
                }
                if(cliInput.ready()){
                    String line;
                    line = cliInput.readLine();
                    serverOutput.writeUTF(line);
                    serverOutput.flush();
                    if(line.equals("quit")) running = false;
                }
                Thread.sleep(250);
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        finally{
            try{
                sock.close();
                serverInput.close();
                serverOutput.close();
                cliInput.close();
                System.out.println("Successfully closed everything...");
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
}