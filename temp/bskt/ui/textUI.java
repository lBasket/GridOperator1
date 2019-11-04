package bskt.ui;

import bskt.sim.FuelMarket;
import bskt.sim.Grid;
import bskt.sim.Owner;
import bskt.sim.contract.NGContract;
import bskt.sim.gen.GasTurbine;
import bskt.sim.gen.Generator;
import bskt.sim.gen.Solar;
import bskt.sim.load.Industrial;
import bskt.sim.load.Residential;
import bskt.util.GridTime;
import bskt.util.Logger;
import java.util.ArrayList;
import java.util.Scanner;

//although the engine is 5minutely, I want the player to see hourly shit

public class textUI {
    Scanner scanner;
    String username;
    public Owner pc;
    Grid grd;
    GridTime gt;
    FuelMarket fm;
    ContractUI contract_store;
    Logger log;


    public textUI (GridTime i_gt, FuelMarket i_fm) {
        //init
        gt = i_gt;
        fm = i_fm;
        grd = new Grid(29.1011, "Players Grid");
        // Load ld = new Load(750.);
        Industrial id1 = new Industrial(500.);
        Residential rd1 = new Residential(500.);
        grd.addLoad(id1);
        grd.addLoad(rd1);
        contract_store = new ContractUI();
        scanner = new Scanner(System.in);
        
        
        //Start Sequence
        System.out.println("Welcome to the Basket Grid Operator Simulator!");

        System.out.println("What's your name?");
        username = scanner.next();
        log = new Logger(username);

        System.out.println("Better get to work "+username+". You start with $10,000,000.");
        pc = new Owner(username, 10000000);

        System.out.println("You ain't gonna make shit with any Generating assets boi");
        
        //Don't let these guys get away with not buying any generators because wtf is the point
        int purchased = 0;
        while ( purchased == 0 ) {
            purchased = buyGenerator();
            
            if ( purchased == 0 ) {
                System.out.println("C'mon man you need a a generator!");
            }
        }
        System.out.println("Good work!");

        showAssets();

        System.out.println("");
        profile();


    }

    public int buyGenerator() {
        // TODO: This asks for a name before telling you invalid gen number selection
        // TODO: prints should be conslidated here
        System.out.println("Welcome to the Generator Store. Here is our current selection:");
        System.out.println("1. $4000k: 1,000MW Natural Gas Turbine");
        System.out.println("2. $2000k: 500MW Natural Gas Turbine");
        System.out.println("3. $750k:  100MW Natural Gas Turbine");
        System.out.println("4. $3000k: 500MW Solar Farm");
        System.out.println("5. $1000k: 100MW Solar Farm");
        System.out.println("6. $500k:  25MW Solar Farm");
        
        //TODO: Don't create dummy Generator, fuckin up the ID's
//        Generator newgen = new Solar(0, "placeholder", pc);
        Generator newgen = null;
        
        int purchase_made = 0;
        int input = 9;

        int loop_end = 5;
        for(int count=0; count < loop_end; count++) {
            try {
                String s_input = scanner.next();
                input = Integer.parseInt(s_input);
                count = loop_end;
            } catch (NumberFormatException e) {
                System.out.println("Please choose a valid number.");
            }
        }

        String name = "";
        System.out.println("Enter a name for your generator:");
        while (name.length() == 0) {
            name = scanner.next();
        }

            if (input > 0 && input <= 6) {
                purchase_made = 1;

            switch (input) {
                case 1:
                    newgen = new GasTurbine(1000, name, pc);
                    pc.transaction(-4000000.);
                    break;
                case 2:
                    newgen = new GasTurbine(500, name, pc);
                    pc.transaction(-2000000.);
                    break;
                case 3:
                    newgen = new GasTurbine(100, name, pc);
                    pc.transaction(-750000.);
                    break;
                case 4:
                    newgen = new Solar(500, name, pc);
                    pc.transaction(-3000000.);
                    break;
                case 5:
                    newgen = new Solar(100, name, pc);
                    pc.transaction(-1000000.);
                    break;
                case 6:
                    newgen = new Solar(25, name, pc);
                    pc.transaction(-500000.);
                    break;
                default:
                    break;
            }


                pc.addGenerator(newgen);
                grd.addGenerator(newgen);
                return purchase_made;

            }
            else {
                System.out.println("Please choose a valid number.");
            }

    return purchase_made;

    }

