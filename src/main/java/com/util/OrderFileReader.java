package com.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A utility class used to support file loading.
 * 
 * @author Zane Ali
 *
 */
public class OrderFileReader {
    private final FileInputStream inputStream;
    private final Scanner scanner;
        
    public OrderFileReader(String path) throws FileNotFoundException{
        inputStream = new FileInputStream(path);
        scanner = new Scanner(inputStream);
    }

    public boolean hasNext() {
        return  scanner.hasNext();
    }

    public String nextLine() {
        return scanner.nextLine();
    }

    public void close() {
        try {
            inputStream.close();
        } catch (IOException e){
            System.out.println("Resource closing failed:" + e);
        }
        scanner.close();
    }
    
    /**
     * Loads a CSV of order messsasge strings into a 
     * 
     * @param headerExists
     * @return
     * @throws FileNotFoundException
     */
    public List<String> loadOrderMessagesToList (boolean headerExists) 
    		throws FileNotFoundException {
    	
    	List<String> orderMessageList = new ArrayList<String>();
    	
    	if(hasNext() && headerExists) nextLine();
    	
    	while (hasNext()) 
    		orderMessageList.add(nextLine());
    	
    	close();
    
    	return orderMessageList;
    }
}
