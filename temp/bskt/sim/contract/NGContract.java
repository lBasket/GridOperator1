package bskt.sim.contract;

import java.time.*;

public class NGContract extends Contract{
    private double min;
    private double max; // max/min of contracted mmbtus [normally they're a range]
    
    
    public NGContract (LocalDateTime i_start, LocalDateTime i_end, double i_cost, double i_min, double i_max, double i_purchase_price) {
        super( i_start, i_end, i_cost);
        
        min = i_min;
        max = i_max;
        setPurchasePrice(i_purchase_price);
    }
    
    @Override
    public double[] getCost ( double amount, int interval ) { // requested NG in btus
        double adj_min = min*interval;
        double adj_max = max*interval;      //expand these so it works with hour,month
        double[] output = new double[2];
        double price = 0; //[0] = price of gas, [1] = btu remaining
        if (amount < adj_min) { //no fine, but pay for min whether use or not?
            price = adj_min * cost;
            amount = 0.;
        }
        else if (amount > adj_max) {
            price = cost * adj_max;
            amount = amount - adj_max;
        }
        else {
            price = amount * cost;
            amount = 0.;
        }
        
        output[0] = price;
        output[1] = amount;
        
    return output;
    
    }
    
    @Override
    public String toString() {
        return "Startdate: "+start_date+" Enddate: "+end_date+"cost: "+cost+" min/max mmBtu: "+min+"/"+max;
    }
    
}