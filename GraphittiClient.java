import java.io.*;
import java.net.*;


/**
 * This is the server side of a connection with a client. It connects with
 * GraphittiCLI, the client side of the connection.
 */
public class GraphittiClient implements Runnable{
    /**
     * active flag true if should continue running. otherwise 
     * GraphittiClient will shut itself down.
     */
    public volatile boolean active = true;
    private DataInputStream input;
    private DataOutputStream output;
    private GraphittiState state = null;
    private Socket socket;

    /**
     * Sets up GraphittiClient and adds to GraphittiState client list. 
     * @param sock  Socket corresponding to a new connection
     * @param gs    Graphitti State
     */
    public GraphittiClient(Socket sock, GraphittiState gs){
        state = gs;
        this.socket = sock;
        try{
            input = new DataInputStream(sock.getInputStream());
            output = new DataOutputStream(sock.getOutputStream());
            
        } catch(IOException e){
            System.out.println("Error opening in I/O Streams");
            System.out.println(e.getMessage());
            this.active = false;
        } finally{
            try{ //try to close everthing if exception
                if(!active){
                    if(input!=null)  input.close();
                    if(output!=null) output.close();
                    if(socket!=null) socket.close();
                }
            }catch(Exception e){
                System.out.println("Exception in GraphittiClient Constructor\n"
                                    +e.getMessage());
            }
        }
    }

    public void run(){
        //return in run will kill the thread, probably from constructor exception
        if (! active) return;

        state.addClient(this);
        
        try{
            while(active){
                String graphitti;
                if( input.available()>0){
                    graphitti = input.readUTF();
                    System.out.println(graphitti);
                    if(graphitti.equals("quit")){
                        this.active = false; //don't write and close connection
                    } else{
                        state.writeToWall(graphitti);
                        state.broadcast();
                    }
                }else{
                    Thread.sleep(500);
                }
            }
        /*
         * Note: we don't really need to use available() for this becuase we're only 
         * ever receiving from the GrahittiCLI. Blocking might be okay (unless we 
         * want to kill the threadgracefully, as this is written to).
         */

        }catch(EOFException e){
            System.out.println(e.getMessage());
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }catch(InterruptedException e){
            System.out.println(e.getMessage());
        }
        finally{
            System.out.println("in finally for GCLient");
            this.active = false; //mark this thread as ready for removal
            try{
                input.close();
                output.close();
                socket.close();
            }catch(IOException e){
                System.out.println("While closing client connection, \n"+e.getMessage());
            }
            
        }

        return;//we're done
    }

    /**
     * Try to send a message to the client. If this fails the client will be 
     * removed from the connection and will have to reconnect.
     * @param s The string to send to the client
     */
    public  void sendMessage(String s){
        //note here that we are synchronizing on the output DataOutputStream
        //all other examples used synchronized methods but synchronizing on the actual 
        //objects needed can prevent too much waiting

        
        synchronized(output){
            try{
                output.writeUTF(s);
                output.flush();
            }
            catch(IOException e){
                System.out.println(e.getMessage());
                e.printStackTrace();
                this.active = false; //kill this thread (Runnable)
            }
       }
    }

}