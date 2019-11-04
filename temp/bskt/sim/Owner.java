package bskt.sim;

import bskt.sim.contract.Contract;
import bskt.sim.contract.NGContract;
import bskt.sim.gen.Generator;
import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class Owner {
    static AtomicInteger nextid = new AtomicInteger();
    private int id;
    private String name;
    private ArrayList<Generator>  gen_list;
    private double cash;
    private ArrayList<NGContract> gas_contracts = new ArrayList<NGContract>();
    
    public Owner (String i_name, double i_cash) {
        id = nextid.incrementAndGet();
        name = i_name;
        cash = i_cash;
        gen_list = new ArrayList<Generator>();
    }
        
    
    public String getName() {
        return name;
    }
    
    public void printContracts () {
        System.out.println("NG Contracts:");
        for(int i=0; i<gas_contracts.size(); i++) {
            System.out.println(gas_contracts.get(i).toString());
        }
    }
    
    public void transaction(double amount) { //assuming a negative transaction is a cost and a postive one is a profit
        cash += amount; 
    }
    
    public ArrayList<Generator> getGenerators() {
        return gen_list;
    }
    
    public int getID () {
        return id;
    }
    
    public void addGenerator( Generator gen) {
        gen_list.add(gen);
    }
    
    public double getCash () {
        return cash;
    }
    
    public void addContract ( Contract i_contract, String type) {
        if (type=="NG") {
            gas_contracts.add( (NGContract) i_contract);
        }
    }
    
    public ArrayList<NGContract> getGasContracts () {
        return gas_contracts;
    }
    
    public void cleanContracts(LocalDateTime curdate) {
        //Runs through contracts and removes the ones that are expired
        ArrayList<Integer> rmv_idxs = new ArrayList<Integer>();
        
        for (int i=0;i<gas_contracts.size();i++) {
            if (!gas_contracts.get(i).checkValidity(curdate)) {
                System.out.println("Contract "+gas_contracts.get(i).toString()+" expired.");
                rmv_idxs.add(i);
            }
        }
        
        Collections.sort(rmv_idxs, Collections.reverseOrder());
        for (int i : rmv_idxs) {
            gas_contracts.remove(i);
        }

    }
    
    public void removeContract (int index, String type) {
        if (type == "NG") {
            gas_contracts.remove(index);
        }
    }
    
}