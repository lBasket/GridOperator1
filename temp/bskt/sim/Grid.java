package bskt.sim;

import bskt.sim.contract.NGContract;
import bskt.sim.gen.Generator;
import bskt.sim.load.Load;
import bskt.util.GridTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public class Grid {
    static AtomicInteger nextid = new AtomicInteger();
    private int id;
    ArrayList<Generator> gen_list = new ArrayList<>();
    ArrayList<Load> load_list = new ArrayList<>();
//    ArrayList<Owner> own_list = new ArrayList<>();
    double elec_price;  //Price of electricty in KW/hr
    String Name;
    
        // i need a good sorting method that works efficient to add when list is already sorted
    
    public Grid(double rate, String name) {
        elec_price = rate;
        Name = name;
        
    }

    public void addGenerator( Generator gen ) {
        int index = sort_index(gen.getEmissFactor());
        gen_list.add(index, gen);
    }
    
    public int sort_index(double target) {
        if (gen_list.isEmpty()) {
            return 0;
        }
        if (gen_list.size() == 1) {
            if (gen_list.get(0).getEmissFactor() > target) {
                return 0;
            }
            else {
                return 1;
            }
        }
        else {
            //binary sort
            int low = 0;
            int high = gen_list.size() - 1;
            int index = high;
            while (low <= high) {
                int mid = (int) Math.ceil((low + high)/2);
                if (gen_list.get(mid).getEmissFactor() < target) {
                    low = mid + 1;
                    index = low;
                } else if (gen_list.get(mid).getEmissFactor() > target) {
                    high = mid -1;
                } else if (gen_list.get(mid).getEmissFactor() == target) {
                    index = mid + 1;
                    break;
                }
            }
        return index;   
        }
    }
    
    public void addLoad( Load ld ) {
        load_list.add(ld);
    }
    
    public void setRate( double new_rate ) {
        elec_price = new_rate;
    }
    
    public double getRate() {
        return elec_price;
    }
    
    
    public ArrayList<ArrayList<ArrayList<Object>>> passTime(GridTime gt, FuelMarket i_fm, Owner i_pc ) { //new one 
        double interval_load = 0;
        double interval_emissions = 0;
        ArrayList<ArrayList<Object>> gen_rows = new ArrayList<>(); 
        ArrayList<ArrayList<Object>> lc_rows = new ArrayList<>(); 
        ArrayList<ArrayList<ArrayList<Object>>> returner = new ArrayList<>();

        // Sum up the total load from all loadcenters
        for (Load lc : load_list) {

            ArrayList<Object> temp = lc.passTime(gt);
            interval_load += (double) temp.get(3);
            temp.set(1, gt.getDate().toString()); // Insert the datetime into the returned array
            lc_rows.add(temp);

        }

        //Now each generator will take the load and then spit out load - its gen
        for (Generator gen : gen_list) {    
            if (interval_load > 0.0) {
                ArrayList<Object> temp = gen.passTime(interval_load, gt, elec_price, i_fm);
                temp.set(1, gt.getDate().toString()); // Insert the datetime into the returned array
                interval_load = (double) temp.get(8);
                gen_rows.add(temp); //generators take the load then do their thing and give it back
//                System.out.println("gen_rows: "+gen_rows);
            }
        }
        
        
        if (interval_load > 0.0) {
            System.out.println("Unsatisfied load "+interval_load);
            missedLoad(interval_load, i_pc);
        }
        
        
        gt.next();
        if (gt.getDayOfMonth() == 1 &&
            gt.getHour() == 0 &&
            gt.getMinute() == 0        
            ) {
                i_pc.cleanContracts(gt.getDate());
            }
            for (Generator gen : gen_list) { 
                // gen.do mantenence??
//                gen.res
            }
            
        
        
        returner.add(lc_rows);
        returner.add(gen_rows);

        return returner;
    }
    

 
    public double NGFuelCost (ArrayList<ArrayList> gen_rows, Owner i_pc, FuelMarket i_fm, int intervals ) {
        double cost = 0.;
        double gas_mmbtu = 0.;
        ArrayList<NGContract> contracts = i_pc.getGasContracts();
        
        for (ArrayList outlist: gen_rows) {
            for (Object inlist : outlist) {
                ArrayList list = (ArrayList) inlist;
                if ( "GT".equals((String) list.get(9)) ) {
                    gas_mmbtu += (double) list.get(4);
                }
            }
        }
        
        for (NGContract ng : i_pc.getGasContracts()) {
            double[] temp = ng.getCost(gas_mmbtu, intervals);
            cost += temp[0]; 
            gas_mmbtu = temp[1];
        }
        
        cost = cost + gas_mmbtu * i_fm.ngPrice(); //if mmBtus left after contracts, pay spot price
        
        return cost;
        
        
    }
    
    private void missedLoad(double load, Owner i_pc) {
        //idk, double the profit you'd have made off of it?
        i_pc.transaction(load*2.*elec_price*1000./12.); //double the electricity price * 1000 bc its in kw/hr /12. bc of 5minutely engine
    }
    
}