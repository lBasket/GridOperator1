package bskt.sim;

import bskt.ui.*;
import bskt.util.GridTime;

public class GridOper {
    
    public static void main(String[] args) {
       
       GridTime timing = new GridTime();
       FuelMarket fm = new FuelMarket();
       textUI tt = new textUI(timing, fm);
        

        while (true) {
            tt.mainMenu();
        }

     
    }
    
    
    
}