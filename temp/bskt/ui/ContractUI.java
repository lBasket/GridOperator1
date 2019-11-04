package bskt.ui;

import bskt.sim.FuelMarket;
import bskt.sim.contract.Contract;
import bskt.sim.contract.NGContract;
import bskt.util.GridTime;
import java.time.*;
import java.util.Scanner;

public class ContractUI {
    String contract_explain = "Contracts are fixed-length at month intervals, giving you a \r\n"
                            + "% discount off the current market price, with a higher discount \r\n"
                            + "for a longer contract purchase.";
    
    public ContractUI () { // GridTime i_gt, FuelMarket i_fm, Owner i_owner) {
        
    }
    
    public Contract openStore(GridTime i_gt, FuelMarket i_fm) {
        Scanner scanner = new Scanner(System.in);
        int input = -1;
        System.out.println("What kind of contract?");
        System.out.println("0. Exit");
        System.out.println("1. Natural Gas Contract");
        Contract new_contract;
        
        try {
            String s_input = scanner.next();
            input = Integer.parseInt(s_input);
        }
        catch (NumberFormatException e) {
            System.out.println("Please choose a valid number.");
        }

        if (input==1) {
            new_contract = buyNGContract(i_gt, i_fm, scanner);
            return new_contract;
        }

        return null;
    }
    
    
    
    public Contract buyNGContract ( GridTime i_gt, FuelMarket i_fm, Scanner scanner) {
        //Check returns from this for null or something so that they can return from this
        int input = -1;
        double discount = 1.;
        double min_mmbtu = 15000.;
        double max_mmbtu = 25000.;
        double price = 0;
        LocalDateTime start_date = i_gt.getDate();
        LocalDateTime end_date = i_gt.getDate();
        long num_months = 1; //the function for LocalDateTime wants a long apparently
        

        System.out.println("How long of a contract do you want?\n"
                         + "1. $4,000:   3 Month for a 5% discount, min/max mmBtu: 15k/25k\n"
                         + "2. $20,000:  6 Month for a 10% discount, min/max mmBtu: 20k/30k\n"
                         + "3. $90,000:  12 Month for a 15% discount, min/max mmBtu: 25k/35k\n"
                         + "4. $300,000: 24 Month for a 20% discount, min/max mmBtu: 30k/40k\n"
                        );
        int check = 0;
        while(check==0) {        
            try {
                String s_input = scanner.next();
                input = Integer.parseInt(s_input);
            }
            catch (NumberFormatException e) {
                System.out.println("Please choose a valid number.");
            }
 //hourly mmbtus for client facing side
            switch (input) {
                case 1:
                    discount = 0.95;
                    check=1;
                    min_mmbtu = 5000.;
                    max_mmbtu = 5500.;
                    num_months = 3;
                    price = 4000;
                    break;
                case 2:
                    discount = 0.9;
                    check=1;
                    min_mmbtu = 20000.;
                    max_mmbtu = 30000.;
                    num_months = 6;
                    price = 20000;
                    break;
                case 3:
                    discount = 0.85;
                    check=1;
                    min_mmbtu = 25000.;
                    max_mmbtu = 35000.;
                    num_months = 12;
                    price = 90000;
                    break;
                case 4:
                    discount = 0.8;
                    check=1;
                    min_mmbtu = 30000.;
                    max_mmbtu = 40000.;
                    num_months = 24;
                    price = 300000;
                    break;
                default:
                    System.out.println("Please choose a valid number.\n");
                    break;
            }
        }
        end_date = end_date.plusMonths(num_months).minusDays(1); //add length of contract minus one day to be the EOM
        
        double discount_price = i_fm.ngPrice() * discount;
        
        System.out.println("Contract for "+(1.-discount)*100.+"% off with a min hourly "+min_mmbtu+"mmBtu and a maximum "+max_mmbtu+"mmBtu purchased.");
        
        NGContract contract = new NGContract(start_date, end_date, discount_price, min_mmbtu, max_mmbtu, price);
        
        return contract;

    }

}