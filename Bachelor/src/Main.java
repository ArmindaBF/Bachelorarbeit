import jdk.nashorn.internal.runtime.ListAdapter;
import org.apache.commons.io.FileUtils;

import java.io.File;
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


    public void readFromFileStationAndFlights() {
        File file = new File("/home/vladimir/test/2/Bachelorarbeit/Bachelor/Flights.csv");
        File file2 = new File("/home/vladimir/test/2/Bachelorarbeit/Bachelor/SortingStations.csv");
        try {
            List<String> l = FileUtils.readLines(file, "UTF-8");
            List<String> l2 = FileUtils.readLines(file2, "UTF-8");
            System.out.println("Hello World! gfhgfhgfhd");

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
                Time tj = new Time(new SimpleDateFormat("HH:mm").parse(e[48]).getTime());
                Double x = Double.valueOf(e[54]);
                Double y = Double.valueOf(e[55]);
                Random random= new Random();
                Flight aFlight = new Flight(instanceNummer, id, outbound, datum, est, stt_est, lst_est, lst, stt_lst, stt,tj, x, y,random.nextInt(10));
                this.listOfFlight.add(aFlight);

                //System.out.println(aLine);
            }
            for (String aLine : l2) {
                String[] e = aLine.split(";");
                int id = Integer.valueOf(e[2]);
                double x = Double.valueOf(e[3]);
                double y = Double.valueOf(e[4]);
                Random random = new Random();
                SortingStation aStation = new SortingStation(id, x, y,random.nextInt(10));
                this.listOfSortingStations.add(aStation);

            }
            ArrayList<Pier> piers = new ArrayList<>();
            ArrayList<Integer> pierIds= new ArrayList<Integer>();
            for (SortingStation s : listOfSortingStations){
                if(pierIds.contains(s.pierId)){

                }else{
                    pierIds.add(s.pierId);
                }
            }
            for (int i :pierIds ){
                piers.add(new Pier(i));
            }
            for (SortingStation s : listOfSortingStations) {
                Pier p = piers.f
            }

            } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.readFromFileStationAndFlights();
        int instance = 3;
        Algorithm algorithms = new Algorithm(m.listOfSortingStations,m.listOfFlight,instance);
        ArrayList<Result> result =algorithms.algorithmA(Algorithm.FlightOrderingMethod.OST);



        System.out.print(2);
    }
}
