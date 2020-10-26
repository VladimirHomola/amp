package cz.vladimir.amp;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class DataService {

    private Map<String, Set<DataPoint>> dataByUser = new HashMap<>();
    private Map<String, Set<DataPoint>> dataByDevice = new HashMap<>();

    public void storeDatapoint(DataPoint dataPoint){

    }

    private void storeDatapointByUser(DataPoint dataPoint){

    }

    private void storeDatapointByDevice(DataPoint dataPoint){

    }

    public String getSomething(){
        return "ja jsem autowired";
    }
}
