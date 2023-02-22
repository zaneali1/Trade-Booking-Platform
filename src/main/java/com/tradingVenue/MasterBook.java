package com.tradingVenue;

import static java.util.stream.Collectors.groupingBy;

import java.util.Comparator;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.constant.Types.SideEnum;
import com.orderBook.Order;

/**
 * A master order book which stores orders for every order message regardless of 
 * instrument. The purpose of this master book is to support the venue in 
 * performing brute-force aggregations per Strategy, Portfolio and User. 
 * 
 * An iterative approach with better performance is used for the BBGCode in
 * the TradingVenue class.
 * 
 * @author Zane Ali
 *
 */
public class MasterBook {

	private HashSet<Order> masterBidBook = new HashSet<Order>();
	private HashSet<Order> masterAskBook = new HashSet<Order>();

	/**
	 * Submit a new order to the masterbook.
	 * @param order
	 */
	public void addOrder(Order order) {
		if(order.getSide() == SideEnum.B)
		    masterBidBook.add(order);
		else
			masterAskBook.add(order);
	}
	
    /**
     * Perform an aggregation by the Strategy, Portfolio or User by grouping by
     * both the property (Strategy, Portfolio or User) and Price, and then
     * summing the corresponding list of volumes for each group.
     * 
     * @param input The order property to be aggregated against (Strategy, 
     * Portfolio or User).
     * @param side
     * @return Returns the volume aggregation of orders in a String CSV format 
     * in the order "property,Price,AggregatedVolume".
     */
	public String aggregateOrders(String input, SideEnum side) {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append(input + ",Price,AggregatedVolume\n");
		
		if(side == SideEnum.B) {
			masterBidBook.stream().collect(groupingBy(Function.identity(),
					  ()->new TreeMap<>(						  
					    // Grouping by [property, price]
					    Comparator.<Order,String>
					    comparing(order->getProperty(order, input)).
					    thenComparing(order->order.getPrice())
					  ), 				  
					  // then aggregating the volume
					  Collectors.summingInt(order->order.getVolume())))
					.forEach((group,volumeSum) -> {
					    // take the property and price from the group (the map Key)
						// and the volume from the aggregation (the map Value).
						if(volumeSum != 0)
					        builder.append(getProperty(group, input) + "," +
						             group.getPrice() + "," + volumeSum + "\n");
					});
		} else {
			masterAskBook.stream().collect(groupingBy(Function.identity(),
					  ()->new TreeMap<>(						  
					    // Grouping by [property, price]
					    Comparator.<Order,String>
					    comparing(order->getProperty(order, input)).
					    thenComparing(order->order.getPrice())
					  ), 				  
					  // then aggregating the volume
					  Collectors.summingInt(order->order.getVolume())))
					.forEach((group,volumeSum) -> {
					    // take the property and price from the group (the map Key)
						// and the volume from the aggregation (the map Value).
						if(volumeSum != 0)
					        builder.append(getProperty(group, input) + "," +
						             group.getPrice() + "," + volumeSum + "\n");
					});
		}
	    				

		return builder.toString();
	}
	
	/**
	 * A helper method to retrieve the order value for a certain property (or
	 * input).
	 * 
	 * @param order
	 * @param input
	 * @return The order value corresponding to the input order property.
	 */
	private String getProperty(Order order, String input) {
		String property = "";
		switch(input) {
		    case "Strategy":
		    	property = order.getStrategy();
		    case "Portfolio":
		        property = order.getPortfolio();
		    case "User":
		    	property = order.getUser();		    
		}
		
		return property;
	}
	
}
