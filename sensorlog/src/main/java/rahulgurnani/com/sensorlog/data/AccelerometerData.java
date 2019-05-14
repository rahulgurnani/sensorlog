package rahulgurnani.com.sensorlog.data;


import java.util.ArrayList;
import java.util.List;

public class AccelerometerData {

    List<Float> aV;
    long ts;

    // Contructors
    public AccelerometerData() {

    }
    public AccelerometerData(float accValues[], long timestamp) {
        this.aV = new ArrayList<Float>();
        this.aV.add(accValues[0]);
        this.aV.add(accValues[1]);
        this.aV.add(accValues[2]);
        ts = timestamp;
    }

    // getters and setters

    public List<Float> getaV() {
        return aV;
    }

    public void setaV(List<Float> accValues) {
        this.aV = accValues;
    }
}
