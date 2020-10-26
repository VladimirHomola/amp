package cz.vladimir.amp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrackEndpoint {

    @Autowired
    private DataService dataService;

    @PostMapping("/datapoints")
    public String foo(@RequestBody DataPoint dataPoint){
        System.out.println("dalsi pokus");
        System.out.println(dataPoint.getDevice());
        return dataService.getSomething();
    }
}
