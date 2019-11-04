package bskt.util;

import java.time.*;
import java.time.format.DateTimeFormatter;


public class GridTime {
    LocalDateTime curdate;
    
    public GridTime() {
        String dateString = "01-02-2016 00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
        curdate = LocalDateTime.parse(dateString, formatter);
    }
    
    public LocalDateTime getDate() {
        return curdate;
    }
    
    public int getHour() { //Return hour... just so I don't have to use [object].getDate().getHour();
        return curdate.getHour();
    }
    
    public int getDayOfMonth() { //Return hour... just so I don't have to use [object].getDate().getHour();
        return curdate.getDayOfMonth();
    }
    
    public int getMinute() { //Return hour... just so I don't have to use [object].getDate().getHour();
        return curdate.getMinute();
    }
    
    
    
    
    public void next() {
        curdate = curdate.plusMinutes(5);
    }
    
    
    public String getSeason() {
        int cur_month = curdate.getMonth().getValue();//was Month
        if (cur_month >= 5 && cur_month < 10 ) {
            return "Summer";
        }
        else return "Winter";
    } 

}