    public void passTime(GridTime gt, String period) {
        ArrayList<ArrayList> gen_data = new ArrayList<>();
        ArrayList<ArrayList> ld_data = new ArrayList<>();
//        owner_data.add(pc.getID());
        int intervals = 0;

        //Calculate distance to skip
        if ("day".equals(period)) {
            intervals = 12*24;
        } else if ("week".equals(period)) {
            intervals = 12*24*7;
        } else if ("month".equals(period)) {
            intervals = 12*24*28;
        } else {
            intervals = 12;
        }

        for(int i=0; i<intervals;i++) {
            ArrayList<ArrayList<ArrayList<Object>>> returned = grd.passTime(gt, fm, pc);
            ld_data.add(returned.get(0)); 
            gen_data.add(returned.get(1)); //for some reason this is getting an array of 2 arrays the first time??
        }
        
        double mwh = rollup(gen_data, 3);
        double btu_burnt = rollup(gen_data, 4);
        double fuel_cost = grd.NGFuelCost(gen_data, pc, fm, intervals/12);
        double revenues = rollup(gen_data, 5);
        System.out.println("revenues was" + revenues);
        System.out.println("fuel cost was:" + fuel_cost);
        double profit = revenues - fuel_cost;  // revenue - gas cost
        
        // Write rows to the SQLite db
        long temp = System.currentTimeMillis() / 1000;
        log.insertGen(gen_data);
        System.out.println(gen_data);
        log.insertLoad(ld_data);

        //Pay player/ take his moneys
        //this is going back into generators so i can have multiple owners on a grid
        //does that really make sense if its utility based? I should do that later
        pc.transaction(profit);
        

        System.out.println("It is now " + gt.getDate()+": Generated "+mwh+"MWh for $"+profit+" burnt mmbtu: "+btu_burnt);
        System.out.println("pc now has: " + pc.getCash());

    }




    public void showAssets () { // TODO: renewables show infinity ramprate, GTs show as an integer. Pls fix
        ArrayList<Generator> lst = pc.getGenerators();
        System.out.println("Your Assets: ");
        for (Generator gen : lst ) {
            System.out.println(gen.toString());
        }
    }

    
    public void profile () {
        System.out.println(username+" has "+pc.getGenerators().size()+" Generator(s) and $"+pc.getCash());
    }

    
    public double rollup(ArrayList<ArrayList> ilst, int index) {
//        log.insertGen(ilst);
        double temp = 0;
        for (ArrayList outlist: ilst) {
            for (Object inlist : outlist) {
                ArrayList list = (ArrayList) inlist;
                temp += (double) list.get(index);
            }
        }
        return temp;
    }


    public void mainMenu () {
        System.out.println ("Main Menu: \r\n"
                           + "1. Show Generator List \r\n"
                           + "2. Buy new Generator\r\n"
                           + "3. Buy NG Contract \r\n"
                           + "4. Pass hour\r\n"
                           + "5. View contracts\r\n"
                           + "6. View Cash Balance\r\n"
                           + "7. Pass day\r\n"
                           + "8. Pass week\r\n"
                           + "9. Pass month\r\n"
                           + "10. Generator Options\r\n"
                           );
        while (true) {
            int input = 0;
            try {
                String s_input = scanner.next();
                input = Integer.parseInt(s_input);
            } catch (NumberFormatException e) {
                System.out.println("Please choose a valid number.");
            }

            switch (input) {
                case 1:
                    showAssets();
                    return;
                case 2:
                    buyGenerator();
                    return;
                case 3:
                    NGContract newcontract = (NGContract) contract_store.openStore(gt, fm); //if i have memory issues i think this can be initialized and ignored to be GCed here down the road
                    try {
                        pc.transaction(-1. * newcontract.getPurchasePrice()); //multiply by -1 to charge the pc since the cost is inherently stored postive
                        pc.addContract( newcontract, "NG");
                    } catch (NullPointerException e) {}
                    return;
                case 4:
                    passTime(gt, "hour");
                    return;
                case 5:
                    pc.printContracts();
                    break;
                case 6:
                    System.out.println("Remaining cash: "+pc.getCash());
                    break;
                case 7:
                    passTime(gt, "day");
                    return;
                case 8:
                    passTime(gt, "week");
                    return;
                case 9:
                    passTime(gt, "month");
                    return;
                case 10:
                    GenOptionsUI goui = new GenOptionsUI(pc);
                    goui.menu();
                    return;
                default:
                    break;
            }
            break;

        }
    }

}

