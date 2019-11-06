package bskt.sim.gen;

//id,timestamp,gen name,MWh Generation,BTU Burn,$,emissionLbs,loadPerc,outputLoad
import bskt.sim.FuelMarket;
import bskt.sim.Owner;
import bskt.util.GridTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Generator {
    static AtomicInteger nextid = new AtomicInteger();
    private int id;
    private double summerCap;      //mw
    private double winterCap;      //mw 
    private String name;          //unit name
    private double heatrate;       // Btu/KWh
    private double emissfactor;
    private double fuel_cap;     //Btu
    private double fuel;
    
    double rampperc;      //% of capacity that can change pre 5min interval
    double prev_load_perc;  //previous load percent for use in ramping
    private Owner owner;
    String PM;
    
    public Generator (double sumcap, double wintcap, String i_name,
                      double i_heatrate, double i_emissfactor, double i_ramp_rate,
                      Owner i_owner, double i_stockpile) {
                          /*
                          subclass notes for consistency:
                          1. ramprate should be decided by PM subclass
                          2. Heatrate at first will be average PM from here(2016!) "86"
                          3. Emission Factor will be fuel based from here (2016) https://www.eia.gov/electricity/annual/html/epa_a_03.html
                          */
        id = nextid.incrementAndGet();
        PM = "generic";
        summerCap = sumcap;
        winterCap = wintcap;
        name = i_name;
        rampperc = 1. / 12. / i_ramp_rate; // 100% / 12 intervals / # hours to ramp
        owner = i_owner;
        fuel_cap = i_stockpile;
        fuel = i_stockpile;
        
        
        prev_load_perc = 0;
        heatrate = i_heatrate;
        emissfactor = i_emissfactor;
        // owner.addGenerator(sthis);

    }
 
 
    public ArrayList<Object> passTime(double i_load, GridTime gt, double elec_price, FuelMarket i_fm) {
        ArrayList<Object> output = new ArrayList<>();
        double season_cap = summerCap;     
        
            
        String season = gt.getSeason();
         //Check if we need to switch the Winter Capacity   
        if ("Winter".equals(season)) {
            season_cap = winterCap;
        }
        
        
        // Calculate how much MW we can get to 
        double new_load = generate(i_load, season_cap, gt );
        
        //Calculate MWh Generation
        double mwh_generated = (i_load - new_load) / 12.;  //MW generated divided by 12 for 5min interval
        
        //Burn fuels in mmBtu using mwh generated
        double burnt_mmbtu = burnFuel(mwh_generated);
        
        //Calculate revenue made
        double revenue = mwh_generated * elec_price;
        
        //Calculate fuel costs
        // double fuel_costs = ??
        
        //transact owners
        // owner.transaction(revenue - fuel_costs);
        
        //Calculate lbs of Emissions
        double lbs_emitted = emissionLbs(burnt_mmbtu);
        
        
        //outputs
        output.add(id);                                 // [0] = object unique id
        output.add("uh");                               // [1] = timestamp (filled later)
        output.add(name);                               // [2] = generator name
        output.add(mwh_generated);                      // [3] = Mwh generation
        output.add(burnt_mmbtu);                        // [4] = mmBtu burnage
        output.add(revenue);                            // [5] = revenue $
        output.add(lbs_emitted);                        // [6] = emission lbs
        output.add(prev_load_perc);                     // [7] = load %
        output.add(new_load);                           // [8] = outputted load
        output.add(PM);                                 // [9] = Prime Movers4
        //id,timestamp,gen name,MWh Generation,BTU Burn,$,emissionLbs,loadPerc,outputLoad
        
        return output;

    } 

    private double emissionLbs(double mmbtus) {
        double emiss = emissfactor * mmbtus * 2.20462; //
        // emissfactor is kg per mmbtu so multiply then by 2.20462 to get lbs
        return emiss;
    }        
    
    
    private double scaleHeatrate(double loadperc) {
        //This is a pretty simple model of increasing the heatrate (making the generator less efficient) in quarterly intervals of load percentage, to mimic generators in real life that tend to be more efficient closer to their maximum load.
        if (loadperc >= 0 && loadperc < 0.25 ) {
            return heatrate *= 1.3;
        }
        else if (loadperc >= 0.25 && loadperc < 0.5 ) {
            return heatrate *= 1.2;
        }
        else if (loadperc >= 0.5 && loadperc < 0.75 ) {
            return heatrate *= 1.1;
        }
        else {
            return heatrate;
        }
    }
        
    
    double burnFuel(double i_mw) {
        double burnt_mmbtu = i_mw*1000*heatrate / 1000000;
        fuel -= burnt_mmbtu;
        return burnt_mmbtu;
    }
    

    void addFuel ( double mmbtu_input) {
        //Add fuel in Btus to the unit's personal stockpile
        fuel += mmbtu_input;
    }

    double generate(double i_load, double cap, GridTime gt) {
        //determine currently rampable boundaries
        
        double max_boundary = cap * Math.min(1., prev_load_perc + rampperc); //These in MW
        double min_boundary = cap * Math.max(0., prev_load_perc - rampperc);
        
                
        //Determine load to ramp generator to
        if (i_load >= max_boundary) {
            prev_load_perc = Math.min(1., prev_load_perc + rampperc);
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
        
        return i_load;
    }
    
    public int getID() {
        return id;
    }
    
    public String getPM() {
        return PM;
    }
    
    
    public String getName() {
        return name;
    }
    
    public double getEmissFactor() {
        return emissfactor;
    }
    
    public double getRemFuelSpace() {
        return fuel_cap - fuel;
    }
    
    @Override
    public String toString() {
        return "ID: "+id+" Name: "+name+" Summer Capacity: "+summerCap+" Ramprate(%/hr): "+(rampperc*12)+" PM: "+PM;
    }
    
    public Owner getOwner() {
        return owner;
    }

    double getPrevPerc() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ArrayList<double[]> getOperModes() {
        return null;
    }
    
    public double[] getCurMode() {
        return null;
    }
  
}