import java.sql.Time;

//definicion de las eigenschaften de los BSS
public class SortingStation {
    public int id;
    public double x;
    public double y;
    public int pierId;
    public boolean occupied;
    public Time freeAt;
    //private Thread couter;

    //lo que me imprime el programa
    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", pierId=" + pierId +
                '}';
    }

    //Constructor de las BSS
    public SortingStation(int id, double x, double y, int pierId, Time freeAt) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.pierId = pierId;
        this.occupied = false;
        this.freeAt = freeAt;
    }

}
