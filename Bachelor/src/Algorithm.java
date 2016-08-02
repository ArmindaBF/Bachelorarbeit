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

    //algorithm A
    public ArrayList<Result> algorithmA(FlightOrderingMethod a) {
        ArrayList<Flight> list1 = new ArrayList<Flight>(this.listOfFlights);
        System.out.println("Algorithm A: ");
        //waehlt die flight ordering method
        if (FlightOrderingMethod.OST == a) {
            Collections.sort(list1, (p1, p2) -> p1.tj.compareTo(p2.tj));
            //instance auswaehlen
            listOfFlights.removeIf(s -> s.instanceNummer != this.instance);
            //flights are coming
            LinkedList<SortingStation> list2 = new LinkedList<SortingStation>(this.listOfStation);
            for (Flight aFlight : listOfFlights) {
                //get sorting station only with same pierId
                List<SortingStation> onlyRelevantPiers = list2.stream()
                        .filter(p -> p.pierId == aFlight.pierId).collect(Collectors.toList());
                //get only free stations (ist nur eine Kopie)
                List<SortingStation> notOccupied = onlyRelevantPiers.stream().filter(p -> !p.occupied).collect(Collectors.toList());
                if (notOccupied.size() != 0) {
                    int id = notOccupied.get(0).id;

                    //Das ist das Original fuer liste stationen
                    //findet eine station die occupied ist y la califica como eso
                    findStationById(id).occupied = true;

                    // en timetodepack hay que meter el tiempo de bearbeitung
                    long timeToDepack = aFlight.stt.getTime() - aFlight.est.getTime(); //la resta es milisekunden
                    //en el 5 de abajo hay que ponerlo aqui (los 5 son segundos)
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

               // la diferencia de lo de arriba y abajo es que arriba es eigene pier y abajo es en otros piers
                //null ist das erste element. die null soll ich nicht aendern. es heist die erste freie station
                int id2 = allFreeStation.get(0).id;
                if (findStationById(id2) != null) {
                    findStationById(id2).occupied = true;
                    //lo mismo de arriba
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

            //habria que escribir lo mismo que arriba
            // alles copy paste ausser die zeile nummer: 39 --> die muss ich nach logik umwandeln . also ODT hay que ordenarlas por los ej
        } else if (FlightOrderingMethod.ODT == a) {

        }


        return new ArrayList<Result>();

    }

}
