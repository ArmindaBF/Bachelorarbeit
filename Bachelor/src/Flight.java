import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

/**
 * Created by vladimir on 8/2/16.
 */

public class Flight {

    public int instanceNummer;
    public int id;
    public String Outbound;
    public Date datum;
    public Time est;
    public Time stt_est;
    public Time lst_est;
    public Time lst;
    public Time stt_lst;
    public Time stt;
    public Time tj;//target starting service time
    public int pierId;
    public double X;
    public double Y;


    public Flight(int instanceNummer, int id,
                  String outbound,
                  Date datum,
                  Time est, Time stt_est,Time lst_est, Time lst, Time stt_lst, Time stt,Time tj,
                  double x, double y,int pierId) {
        this.instanceNummer = instanceNummer;
        this.id = id;
        Outbound = outbound;
        this.datum = datum;
        this.est = est;
        this.stt_est = stt_est;
        this.lst_est = lst_est;
        this.lst = lst;
        this.stt_lst = stt_lst;
        this.stt = stt;
        this.tj = tj;

        this.X = x;
        this.Y = y;
        this.pierId = pierId;
    }
}
