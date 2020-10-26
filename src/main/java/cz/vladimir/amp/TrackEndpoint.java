package cz.vladimir.amp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TrackEndpoint {

    @Autowired
    private DataService dataService;

    @PostMapping("/datapoints")
    public void addDatapoint(@RequestBody Datapoint dataPoint) {
            dataService.storeDataPoint(dataPoint);
    }

    @GetMapping("/statistics/devices/{device}/avg")
    public List<AverageForInterval> getDeviceAverages(@PathVariable String device){
        return dataService.getAveragesForDevice(device);
    }

    @GetMapping("/statistics/devices/{device}/moving_avg")
    public List<AverageForInterval> getDeviceMovingAverages(@PathVariable String device, @RequestParam(value = "window_size") long windowSize){
        return dataService.getMovingAveragesForDevice(device, windowSize);
    }

    @GetMapping("/statistics/users/{user}/avg")
    public List<AverageForInterval> getUserAverages(@PathVariable String user){
        return dataService.getAveragesForUser(user);
    }

    @GetMapping("/statistics/users/{user}/moving_avg")
    public List<AverageForInterval> getUserMovingAverages(@PathVariable String user, @RequestParam(value = "window_size") long windowSize){
        return dataService.getMovingAveragesForUser(user, windowSize);
    }

    @DeleteMapping("/devices/{device}/datapoints")
    void deleteDeviceDatapoints(@PathVariable String device) {
        dataService.deleteDeviceDatapoints(device);
    }

    @DeleteMapping("/devices/{user}/datapoints")
    void deleteUserDatapoints(@PathVariable String user) {
        dataService.deleteUserDatapoints(user);
    }



    //temp. just for test
    @PostMapping("/datapointsx")
    public void addDatapointx(@RequestBody List<Datapoint> datapoints){
        datapoints.forEach(v -> {

            dataService.storeDataPoint(v);

        });
    }

}
