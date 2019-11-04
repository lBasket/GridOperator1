package bskt.sim;

public class FuelMarket {
    private double NG_$_mmbtu;
    
    public FuelMarket () {
        NG_$_mmbtu = 2.82; //$ per mmbtu
        
    }
    
    public double ngPrice() {
        return NG_$_mmbtu;
    }
    
    
}