import java.sql.Time;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class Algorithm {

    public ArrayList<SortingStation> listOfStation;
    public ArrayList<Flight> listOfFlights;
    ArrayList<Pier> listOfPier = new ArrayList<Pier>();
    public int instance;

    //Flight Ordering Methods
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

    private ArrayList<Flight> sortFlightList(FlightOrderingMethod a, ArrayList<Flight> list1) {
        if (FlightOrderingMethod.OST == a) {
            Collections.sort(list1, (p1, p2) -> p1.tj.compareTo(p2.tj));
        } else {
            Collections.sort(list1, (p1, p2) -> p1.stt.compareTo(p2.stt) == 0? p1.tj.compareTo(p2.tj) : p1.stt.compareTo(p2.stt));
        }
        return list1;
    }

    private Result assignFlightToResult(ArrayList<Result> resultSet, Flight aFlight, SortingStation currentStation, Time freed) {
        Result result = new Result();
        result.setTheFlight(aFlight);
        result.setTheStation(currentStation);
        long r_j = 0;
        if (freed.getTime() >= aFlight.est.getTime()) {
            r_j = freed.getTime() - aFlight.est.getTime();
        }
        result.setR_j(r_j);

        resultSet.add(result);
        return result;
    }

    public static SortingStation getNearestStation(List<SortingStation> stations, Flight currentFlight ){

        SortingStation nearestStation = null;
        double smallestDistance = 999999999;
        for(SortingStation station : stations){
            double thisDistance = distFrom( (int)station.x, (int)station.y, (int)currentFlight.X, (int) currentFlight.Y);
            if(smallestDistance > thisDistance){
                nearestStation = station;
                smallestDistance = thisDistance;
            }


        }
        return nearestStation;

    }

    public static double distFrom(int x1, int y1, int x2, int y2) {
        double dist = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        return dist;
    }


    private void AssignFlight(String assignMethod, ArrayList<Result> resultSet, Flight aFlight, List<SortingStation> notOccupied) {
        //FIFO
        SortingStation currentStation;
        if (assignMethod.equals("FIFO")) {
            currentStation = notOccupied.get(0);
        } else if (assignMethod.equals("LIFO")) {
            currentStation = notOccupied.get(notOccupied.size()-1);
        } else{
            currentStation = getNearestStation(notOccupied,aFlight);
            double distance = distFrom((int) currentStation.x,(int) currentStation.y, (int)aFlight.X, (int)aFlight.Y);
            System.out.println("\n Distance in m: "+distance);

        }
        //assign flight to sorting station
        currentStation.occupied = true;
        Time freed = currentStation.freeAt;
        currentStation.freeAt = aFlight.stt;


        Result result = assignFlightToResult(resultSet, aFlight, currentStation, freed);

        /////////LOG INFORMATION//////

        String time = String.format("%02d h, %02d min",
                (int) ((result.getR_j() / (1000*60*60)) % 24),
                (int) ((result.getR_j() / (1000*60)) % 60)
        );


        System.out.println("\n" + aFlight.id + " took  station \t" + currentStation.id + " in pier\t"
                + currentStation.pierId + " with r_j=" + result.getR_j() + ", Time freed: "+ freed + ", flight EST: "+aFlight.est + ", Flight LST: "+ aFlight.lst);
        System.out.print(" Occupied:\t");
        for (SortingStation s : listOfStation.stream().filter(p -> p.occupied).collect(Collectors.toList())) {
            System.out.print(s + " ");
        }
        /////////LOG INFORMATION//////
    }


    //algorithm A
    public ArrayList<Result> algorithmA(FlightOrderingMethod orderMethod, String assignMethod) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> allFlights = new ArrayList<>(this.listOfFlights);
        ArrayList<Flight> flightsForForeignPier = new ArrayList<>();
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        System.out.println("Algorithm A: ");
        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);
        System.out.println("Anzahl Fluege in Instanz: "+listOfFlights.size());

        //sorting flight list according sort method
        sortFlightList(orderMethod, allFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);

        for (SortingStation s: currentStations) {
            s.freeAt = new Time(-3600000);
        }

        for (Flight aFlight : listOfFlights) {

            ////// Try to assign flight WITHOUT buffer reduction to own pier //////

            //get sorting station only with same pierId
            List<SortingStation> ssOnOwnPier = currentStations.stream()
                    .filter(p -> p.pierId == aFlight.pierId).collect(Collectors.toList());


            for (SortingStation ss : ssOnOwnPier) {
                if (ss.freeAt.getTime() <= aFlight.est.getTime()) {
                    ss.occupied = false;
                }else{
                    ss.occupied = true;
                }
            }
            List<SortingStation> notOccupied = ssOnOwnPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on own Pier without buffer red");

                AssignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }


            ////// Try to assign flight WITH buffer reduction to own pier //////
            notOccupied = ssOnOwnPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());

            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : ssOnOwnPier) {
                if (ss.freeAt.getTime() < aFlight.lst.getTime()) {
                    ss.occupied = false;
                }else{
                ss.occupied = true;
            }
            }

            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on own Pier with buffer red");

                AssignFlight(assignMethod, resultSet, aFlight, notOccupied);

                continue;
            } else {

                //there are no more free stations in pier -> look for all free station on foreign piers;
                flightsForForeignPier.add(aFlight);
            }


        }
        for (Flight aFlight : flightsForForeignPier) {
            List<SortingStation> ssOnForeignPier = currentStations.stream()
                    .filter(p -> p.pierId != aFlight.pierId).collect(Collectors.toList());

            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : ssOnForeignPier) {
                if(ss.id == 2){
                    String time = String.format("%02d h, %02d min",
                            (int) ((ss.freeAt.getTime() / (1000*60*60)) % 24),
                            (int) ((ss.freeAt.getTime() / (1000*60)) % 60)
                    );


                }
                if (ss.freeAt.getTime() < aFlight.est.getTime()) {
                    ss.occupied = false;
                }else{
                    ss.occupied = true;
                }
            }
            List<SortingStation> notOccupied = ssOnForeignPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on foreign Pier without buffer red");
                AssignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }
            notOccupied = ssOnForeignPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : ssOnForeignPier) {
                if (ss.freeAt.getTime() < aFlight.lst.getTime()) {
                    ss.occupied = false;
                }else{
                    ss.occupied = true;
                }
            }
            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on foreign Pier with buffer red");

                AssignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }
            unmatchingFlights.add(aFlight);

        }
        System.out.println("unmatched Flights: "+ unmatchingFlights.size());
        return resultSet;
    }

    public ArrayList<Result> algorithmB(FlightOrderingMethod orderMethod, String assignMethod) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> allFlights = new ArrayList<>(this.listOfFlights);
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        System.out.println("Algorithm B: ");
        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);
        System.out.println("Anzahl Fluege in Instanz: "+listOfFlights.size());

        //sorting flight list according sort method
        sortFlightList(orderMethod, allFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);

        for (SortingStation s: currentStations) {
            s.freeAt = new Time(-3600000);
        }

        for (Flight aFlight : listOfFlights) {

            ////// Try to assign flight WITHOUT buffer reduction to own pier //////

            //get sorting station only with same pierId
            List<SortingStation> ssOnOwnPier = currentStations.stream()
                    .filter(p -> p.pierId == aFlight.pierId).collect(Collectors.toList());


            for (SortingStation ss : ssOnOwnPier) {
                if (ss.freeAt.getTime() <= aFlight.est.getTime()) {
                    ss.occupied = false;
                }else{
                    ss.occupied = true;
                }
            }
            List<SortingStation> notOccupied = ssOnOwnPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on own Pier without buffer red");

                AssignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }


            ////// Try to assign flight WITH buffer reduction to own pier //////
            notOccupied = ssOnOwnPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());

            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : ssOnOwnPier) {
                if (ss.freeAt.getTime() < aFlight.lst.getTime()) {
                    ss.occupied = false;
                }else{
                    ss.occupied = true;
                }
            }

            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on own Pier with buffer red");

                AssignFlight(assignMethod, resultSet, aFlight, notOccupied);

                continue;
            }

            //Switch to foreign piers

            List<SortingStation> ssOnForeignPier = currentStations.stream()
                    .filter(p -> p.pierId != aFlight.pierId).collect(Collectors.toList());


            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : ssOnForeignPier) {

                if (ss.freeAt.getTime() < aFlight.est.getTime()) {
                    ss.occupied = false;
                }else{
                    ss.occupied = true;
                }
            }
            notOccupied = ssOnForeignPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on foreign Pier without buffer red");
                AssignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }
            notOccupied = ssOnForeignPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : ssOnForeignPier) {
                if (ss.freeAt.getTime() < aFlight.lst.getTime()) {
                    ss.occupied = false;
                }else{
                    ss.occupied = true;
                }
            }
            if (notOccupied.size() != 0) {

                AssignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }
            unmatchingFlights.add(aFlight);

        }
        System.out.println("unmatched Flights: "+ unmatchingFlights.size());
        return resultSet;

    }


    public ArrayList<Result> algorithmC(FlightOrderingMethod orderMethod, String assignMethod) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> allFlights = new ArrayList<>(this.listOfFlights);
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        System.out.println("Algorithm C: ");
        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);
        System.out.println("Anzahl Fluege in Instanz: "+listOfFlights.size());

        //sorting flight list according sort method
        sortFlightList(orderMethod, allFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);
        for (SortingStation s: currentStations) {
            s.freeAt = new Time(-3600000);
        }

        for (Flight aFlight : listOfFlights) {

            ////// Try to assign flight WITHOUT buffer reduction to own pier //////

            //get sorting station only with same pierId
            List<SortingStation> allStations = currentStations.stream().collect(Collectors.toList());


            for (SortingStation ss : allStations) {
                if (ss.freeAt.getTime() <= aFlight.est.getTime()) {
                    ss.occupied = false;
                }else{
                    ss.occupied = true;
                }
            }
            List<SortingStation> notOccupied = allStations.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on any Pier without buffer red");

                AssignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }


            ////// Try to assign flight WITH buffer reduction to own pier //////
            notOccupied = allStations.stream().filter(p -> !p.occupied).collect(Collectors.toList());

            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : allStations) {
                if (ss.freeAt.getTime() < aFlight.lst.getTime()) {
                    ss.occupied = false;
                }else{
                    ss.occupied = true;
                }
            }

            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on any Pier with buffer red");

                AssignFlight(assignMethod, resultSet, aFlight, notOccupied);

                continue;
            }

            unmatchingFlights.add(aFlight);

        }
        System.out.println("unmatched Flights: "+ unmatchingFlights.size());
        return resultSet;

    }


}
