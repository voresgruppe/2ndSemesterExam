package dk.vores.BLL;

import dk.vores.DAL.DataExampleRepository;
import dk.vores.be.DataExample;

import java.util.List;

public class DataManager {
    DataExampleRepository dRepo = new DataExampleRepository();

    public List<DataExample> getAllData(String source){
        return dRepo.getAllData(source);
    }
}
