public class SquareRooter implements Runnable{
    int begin;
    int end;
    double[] arr;
    double[] out;
    int n_sqrts;
      
    public SquareRooter(int begin, int end, double[] arr, double[] out, int n_sqrts){
        this.begin = begin;
        this.end = end;
        this.arr = arr;
        this.out = out;
        this.n_sqrts = n_sqrts;
    }

    public void run(){
        for(int i = begin; i< end; ++i){
            out[i] =SquareRootList.sqrt_n_times(arr[i],n_sqrts);
        }
        System.out.println("Thread Done");
    }
}