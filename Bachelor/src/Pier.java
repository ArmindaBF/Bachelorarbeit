import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by vladimir on 8/2/16.
 */

public class Pier {
    ArrayList<Flight> lisoOfFlight = new ArrayList<>();
    ArrayList<SortingStation> listOfStation = new ArrayList<>();
    public int id ;

    public Pier(ArrayList<Flight> lisoOfFlight, ArrayList<SortingStation> listOfStation) {
        this.lisoOfFlight = lisoOfFlight;
        this.listOfStation = listOfStation;
    }
    public Pier(int i){
        this.id = i;
    }
    @Override
    public boolean equals(Object o) {
        if(o instanceof Pier){
            return false;
        }
        return this.id==((Pier )o).id;
    }
}
