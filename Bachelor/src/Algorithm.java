import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


public class Algorithm {


    public ArrayList<SortingStation> listOfStation;
    public ArrayList<Flight> listOfFlights;
    ArrayList<Pier> listOfPier = new ArrayList<Pier>();
    public int instance;


    private BufferedWriter writer;

    public BufferedWriter getWriter() {
        return writer;
    }

    public void setWriter(BufferedWriter writer) {
        if (writer != null) {
            this.writer = writer;
            try {
                //write Headline
                writer.write("Instance;" +
                        "FlightID;" +
                        "Date;" +
                        "Flight X-Coordinate;" +
                        "Flight Y-Coordinate;" +
                        "Flight Outbound;" +
                        "Flight-PierID;" +
                        "SortingStationID;" +
                        "Station X-Coordinate;" +
                        "Station Y-Coordinate;" +
                        "Station-PierID;" +
                        "OccupiedStations(Count);" +
                        "OccupiedStations(IDs);" +
                        "Distance(if used);" +
                        "StationGotFreeAt;" +
                        "r_ij (Buffer Reduction);" +
                        "Flight EST;" +
                        "Flight s_ij;" +
                        "Flight LST;" +
                        "Flight STT"
                );
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.writer = null;
        }
    }

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

    private ArrayList<Flight> sortFlightList(FlightOrderingMethod a, ArrayList<Flight> list1) {
        if (FlightOrderingMethod.OST == a) {
            Collections.sort(list1, (p1, p2) -> p1.tj.compareTo(p2.tj));
        } else {
            Collections.sort(list1, (p1, p2) -> p1.stt.compareTo(p2.stt) == 0 ? p1.tj.compareTo(p2.tj) : p1.stt.compareTo(p2.stt));
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

    public static SortingStation getNearestStation(List<SortingStation> stations, Flight currentFlight) {

        SortingStation nearestStation = null;
        double smallestDistance = 999999999;
        for (SortingStation station : stations) {
            double thisDistance = distFrom((int) station.x, (int) station.y, (int) currentFlight.x, (int) currentFlight.y);
            if (smallestDistance > thisDistance) {
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




    private void assignFlight(String assignMethod, ArrayList<Result> resultSet, Flight aFlight, List<SortingStation> notOccupied) {
        SortingStation currentStation;
        Time freed;
        String time;
        String realStartTime;
        double distance = 0;
        if (resultSet != null) {


            //FIFO
            switch (assignMethod) {
                case "FIFO":
                    currentStation = notOccupied.get(0);
                    break;
                case "LIFO":
                    currentStation = notOccupied.get(notOccupied.size() - 1);
                    break;
                default:
                    currentStation = getNearestStation(notOccupied, aFlight);
                    distance = distFrom((int) currentStation.x, (int) currentStation.y, (int) aFlight.x, (int) aFlight.y);
                    System.out.println("\n Distance in m: " + distance);

                    break;
            }
            //assign flight to sorting station
            currentStation.occupied = true;
            freed = currentStation.freeAt;
            currentStation.freeAt = aFlight.stt;


            Result result = assignFlightToResult(resultSet, aFlight, currentStation, freed);

             time = String.format("%02d:%02d:00",
                    (int) ((result.getR_j() / (1000 * 60 * 60)) % 24),
                    (int) ((result.getR_j() / (1000 * 60)) % 60)
            );


            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            long s_ij = aFlight.est.getTime() + result.getR_j();
             realStartTime = sdf.format(s_ij);
        }else{
            currentStation = new SortingStation(0,0,0,0,new Time(-3600000));
            freed = new Time(-3600000);
            time ="00:00:00";
            realStartTime = "NEVER!!";
        }
        List<SortingStation> occupiedStations = listOfStation.stream().filter(p -> p.occupied).collect(Collectors.toList());
        String ids = "";
        for (SortingStation s : occupiedStations) {
            ids += s.id + ",";
        }

        SimpleDateFormat sdf_datum = new SimpleDateFormat("dd/MM/yyyy");

        if (this.writer != null) {
            try {
                this.writer.write(this.instance + ";"
                        + aFlight.id + ";"
                        + sdf_datum.format(aFlight.datum) + ";"
                        + aFlight.x + ";"
                        + aFlight.y + ";"
                        + aFlight.Outbound + ";"
                        + aFlight.pierId + ";"
                        + currentStation.id + ";"
                        + currentStation.x + ";"
                        + currentStation.y + ";"
                        + currentStation.pierId + ";"
                        + occupiedStations.size() + ";"
                        + ids + ";"
                        + new BigDecimal(distance).setScale(2, BigDecimal.ROUND_HALF_EVEN) + ";"
                        + freed + ";"
                        + time + ";"
                        + aFlight.est + ";"
                        + realStartTime + ";"
                        + aFlight.lst + ";"
                        + aFlight.stt + ";"


                );
                this.writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    //algorithm A : First try to assign on own peer -> first without then with time reduction
    //then switch to foreign peers, try to assign all yet unassigned flights without then with time reduction (two separate lists)
    public ArrayList<Result> algorithmA(FlightOrderingMethod orderMethod, String assignMethod) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> allFlights = new ArrayList<>(this.listOfFlights);
        ArrayList<Flight> flightsForForeignPier = new ArrayList<>();
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        System.out.println("Algorithm A: ");
        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);
        System.out.println("Anzahl Fluege in Instanz: " + listOfFlights.size());

        //sorting flight list according sort method
        sortFlightList(orderMethod, allFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);

        for (SortingStation s : currentStations) {
            s.freeAt = new Time(-3600000);
            s.occupied = false;

        }

        for (Flight aFlight : listOfFlights) {

            ////// Try to assign flight WITHOUT buffer reduction to own pier //////

            //get sorting station only with same pierId
            List<SortingStation> ssOnOwnPier = currentStations.stream()
                    .filter(p -> p.pierId == aFlight.pierId).collect(Collectors.toList());


            for (SortingStation ss : ssOnOwnPier) {
                ss.occupied = ss.freeAt.getTime() > aFlight.est.getTime();
            }
            List<SortingStation> notOccupied = ssOnOwnPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on own Pier without buffer red");

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }


            ////// Try to assign flight WITH buffer reduction to own pier //////

            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : ssOnOwnPier) {
                ss.occupied = ss.freeAt.getTime() >= aFlight.lst.getTime();
            }

            notOccupied = ssOnOwnPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());

            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on own Pier with buffer red");

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);

            } else {

                //there are no more free stations in pier -> look for all free station on foreign piers;
                flightsForForeignPier.add(aFlight);
            }


        }
        for (Flight aFlight : flightsForForeignPier) {
            List<SortingStation> ssOnForeignPier = currentStations.stream()
                    .filter(p -> p.pierId != aFlight.pierId).collect(Collectors.toList());


            ////// Try to assign flight WITHOUT buffer reduction to foreign pier //////

            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : ssOnForeignPier) {
                ss.occupied = ss.freeAt.getTime() >= aFlight.est.getTime();
            }
            List<SortingStation> notOccupied = ssOnForeignPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on foreign Pier without buffer red");
                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }

            ////// Try to assign flight WITH buffer reduction to foreign pier //////

            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : ssOnForeignPier) {
                ss.occupied = ss.freeAt.getTime() >= aFlight.lst.getTime();
            }
            notOccupied = ssOnForeignPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());

            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on foreign Pier with buffer red");

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }
            unmatchingFlights.add(aFlight);
            assignFlight(assignMethod, null, aFlight, notOccupied);



        }
        System.out.println("unmatched Flights: " + unmatchingFlights.size());
        return resultSet;
    }


    //algorithm A-2 : First try to assign on own peer without time reduction
    //then switch to foreign peers, try to assign all yet unassigned flights without time reduction (two separate lists)
    public ArrayList<Result> algorithmAwithoutReduction(FlightOrderingMethod orderMethod, String assignMethod) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> allFlights = new ArrayList<>(this.listOfFlights);
        ArrayList<Flight> flightsForForeignPier = new ArrayList<>();
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        System.out.println("Algorithm A: ");
        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);
        System.out.println("Anzahl Fluege in Instanz: " + listOfFlights.size());

        //sorting flight list according sort method
        sortFlightList(orderMethod, allFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);

        for (SortingStation s : currentStations) {
            s.freeAt = new Time(-3600000);
            s.occupied = false;
        }

        for (Flight aFlight : listOfFlights) {

            ////// Try to assign flight WITHOUT buffer reduction to own pier //////

            //get sorting station only with same pierId
            List<SortingStation> ssOnOwnPier = currentStations.stream()
                    .filter(p -> p.pierId == aFlight.pierId).collect(Collectors.toList());


            for (SortingStation ss : ssOnOwnPier) {
                ss.occupied = ss.freeAt.getTime() > aFlight.est.getTime();
            }
            List<SortingStation> notOccupied = ssOnOwnPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on own Pier without buffer red");

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }


            //there are no more free stations in pier -> look for all free stations on foreign piers;
            flightsForForeignPier.add(aFlight);


        }
        for (Flight aFlight : flightsForForeignPier) {
            List<SortingStation> ssOnForeignPier = currentStations.stream()
                    .filter(p -> p.pierId != aFlight.pierId).collect(Collectors.toList());

            ////// Try to assign flight WITHOUT buffer reduction to foreign pier //////

            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : ssOnForeignPier) {
                ss.occupied = ss.freeAt.getTime() >= aFlight.est.getTime();
            }
            List<SortingStation> notOccupied = ssOnForeignPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on foreign Pier without buffer red");
                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }

            unmatchingFlights.add(aFlight);
            assignFlight(assignMethod, null, aFlight, notOccupied);

        }
        System.out.println("unmatched Flights: " + unmatchingFlights.size());
        return resultSet;
    }

    //algorithm A-3 : First try to assign on own peer with time reduction
    //then switch to foreign peers, try to assign all yet unassigned flights with time reduction (two separate lists)
    public ArrayList<Result> algorithmAwithReduction(FlightOrderingMethod orderMethod, String assignMethod) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> allFlights = new ArrayList<>(this.listOfFlights);
        ArrayList<Flight> flightsForForeignPier = new ArrayList<>();
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        System.out.println("Algorithm A: ");
        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);
        System.out.println("Anzahl Fluege in Instanz: " + listOfFlights.size());

        //sorting flight list according sort method
        sortFlightList(orderMethod, allFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);

        for (SortingStation s : currentStations) {
            s.freeAt = new Time(-3600000);
            s.occupied = false;

        }

        for (Flight aFlight : listOfFlights) {

            ////// Try to assign flight WITH buffer reduction to own pier //////

            //get sorting station only with same pierId
            List<SortingStation> ssOnOwnPier = currentStations.stream()
                    .filter(p -> p.pierId == aFlight.pierId).collect(Collectors.toList());


            for (SortingStation ss : ssOnOwnPier) {
                ss.occupied = ss.freeAt.getTime() > aFlight.lst.getTime();
            }
            List<SortingStation> notOccupied = ssOnOwnPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on own Pier without buffer red");

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }


            //there are no more free stations in pier -> look for all free stations on foreign piers;
            flightsForForeignPier.add(aFlight);


        }
        for (Flight aFlight : flightsForForeignPier) {
            List<SortingStation> ssOnForeignPier = currentStations.stream()
                    .filter(p -> p.pierId != aFlight.pierId).collect(Collectors.toList());

            ////// Try to assign flight WITH buffer reduction to foreign pier //////

            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : ssOnForeignPier) {
                ss.occupied = ss.freeAt.getTime() >= aFlight.lst.getTime();
            }
            List<SortingStation> notOccupied = ssOnForeignPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on foreign Pier without buffer red");
                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }

            unmatchingFlights.add(aFlight);
            assignFlight(assignMethod, null, aFlight, notOccupied);

        }
        System.out.println("unmatched Flights: " + unmatchingFlights.size());
        return resultSet;
    }

    //algorithm B : First try to assign on own peer -> first without then with time reduction
    //then switch to foreign peers, try to assign the flight without then with time reduction (each flight in one list)
    public ArrayList<Result> algorithmB(FlightOrderingMethod orderMethod, String assignMethod) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> allFlights = new ArrayList<>(this.listOfFlights);
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        System.out.println("Algorithm B: ");
        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);
        System.out.println("Anzahl Fluege in Instanz: " + listOfFlights.size());

        //sorting flight list according sort method
        sortFlightList(orderMethod, allFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);

        for (SortingStation s : currentStations) {
            s.freeAt = new Time(-3600000);
            s.occupied = false;

        }

        for (Flight aFlight : listOfFlights) {

            ////// Try to assign flight WITHOUT buffer reduction to own pier //////

            //get sorting station only with same pierId
            List<SortingStation> ssOnOwnPier = currentStations.stream()
                    .filter(p -> p.pierId == aFlight.pierId).collect(Collectors.toList());


            for (SortingStation ss : ssOnOwnPier) {
                ss.occupied = ss.freeAt.getTime() > aFlight.est.getTime();
            }
            List<SortingStation> notOccupied = ssOnOwnPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on own Pier without buffer red");

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }


            ////// Try to assign flight WITH buffer reduction to own pier //////

            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : ssOnOwnPier) {
                ss.occupied = ss.freeAt.getTime() >= aFlight.lst.getTime();
            }
            notOccupied = ssOnOwnPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());

            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on own Pier with buffer red");

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);

                continue;
            }

            //Switch to foreign piers

            List<SortingStation> ssOnForeignPier = currentStations.stream()
                    .filter(p -> p.pierId != aFlight.pierId).collect(Collectors.toList());


            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : ssOnForeignPier) {

                ss.occupied = ss.freeAt.getTime() >= aFlight.est.getTime();
            }
            notOccupied = ssOnForeignPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on foreign Pier without buffer red");
                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }
            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : ssOnForeignPier) {
                ss.occupied = ss.freeAt.getTime() >= aFlight.lst.getTime();
            }

            notOccupied = ssOnForeignPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());

            if (notOccupied.size() != 0) {

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }
            unmatchingFlights.add(aFlight);
            assignFlight(assignMethod, null, aFlight, notOccupied);


        }
        System.out.println("unmatched Flights: " + unmatchingFlights.size());
        return resultSet;

    }


    //Algorithm B-2: try to assign the flight WITHOUT any time reduction at first on own peer then foreign peer
    public ArrayList<Result> algorithmBwithoutReduction(FlightOrderingMethod orderMethod, String assignMethod) {

        return algorithmOnlyWithOrWithoutReduction(orderMethod, assignMethod, true);
    }


    //Algorithm B-3: try to assign the flight WITH time reduction at first on own peer then foreign peer
    public ArrayList<Result> algorithmBwithReduction(FlightOrderingMethod orderMethod, String assignMethod) {

        return algorithmOnlyWithOrWithoutReduction(orderMethod, assignMethod, false);
    }


    //algorithm C : First try to assign the flight on any peer without then with time reduction (each flight in one list)
    public ArrayList<Result> algorithmC(FlightOrderingMethod orderMethod, String assignMethod) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> allFlights = new ArrayList<>(this.listOfFlights);
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        System.out.println("Algorithm C: ");
        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);
        System.out.println("Anzahl Fluege in Instanz: " + listOfFlights.size());

        //sorting flight list according sort method
        sortFlightList(orderMethod, allFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);
        for (SortingStation s : currentStations) {
            s.freeAt = new Time(-3600000);
            s.occupied = false;

        }

        for (Flight aFlight : listOfFlights) {

            ////// Try to assign flight WITHOUT buffer reduction to own pier //////

            //get sorting station only with same pierId
            List<SortingStation> allStations = currentStations.stream().collect(Collectors.toList());


            for (SortingStation ss : allStations) {
                ss.occupied = ss.freeAt.getTime() > aFlight.est.getTime();
            }
            List<SortingStation> notOccupied = allStations.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on any Pier without buffer red");

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }


            ////// Try to assign flight WITH buffer reduction to any pier //////

            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : allStations) {
                ss.occupied = ss.freeAt.getTime() >= aFlight.lst.getTime();
            }
            notOccupied = allStations.stream().filter(p -> !p.occupied).collect(Collectors.toList());

            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on any Pier with buffer red");

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);

                continue;
            }

            unmatchingFlights.add(aFlight);
            assignFlight(assignMethod, null, aFlight, notOccupied);


        }
        System.out.println("unmatched Flights: " + unmatchingFlights.size());
        return resultSet;

    }


    //algorithm C-2 : Try to assign the flight on any peer WITHOUT time reduction (each flight in one list)
    public ArrayList<Result> algorithmCwithoutReduction(FlightOrderingMethod orderMethod, String assignMethod) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> allFlights = new ArrayList<>(this.listOfFlights);
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        System.out.println("Algorithm C: ");
        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);
        System.out.println("Anzahl Fluege in Instanz: " + listOfFlights.size());

        //sorting flight list according sort method
        sortFlightList(orderMethod, allFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);
        for (SortingStation s : currentStations) {
            s.freeAt = new Time(-3600000);
            s.occupied = false;

        }

        for (Flight aFlight : listOfFlights) {

            ////// Try to assign flight WITHOUT buffer reduction to any pier //////

            //get sorting station only with same pierId
            List<SortingStation> allStations = currentStations.stream().collect(Collectors.toList());


            for (SortingStation ss : allStations) {
                ss.occupied = ss.freeAt.getTime() > aFlight.est.getTime();
            }
            List<SortingStation> notOccupied = allStations.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on any Pier without buffer red");

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }

            unmatchingFlights.add(aFlight);
            assignFlight(assignMethod, null, aFlight, notOccupied);


        }
        System.out.println("unmatched Flights: " + unmatchingFlights.size());
        return resultSet;

    }

    //algorithm C-3 : Try to assign the flight on any peer WITH time reduction (each flight in one list)
    public ArrayList<Result> algorithmCwithReduction(FlightOrderingMethod orderMethod, String assignMethod) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> allFlights = new ArrayList<>(this.listOfFlights);
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        System.out.println("Algorithm C: ");
        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);
        System.out.println("Anzahl Fluege in Instanz: " + listOfFlights.size());

        //sorting flight list according sort method
        sortFlightList(orderMethod, allFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);
        for (SortingStation s : currentStations) {
            s.freeAt = new Time(-3600000);
            s.occupied = false;

        }

        for (Flight aFlight : listOfFlights) {
            //get sorting station only with same pierId
            List<SortingStation> allStations = currentStations.stream().collect(Collectors.toList());

            ////// Try to assign flight WITH buffer reduction to any pier //////

            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : allStations) {
                ss.occupied = ss.freeAt.getTime() >= aFlight.lst.getTime();
            }
            List<SortingStation> notOccupied = allStations.stream().filter(p -> !p.occupied).collect(Collectors.toList());

            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on any Pier with buffer red");

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);

                continue;
            }

            unmatchingFlights.add(aFlight);
            assignFlight(assignMethod, null, aFlight, notOccupied);


        }
        System.out.println("unmatched Flights: " + unmatchingFlights.size());
        return resultSet;

    }





    public ArrayList<Result> algorithmOnlyWithOrWithoutReduction(FlightOrderingMethod orderMethod, String assignMethod, boolean without) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> allFlights = new ArrayList<>(this.listOfFlights);
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        System.out.println("Algorithm B: ");
        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);
        System.out.println("Anzahl Fluege in Instanz: " + listOfFlights.size());

        //sorting flight list according sort method
        sortFlightList(orderMethod, allFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);

        for (SortingStation s : currentStations) {
            s.freeAt = new Time(-3600000);
            s.occupied = false;
        }

        for (Flight aFlight : listOfFlights) {


            //get sorting station only with same pierId
            List<SortingStation> ssOnOwnPier = currentStations.stream()
                    .filter(p -> p.pierId == aFlight.pierId).collect(Collectors.toList());

            ////// Try to assign flight WITH/WITHOUT buffer reduction to OWN pier //////

            if (without) {
                for (SortingStation ss : ssOnOwnPier) {
                    ss.occupied = ss.freeAt.getTime() > aFlight.est.getTime();
                }
            } else {
                for (SortingStation ss : ssOnOwnPier) {
                    ss.occupied = ss.freeAt.getTime() > aFlight.lst.getTime();
                }
            }
            List<SortingStation> notOccupied = ssOnOwnPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on own Pier without buffer red");

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }

            ////// Try to assign flight WITH/WITHOUT buffer reduction to FOREIGN pier //////

            List<SortingStation> ssOnForeignPier = currentStations.stream()
                    .filter(p -> p.pierId != aFlight.pierId).collect(Collectors.toList());


            //get only free stations (ist nur eine Kopie)
            if (without) {
                for (SortingStation ss : ssOnForeignPier) {
                    ss.occupied = ss.freeAt.getTime() > aFlight.est.getTime();
                }
            } else {
                for (SortingStation ss : ssOnForeignPier) {
                    ss.occupied = ss.freeAt.getTime() > aFlight.lst.getTime();
                }
            }
            notOccupied = ssOnForeignPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on foreign Pier without buffer red");
                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }
            unmatchingFlights.add(aFlight);
            assignFlight(assignMethod, null, aFlight, notOccupied);


        }
        System.out.println("unmatched Flights: " + unmatchingFlights.size());
        return resultSet;

    }


    //Algorithm D: try to assign the flight at first to own then foreign WITHOUT any time reduction
    // then try to assign the flight at first to own then foreign WITH any time reduction (each flight in one list)
    public ArrayList<Result> algorithmD(FlightOrderingMethod orderMethod, String assignMethod) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> allFlights = new ArrayList<>(this.listOfFlights);
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        System.out.println("Algorithm B: ");
        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);
        System.out.println("Anzahl Fluege in Instanz: " + listOfFlights.size());

        //sorting flight list according sort method
        sortFlightList(orderMethod, allFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);

        for (SortingStation s : currentStations) {
            s.freeAt = new Time(-3600000);
            s.occupied = false;

        }

        for (Flight aFlight : listOfFlights) {


            //get sorting station only with same pierId
            List<SortingStation> ssOnOwnPier = currentStations.stream()
                    .filter(p -> p.pierId == aFlight.pierId).collect(Collectors.toList());

            //get sorting station only with foreign pierId
            List<SortingStation> ssOnForeignPier = currentStations.stream()
                    .filter(p -> p.pierId != aFlight.pierId).collect(Collectors.toList());


            ////// Try to assign flight WITHOUT buffer reduction to own pier //////

            for (SortingStation ss : ssOnOwnPier) {
                ss.occupied = ss.freeAt.getTime() > aFlight.est.getTime();
            }

            List<SortingStation> notOccupied = ssOnOwnPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on own Pier without buffer red");

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }


            ////// Try to assign flight WITHOUT buffer reduction to foreign pier //////

            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : ssOnForeignPier) {

                ss.occupied = ss.freeAt.getTime() >= aFlight.est.getTime();
            }
            notOccupied = ssOnForeignPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on foreign Pier without buffer red");
                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }


            ////// Try to assign flight WITH buffer reduction to own pier //////

            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : ssOnOwnPier) {
                ss.occupied = ss.freeAt.getTime() >= aFlight.lst.getTime();
            }

            notOccupied = ssOnOwnPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());

            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on own Pier with buffer red");

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);

                continue;
            }


            ////// Try to assign flight WITH buffer reduction to foreign pier //////


            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : ssOnForeignPier) {
                ss.occupied = ss.freeAt.getTime() >= aFlight.lst.getTime();
            }
            notOccupied = ssOnForeignPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());

            if (notOccupied.size() != 0) {

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }
            unmatchingFlights.add(aFlight);
            assignFlight(assignMethod, null, aFlight, notOccupied);


        }
        System.out.println("unmatched Flights: " + unmatchingFlights.size());
        return resultSet;

    }


    //algorithm E : First try to assign without time reduction -> first own then foreign peer
    //then try to assign all yet unassigned flights with  time reduction -> first own then foreign peer (two separate lists)
    public ArrayList<Result> algorithmE(FlightOrderingMethod orderMethod, String assignMethod) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> allFlights = new ArrayList<>(this.listOfFlights);
        ArrayList<Flight> flightsForForeignPier = new ArrayList<>();
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        System.out.println("Algorithm E: ");
        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);
        System.out.println("Anzahl Fluege in Instanz: " + listOfFlights.size());

        //sorting flight list according sort method
        sortFlightList(orderMethod, allFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<SortingStation>((Collection<? extends SortingStation>) this.listOfStation.clone());

        for (SortingStation s : currentStations) {
            s.freeAt = new Time(-3600000);
            s.occupied = false;

        }

        for (Flight aFlight : listOfFlights) {

            ////// Try to assign flight WITHOUT buffer reduction to own pier //////

            //get sorting station only with same pierId
            List<SortingStation> ssOnOwnPier = currentStations.stream()
                    .filter(p -> p.pierId == aFlight.pierId).collect(Collectors.toList());

            List<SortingStation> ssOnForeignPier = currentStations.stream()
                    .filter(p -> p.pierId != aFlight.pierId).collect(Collectors.toList());


            for (SortingStation ss : ssOnOwnPier) {
                ss.occupied = ss.freeAt.getTime() > aFlight.est.getTime();
            }

            List<SortingStation> notOccupied = ssOnOwnPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());

            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on own Pier without buffer red");

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }


            ////// Try to assign flight WITHOUT buffer reduction to foreign pier //////

            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : ssOnForeignPier) {
                ss.occupied = ss.freeAt.getTime() >= aFlight.est.getTime();
            }
            notOccupied = ssOnForeignPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on foreign Pier without buffer red");
                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }

            ///// There are no more free stations without time reduction /////
            //////-> look for all free station with time reduction      //////
            flightsForForeignPier.add(aFlight);


        }

        ///// try to assign all yet unassigned flights with  time reduction -> first own then foreign peers /////

        for (Flight aFlight : flightsForForeignPier) {
            List<SortingStation> ssOnOwnPier = currentStations.stream()
                    .filter(p -> p.pierId == aFlight.pierId).collect(Collectors.toList());

            List<SortingStation> ssOnForeignPier = currentStations.stream()
                    .filter(p -> p.pierId != aFlight.pierId).collect(Collectors.toList());


            ////// Try to assign flight WITH buffer reduction to own pier //////

            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : ssOnOwnPier) {
                ss.occupied = ss.freeAt.getTime() >= aFlight.lst.getTime();
            }

            List<SortingStation> notOccupied = ssOnOwnPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());

            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on own Pier with buffer red");

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);

                continue;
            }

            ////// Try to assign flight WITH buffer reduction to foreign pier //////


            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : ssOnForeignPier) {
                ss.occupied = ss.freeAt.getTime() >= aFlight.lst.getTime();
            }

            notOccupied = ssOnForeignPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());

            if (notOccupied.size() != 0) {
                System.out.print("\n\n Match on foreign Pier with buffer red");

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }
            unmatchingFlights.add(aFlight);
            assignFlight(assignMethod, null, aFlight, notOccupied);


        }
        System.out.println("unmatched Flights: " + unmatchingFlights.size());
        return resultSet;
    }

}
