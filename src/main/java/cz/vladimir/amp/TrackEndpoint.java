package cz.vladimir.amp;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
public class TrackEndpoint {

    @Autowired
    private AverageCalculations averageCalculations;

    @PostMapping("/datapoints")
    public void addDatapoint(@RequestBody Datapoint dataPoint) {
        averageCalculations.storeDataPoint(dataPoint);
    }

    @GetMapping("/statistics/devices/{device}/avg")
    public List<AverageForInterval> getDeviceAverages(@PathVariable @NotBlank String device){
        return averageCalculations.getAveragesForDevice(device);
    }

    @GetMapping("/statistics/devices/{device}/moving_avg")
    public List<AverageForInterval> getDeviceMovingAverages(@PathVariable @NotBlank String device, @RequestParam(value = "window_size") @Min(1) long windowSize){
        return averageCalculations.getMovingAveragesForDevice(device, windowSize);
    }

    @GetMapping("/statistics/users/{user}/avg")
    public List<AverageForInterval> getUserAverages(@PathVariable @NotBlank String user){
        return averageCalculations.getAveragesForUser(user);
    }

    @GetMapping("/statistics/users/{user}/moving_avg")
    public List<AverageForInterval> getUserMovingAverages(@PathVariable @NotBlank String user, @RequestParam(value = "window_size") @Min(1) long windowSize){
        return averageCalculations.getMovingAveragesForUser(user, windowSize);
    }

    @DeleteMapping("/devices/{device}/datapoints")
    void deleteDeviceDatapoints(@PathVariable @NotBlank String device) {
        averageCalculations.deleteDeviceDatapoints(device);
    }

    @DeleteMapping("/devices/{user}/datapoints")
    void deleteUserDatapoints(@PathVariable @NotBlank String user) {
        averageCalculations.deleteUserDatapoints(user);
    }

}
