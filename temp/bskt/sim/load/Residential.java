package bskt.sim.load;

import bskt.util.GridTime;
import java.util.*;

public class Residential extends Load {
    
    private static final Map<Integer, Double> duck_map = createMap();
    private static final String ld_type = "Residential";

    private static Map<Integer, Double> createMap() {
        Map<Integer, Double> result = new HashMap<Integer, Double>();
        result.put(0, 0.5);
        result.put(1, 0.55);
        result.put(2, 0.6);
        result.put(3, 0.6);
        result.put(4, 0.7);
        result.put(5, 0.75);
        result.put(6, 0.8);
        result.put(7, 0.85);
        result.put(8, 0.9);
        result.put(9, 1.0);
        result.put(10, 1.0);
        result.put(11, 1.0);
        result.put(12, 1.0);
        result.put(13, 1.0);
        result.put(14, 1.0);
        result.put(15, 1.0);
        result.put(16, 1.0);
        result.put(17, 0.95);
        result.put(18, 0.85);
        result.put(19, 0.8);
        result.put(20, 0.8);
        result.put(21, 0.75);
        result.put(22, 0.65);
        result.put(23, 0.55);
        return Collections.unmodifiableMap(result);
    }

    public Residential(double i_baseload) {
        super(i_baseload);
        super.setTypeStr("Residential");
    }
        
        
    @Override
    protected double curve_baseload(GridTime gt) {
        return base_load*duck_map.get(gt.getHour());
    }    

    



}