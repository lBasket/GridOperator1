
package bskt.ui;

import bskt.sim.Owner;
import bskt.sim.gen.Generator;
import java.util.ArrayList;
import java.util.Scanner;

public class GenOptionsUI {
    Owner pc;
    Scanner scnr;
    
    public GenOptionsUI (Owner i_pc) {
        pc = i_pc;
    }
    
    public void menu () {
        scnr = new Scanner(System.in);
        int input = -1;
                
        String menu_str = "What you wanna see?\r\n" 
                        + "1. Operating Modes\r\n"
                        + "2. Maintenence Parts\r\n"
                        + "0. exit";
        
        System.out.println(menu_str);
        
        try {
            String s_input = scnr.next();
            input = Integer.parseInt(s_input);
        }
        catch (NumberFormatException e) {
            System.out.println("Please choose a valid number.");
        }
        
        if (input==1) {
            operatingModesMenu();
        }
        if (input==2) {
            //open maintence parts function
            maintMenu();
        }
        if (input ==0) {
            //exit?
        }
        
        
        
    }
    
    void operatingModesMenu () { //This needs to show all then handle changing them
        ArrayList<Generator> gen_list = pc.getGenerators();
        String modes_table = " # | Name | Fuel Factor | Load Factor | Steam Inj\r\n";
        
        for (Generator gen : gen_list) {
            if ("GT".equals(gen.getPM())) {
                double[] cur_opermode = gen.getCurMode();
                modes_table = modes_table + gen.getID() + " | "
                                          + gen.getName() + " | "
                                          + cur_opermode[0] + " | "
                                          + cur_opermode[1] + " | "
                                          + cur_opermode[2] + " | "
                                          + "\r\n"; 
            }
        }
        System.out.println(modes_table);
        System.out.println("");
        System.out.println("What do you want to do?\r\n"
                         + "asd");
        
        
        
    }
    
    void maintMenu() { //This should show inventories and allow you to buy more
        // this
        System.out.println("How long of a contract do you want?\n"
                         + "1. $4,000:   3 Month for a 5% discount, min/max mmBtu: 15k/25k\n"
                         + "2. $20,000:  6 Month for a 10% discount, min/max mmBtu: 20k/30k\n"
                         + "3. $90,000:  12 Month for a 15% discount, min/max mmBtu: 25k/35k\n"
                         + "4. $300,000: 24 Month for a 20% discount, min/max mmBtu: 30k/40k\n"
                        );
        int check = 0;
        int input = 0;
        while(check==0) {        
            try {
                
                String s_input = scnr.next();
                input = Integer.parseInt(s_input);
            }
            catch (NumberFormatException e) {
                System.out.println("Please choose a valid number.");
            }
            
 //hourly mmbtus for client facing side
            switch (input) {
                case 1:
                    break;
                case 2:
                    break;
                default:
                    System.out.println("Please choose a valid number.\n");
                    break;
            }
        }
        //this end
    }
    
    /*
    to change gen options:
        1. show menu to pick between oper modes & maint items
        2. maint items: show all gens with levels of overhaul kits/hgp/combust parts + exit
            Some kind of waiting period? 3 months? or something. that'll probably be harder to implement than it seems...
    
          *ID | Name | Type        | HGP Kits | EOH to Repair    | Overhaul Kits | EOH to Repair
           xx | GT1  | Gas Turbine | xx       | xx               | xx            | xx 
           --
           0. exit
           #. Purchase parts for                 
           f. Fill stockpiles for all* (redisplay menus)
            
                # ->  0. exit 
                      1. Buy HGP kits (back to #)
                      2. Buy Overhaul kits (back to #)
                      3. Fill Storage (back to #)
    
    
            i. either exit or pick generator
                a. if gen picked, option to buy either (hgp/combust should be together)
                b. go back to 2. 
        3. oper modes: list each gen with current oper mode stats
          *GT1 | Gas Turbine | Oper Mode xx | xx EOH factor
            i. either exit or pick generator
                a. if gen picked display all oper modes & seleted, option to add/rmv/exit
                  * # | Fuel Factor | Load Factor | Steam Injection
                    1 | 1.0         | 1.0         | 1.0
                    -- 
                    0. exit
                    n. New Operating Mode
                    #. Switch Operating Mode to #
           
    
    */
}
