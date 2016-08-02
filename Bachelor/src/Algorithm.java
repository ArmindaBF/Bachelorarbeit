import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by vladimir on 8/2/16.
 */
public class Algorithm {
    public ArrayList<SortingStation> listOfStation;
    public ArrayList<Flight> listOfFlights;
    ArrayList<Pier> listOfPier = new ArrayList<Pier>();
    public int instance;

    public enum FlightOrderingMethod {
        OST, ODT
    }

    public Algorithm(ArrayList<SortingStation> listOfStation, ArrayList<Flight> listOfFlights, ArrayList<Pier> listOfPier, int instance) {
        this.listOfStation = listOfStation;
        this.listOfFlights = listOfFlights;
        this.listOfPier = listOfPier;
        this.instance = instance;
    }

    public SortingStation findStationById(int id) {
        for (SortingStation s : this.listOfStation) {
            if (s.id == id) {
                return s;
            }
        }
        return null;
    }

    public ArrayList<Result> algorithmA(FlightOrderingMethod a) {
        ArrayList<Flight> list1 = new ArrayList<Flight>(this.listOfFlights);
        System.out.println("Algorithm A: ");
        if (FlightOrderingMethod.OST == a) {
            Collections.sort(list1, (p1, p2) -> p1.tj.compareTo(p2.tj));
            listOfFlights.removeIf(s -> s.instanceNummer != this.instance);
            //flights are coming
            LinkedList<SortingStation> list2 = new LinkedList<SortingStation>(this.listOfStation);
            for (Flight f : listOfFlights) {
                //get sorting station only with same pierId
                List<SortingStation> onlyRelevantPiers = list2.stream()
                        .filter(p -> p.pierId == f.pierId).collect(Collectors.toList());
                //get only free stations
                List<SortingStation> notOccuoied = onlyRelevantPiers.stream().filter(p -> !p.occupied).collect(Collectors.toList());
                if(notOccuoied.size()!=0){
                    int id = notOccuoied.get(0).id;
                    if (findStationById(id) != null) {
                        findStationById(id).occupied = true;
                        System.out.println(f.id + " took  station \t" + findStationById(id).id + " in pier\t" + f.pierId);
                    } else {


                        //there is no more free station in pier -> look for all free station
                        List<SortingStation> allFreeStation = list2.stream()
                                .filter(p -> !p.occupied).collect(Collectors.toList());

                        int id2 = notOccuoied.get(0).id;
                        if (findStationById(id2) != null) {
                            findStationById(id2).occupied = true;
                            System.out.println(f.id + " took  station \t" + findStationById(id).id + " in pier\t" + findStationById(id).id);
                        }
                    }
                }


            }

            System.out.println("Algorithm A: ");

        } else if (FlightOrderingMethod.ODT == a) {

        }


        return new ArrayList<Result>();

    }

}
