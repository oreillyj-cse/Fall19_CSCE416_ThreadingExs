

public class HelloState{
    private String last_seen = "";
    public synchronized void set_last_visitor(String name){
        last_seen = name;
    }

    public synchronized String get_last_visitor(){
        return last_seen;
    }
}