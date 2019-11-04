package bskt.sim.contract;

import java.time.*;


public class Contract {
    protected LocalDateTime start_date;
    protected LocalDateTime end_date;
    protected double cost;  //this will hopefully get used for all subclasses in their own lil way
    protected double purchase_price;
    
    public Contract (LocalDateTime i_start, LocalDateTime i_end, double i_cost, double purchase_price) {
        
        start_date = i_start;
        end_date = i_end;
        cost = i_cost;
        
        
    }
    
    public Boolean checkValidity( LocalDateTime i_date ) {
        if ( end_date.compareTo(i_date) < 0 ) { //returns a negative value is end_date < i_date
            return false; //contract has expired
        }
        else {
            return true;
        }
    }
    
    protected double[] getCost ( double amount, int interval ) { // is amount any good? amnt btu, amnt % repair...
        double[] output = new double[2];
        output[0] = amount * cost;
        output[1] = 0;
        return output;
    }
    
    public String toString() {
        return "Startdate: "+start_date+" Enddate: "+end_date+"cost: "+cost;
    }
    
    public double getPurchasePrice() {
        return purchase_price;
    }
    
}