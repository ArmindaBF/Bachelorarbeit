import java.util.*;

/**
 * Created by vladimir on 8/2/16.
 */
public class Algorithm {
    public ArrayList<SortingStation> listOfStation;
    public ArrayList<Flight> listOfFlights;
    public int instance;

    public enum FlightOrderingMethod {
        OST, ODT
    }

    public Algorithm(ArrayList<SortingStation> listOfStation, ArrayList<Flight> listOfFlights,int instance) {
        this.listOfStation = listOfStation;
        this.listOfFlights = listOfFlights;
        this.instance = instance;
    }

    public ArrayList<Result> algorithmA(FlightOrderingMethod a) {
        ArrayList<Flight> list1 = new ArrayList<Flight>(this.listOfFlights);
        ArrayList<SortingStation> list2 = new ArrayList<SortingStation>(this.listOfStation);
        System.out.println("Algorithm A: ");
        if (FlightOrderingMethod.OST == a) {
            Collections.sort(list1, (p1, p2) -> p1.tj.compareTo(p2.tj));
            listOfFlights.removeIf(s -> s.instanceNummer!=this.instance);
            System.out.println("Algorithm A: ");

        } else if (FlightOrderingMethod.ODT == a) {

        }


        return new ArrayList<Result>();

    }

}
