import jdk.nashorn.internal.runtime.ListAdapter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Main {
    ArrayList<SortingStation> listOfSortingStations = new ArrayList<SortingStation>();
    ArrayList<Flight> listOfFlight = new ArrayList<Flight>();
    ArrayList<Pier> listOfPier = new ArrayList<Pier>();

    //importar todos los datos
    public void readFromFileStationAndFlights() {

        File file = new File("C:\\Users\\Usuario\\Documents\\Bachelorarbeit\\Bachelor\\Flights.csv");
        File file2 = new File("C:\\Users\\Usuario\\Documents\\Bachelorarbeit\\Bachelor\\SortingStations.csv");
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
                Flight aFlight = new Flight(instanceNummer, id, outbound, datum, est, stt_est, lst_est, lst, stt_lst, stt, tj, x, y,Integer.valueOf(e[58]));
                this.listOfFlight.add(aFlight);

                //System.out.println(aLine);
            }
            System.out.println("flights recorded: "+this.listOfFlight.size());

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
            //Lista para los Pierid
            ArrayList<Pier> piers = new ArrayList<>();
            ArrayList<Integer> pierIds = new ArrayList<Integer>();
            for (SortingStation s : listOfSortingStations) {
                if (!pierIds.contains(s.pierId)) {
                    pierIds.add(s.pierId);
                }
            }
            for(int i : pierIds){
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
        int instance = 3;

        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream("log.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setOut(out);

        Algorithm algorithms = new Algorithm(m.listOfSortingStations, m.listOfFlight,m.listOfPier, instance);
        ArrayList<Result> result1 = algorithms.algorithmA(Algorithm.FlightOrderingMethod.OST, "FIFO");
        ArrayList<Result> result2 = algorithms.algorithmA(Algorithm.FlightOrderingMethod.OST, "LIFO");
        ArrayList<Result> result3 = algorithms.algorithmA(Algorithm.FlightOrderingMethod.OST, "DISTANCE");
        ArrayList<Result> result4 = algorithms.algorithmA(Algorithm.FlightOrderingMethod.ODT, "FIFO");
        ArrayList<Result> result5 = algorithms.algorithmA(Algorithm.FlightOrderingMethod.ODT, "LIFO");
        ArrayList<Result> result6 = algorithms.algorithmA(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");

        ArrayList<Result> result7 = algorithms.algorithmB(Algorithm.FlightOrderingMethod.OST, "FIFO");
        ArrayList<Result> result8 = algorithms.algorithmB(Algorithm.FlightOrderingMethod.OST, "LIFO");
        ArrayList<Result> result9 = algorithms.algorithmB(Algorithm.FlightOrderingMethod.OST, "DISTANCE");
        ArrayList<Result> result10 = algorithms.algorithmB(Algorithm.FlightOrderingMethod.ODT, "FIFO");
        ArrayList<Result> result11 = algorithms.algorithmB(Algorithm.FlightOrderingMethod.ODT, "LIFO");
        ArrayList<Result> result12 = algorithms.algorithmB(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");

        ArrayList<Result> result13 = algorithms.algorithmC(Algorithm.FlightOrderingMethod.OST, "FIFO");
        ArrayList<Result> result14= algorithms.algorithmC(Algorithm.FlightOrderingMethod.OST, "LIFO");
        ArrayList<Result> result15= algorithms.algorithmC(Algorithm.FlightOrderingMethod.OST, "DISTANCE");
        ArrayList<Result> result16 = algorithms.algorithmC(Algorithm.FlightOrderingMethod.ODT, "FIFO");
        ArrayList<Result> result17 = algorithms.algorithmC(Algorithm.FlightOrderingMethod.ODT, "LIFO");
        ArrayList<Result> result18 = algorithms.algorithmC(Algorithm.FlightOrderingMethod.ODT, "DISTANCE");



        // System.out.print(2);
    }
}
