import org.apache.commons.io.FileUtils;

import java.io.*;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Main {
    public static final int INSTANCE = 8;
    ArrayList<SortingStation> listOfSortingStations = new ArrayList<SortingStation>();
    ArrayList<Flight> listOfFlight = new ArrayList<Flight>();
    ArrayList<Pier> listOfPier = new ArrayList<Pier>();

    //importar todos los datos
    public void readFromFileStationAndFlights() {

        File file = new File("C:\\Users\\Usuario\\Documents\\Bachelorarbeit\\Bachelor\\Flights.csv");
        File file2 = new File("C:\\Users\\Usuario\\Documents\\Bachelorarbeit\\Bachelor\\SortingStation.csv");
        try {
            List<String> l = FileUtils.readLines(file, "UTF-8");
            List<String> l2 = FileUtils.readLines(file2, "UTF-8");
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

            //hace lo mismo que arriba para los flights pero para las SS
            for (String aLine : l2) {
                String[] e = aLine.split(";");
                int id = Integer.valueOf(e[2]);
                double x = Double.valueOf(e[3]);
                double y = Double.valueOf(e[4]);
                Random random = new Random();
                SortingStation aStation = new SortingStation(id, x, y, Integer.valueOf(e[9]), new Time(0));
                this.listOfSortingStations.add(aStation);

            }
            System.out.println("available stations: " + this.listOfSortingStations.size());
            //Lista para los Pierid
            ArrayList<Pier> piers = new ArrayList<>();
            ArrayList<Integer> pierIds = new ArrayList<Integer>();
            for (SortingStation s : listOfSortingStations) {
                if (!pierIds.contains(s.pierId)) {
                    pierIds.add(s.pierId);
                }
            }
            for (int i : pierIds) {
                listOfPier.add(new Pier(i));
            }

            //   System.out.print(2);

        } catch (Exception e) {
            e.printStackTrace();
            e.fillInStackTrace();
        }
    }

    //Main
    public static void main(String[] args) {
        Main m = new Main();
        m.readFromFileStationAndFlights();

        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream("log.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setOut(out);

        try {

            Algorithm algorithms = new Algorithm(m.listOfSortingStations, m.listOfFlight, m.listOfPier, INSTANCE);
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
            
        } catch (IOException e) {
            e.printStackTrace();
        }


        // System.out.print(2);
    }

    private static BufferedWriter getBufferedWriter(String algoName, String orderingMethod, String assignMethod) throws FileNotFoundException {
        File csv = new File("C:\\Users\\Usuario\\Documents\\Bachelorarbeit\\Bachelor\\algorithmResults\\"+algoName + "_" + orderingMethod + "_" + assignMethod + ".csv");
        FileOutputStream fos = new FileOutputStream(csv);
        return new BufferedWriter(new OutputStreamWriter(fos));
    }
}
