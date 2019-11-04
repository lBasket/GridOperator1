package bskt.sim.gen;

import bskt.sim.FuelMarket;
import bskt.sim.Owner;
import bskt.util.GridTime;
import java.util.ArrayList;
//These are obviously going to be NG fired only...

public class GasTurbine extends Generator {
//    private static String PM = "GT";
    
    private ArrayList<double[]> oper_modes; //[0] = Fuel Factor, [1] = Load Factor, [2] = Steam Injection
    private double[] cur_oper_mode;     //opermode currently in use
    
    double[] cur_opermode;
    
    //Maintenence variables
    private double EOH;
    private double startEOH;    //just going to double this if on peak
    private double tripEOH;     //just going to double this if on peak
    private boolean on_startup; //true if starting, false if not. for checking for trips
    private double min_trip_perc; //if load falls below this when on_startup is false, 'trip' and turbine turns off and sustains maintenence increment
    private final double hgp_hrs;    //hrs per maintence routine for HGP/Combustion
    private final double major_hrs; //hrs per maintence routine for major overhaul
    private double hgp_cnt;  //counter of EOH left
    private double major_cnt;  //counter of EOH left
    private double trip_perc;
    

    
    public GasTurbine (double i_nameplate, String i_name, Owner i_owner) { //with a capacity
        /*
        GT Attributes:
        Heatrate Average is 11,214 Btu/KWh
        Emission factor average is 53.07 Kg CO2 / mmBtu
        Ramp rates are 1200MW/Hr
        .
        */              

        super(0.98*i_nameplate , 1.02*i_nameplate , i_name, 7907., 53.07,
             (i_nameplate / 1200), i_owner, 0.);
//        Set up starting Operating Modes
        oper_modes = new ArrayList<double[]>();
        PM = "GT";
        cur_oper_mode = new double[3]; // TODO: Figure out how the fuck you instantiate a full array
        cur_oper_mode[0] = 1.;
        cur_oper_mode[1] = 1.;
        cur_oper_mode[2] = 1.;
        System.out.println("whats my oper mode");
        System.out.println(cur_oper_mode[2]);
        oper_modes.add(cur_oper_mode);
        cur_opermode = oper_modes.get(0);
        
        //Set up other maint info
        startEOH = 5.;
        tripEOH = 10.;
        on_startup = true;
        EOH = 0;
        trip_perc = 0.4;
        hgp_hrs = 32000.;
        hgp_cnt = hgp_hrs;
        
        major_hrs = 64000.;
        major_cnt = major_hrs;

    }
    
          
    @Override
    public ArrayList<Object> passTime(double i_load, GridTime gt, double elec_price, FuelMarket i_fm) {
            ArrayList<Object> super_result = super.passTime( i_load, gt, elec_price, i_fm);            
            
            EOH += cur_opermode[0] * cur_opermode[1] * cur_opermode[2];
            
            return super_result;
            
    }
    
        
    @Override
    protected double generate(double i_load, double cap, GridTime gt) {
        double peak_factor = 0.;   //extra ramping factors at price of more EOH
        double steam_factor = 0.;
        
        //Determine if peaking, get 1.25% bonus currently on simple current model
        if ( cur_opermode[1] > 1.) {
            peak_factor = 1.25;
        }
//        System.out.println("peakfactor:"+peak_factor);
        //Determine if steam injection, get 1.25% bonus currently on simple current model
        if ( cur_opermode[2] > 1.) {
            steam_factor = 1.25;
        }
//        System.out.println("steamfactor:"+steam_factor);
        if ( prev_load_perc == 0. && on_startup) { //on startup and 0 load, so first starting, add start EOH
            EOH += startEOH;
        }
        
        //determine currently rampable boundaries
        double max_boundary = cap * Math.min(1., prev_load_perc + rampperc); //These in MW
        double min_boundary = cap * Math.max(0., prev_load_perc - rampperc);
        
                
        //Determine load to ramp generator to
        if (prev_load_perc >= trip_perc || on_startup ) {   //either above trip level or starting up
             
            if (i_load >= max_boundary) {
                prev_load_perc = Math.min(1., prev_load_perc + rampperc + peak_factor + steam_factor);
                i_load -= max_boundary;
            }
            else if (i_load <= min_boundary) {
                prev_load_perc = Math.max(0., prev_load_perc - rampperc);
                i_load -= min_boundary;
            }
            else {
                prev_load_perc = Math.max(0., Math.min(1., i_load / cap));
                i_load = 0.;
            }
        } else {
            prev_load_perc = 0.;
            on_startup = true;
            EOH += tripEOH; //tripped so add tripEOH
            System.out.println("tripped!");
        }
        
        
        return i_load;
    }
    
    @Override
    public ArrayList<double[]> getOperModes() {
        return oper_modes;
    }
    
    @Override
    public double[] getCurMode() {
        return cur_oper_mode;
    }
    
    public void addOperMode (double fuel_fact, boolean peak_flag, boolean steam_flag) { //this will probably need to be changed to handle more user-friendly inputs
        double[] new_mode = new double[3];
        
        new_mode[0] = fuel_fact;
        
        if (peak_flag) {
            new_mode[1] = 1.5;
        } else {
            new_mode[1] = 1.;
        }
        
        if (steam_flag) {
            new_mode[2] = 1.5;
        } else {
            new_mode[2] = 1.;
        }
        
        oper_modes.add(new_mode);
    }
    
    
    public boolean removeOperMode (int idx) {
        if (idx == 0) {     //Don't let them remove the base mode
            return false;
        }
        
        try {               //return false if bad index.. else true for success
            oper_modes.remove(idx);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        
        return true;
    }
    
    public double getEOH() {
        return EOH;
    }
    
    public void resetEOH() {
        EOH = 0;
    }

//    public String getCurrOperMode() {
//        return ;
//    }

    public void checkMaint () {
        
    }
    
    
}
