package bskt.sim.load;

import bskt.util.GridTime;
import java.util.*;

public class Industrial extends Load {
    private static final Map<Integer, Double> workinghrs_map = createMap();

    private static Map<Integer, Double> createMap() {
        Map<Integer, Double> result = new HashMap<Integer, Double>();
        result.put(0, 0.1);
        result.put(1, 0.1);
        result.put(2, 0.1);
        result.put(3, 0.1);
        result.put(4, 0.1);
        result.put(5, 0.1);
        result.put(6, 0.5);
        result.put(7, 0.5);
        result.put(8, 0.5);
        result.put(9, 0.5);
        result.put(10, 0.5);
        result.put(11, 1.0);
        result.put(12, 1.0);
        result.put(13, 1.0);
        result.put(14, 1.0);
        result.put(15, 1.0);
        result.put(16, 0.5);
        result.put(17, 0.5);
        result.put(18, 0.5);
        result.put(19, 0.5);
        result.put(20, 0.5);
        result.put(21, 0.1);
        result.put(22, 0.1);
        result.put(23, 0.1);
        return Collections.unmodifiableMap(result);
    }    

    public Industrial (double i_baseload) {
        super(i_baseload);
        super.setTypeStr("Industrial");
    }
    
    @Override
    protected double curve_baseload(GridTime gt) {
        return base_load*workinghrs_map.get(gt.getHour());
    }

}
    
       