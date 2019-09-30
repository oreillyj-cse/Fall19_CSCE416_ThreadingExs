
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class HelloClient{
    public static void main(String[] args) throws IOException{
        Socket socket = null;
        BufferedReader input = null;
        DataOutputStream out = null;
        DataInputStream in = null;
        try {
            socket = new Socket("127.0.0.1", 4321); 
        } catch(Exception i) {
            System.out.println("Error in IP or port");
            System.exit(1);
        }
        System.out.println("Connected"); 

        try { 
            /* takes input from terminal */
            input = new BufferedReader(new InputStreamReader(System.in)); 

            /* sends output to the socket */
            out = new DataOutputStream(socket.getOutputStream()); 
            in = new DataInputStream( 
                        new BufferedInputStream(socket.getInputStream())); 
            System.out.println(in.readUTF());
            out.writeUTF(input.readLine());
            String line="";
            do{
                line=in.readUTF();
                System.out.println(line);
            }
            while( ! line.equals("Good Bye"));

        } catch(EOFException e){
            System.out.println("Server closed connection");   
        }catch(IOException i) { 
            System.out.println(i); 
        } 
        finally{
            if (input!=null) input.close();
            if (out!=null) out.close();
            if (in!=null) in.close();
        }
    }
}