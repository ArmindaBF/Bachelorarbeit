import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {


        File file = new File("..\\Flights.csv");
        try {
            List<String> lines = FileUtils.readLines(file, "UTF-8");
            System.out.println("Hello World! gfhgfhgfhd");

        }catch (Exception e){
            e.fillInStackTrace();
        }

    }
}
