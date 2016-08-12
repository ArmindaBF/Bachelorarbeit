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
    public String currentAlgorithm;
    public String flightOrderingMethod;
    public String assignMethod; // FIFO LIFO DISDANCE


    private BufferedWriter writer; // puts all output into one file

    public BufferedWriter getWriter() {
        return writer;
    }

    public void setWriter(BufferedWriter writer) {
        if (writer != null) {
            this.writer = writer;
            try {
                //write Headline
                writer.write("Instance;" +
                        "# SS;" +
                        "Algorithm;" +
                        "FOM;" +
                        "SS-Prio;" +
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



    /**
     *  Constructor for Algorithm, filling data from Main
     * @param listOfStations
     * @param listOfFlights
     * @param listOfPier
     * @param instance
     */
    public Algorithm(ArrayList<SortingStation> listOfStations, ArrayList<Flight> listOfFlights, ArrayList<Pier> listOfPier, int instance) {
        this.listOfStation = listOfStations;
        this.listOfFlights = listOfFlights;
        this.listOfPier = listOfPier;
        this.instance = instance;

    }


    //algorithm A : First try to assign on own peer -> first without then with time reduction
    //then switch to foreign peers, try to assign all yet unassigned flights without then with time reduction (two separate lists)
    public ArrayList<Result> algorithmA(FlightOrderingMethod orderMethod, String assignMethod) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> flightsForForeignPier = new ArrayList<>();
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();


        //filter list for current instance -> only relevant flights stay in list
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);


        //sorting flight list according sort method
        ArrayList<Flight> allFlights = sortFlightList(orderMethod, listOfFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);

        //reset all stations to free and never used before
        for (SortingStation s : currentStations) {
            // -36000000 -> Time-Bug 1 hour
            s.freeAt = new Time(-3600000);
            s.occupied = false;

        }

        //check for each flight
        for (Flight aFlight : allFlights) {

            ////// Try to assign flight WITHOUT buffer reduction to own pier //////

            //get sorting station only with same pierId
            List<SortingStation> ssOnOwnPier = currentStations.stream()
                    .filter(p -> p.pierId == aFlight.pierId).collect(Collectors.toList());

            //compare each station on pier with own est and if est is after freeAt -> station is set to free
            //let it gooo let it gooooooooooooooo!

            for (SortingStation ss : ssOnOwnPier) {
                ss.occupied = ss.freeAt.getTime() > aFlight.est.getTime();
            }
            //get all free stations on own pier
            List<SortingStation> notOccupied = ssOnOwnPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            //if there is at least one free station, assign the flight to it
            if (notOccupied.size() != 0) {
                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                //jump to next flight
                continue;

            }


            ////// Try to assign flight WITH buffer reduction to own pier //////

            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : ssOnOwnPier) {
                ss.occupied = ss.freeAt.getTime() >= aFlight.lst.getTime();
            }

            notOccupied = ssOnOwnPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());

            if (notOccupied.size() != 0) {
                assignFlight(assignMethod, resultSet, aFlight, notOccupied);

            } else {

                //there are no more free stations in pier -> look for all free station on foreign piers;
                flightsForForeignPier.add(aFlight);
            }
        }

        //After checking every flight on OWN pier -> Take the remaining flights and check on their foreign piers
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

        return resultSet;
    }


    //algorithm A-2 : First try to assign on own peer without time reduction
    //then switch to foreign peers, try to assign all yet unassigned flights without time reduction (two separate lists)
    public ArrayList<Result> algorithmAwithoutReduction(FlightOrderingMethod orderMethod, String assignMethod) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> flightsForForeignPier = new ArrayList<>();
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);


        //sorting flight list according sort method
        ArrayList<Flight> allFlights = sortFlightList(orderMethod, listOfFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);

        for (SortingStation s : currentStations) {
            s.freeAt = new Time(-3600000);
            s.occupied = false;

        }

        for (Flight aFlight : allFlights) {


            ////// Try to assign flight WITHOUT buffer reduction to own pier //////

            //get sorting station only with same pierId
            List<SortingStation> ssOnOwnPier = currentStations.stream()
                    .filter(p -> p.pierId == aFlight.pierId).collect(Collectors.toList());


            for (SortingStation ss : ssOnOwnPier) {
                ss.occupied = ss.freeAt.getTime() > aFlight.est.getTime();
            }
            List<SortingStation> notOccupied = ssOnOwnPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {


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

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }

            unmatchingFlights.add(aFlight);
            assignFlight(assignMethod, null, aFlight, notOccupied);

        }

        return resultSet;
    }

    //algorithm A-3 : First try to assign on own peer with time reduction
    //then switch to foreign peers, try to assign all yet unassigned flights with time reduction (two separate lists)
    public ArrayList<Result> algorithmAwithReduction(FlightOrderingMethod orderMethod, String assignMethod) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> flightsForForeignPier = new ArrayList<>();
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);


        //sorting flight list according sort method
        ArrayList<Flight> allFlights = sortFlightList(orderMethod, listOfFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);

        for (SortingStation s : currentStations) {
            s.freeAt = new Time(-3600000);
            s.occupied = false;

        }

        for (Flight aFlight : allFlights) {


            ////// Try to assign flight WITH buffer reduction to own pier //////

            //get sorting station only with same pierId
            List<SortingStation> ssOnOwnPier = currentStations.stream()
                    .filter(p -> p.pierId == aFlight.pierId).collect(Collectors.toList());


            for (SortingStation ss : ssOnOwnPier) {
                ss.occupied = ss.freeAt.getTime() > aFlight.lst.getTime();
            }
            List<SortingStation> notOccupied = ssOnOwnPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {


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

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }

            unmatchingFlights.add(aFlight);
            assignFlight(assignMethod, null, aFlight, notOccupied);

        }

        return resultSet;
    }

    //algorithm B : First try to assign on own peer -> first without then with time reduction
    //then switch to foreign peers, try to assign the flight without then with time reduction (each flight in one list)
    public ArrayList<Result> algorithmB(FlightOrderingMethod orderMethod, String assignMethod) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);


        //sorting flight list according sort method
        ArrayList<Flight> allFlights = sortFlightList(orderMethod, listOfFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);

        for (SortingStation s : currentStations) {
            s.freeAt = new Time(-3600000);
            s.occupied = false;

        }

        for (Flight aFlight : allFlights) {

            ////// Try to assign flight WITHOUT buffer reduction to own pier //////

            //get sorting station only with same pierId
            List<SortingStation> ssOnOwnPier = currentStations.stream()
                    .filter(p -> p.pierId == aFlight.pierId).collect(Collectors.toList());


            for (SortingStation ss : ssOnOwnPier) {
                ss.occupied = ss.freeAt.getTime() > aFlight.est.getTime();
            }
            List<SortingStation> notOccupied = ssOnOwnPier.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {


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
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);


        //sorting flight list according sort method
        ArrayList<Flight> allFlights = sortFlightList(orderMethod, listOfFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);

        for (SortingStation s : currentStations) {
            s.freeAt = new Time(-3600000);
            s.occupied = false;

        }

        for (Flight aFlight : allFlights) {


            ////// Try to assign flight WITHOUT buffer reduction to any pier //////

            //get all sorting stations
            List<SortingStation> allStations = currentStations.stream().collect(Collectors.toList());


            for (SortingStation ss : allStations) {
                ss.occupied = ss.freeAt.getTime() > aFlight.est.getTime();
            }
            List<SortingStation> notOccupied = allStations.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {


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


                assignFlight(assignMethod, resultSet, aFlight, notOccupied);

                continue;
            }

            unmatchingFlights.add(aFlight);
            assignFlight(assignMethod, null, aFlight, notOccupied);


        }

        return resultSet;

    }


    //algorithm C-2 : Try to assign the flight on any peer WITHOUT time reduction (each flight in one list)
    public ArrayList<Result> algorithmCwithoutReduction(FlightOrderingMethod orderMethod, String assignMethod) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);


        //sorting flight list according sort method
        ArrayList<Flight> allFlights = sortFlightList(orderMethod, listOfFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);

        for (SortingStation s : currentStations) {
            s.freeAt = new Time(-3600000);
            s.occupied = false;

        }

        for (Flight aFlight : allFlights) {


            ////// Try to assign flight WITHOUT buffer reduction to any pier //////

            //get sorting station only with same pierId
            List<SortingStation> allStations = currentStations.stream().collect(Collectors.toList());


            for (SortingStation ss : allStations) {
                ss.occupied = ss.freeAt.getTime() > aFlight.est.getTime();
            }
            List<SortingStation> notOccupied = allStations.stream().filter(p -> !p.occupied).collect(Collectors.toList());
            if (notOccupied.size() != 0) {


                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }

            unmatchingFlights.add(aFlight);
            assignFlight(assignMethod, null, aFlight, notOccupied);


        }

        return resultSet;

    }

    //algorithm C-3 : Try to assign the flight on any peer WITH time reduction (each flight in one list)
    public ArrayList<Result> algorithmCwithReduction(FlightOrderingMethod orderMethod, String assignMethod) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);


        //sorting flight list according sort method
        ArrayList<Flight> allFlights = sortFlightList(orderMethod, listOfFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);

        for (SortingStation s : currentStations) {
            s.freeAt = new Time(-3600000);
            s.occupied = false;

        }

        for (Flight aFlight : allFlights) {
            //get sorting station only with same pierId
            List<SortingStation> allStations = currentStations.stream().collect(Collectors.toList());

            ////// Try to assign flight WITH buffer reduction to any pier //////

            //get only free stations (ist nur eine Kopie)
            for (SortingStation ss : allStations) {
                ss.occupied = ss.freeAt.getTime() >= aFlight.lst.getTime();
            }
            List<SortingStation> notOccupied = allStations.stream().filter(p -> !p.occupied).collect(Collectors.toList());

            if (notOccupied.size() != 0) {


                assignFlight(assignMethod, resultSet, aFlight, notOccupied);

                continue;
            }

            unmatchingFlights.add(aFlight);
            assignFlight(assignMethod, null, aFlight, notOccupied);


        }

        return resultSet;

    }


    public ArrayList<Result> algorithmOnlyWithOrWithoutReduction(FlightOrderingMethod orderMethod, String assignMethod, boolean without) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);


        //sorting flight list according sort method
        ArrayList<Flight> allFlights = sortFlightList(orderMethod, listOfFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);

        for (SortingStation s : currentStations) {
            s.freeAt = new Time(-3600000);
            s.occupied = false;

        }

        for (Flight aFlight : allFlights) {


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

                assignFlight(assignMethod, resultSet, aFlight, notOccupied);
                continue;

            }
            unmatchingFlights.add(aFlight);
            assignFlight(assignMethod, null, aFlight, notOccupied);


        }

        return resultSet;

    }


    //Algorithm D: try to assign the flight at first to own then foreign WITHOUT any time reduction
    // then try to assign the flight at first to own then foreign WITH any time reduction (each flight in one list)
    public ArrayList<Result> algorithmD(FlightOrderingMethod orderMethod, String assignMethod) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);


        //sorting flight list according sort method
        ArrayList<Flight> allFlights = sortFlightList(orderMethod, listOfFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);

        for (SortingStation s : currentStations) {
            s.freeAt = new Time(-3600000);
            s.occupied = false;

        }

        for (Flight aFlight : allFlights) {


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

        return resultSet;

    }


    //algorithm E : First try to assign without time reduction -> first own then foreign peer
    //then try to assign all yet unassigned flights with  time reduction -> first own then foreign peer (two separate lists)
    public ArrayList<Result> algorithmE(FlightOrderingMethod orderMethod, String assignMethod) {
        ArrayList<Result> resultSet = new ArrayList<>();
        ArrayList<Flight> flightsForForeignPier = new ArrayList<>();
        ArrayList<Flight> unmatchingFlights = new ArrayList<>();

        //instance auswaehlen
        listOfFlights.removeIf(s -> s.instanceNummer != this.instance);

        //sorting flight list according sort method
        ArrayList<Flight> allFlights = sortFlightList(orderMethod, listOfFlights);

        LinkedList<SortingStation> currentStations = new LinkedList<>(this.listOfStation);

        for (SortingStation s : currentStations) {
            s.freeAt = new Time(-3600000);
            s.occupied = false;

        }

        for (Flight aFlight : allFlights) {

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

        return resultSet;
    }


    private ArrayList<Flight> sortFlightList(FlightOrderingMethod a, ArrayList<Flight> list1) {
        if (FlightOrderingMethod.OST == a) {
            //sort by ascending values of tj ... leeet it gooo!
            Collections.sort(list1, (p1, p2) -> p1.tj.compareTo(p2.tj));
        } else if (FlightOrderingMethod.ODT == a) {
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
            //Sort free stations according to the free-at Time
            Collections.sort(notOccupied, (p1, p2) -> p1.freeAt.compareTo(p2.freeAt) == 0 ? compareInts(p1, p2) : p1.freeAt.compareTo(p2.freeAt));
            //FIFO
            switch (assignMethod) {
                case "FIFO":
                    //Take the first list element
                    currentStation = notOccupied.get(0);
                    break;
                case "LIFO":
                    //take the last uuunicoorn
                    currentStation = notOccupied.get(notOccupied.size() - 1);
                    break;
                default:
                    //get the nearest station of all free stations
                    currentStation = getNearestStation(notOccupied, aFlight);
                    //return the exact distance value
                    distance = distFrom((int) currentStation.x, (int) currentStation.y, (int) aFlight.x, (int) aFlight.y);
                    break;
            }
            //assign flight to sorting station
            currentStation.occupied = true;
            //keep old free-at time
            freed = currentStation.freeAt;
            //set new free at time
            currentStation.freeAt = aFlight.stt;

            //collect values in a result object (not really necessary)
            Result result = assignFlightToResult(resultSet, aFlight, currentStation, freed);

            //just formatting tom 00:00:00
            time = String.format("%02d:%02d:00",
                    (int) ((result.getR_j() / (1000 * 60 * 60)) % 24),
                    (int) ((result.getR_j() / (1000 * 60)) % 60)
            );


            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            long s_ij = aFlight.est.getTime() + result.getR_j();
            realStartTime = sdf.format(s_ij);

            // If flight nevr got to tenerife, we say that he has a dummy station
        } else {
            currentStation = new SortingStation(0, 0, 0, 0, new Time(-3600000));
            freed = new Time(-3600000);
            time = "00:00:00";
            realStartTime = "NEVER!!";
        }

        //check which stations are currently occupied (all)
        List<SortingStation> occupiedStations = listOfStation.stream().filter(p -> p.occupied).collect(Collectors.toList());
        String ids = "";

        //get their ids
        for (SortingStation s : occupiedStations) {
            ids += s.id + ",";
        }

        SimpleDateFormat sdf_datum = new SimpleDateFormat("dd/MM/yyyy");

        //write data of the flight as new line to the output file
        if (this.writer != null) {
            try {
                this.writer.write(this.instance + ";"
                        + listOfStation.size() + ";"
                        + currentAlgorithm + ";"
                        + flightOrderingMethod + ";"
                        + assignMethod + ";"
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

    //helpermethod haare glaetten
    private int compareInts(SortingStation p1, SortingStation p2) {
        return p1.id > p2.id ? +1 : p1.id < p2.id ? -1 : 0;
    }

}
