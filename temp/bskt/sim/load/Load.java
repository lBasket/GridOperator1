package bskt.sim.load;

import bskt.util.GridTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public class Load {
    static AtomicInteger nextid = new AtomicInteger();
    private String ld_type = "Generic";
    private int id;
    double base_load;
    
    
    public Load (double i_baseLoad) {
        id = nextid.incrementAndGet();
        base_load = i_baseLoad;
    }
    
    public ArrayList<Object> passTime(GridTime gt) {
        ArrayList<Object> data = new ArrayList<>();
        
        data.add(id);                   //[0] id
        data.add(0);                    //[1] time (not set here)
        data.add(ld_type);              //[2] load type
        data.add(curve_baseload(gt));   //[3] load mw
        
        return data;
    }
    
    protected double curve_baseload(GridTime gt) {
        return base_load;
    }
    
    protected void  setTypeStr (String inp) {
        ld_type = inp;
    }
    
}