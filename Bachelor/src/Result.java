import java.sql.Time;
import java.util.Date;

public class Result {
    private Flight theFlight;
    private SortingStation theStation;
    private long r_j;

    public Flight getTheFlight() {
        return theFlight;
    }

    public void setTheFlight(Flight theFlight) {
        this.theFlight = theFlight;
    }

    public SortingStation getTheStation() {
        return theStation;
    }

    public void setTheStation(SortingStation theStation) {
        this.theStation = theStation;
    }

    public long getR_j() {
        return r_j;
    }

    public void setR_j(long r_j) {
        this.r_j = r_j;
    }

    public Time getAssignedStartTime() {
        return assignedStartTime;
    }

    public void setAssignedStartTime(Time assignedStartTime) {
        this.assignedStartTime = assignedStartTime;
    }

    public Time assignedStartTime;

    public Result() {
    }
    public Result(Flight theFlight, SortingStation theStation) {
        this.theFlight = theFlight;
        this.theStation = theStation;
    }



}
