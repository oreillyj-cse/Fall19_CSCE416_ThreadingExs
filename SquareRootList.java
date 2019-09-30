
public class SquareRootList{

    private static double to_secs(long start, long end){
        return (double) ((end-start)/1e9);
    }

    public static double sqrt_n_times(double x, int n){
        for(int i =0;i<n;++i) x = Math.sqrt(x);
        return x;
    }

    public static void main(String args[]){
        int n_threads = Integer.parseInt(args[0]);

        int n_sqrts =  Integer.parseInt(args[1]);

        final int n = (int)1e7;
        double[] nums = new double[n];
        double[] singlenums = new double[n];
        double[] threadnums = new double[n]; 
        for(int i = 0; i< n; ++i){
            nums[i] = i;
        
        }


        long start = System.nanoTime();
        for(int i = 0; i <n;++i){
            singlenums[i]=sqrt_n_times(nums[i],n_sqrts);
        }
        System.out.println("Main thread Done");
        long end = System.nanoTime();
        double single_thread_time = to_secs(start,end);
        


        start = System.nanoTime();
        
        Thread[] threads=new Thread[n_threads];
        for(int i =0 ; i<n_threads; ++i){
            threads[i] = new Thread(new SquareRooter(i*n/n_threads,(i+1)*n/n_threads,nums,threadnums,n_sqrts));
        }

        for(Thread t : threads) t.start();

        try{
            Thread.sleep(15000);
        }catch(InterruptedException e){
            System.out.println(e.getMessage());
        }

        try{
            for(Thread t: threads) t.join();
        }catch(InterruptedException e){
            System.out.println(e.getMessage());
        }


        end = System.nanoTime();
        double multi_thread_time = to_secs(start,end);
        
        boolean all_equal = true;
        int equal_index =0;
        for( ; equal_index<n && all_equal;++equal_index){
            if (singlenums[equal_index]!=threadnums[equal_index]) all_equal = false;
        }
        //NOTE: comparing floating point numbers with equality is bad, 
        //due to rounding errors, but we're calling EXACTLY the same function so...
        if(all_equal) System.out.println("Outputs are the same");
        else System.out.println("Numbers not all the same, at index "+ equal_index );

        //if(correct_sum != sum) System.out.println("Er, the sum was wrong");
        System.out.println("Single Threaded: " + "Square rooting " + n + " numbers took "+ single_thread_time + " seconds." );
        System.out.println("Multi Threaded: " + "Square rooting " + n + " numbers took "+ multi_thread_time + " seconds with "+n_threads+" threads." );

    }
}