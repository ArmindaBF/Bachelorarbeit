import java.util.ArrayList;
import java.util.Objects;


public class Pier {
    ArrayList<Flight> lisoOfFlight = new ArrayList<>();
    ArrayList<SortingStation> listOfStation = new ArrayList<>();
    public int id ;

    //Lista de los Piers
    public Pier(ArrayList<Flight> lisoOfFlight, ArrayList<SortingStation> listOfStation) {
        this.lisoOfFlight = lisoOfFlight;
        this.listOfStation = listOfStation;
    }
    public Pier(int i){
        this.id = i;
    }

    //otra parte de lo que imprime el programa
    @Override
    public boolean equals(Object o) {
        if(o instanceof Pier){
            return false;
        }
        return this.id==((Pier )o).id;
    }
}
