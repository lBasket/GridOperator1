package bskt.sim.gen;

import bskt.sim.Owner;
import bskt.util.GridTime;
import java.util.*;

public class Solar extends Generator {
    private static final Map<Integer, Double> insol_map = createMap();

    private static String PM = "PV";
    
    private static Map<Integer, Double> createMap() {
        Map<Integer, Double> result = new HashMap<Integer, Double>();
        result.put(0, 0.0);
        result.put(1, 0.0);
        result.put(2, 0.0);
        result.put(3, 0.0);
        result.put(4, 0.0);
        result.put(5, 0.0);
        result.put(6, 0.0);
        result.put(7, 0.0);
        result.put(8, 0.05);
        result.put(9, 0.2);
        result.put(10, 0.75);
        result.put(11, 0.99);
        result.put(12, 1.0);
        result.put(13, 1.0);
        result.put(14, 1.0);
        result.put(15, 1.0);
        result.put(16, 1.0);
        result.put(17, 0.99);
        result.put(18, 0.95);
        result.put(19, 0.65);
        result.put(20, 0.14);
        result.put(21, 0.03);
        result.put(22, 0.0);
        result.put(23, 0.0);
        return Collections.unmodifiableMap(result);
    }    
    
    public Solar (double i_nameplate, String i_name, Owner i_owner) {
        /*
        Solar Attributes:
        Heatrate will be set to 0, change function scaleHeatRate (same inputs)
        Emission factor will be set to 0, change function emissionLbs (same inputs)
        Ramp Rates are instant, change function generate to compensate & consider weather
        private 
        */
        super(i_nameplate, i_nameplate*0.90, i_name, 0, 0, 0, i_owner, 0);
    }
    
    private double scaleHeatrate(double loadperc) {
        return 0.0; //remove the unnessecary computations
    }
    
    private double emissionLbs(double btus) {
        return 0.0; //remove unnessecary computations
    }
    
    @Override
    protected double generate(double i_load, double cap, GridTime gt) {
        
        double max_boundary = cap * insol_map.get(gt.getHour());
        double min_boundary = 0.0;

        if (i_load >= max_boundary) {
            i_load -= max_boundary;
        }
        
        else {
            i_load = 0;
        }
        
        return i_load;
    }
    
    @Override
    protected double burnFuel(double i_mwh) {
        return 0.;
    }
    
    @Override
    protected void addFuel ( double btu_input) {
        
    }

    
    
}