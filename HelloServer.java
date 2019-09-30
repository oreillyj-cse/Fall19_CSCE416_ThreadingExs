
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class HelloServer{





    public static void main(String[] args){
        Server svr = new Server();
        Thread thread = new Thread(svr);
        thread.start();
        Scanner sysin = new Scanner(System.in);

        String line="";
        while(!(line=sysin.nextLine()).equals("quit"))
        ;

        

        System.out.println("Stopping Server");
        svr.active = false;
        try{
            Thread.sleep(20000);
        }catch(InterruptedException e){
            System.out.println("Shutdown timeout interrupted, shutting down immediately");
        }
        System.out.println("Shutting Down");
        System.exit(0);

    }

}