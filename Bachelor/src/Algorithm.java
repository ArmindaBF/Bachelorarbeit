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
            for (Flight aFlight : listOfFlights) {
                //get sorting station only with same pierId
                List<SortingStation> onlyRelevantPiers = list2.stream()
                        .filter(p -> p.pierId == aFlight.pierId).collect(Collectors.toList());
                //get only free stations
                List<SortingStation> notOccuoied = onlyRelevantPiers.stream().filter(p -> !p.occupied).collect(Collectors.toList());
                if (notOccuoied.size() != 0) {
                    int id = notOccuoied.get(0).id;

                    findStationById(id).occupied = true;
                    long timeToDepack = aFlight.stt.getTime() - aFlight.est.getTime();

                    findStationById(id).runCounter(5);//the Thread wir erzeugt mit secunden time. Danach wird station wieder frei
                    /////////LOG INFORMATION//////
                    System.out.println("\n" + aFlight.id + " took  station \t" + findStationById(id).id + " in pier\t" + aFlight.pierId);
                    System.out.print(" Occupied:\t");
                    for (SortingStation s : listOfStation.stream().filter(p -> p.occupied).collect(Collectors.toList())) {
                        System.out.print(s + " ");
                    }
                    /////////LOG INFORMATION//////
                    continue;
                }
                //there is no more free station in pier -> look for all free station
                List<SortingStation> allFreeStation = list2.stream()
                        .filter(p -> !p.occupied).collect(Collectors.toList());

                int id2 = allFreeStation.get(0).id;
                if (findStationById(id2) != null) {
                    findStationById(id2).occupied = true;
                    long timeToDepack = aFlight.stt.getTime() - aFlight.est.getTime();
                    findStationById(id2).runCounter(5);

                    /////////LOG INFORMATION//////
                    System.out.println("\n" + aFlight.id + " took  station \t" + findStationById(id2).id + " in pier\t" + findStationById(id2).id);
                    System.out.print(" Occupied:\t");
                    for (SortingStation s : listOfStation.stream().filter(p -> p.occupied).collect(Collectors.toList())) {
                        System.out.print(s + " ");
                    }
                    System.out.print("\n");
                    /////////LOG INFORMATION//////
                    continue;
                }


            }

            System.out.println("Algorithm A: ");

        } else if (FlightOrderingMethod.ODT == a) {

        }


        return new ArrayList<Result>();

    }

}
