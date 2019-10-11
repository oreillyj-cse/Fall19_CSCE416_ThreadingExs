import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList; //might want to look this one up

/**
 * Manages the shared state for the Graphitti Wall server. 
 * All public methods are synchronized since there is one thread per connection.
 * (there are better ways but you need to understand threading to evaluate your options)
 * This is just _a_ reasonable way.
 */
public class GraphittiState {
    private CopyOnWriteArrayList<GraphittiClient> clients; //there are better choices...
    private String[] lines;
    private int maxLines;
    private int nextToWriteTo;

    
    
    /**
     * Constructor for Graphitti State
     * @param   maxLines    number of lines of the graphitti wall
     */
    public GraphittiState(int maxLines){
        this.maxLines = maxLines;
        lines = new String[maxLines];
        nextToWriteTo = 0;
        wipeWall();

        this.clients = new CopyOnWriteArrayList<GraphittiClient>();
    }

    private void wipeWall(){
        for(int i = 0; i< maxLines;++i)
            lines[i] = "--";
    }

    /**
     * Adds a client to our current list of clients. Client also gets the 
     * current wall without broadcasting to everyone. Clients removed in broadcast.
     * @param gc    The GrpahittiClient to be added
     */
    public synchronized void addClient(GraphittiClient gc){
        clients.add(gc);
        //following line to monitor number of clients on server
        //GClients lazily removed in broadcast().
        System.out.println("New Client added"+this.clients.size());
        sendWall(gc);

    }

    /**
     * Writes to wall. Note: does not automatically broadcast().
     * @param s string to be written to wall
     */
    public synchronized void writeToWall(String s){
        
        lines[nextToWriteTo] = s;
        nextToWriteTo = (nextToWriteTo + 1) % maxLines;
    }

    private String getWall(){
        String wall = "===========Graphitti=Wall===========\n";
       
        for(String line:lines){
            wall += line + '\n';
        }
        wall       += "====================================\n";
        return wall;
    }

    /**
     * Sends wall to everyone. Will purge dead connections.
     */
    public synchronized void broadcast(){
        System.out.println("broadcast()");
        String wall = getWall();

        broadcast(wall);

        //lazily deletes gclients
        System.out.println("number of GClients ==" + clients.size());

    }
    
    /**
     * Sends a message to all clients and also purges the current client list of 
     * dead connections
     * @param msg   some message
     */
    public synchronized void broadcast(String msg){
        Iterator<GraphittiClient> igc = clients.iterator();

        while(igc.hasNext()){
            GraphittiClient gc = igc.next();
            if ( ! gc.active ) {
                clients.remove(gc);
            }else{
                gc.sendMessage(msg);
            }
        }

        //Note: purging here may or may not be the best way
        // disadvantage: we're mixing separate issues
        // advantage: broadcast gets called a lot and we can't forget to purge
    }


    /**
     * Sends then current wall to a particular client
     * 
     * @param   gc  the Specific GraphittiClient that needs a wall
     */
    public synchronized void sendWall(GraphittiClient gc){
        gc.sendMessage( getWall() );//look at send message
    }

    /**
     * Notifies all users that the server is shutting down and then tells them to shutdown
     */
    public synchronized void sendShutdown(){
        broadcast("Server is shutting down...");//good to tell people you're shutting down
        broadcast("shutdown");//clients listen for this and will shut themselves down
    }

    



}