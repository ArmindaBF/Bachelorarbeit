/**
 * Created by vladimir on 8/2/16.
 */
public class SortingStation extends Thread {
    public int id;
    public double x;
    public double y;
    public int pierId;
    public boolean occupied;
    private Thread couter;
    long timOut;

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", pierId=" + pierId +
                '}';
    }

    public void run() {
        while (this.timOut>0) {

            try {
                Thread.sleep(1000);
                this.timOut--;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.err.print(this+"\t"+timOut);

        }
            this.occupied=false;
            System.err.print(" \n"+this+"\t is free now\n");
    }

    public void start() {
        System.out.println("Starting ");
        if (couter == null) {
            couter = new Thread(this);
            couter.start();
        }
    }

    public void runCounter(long a) {
        this.timOut = a;
        this.start();
    }

    public SortingStation(int id, double x, double y, int pierId) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.pierId = pierId;
        this.occupied = false;
    }


}
