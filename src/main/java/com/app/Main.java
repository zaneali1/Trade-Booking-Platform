package com.app;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.constant.Types.SideEnum;
import com.tradingVenue.TradingVenue;
import com.util.OrderFileReader;

/**
* The main class used to interface with the trading venue via a simple 
* interactive tool.
* 
* @author Zane Ali
* 
*/
public class Main {
	
	/**
	 * The main method.
	 * 
	 * An simple, interactive tool which provides a user the option to:
	 *       - Load and execute all trades from the sample_trades.csv file.
	 *       - Add new trades (on top of what is loaded by the CSV).
	 *       - Load position aggregation results (aggregations of volume per
	 *         price) per BBGCode, per portfolio, per strategy or per user 
	 *         into CSV files. 
	 * 	 
	 * @param args The command line arguments. Unused.
	 * @throws java.io.IOException when keyboard inputs cannot be read.
	 **/
    public static void main(String[] args) throws IOException {
    	
    	TradingVenue venue = new TradingVenue();
    
        InputStreamReader inp = new InputStreamReader(System.in);
        InputStreamReader inp1 = new InputStreamReader(System.in);
        InputStreamReader inp2 = new InputStreamReader(System.in);
        InputStreamReader inp3 = new InputStreamReader(System.in);
        InputStreamReader inp4 = new InputStreamReader(System.in);
        InputStreamReader inp5 = new InputStreamReader(System.in);
        InputStreamReader inp6 = new InputStreamReader(System.in);


        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));

        char c;
        
        do {
        	
        	 System.out.println(
        		   "------------------------------------------------------\n"
        	       + "Choose a numeric option from the following menu:\n"
        		   + "------------------------------------------------------\n" 
  		           + "1. Load the sample trades (SampleTrades.csv) into "
  		           +     "the trading venue\n"
  		           + "2. Add a new trade\n\n"
  		           + "Create/update the csv file for volume aggregations by both price and:\n"
  		           + "3. User\n"
  		           + "4. Portfolio\n"
  		           + "5. Strategy\n"
  		           + "6. BBGCode\n"
  		           + "7. Exit\n" );
        	 
             c = (char) inp.read();

            
            switch (c) {
                case('1'):
                	System.out.println("\nLoading data into the venue...");
                    loadSampleData(venue);
                    System.out.println("\nThe data from src/main/data/resources/sample_trades.csv"
                    		            + " has been loaded into the venue.\n"
                    		            + "Press any key to exit.");
                    inp1.read();
                    break;
                    
                case('2'):
                	System.out.println("\nEnter your order message in the comma-seperated format: \n"
                			+ "TradeID,BBGCode,Currency,Side,Price,Volume,Portfolio,Action,Account,Strategy,User,TradeTimeUTC,ValueDate\n");
                    String message = reader.readLine();
                    try {
                    	venue.submitToOrderBook(message);
                    	System.out.println("\nThe message was processed by the venue.");
                    } catch (Exception e) {
                    	System.out.println("\nThe message was not accepted. Please check the formatting of your message.");
                    }
                    System.out.println("Press any key to continue.");
                    inp2.read();
                    break;
                    
                case ('3'):
                	generateAggregationFile(venue, "User");
                    System.out.println("The bid and ask aggregations per user have been generated in "
                    		+ "src/main/data/output/ as BidAggregationsPerUser.csv and AskAggregationsPerUser.csv.\n" 
                    		+ "Press any key to continue.");
                    inp3.read();
                    break;
                    
                case ('4'):
                	generateAggregationFile(venue, "Portfolio");
                    System.out.println("The bid and ask aggregations per portfolio have been generated in "
                    		+ "src/main/data/output/ as BidAggregationsPerPortfolio.csv and AskAggregationsPerPortfolio.csv.\n" 
                    		+ "Press any key to continue.");
                    inp4.read();
                    break;
                    
                case ('5'):
                	generateAggregationFile(venue, "Strategy");
                    System.out.println("The bid and ask aggregations per strategy have been generated in "
                    		+ "src/main/data/output/ as BidAggregationsPerStrategy.csv and AskAggregationsPerStrategy.csv.\n" 
                    		+ "Press any key to continue.");
                    inp5.read();
                    break;
                    
                case ('6'):
                	generateAggregationFile(venue, "BBGCode");
                System.out.println("The bid and ask aggregations per BBGCode have been generated in "
                		+ "src/main/data/output/ as BidAggregationsPerBBGCode.csv and AskAggregationsPerBBGCode.csv.\n" 
                		+ "Press any key to continue.");
                inp6.read();
                break;
                	

            }
            
        } while (c != '7');

    	
    }
    
	/**
	 * A helper method which read the data from the sample_trades.csv file
	 * into the venue.
	 * 	 
	 * @param venue The trading venue to which orders are loaded.
	 **/
    public static void loadSampleData (TradingVenue venue) {
    	
    	OrderFileReader reader;
    	List<String> orderMessages = null;
        String readPath = "src/main/data/resources/sample_trades.csv";
        
        try {
			reader = new OrderFileReader(readPath);
			orderMessages = reader.loadOrderMessagesToList(true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;		
		}
    	
		venue.handleOrderMessages(orderMessages);
    }
    
    
	/**
	 * A helper method which writes bid and ask aggregation data into 
	 * CSV files per an input property.
	 *  	 
	 * @param venue The trading venue from which orders are extracted.
	 * @param aggregateBy The parameter to be used to aggregate the data.
	 **/
    public static void generateAggregationFile(TradingVenue venue, 
    		                                    String aggregateBy) 
    {
    	
        String bidAggregations = "";
        String askAggregations = "";
    	   	
        String writePath = "src/main/data/output/";      

        
        if(aggregateBy == "BBGCode") {
        	bidAggregations = venue.aggregateByBbgCode(SideEnum.B);
        	askAggregations = venue.aggregateByBbgCode(SideEnum.S);
        } else {
        	bidAggregations = venue.getMasterBook().
        			             aggregateOrders(aggregateBy, SideEnum.B); 
            askAggregations = venue.getMasterBook().
            		              aggregateOrders(aggregateBy, SideEnum.S);                	
        }
        
        
    	try {
			Files.write(Paths.get(writePath + "BidAggregationsPer" + aggregateBy
                                 + ".csv"), bidAggregations.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
    	        
    	try {
			Files.write(Paths.get(writePath + "AskAggregationsPer" + aggregateBy 
					              + ".csv"), askAggregations.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
    }
}
