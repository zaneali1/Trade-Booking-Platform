package com.tradingVenue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.constant.Types.ActionEnum;
import com.constant.Types.SideEnum;
import com.orderBook.Order;
import com.orderBook.OrderBook;

/**
 * The trading venue which receives order messages as strings and allocates them 
 * to their respective order books (which is unique to each BBGCode) and
 * to a master book.
 * 
 * By submitting the order to its order book, the order is also automatically
 * executed if necessary.
 * 
 * @author Zane Ali
 *
 */
public class TradingVenue {
	
	private Map<String, OrderBook> orderBooks = 
                                  new HashMap<String, OrderBook>();		
	private MasterBook masterBook = new MasterBook();
	
	public Map<String, OrderBook> getOrderBooks(){
		return orderBooks;
	}
	
	public MasterBook getMasterBook() {
		return masterBook;		
	}
	
	/**
	 * Submits a list of comma-separated order message strings to the order
	 * book. Useful for loading large sets of data.
	 * 
	 * @param orderMessages
	 */
	public void handleOrderMessages (List<String> orderMessages) {
		
		for (int i = 0; i < orderMessages.size(); i++) {
			submitToOrderBook(orderMessages.get(i));
		}				
	}
	
    /**
     * Submits a singular comma-separated order message string to the order
     * book.
     * 
     * @param orderMessage
     */
    public void submitToOrderBook (String orderMessage) {
    	
    	Order order = parseOrderMessage(orderMessage);

        if (!orderBooks.containsKey(order.getBbgCode())) {
            orderBooks.put(order.getBbgCode(), new OrderBook());		
        }

        OrderBook currentBook = orderBooks.get(order.getBbgCode());		
        currentBook.processOrder(order, masterBook);
    }
    
    
    /**
     * 
     * A helper method to parse an order message and create its corresponding
     *  order object.
     *  
     *  The order message must be in the format:
     *   
     *  TradeID,BBGCode,Currency,Side,Price,Volume,Portfolio,Action,Account,
     *  Strategy,User,TradeTimeUTC,ValueDate

     * Where each order is unique by its TradeID, the Side is set to 'B' or 'S'
     * (buy-side or sell-side orders), the price is numeric, the volume is a 
     * numeric integer, the Action is set to 'New', 'Amend' or 'Cancel' and 
     * the TradeTimeUTC is in the format: yyyy-MM-ddTHH:mm:ss.SSSSSS.
     * 
     * @param orderMessage
     * @return The order object corresponding to the input order message string.
     */
	private Order parseOrderMessage(String orderMessage) {
		
    	String[] orderData = orderMessage.split(",");
    	
    	DateTimeFormatter formatter = 
    			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    	LocalDateTime timestamp = LocalDateTime.parse(orderData[11], formatter);

    	
    	Order order = new Order(orderData[0],
    			                orderData[1], 
    			                orderData[2],
    			                SideEnum.valueOf(orderData[3]),
    			                Double.valueOf(orderData[4]),
    			                Integer.valueOf(orderData[5]),
    			                orderData[6],
    			                ActionEnum.valueOf(orderData[7]),
    			                orderData[8],
    			                orderData[9],
    			                orderData[10],
    			                timestamp);
    	
        return order;
	}
	
	/**
	 * A method which aggregates the BBGCode by volume using an iterative
	 * approach.
	 * 
	 * @param side
	 * @return Returns the volume aggregation of orders in a String CSV format 
     * in the order "BBGCode,Price,AggregatedVolume".
	 */
	public String aggregateByBbgCode(SideEnum side) {	
		
		StringBuilder builder = new StringBuilder();
		builder.append("BBGCode, Price, AggregatedVolume\n");		
			
		for(Map.Entry<String, OrderBook> orderBookEntry : orderBooks.entrySet()) 
		{
			
			if(side == SideEnum.B) {
				for(Map.Entry<Double, Integer> aggregationEntry : 
					orderBookEntry.getValue().getBidsAggregation().entrySet()) {
					
					builder.append(orderBookEntry.getKey() + "," +
							       aggregationEntry.getKey() + "," +
							       aggregationEntry.getValue() + "\n");
				}
			} else {
				for(Map.Entry<Double, Integer> aggregationEntry : 
					orderBookEntry.getValue().getBidsAggregation().entrySet()) {
					
					builder.append(orderBookEntry.getKey() + "," +
							       aggregationEntry.getKey() + "," +
							       aggregationEntry.getValue() + "\n");
				}
			}								

		}
		
		return builder.toString();
	}
	
	
}
