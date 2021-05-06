package dk.vores.DAL;

import dk.vores.be.DataExample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataRepository {

    public List<DataExample> getAllData(String source){
        List<DataExample> allDates = new ArrayList<>();
        File file = new File(source);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                DataExample date = stringDataToArray(line.replaceAll("\\uFEFF", ""));
                allDates.add(date);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allDates;
    }

    public DataExample stringDataToArray(String line){
        String[] arrData = line.split(",");
        String[] arrDate = arrData[0].split("-");
        LocalDate date = LocalDate.of(Integer.parseInt(arrDate[2]), Integer.parseInt(arrDate[1]), Integer.parseInt(arrDate[0]));

        int stock = Integer.parseInt(arrData[1]);
        double unitsMade = Double.parseDouble(arrData[2]);
        double unitsSold = Double.parseDouble(arrData[3]);


        return new DataExample(date, stock, unitsMade, unitsSold);

    }

}
