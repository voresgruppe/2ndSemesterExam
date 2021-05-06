package dk.vores.BLL;

import dk.vores.DAL.DataRepository;
import dk.vores.be.DataExample;

import java.util.List;

public class DataManager {
    DataRepository dRepo = new DataRepository();

    public List<DataExample> getAllData(String source){
        return dRepo.getAllData(source);
    }
}
