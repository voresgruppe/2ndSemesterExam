package dk.vores.util;

import dk.vores.be.DataType;

public class DataUtils {

    public DataType typeFromString(String str){
        for(DataType current: DataType.values()) {
            if(str.toLowerCase().matches(current.name().toLowerCase())){
                return current;
            }
        }
        return null;
        //TODO userError
    }
}
