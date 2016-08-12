import org.apache.commons.io.FileUtils;

import java.io.*;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {
    public static final int MIN_STATIONS = 81;
    public static final int MAX_STATIONS = 120;

    public static final int MIN_INSTANCE = 7;
    public static final int MAX_INSTANCE = 11;

    public static int instance = 8;
    ArrayList<SortingStation> listOfSortingStations = new ArrayList<>();
    ArrayList<Flight> listOfFlight = new ArrayList<Flight>();
    ArrayList<Pier> listOfPier = new ArrayList<Pier>();
    public static Algorithm algorithms;
    public static final String STANDARDLOCATION_SS = "C:\\Users\\Usuario\\Documents\\Bachelorarbeit\\Bachelor\\SortingStation.csv";


    //importar todos los datos
    public void readFromFileStationAndFlights(String ssURL) {
        //At first, set following three lists as empty

        this.listOfSortingStations.clear();
        this.listOfFlight.clear();
        this.listOfPier.clear();

        File file = new File("C:\\Users\\Usuario\\Documents\\Bachelorarbeit\\Bachelor\\Flights.csv");
        try {
            List<String> l = FileUtils.readLines(file, "UTF-8");
            // System.out.println("Hello World! gfhgfhgfhd");

            //separa la lista de ; a lista einzeln
            //convierte los string en el format que necesitamos
            for (String aLine : l) {
                String[] e = aLine.split(";");
                int instanceNummer = Integer.valueOf(e[0]);
                int id = Integer.valueOf(e[1]);
                String outbound = e[2];
                Date datum = new SimpleDateFormat("dd/MM/yyyy").parse(e[3]);
                SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
                Time est = new Time(new SimpleDateFormat("HH:mm").parse(e[11]).getTime());
                Time stt_est = new Time(new SimpleDateFormat("HH:mm").parse(e[16]).getTime());
                Time lst_est = new Time(new SimpleDateFormat("HH:mm").parse(e[21]).getTime());
                Time lst = new Time(new SimpleDateFormat("HH:mm").parse(e[27]).getTime());
                Time stt_lst = new Time(new SimpleDateFormat("HH:mm").parse(e[32]).getTime());
                Time stt = new Time(new SimpleDateFormat("HH:mm").parse(e[38]).getTime());
                Time tj = new Time(new SimpleDateFormat("HH:mm").parse(e[11]).getTime());
                Double x = Double.valueOf(e[54]);
                Double y = Double.valueOf(e[55]);
                Random random = new Random();
                Flight aFlight = new Flight(instanceNummer, id, outbound, datum, est, stt_est, lst_est, lst, stt_lst, stt, tj, x, y, Integer.valueOf(e[58]));
                this.listOfFlight.add(aFlight);

                //System.out.println(aLine);
            }
            System.out.println("flights recorded: " + this.listOfFlight.size());

            getSortingStationsFromFile(ssURL);

            System.out.println("available stations: " + this.listOfSortingStations.size());
            //Lista para los Pierid
            ArrayList<Pier> piers = new ArrayList<>();
            ArrayList<Integer> pierIds = new ArrayList<Integer>();
            listOfSortingStations.stream().filter(s -> !pierIds.contains(s.pierId)).forEach(s -> {
                pierIds.add(s.pierId);
            });
            listOfPier.addAll(pierIds.stream().map(Pier::new).collect(Collectors.toList()));

            //   System.out.print(2);

        } catch (Exception e) {
            e.printStackTrace();
            e.fillInStackTrace();
        }
    }

    private void getSortingStationsFromFile(String fileURL) throws IOException {
        this.listOfSortingStations = new ArrayList<>();
        File file2 = new File(fileURL);
        List<String> l2 = FileUtils.readLines(file2, "UTF-8");
        //hace lo mismo que arriba para los flights pero para las SS
        System.out.println(l2.size());
        for (String aLine : l2) {
            String[] fields = aLine.split(";");

            try {
                int id = Integer.valueOf(fields[2]);
                double x = Double.valueOf(fields[3]);
                double y = Double.valueOf(fields[4]);
                Random random = new Random();
                SortingStation aStation = new SortingStation(id, x, y, Integer.valueOf(fields[9]), new Time(0));
                this.listOfSortingStations.add(aStation);

            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Check File " + fileURL);
                e.printStackTrace();
            }

        }
    }

    //Main
    public static void main(String[] args) {
        Main m = new Main();



        try {

            algorithms = new Algorithm(m.listOfSortingStations, m.listOfFlight, m.listOfPier, instance);

            //get ABC and A23 B23 C23 separated files
            m.ABCseparatedFiles();

            // diferentes maneras de imprimir los datos
            //Change here for your type of output

            // -> activate this line for the following two!!
            // m.readFromFileStationAndFlights(STANDARDLOCATION_SS);

            //m.allAlgorithmsInSeparateFiles();
            //m.allAlgorithmsInOneFile();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

        //for printing (no se usan todos a la vez, depende de lo que quiera imprimir)
    private void allAlgorithmsInSeparateFiles() throws IOException {

        algorithms.setWriter(getBufferedWriter("AlgorithmA", "OST", "FIFO"));
        algorithms.algorithmA(Algorithm.FlightOrderingMethod.OST, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmA", "OST", "LIFO"));
        algorithms.algorithmA(Algorithm.FlightOrderingMethod.OST, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmA", "OST", "DISTANCE"));
        algorithms.algorithmA(Algorithm.FlightOrderingMethod.OST, "DISTANCE");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmA", "ODT", "FIFO"));
        algorithms.algorithmA(Algorithm.FlightOrderingMethod.ODT, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmA", "ODT", "LIFO"));
        algorithms.algorithmA(Algorithm.FlightOrderingMethod.ODT, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmA", "ODT", "DISTANCE"));
        algorithms.algorithmA(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");
        algorithms.getWriter().close();


        algorithms.setWriter(getBufferedWriter("AlgorithmB", "OST", "FIFO"));
        algorithms.algorithmB(Algorithm.FlightOrderingMethod.OST, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmB", "OST", "LIFO"));
        algorithms.algorithmB(Algorithm.FlightOrderingMethod.OST, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmB", "OST", "DISTANCE"));
        algorithms.algorithmB(Algorithm.FlightOrderingMethod.OST, "DISTANCE");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmB", "ODT", "FIFO"));
        algorithms.algorithmB(Algorithm.FlightOrderingMethod.ODT, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmB", "ODT", "LIFO"));
        algorithms.algorithmB(Algorithm.FlightOrderingMethod.ODT, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmB", "ODT", "DISTANCE"));
        algorithms.algorithmB(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");
        algorithms.getWriter().close();

        algorithms.setWriter(getBufferedWriter("AlgorithmC", "OST", "FIFO"));
        algorithms.algorithmC(Algorithm.FlightOrderingMethod.OST, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmC", "OST", "LIFO"));
        algorithms.algorithmC(Algorithm.FlightOrderingMethod.OST, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmC", "OST", "DISTANCE"));
        algorithms.algorithmC(Algorithm.FlightOrderingMethod.OST, "DISTANCE");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmC", "ODT", "FIFO"));
        algorithms.algorithmC(Algorithm.FlightOrderingMethod.ODT, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmC", "ODT", "LIFO"));
        algorithms.algorithmC(Algorithm.FlightOrderingMethod.ODT, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmC", "ODT", "DISTANCE"));
        algorithms.algorithmC(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");
        algorithms.getWriter().close();


        algorithms.setWriter(getBufferedWriter("AlgorithmD", "OST", "FIFO"));
        algorithms.algorithmD(Algorithm.FlightOrderingMethod.OST, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmD", "OST", "LIFO"));
        algorithms.algorithmD(Algorithm.FlightOrderingMethod.OST, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmD", "OST", "DISTANCE"));
        algorithms.algorithmD(Algorithm.FlightOrderingMethod.OST, "DISTANCE");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmD", "ODT", "FIFO"));
        algorithms.algorithmD(Algorithm.FlightOrderingMethod.ODT, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmD", "ODT", "LIFO"));
        algorithms.algorithmD(Algorithm.FlightOrderingMethod.ODT, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmD", "ODT", "DISTANCE"));
        algorithms.algorithmD(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");
        algorithms.getWriter().close();


        algorithms.setWriter(getBufferedWriter("AlgorithmE", "OST", "FIFO"));
        algorithms.algorithmE(Algorithm.FlightOrderingMethod.OST, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmE", "OST", "LIFO"));
        algorithms.algorithmE(Algorithm.FlightOrderingMethod.OST, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmE", "OST", "DISTANCE"));
        algorithms.algorithmE(Algorithm.FlightOrderingMethod.OST, "DISTANCE");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmE", "ODT", "FIFO"));
        algorithms.algorithmE(Algorithm.FlightOrderingMethod.ODT, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmE", "ODT", "LIFO"));
        algorithms.algorithmE(Algorithm.FlightOrderingMethod.ODT, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmE", "ODT", "DISTANCE"));
        algorithms.algorithmE(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");
        algorithms.getWriter().close();

        algorithms.setWriter(getBufferedWriter("algorithmA-2", "OST", "FIFO"));
        algorithms.algorithmAwithoutReduction(Algorithm.FlightOrderingMethod.OST, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("algorithmA-2", "OST", "LIFO"));
        algorithms.algorithmAwithoutReduction(Algorithm.FlightOrderingMethod.OST, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("algorithmA-2", "OST", "DISTANCE"));
        algorithms.algorithmAwithoutReduction(Algorithm.FlightOrderingMethod.OST, "DISTANCE");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("algorithmA-2", "ODT", "FIFO"));
        algorithms.algorithmAwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("algorithmA-2", "ODT", "LIFO"));
        algorithms.algorithmAwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("algorithmA-2", "ODT", "DISTANCE"));
        algorithms.algorithmAwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");
        algorithms.getWriter().close();

        algorithms.setWriter(getBufferedWriter("algorithmB-2", "OST", "FIFO"));
        algorithms.algorithmBwithoutReduction(Algorithm.FlightOrderingMethod.OST, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("algorithmB-2", "OST", "LIFO"));
        algorithms.algorithmBwithoutReduction(Algorithm.FlightOrderingMethod.OST, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("algorithmB-2", "OST", "DISTANCE"));
        algorithms.algorithmBwithoutReduction(Algorithm.FlightOrderingMethod.OST, "DISTANCE");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("algorithmB-2", "ODT", "FIFO"));
        algorithms.algorithmBwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("algorithmB-2", "ODT", "LIFO"));
        algorithms.algorithmBwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("algorithmB-2", "ODT", "DISTANCE"));
        algorithms.algorithmBwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");
        algorithms.getWriter().close();

        algorithms.setWriter(getBufferedWriter("algorithmC-2", "OST", "FIFO"));
        algorithms.algorithmCwithoutReduction(Algorithm.FlightOrderingMethod.OST, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("algorithmC-2", "OST", "LIFO"));
        algorithms.algorithmCwithoutReduction(Algorithm.FlightOrderingMethod.OST, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("algorithmC-2", "OST", "DISTANCE"));
        algorithms.algorithmCwithoutReduction(Algorithm.FlightOrderingMethod.OST, "DISTANCE");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("algorithmC-2", "ODT", "FIFO"));
        algorithms.algorithmCwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("algorithmC-2", "ODT", "LIFO"));
        algorithms.algorithmCwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("algorithmC-2", "ODT", "DISTANCE"));
        algorithms.algorithmCwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");
        algorithms.getWriter().close();


        algorithms.setWriter(getBufferedWriter("AlgorithmA-3", "OST", "FIFO"));
        algorithms.algorithmAwithReduction(Algorithm.FlightOrderingMethod.OST, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmA-3", "OST", "LIFO"));
        algorithms.algorithmAwithReduction(Algorithm.FlightOrderingMethod.OST, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmA-3", "OST", "DISTANCE"));
        algorithms.algorithmAwithReduction(Algorithm.FlightOrderingMethod.OST, "DISTANCE");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmA-3", "ODT", "FIFO"));
        algorithms.algorithmAwithReduction(Algorithm.FlightOrderingMethod.ODT, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmA-3", "ODT", "LIFO"));
        algorithms.algorithmAwithReduction(Algorithm.FlightOrderingMethod.ODT, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmA-3", "ODT", "DISTANCE"));
        algorithms.algorithmAwithReduction(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");
        algorithms.getWriter().close();


        algorithms.setWriter(getBufferedWriter("AlgorithmB-3", "OST", "FIFO"));
        algorithms.algorithmBwithReduction(Algorithm.FlightOrderingMethod.OST, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmB-3", "OST", "LIFO"));
        algorithms.algorithmBwithReduction(Algorithm.FlightOrderingMethod.OST, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmB-3", "OST", "DISTANCE"));
        algorithms.algorithmBwithReduction(Algorithm.FlightOrderingMethod.OST, "DISTANCE");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmB-3", "ODT", "FIFO"));
        algorithms.algorithmBwithReduction(Algorithm.FlightOrderingMethod.ODT, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmB-3", "ODT", "LIFO"));
        algorithms.algorithmBwithReduction(Algorithm.FlightOrderingMethod.ODT, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmB-3", "ODT", "DISTANCE"));
        algorithms.algorithmBwithReduction(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");
        algorithms.getWriter().close();


        algorithms.setWriter(getBufferedWriter("AlgorithmC-3", "OST", "FIFO"));
        algorithms.algorithmCwithReduction(Algorithm.FlightOrderingMethod.OST, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmC-3", "OST", "LIFO"));
        algorithms.algorithmCwithReduction(Algorithm.FlightOrderingMethod.OST, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmC-3", "OST", "DISTANCE"));
        algorithms.algorithmCwithReduction(Algorithm.FlightOrderingMethod.OST, "DISTANCE");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmC-3", "ODT", "FIFO"));
        algorithms.algorithmCwithReduction(Algorithm.FlightOrderingMethod.ODT, "FIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmC-3", "ODT", "LIFO"));
        algorithms.algorithmCwithReduction(Algorithm.FlightOrderingMethod.ODT, "LIFO");
        algorithms.getWriter().close();
        algorithms.setWriter(getBufferedWriter("AlgorithmC-3", "ODT", "DISTANCE"));
        algorithms.algorithmCwithReduction(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");
        algorithms.getWriter().close();
    }

    private void allAlgorithmsInOneFile() throws IOException {
        for (int i = MIN_INSTANCE; i <= MAX_INSTANCE; i++) {
            algorithms.instance = i;
            algorithms.setWriter(getBufferedWriter("Instance_" + algorithms.instance, "AllFOM", "AllPrios"));

            for (int j = MIN_STATIONS; j <= MAX_STATIONS; j++) {
                System.out.println("Start file " + j + " on instance " + i + "...");
                readFromFileStationAndFlights("C:\\Users\\Usuario\\Desktop\\Bachelorarbeit UNI 09.08.2016\\Daten 22.07.16\\SS\\CSV\\" + j + " SS.csv");
                algorithms.listOfStation = this.listOfSortingStations;
                algorithms.listOfFlights = this.listOfFlight;
                algorithms.listOfPier = this.listOfPier;
                writeAlgos();
            }

            algorithms.getWriter().close();
        }
    }

    private void ABCseparatedFiles() throws IOException {
        for (int i = MIN_INSTANCE; i <= MAX_INSTANCE; i++) {
            algorithms.instance = i;

            //start new file for current instance
            algorithms.setWriter(getBufferedWriter("nonABC_Instance_" + algorithms.instance, "AllFOM", "AllPrios"));
            for (int j = MIN_STATIONS; j <= MAX_STATIONS; j++) {
                System.out.println("Start file " + j + " on instance " + i + "...");
                readFromFileStationAndFlights("C:\\Users\\Usuario\\Documents\\Bachelorarbeit\\Bachelor\\CSV\\" + j + " SS.csv");
                algorithms.listOfStation = this.listOfSortingStations;
                algorithms.listOfFlights = this.listOfFlight;
                algorithms.listOfPier = this.listOfPier;
                writeAlgosNotABC();
                //allAlgorithmsInSeparateFiles();
            }
            //finish file for current instance
            algorithms.getWriter().close();
        }

        for (int i = MIN_INSTANCE; i <= MAX_INSTANCE; i++) {
            algorithms.instance = i;
            algorithms.setWriter(getBufferedWriter("ABC_Instance_" + algorithms.instance, "AllFOM", "AllPrios"));

            for (int j = MIN_STATIONS; j <= MAX_STATIONS; j++) {
                System.out.println("Start file " + j + " on instance " + i + "...");
                readFromFileStationAndFlights("C:\\Users\\Usuario\\Documents\\Bachelorarbeit\\Bachelor\\CSV\\" + j + " SS.csv");
                algorithms.listOfStation = this.listOfSortingStations;
                algorithms.listOfFlights = this.listOfFlight;
                algorithms.listOfPier = this.listOfPier;
                writeAlgosABC();
            }

            algorithms.getWriter().close();
        }
    }

    private static void writeAlgosABC() {
        setAlgoData("AlgorithmA", "OST", "FIFO");
        algorithms.algorithmA(Algorithm.FlightOrderingMethod.OST, "FIFO");

        setAlgoData("AlgorithmA", "OST", "LIFO");
        algorithms.algorithmA(Algorithm.FlightOrderingMethod.OST, "LIFO");

        setAlgoData("AlgorithmA", "OST", "DISTANCE");
        algorithms.algorithmA(Algorithm.FlightOrderingMethod.OST, "DISTANCE");

        setAlgoData("AlgorithmA", "ODT", "FIFO");
        algorithms.algorithmA(Algorithm.FlightOrderingMethod.ODT, "FIFO");

        setAlgoData("AlgorithmA", "ODT", "LIFO");
        algorithms.algorithmA(Algorithm.FlightOrderingMethod.ODT, "LIFO");

        setAlgoData("AlgorithmA", "ODT", "DISTANCE");
        algorithms.algorithmA(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");


        setAlgoData("AlgorithmB", "OST", "FIFO");
        algorithms.algorithmB(Algorithm.FlightOrderingMethod.OST, "FIFO");

        setAlgoData("AlgorithmB", "OST", "LIFO");
        algorithms.algorithmB(Algorithm.FlightOrderingMethod.OST, "LIFO");

        setAlgoData("AlgorithmB", "OST", "DISTANCE");
        algorithms.algorithmB(Algorithm.FlightOrderingMethod.OST, "DISTANCE");

        setAlgoData("AlgorithmB", "ODT", "FIFO");
        algorithms.algorithmB(Algorithm.FlightOrderingMethod.ODT, "FIFO");

        setAlgoData("AlgorithmB", "ODT", "LIFO");
        algorithms.algorithmB(Algorithm.FlightOrderingMethod.ODT, "LIFO");

        setAlgoData("AlgorithmB", "ODT", "DISTANCE");
        algorithms.algorithmB(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");


        setAlgoData("AlgorithmC", "OST", "FIFO");
        algorithms.algorithmC(Algorithm.FlightOrderingMethod.OST, "FIFO");

        setAlgoData("AlgorithmC", "OST", "LIFO");
        algorithms.algorithmC(Algorithm.FlightOrderingMethod.OST, "LIFO");

        setAlgoData("AlgorithmC", "OST", "DISTANCE");
        algorithms.algorithmC(Algorithm.FlightOrderingMethod.OST, "DISTANCE");

        setAlgoData("AlgorithmC", "ODT", "FIFO");
        algorithms.algorithmC(Algorithm.FlightOrderingMethod.ODT, "FIFO");

        setAlgoData("AlgorithmC", "ODT", "LIFO");
        algorithms.algorithmC(Algorithm.FlightOrderingMethod.ODT, "LIFO");

        setAlgoData("AlgorithmC", "ODT", "DISTANCE");
        algorithms.algorithmC(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");
    }
    private static void writeAlgosNotABC() {

        setAlgoData("algorithmA-2", "OST", "FIFO");
        algorithms.algorithmAwithoutReduction(Algorithm.FlightOrderingMethod.OST, "FIFO");

        setAlgoData("algorithmA-2", "OST", "LIFO");
        algorithms.algorithmAwithoutReduction(Algorithm.FlightOrderingMethod.OST, "LIFO");

        setAlgoData("algorithmA-2", "OST", "DISTANCE");
        algorithms.algorithmAwithoutReduction(Algorithm.FlightOrderingMethod.OST, "DISTANCE");

        setAlgoData("algorithmA-2", "ODT", "FIFO");
        algorithms.algorithmAwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "FIFO");

        setAlgoData("algorithmA-2", "ODT", "LIFO");
        algorithms.algorithmAwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "LIFO");

        setAlgoData("algorithmA-2", "ODT", "DISTANCE");
        algorithms.algorithmAwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");


        setAlgoData("algorithmB-2", "OST", "FIFO");
        algorithms.algorithmBwithoutReduction(Algorithm.FlightOrderingMethod.OST, "FIFO");

        setAlgoData("algorithmB-2", "OST", "LIFO");
        algorithms.algorithmBwithoutReduction(Algorithm.FlightOrderingMethod.OST, "LIFO");

        setAlgoData("algorithmB-2", "OST", "DISTANCE");
        algorithms.algorithmBwithoutReduction(Algorithm.FlightOrderingMethod.OST, "DISTANCE");

        setAlgoData("algorithmB-2", "ODT", "FIFO");
        algorithms.algorithmBwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "FIFO");

        setAlgoData("algorithmB-2", "ODT", "LIFO");
        algorithms.algorithmBwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "LIFO");

        setAlgoData("algorithmB-2", "ODT", "DISTANCE");
        algorithms.algorithmBwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");


        setAlgoData("algorithmC-2", "OST", "FIFO");
        algorithms.algorithmCwithoutReduction(Algorithm.FlightOrderingMethod.OST, "FIFO");

        setAlgoData("algorithmC-2", "OST", "LIFO");
        algorithms.algorithmCwithoutReduction(Algorithm.FlightOrderingMethod.OST, "LIFO");

        setAlgoData("algorithmC-2", "OST", "DISTANCE");
        algorithms.algorithmCwithoutReduction(Algorithm.FlightOrderingMethod.OST, "DISTANCE");

        setAlgoData("algorithmC-2", "ODT", "FIFO");
        algorithms.algorithmCwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "FIFO");

        setAlgoData("algorithmC-2", "ODT", "LIFO");
        algorithms.algorithmCwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "LIFO");

        setAlgoData("algorithmC-2", "ODT", "DISTANCE");
        algorithms.algorithmCwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");


        setAlgoData("AlgorithmA-3", "OST", "FIFO");
        algorithms.algorithmAwithReduction(Algorithm.FlightOrderingMethod.OST, "FIFO");

        setAlgoData("AlgorithmA-3", "OST", "LIFO");
        algorithms.algorithmAwithReduction(Algorithm.FlightOrderingMethod.OST, "LIFO");

        setAlgoData("AlgorithmA-3", "OST", "DISTANCE");
        algorithms.algorithmAwithReduction(Algorithm.FlightOrderingMethod.OST, "DISTANCE");

        setAlgoData("AlgorithmA-3", "ODT", "FIFO");
        algorithms.algorithmAwithReduction(Algorithm.FlightOrderingMethod.ODT, "FIFO");

        setAlgoData("AlgorithmA-3", "ODT", "LIFO");
        algorithms.algorithmAwithReduction(Algorithm.FlightOrderingMethod.ODT, "LIFO");

        setAlgoData("AlgorithmA-3", "ODT", "DISTANCE");
        algorithms.algorithmAwithReduction(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");


        setAlgoData("AlgorithmB-3", "OST", "FIFO");
        algorithms.algorithmBwithReduction(Algorithm.FlightOrderingMethod.OST, "FIFO");

        setAlgoData("AlgorithmB-3", "OST", "LIFO");
        algorithms.algorithmBwithReduction(Algorithm.FlightOrderingMethod.OST, "LIFO");

        setAlgoData("AlgorithmB-3", "OST", "DISTANCE");
        algorithms.algorithmBwithReduction(Algorithm.FlightOrderingMethod.OST, "DISTANCE");

        setAlgoData("AlgorithmB-3", "ODT", "FIFO");
        algorithms.algorithmBwithReduction(Algorithm.FlightOrderingMethod.ODT, "FIFO");

        setAlgoData("AlgorithmB-3", "ODT", "LIFO");
        algorithms.algorithmBwithReduction(Algorithm.FlightOrderingMethod.ODT, "LIFO");

        setAlgoData("AlgorithmB-3", "ODT", "DISTANCE");
        algorithms.algorithmBwithReduction(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");


        setAlgoData("AlgorithmC-3", "OST", "FIFO");
        algorithms.algorithmCwithReduction(Algorithm.FlightOrderingMethod.OST, "FIFO");

        setAlgoData("AlgorithmC-3", "OST", "LIFO");
        algorithms.algorithmCwithReduction(Algorithm.FlightOrderingMethod.OST, "LIFO");

        setAlgoData("AlgorithmC-3", "OST", "DISTANCE");
        algorithms.algorithmCwithReduction(Algorithm.FlightOrderingMethod.OST, "DISTANCE");

        setAlgoData("AlgorithmC-3", "ODT", "FIFO");
        algorithms.algorithmCwithReduction(Algorithm.FlightOrderingMethod.ODT, "FIFO");

        setAlgoData("AlgorithmC-3", "ODT", "LIFO");
        algorithms.algorithmCwithReduction(Algorithm.FlightOrderingMethod.ODT, "LIFO");

        setAlgoData("AlgorithmC-3", "ODT", "DISTANCE");
        algorithms.algorithmCwithReduction(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");
    }

    private static void writeAlgos() {
        setAlgoData("AlgorithmA", "OST", "FIFO");
        algorithms.algorithmA(Algorithm.FlightOrderingMethod.OST, "FIFO");

        setAlgoData("AlgorithmA", "OST", "LIFO");
        algorithms.algorithmA(Algorithm.FlightOrderingMethod.OST, "LIFO");

        setAlgoData("AlgorithmA", "OST", "DISTANCE");
        algorithms.algorithmA(Algorithm.FlightOrderingMethod.OST, "DISTANCE");

        setAlgoData("AlgorithmA", "ODT", "FIFO");
        algorithms.algorithmA(Algorithm.FlightOrderingMethod.ODT, "FIFO");

        setAlgoData("AlgorithmA", "ODT", "LIFO");
        algorithms.algorithmA(Algorithm.FlightOrderingMethod.ODT, "LIFO");

        setAlgoData("AlgorithmA", "ODT", "DISTANCE");
        algorithms.algorithmA(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");


        setAlgoData("AlgorithmB", "OST", "FIFO");
        algorithms.algorithmB(Algorithm.FlightOrderingMethod.OST, "FIFO");

        setAlgoData("AlgorithmB", "OST", "LIFO");
        algorithms.algorithmB(Algorithm.FlightOrderingMethod.OST, "LIFO");

        setAlgoData("AlgorithmB", "OST", "DISTANCE");
        algorithms.algorithmB(Algorithm.FlightOrderingMethod.OST, "DISTANCE");

        setAlgoData("AlgorithmB", "ODT", "FIFO");
        algorithms.algorithmB(Algorithm.FlightOrderingMethod.ODT, "FIFO");

        setAlgoData("AlgorithmB", "ODT", "LIFO");
        algorithms.algorithmB(Algorithm.FlightOrderingMethod.ODT, "LIFO");

        setAlgoData("AlgorithmB", "ODT", "DISTANCE");
        algorithms.algorithmB(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");


        setAlgoData("AlgorithmC", "OST", "FIFO");
        algorithms.algorithmC(Algorithm.FlightOrderingMethod.OST, "FIFO");

        setAlgoData("AlgorithmC", "OST", "LIFO");
        algorithms.algorithmC(Algorithm.FlightOrderingMethod.OST, "LIFO");

        setAlgoData("AlgorithmC", "OST", "DISTANCE");
        algorithms.algorithmC(Algorithm.FlightOrderingMethod.OST, "DISTANCE");

        setAlgoData("AlgorithmC", "ODT", "FIFO");
        algorithms.algorithmC(Algorithm.FlightOrderingMethod.ODT, "FIFO");

        setAlgoData("AlgorithmC", "ODT", "LIFO");
        algorithms.algorithmC(Algorithm.FlightOrderingMethod.ODT, "LIFO");

        setAlgoData("AlgorithmC", "ODT", "DISTANCE");
        algorithms.algorithmC(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");

//
//        setAlgoData("AlgorithmD", "OST", "FIFO");
//        algorithms.algorithmD(Algorithm.FlightOrderingMethod.OST, "FIFO");
//
//        setAlgoData("AlgorithmD", "OST", "LIFO");
//        algorithms.algorithmD(Algorithm.FlightOrderingMethod.OST, "LIFO");
//
//        setAlgoData("AlgorithmD", "OST", "DISTANCE");
//        algorithms.algorithmD(Algorithm.FlightOrderingMethod.OST, "DISTANCE");
//
//        setAlgoData("AlgorithmD", "ODT", "FIFO");
//        algorithms.algorithmD(Algorithm.FlightOrderingMethod.ODT, "FIFO");
//
//        setAlgoData("AlgorithmD", "ODT", "LIFO");
//        algorithms.algorithmD(Algorithm.FlightOrderingMethod.ODT, "LIFO");
//
//        setAlgoData("AlgorithmD", "ODT", "DISTANCE");
//        algorithms.algorithmD(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");
//
//
//        setAlgoData("AlgorithmE", "OST", "FIFO");
//        algorithms.algorithmE(Algorithm.FlightOrderingMethod.OST, "FIFO");
//
//        setAlgoData("AlgorithmE", "OST", "LIFO");
//        algorithms.algorithmE(Algorithm.FlightOrderingMethod.OST, "LIFO");
//
//        setAlgoData("AlgorithmE", "OST", "DISTANCE");
//        algorithms.algorithmE(Algorithm.FlightOrderingMethod.OST, "DISTANCE");
//
//        setAlgoData("AlgorithmE", "ODT", "FIFO");
//        algorithms.algorithmE(Algorithm.FlightOrderingMethod.ODT, "FIFO");
//
//        setAlgoData("AlgorithmE", "ODT", "LIFO");
//        algorithms.algorithmE(Algorithm.FlightOrderingMethod.ODT, "LIFO");
//
//        setAlgoData("AlgorithmE", "ODT", "DISTANCE");
//        algorithms.algorithmE(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");


        setAlgoData("algorithmA-2", "OST", "FIFO");
        algorithms.algorithmAwithoutReduction(Algorithm.FlightOrderingMethod.OST, "FIFO");

        setAlgoData("algorithmA-2", "OST", "LIFO");
        algorithms.algorithmAwithoutReduction(Algorithm.FlightOrderingMethod.OST, "LIFO");

        setAlgoData("algorithmA-2", "OST", "DISTANCE");
        algorithms.algorithmAwithoutReduction(Algorithm.FlightOrderingMethod.OST, "DISTANCE");

        setAlgoData("algorithmA-2", "ODT", "FIFO");
        algorithms.algorithmAwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "FIFO");

        setAlgoData("algorithmA-2", "ODT", "LIFO");
        algorithms.algorithmAwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "LIFO");

        setAlgoData("algorithmA-2", "ODT", "DISTANCE");
        algorithms.algorithmAwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");


        setAlgoData("algorithmB-2", "OST", "FIFO");
        algorithms.algorithmBwithoutReduction(Algorithm.FlightOrderingMethod.OST, "FIFO");

        setAlgoData("algorithmB-2", "OST", "LIFO");
        algorithms.algorithmBwithoutReduction(Algorithm.FlightOrderingMethod.OST, "LIFO");

        setAlgoData("algorithmB-2", "OST", "DISTANCE");
        algorithms.algorithmBwithoutReduction(Algorithm.FlightOrderingMethod.OST, "DISTANCE");

        setAlgoData("algorithmB-2", "ODT", "FIFO");
        algorithms.algorithmBwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "FIFO");

        setAlgoData("algorithmB-2", "ODT", "LIFO");
        algorithms.algorithmBwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "LIFO");

        setAlgoData("algorithmB-2", "ODT", "DISTANCE");
        algorithms.algorithmBwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");


        setAlgoData("algorithmC-2", "OST", "FIFO");
        algorithms.algorithmCwithoutReduction(Algorithm.FlightOrderingMethod.OST, "FIFO");

        setAlgoData("algorithmC-2", "OST", "LIFO");
        algorithms.algorithmCwithoutReduction(Algorithm.FlightOrderingMethod.OST, "LIFO");

        setAlgoData("algorithmC-2", "OST", "DISTANCE");
        algorithms.algorithmCwithoutReduction(Algorithm.FlightOrderingMethod.OST, "DISTANCE");

        setAlgoData("algorithmC-2", "ODT", "FIFO");
        algorithms.algorithmCwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "FIFO");

        setAlgoData("algorithmC-2", "ODT", "LIFO");
        algorithms.algorithmCwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "LIFO");

        setAlgoData("algorithmC-2", "ODT", "DISTANCE");
        algorithms.algorithmCwithoutReduction(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");


        setAlgoData("AlgorithmA-3", "OST", "FIFO");
        algorithms.algorithmAwithReduction(Algorithm.FlightOrderingMethod.OST, "FIFO");

        setAlgoData("AlgorithmA-3", "OST", "LIFO");
        algorithms.algorithmAwithReduction(Algorithm.FlightOrderingMethod.OST, "LIFO");

        setAlgoData("AlgorithmA-3", "OST", "DISTANCE");
        algorithms.algorithmAwithReduction(Algorithm.FlightOrderingMethod.OST, "DISTANCE");

        setAlgoData("AlgorithmA-3", "ODT", "FIFO");
        algorithms.algorithmAwithReduction(Algorithm.FlightOrderingMethod.ODT, "FIFO");

        setAlgoData("AlgorithmA-3", "ODT", "LIFO");
        algorithms.algorithmAwithReduction(Algorithm.FlightOrderingMethod.ODT, "LIFO");

        setAlgoData("AlgorithmA-3", "ODT", "DISTANCE");
        algorithms.algorithmAwithReduction(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");


        setAlgoData("AlgorithmB-3", "OST", "FIFO");
        algorithms.algorithmBwithReduction(Algorithm.FlightOrderingMethod.OST, "FIFO");

        setAlgoData("AlgorithmB-3", "OST", "LIFO");
        algorithms.algorithmBwithReduction(Algorithm.FlightOrderingMethod.OST, "LIFO");

        setAlgoData("AlgorithmB-3", "OST", "DISTANCE");
        algorithms.algorithmBwithReduction(Algorithm.FlightOrderingMethod.OST, "DISTANCE");

        setAlgoData("AlgorithmB-3", "ODT", "FIFO");
        algorithms.algorithmBwithReduction(Algorithm.FlightOrderingMethod.ODT, "FIFO");

        setAlgoData("AlgorithmB-3", "ODT", "LIFO");
        algorithms.algorithmBwithReduction(Algorithm.FlightOrderingMethod.ODT, "LIFO");

        setAlgoData("AlgorithmB-3", "ODT", "DISTANCE");
        algorithms.algorithmBwithReduction(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");


        setAlgoData("AlgorithmC-3", "OST", "FIFO");
        algorithms.algorithmCwithReduction(Algorithm.FlightOrderingMethod.OST, "FIFO");

        setAlgoData("AlgorithmC-3", "OST", "LIFO");
        algorithms.algorithmCwithReduction(Algorithm.FlightOrderingMethod.OST, "LIFO");

        setAlgoData("AlgorithmC-3", "OST", "DISTANCE");
        algorithms.algorithmCwithReduction(Algorithm.FlightOrderingMethod.OST, "DISTANCE");

        setAlgoData("AlgorithmC-3", "ODT", "FIFO");
        algorithms.algorithmCwithReduction(Algorithm.FlightOrderingMethod.ODT, "FIFO");

        setAlgoData("AlgorithmC-3", "ODT", "LIFO");
        algorithms.algorithmCwithReduction(Algorithm.FlightOrderingMethod.ODT, "LIFO");

        setAlgoData("AlgorithmC-3", "ODT", "DISTANCE");
        algorithms.algorithmCwithReduction(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");
    }

    //this is needed for the first three columns
    private static void setAlgoData(String algoName, String fom, String assignMethod) {
        algorithms.currentAlgorithm = algoName;
        algorithms.flightOrderingMethod = fom;
        algorithms.assignMethod = assignMethod;
    }


    //this method creates a writer for writing into a file --> exportiert alles

    private static BufferedWriter getBufferedWriter(String algoName, String orderingMethod, String assignMethod) throws FileNotFoundException {
        setAlgoData(algoName, orderingMethod, assignMethod);
        File csv = new File("C:\\Users\\Usuario\\Documents\\Bachelorarbeit\\Bachelor\\algorithmResults-Separated\\" + algoName + "_" + orderingMethod + "_" + assignMethod + ".csv");
        FileOutputStream fos = new FileOutputStream(csv);
        return new BufferedWriter(new OutputStreamWriter(fos));
    }
}
