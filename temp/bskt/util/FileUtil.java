package bskt.util;

import java.io.*;
import java.util.ArrayList;

public class FileUtil {
    BufferedWriter writer;
    
    public FileUtil (String filename) {  //This opens my file
        filename = filename + ".csv";
        
        try {
            writer = new BufferedWriter(new FileWriter(filename, true));
            
        } catch (IOException e) {
            System.out.println("Catch that exception boi1");
        }
    }
    
    
    public void writeRows( ArrayList<ArrayList<Object>> rList ) {
        for (ArrayList<Object> row : rList) {
            String row_string = "";
            
            for (Object field : row) {
                row_string = row_string + field.toString() + ","    ;
            }
            
            try {
            row_string = row_string.replace("[", "").replace("]","");
                writer.write(row_string + "\n");
            }
            catch (IOException e) {
                System.out.println("e");
            }
            
        }
    }
    
    public void close() {
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println(e);
        }
       
    }
    
    
    
    
    public void write_test (String tt) {
        try {
            writer.write(tt + "\n");
            writer.flush();
            writer.close();
        } catch ( IOException e) {
            System.out.println("Write test exception");
        }
        
    }

}