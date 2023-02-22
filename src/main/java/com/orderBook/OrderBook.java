package com.orderBook;
import java.util.*;

import com.constant.Types.*;
import com.tradingVenue.MasterBook;

/**
 * A class which represents both the bid and ask order book for an exchange
 * instrument (BBGCode). 
 * 
 * The OrderBook class also facilitates order executions between bids and asks. 
 * Each order book is linked to a new instrument since orders can only execute
 * against (opposite) orders with the same instrument code.
 * 
 * Since order books are unique to instruments, the volume aggregation by BBGCode
 * (and price) is performed iteratively in this class, with each new order on 
 * the book. This significantly improves the performance of aggregation compared
 * to other methods demonstrated in this program (in the MasterBook class). 
 * 
 * @author Zane Ali
 *
 */
public class OrderBook {
	
	private Set<Order> bids = new TreeSet<Order>();
	private Set<Order> asks = new TreeSet<Order>();
	
	private Map<Double, Integer> bidsAggregation = new HashMap<Double, Integer>();
	private Map<Double, Integer> asksAggregation = new HashMap<Double, Integer>();
	
	public Set<Order> getBids() {
		return bids;
	}
	
	public Set<Order> getAsks() {
		return asks;
	}
	
	public Map<Double, Integer> getBidsAggregation() {
		return bidsAggregation;
	}
	
	public Map<Double, Integer> getAsksAggregation() {
		return asksAggregation;
	}
	
	
	/**
	 * Directs order to helper classes to perform necessary storage and
	 * processing in the order book.
	 * 
	 * @param order The order to be processed.
	 * @param masterBook A master order book which contains orders for every
	 * order regardless of instrument.
	 */
	public void processOrder(Order order, MasterBook masterBook) {	
		
		Set<Order> orderBook;
		Set<Order> oppositeBook;
		
		Map<Double, Integer> orderAggregation;
		Map<Double, Integer> oppositeAggregation;
				
		if (order.getSide() == SideEnum.B) {	
	        orderBook = bids;
			oppositeBook = asks;
			
			orderAggregation = bidsAggregation;
			oppositeAggregation = asksAggregation;
		} else {
		    orderBook = asks;
		    oppositeBook = bids;	
		    
			orderAggregation = asksAggregation;
			oppositeAggregation = asksAggregation;
		}
		
		ActionEnum action = order.getAction();
		
		switch (action) {
		    case NEW:
		    	newOrder(order, orderBook, oppositeBook, 
		    			 orderAggregation, oppositeAggregation, masterBook);
		    	break;
		    case CANCEL:
		    	cancelOrder(order, orderBook, orderAggregation, masterBook);
		    	break;
		    case AMEND:
		    	amendOrder(order, orderBook, oppositeBook, 
		    			   orderAggregation, oppositeAggregation, masterBook);
		    	break;

		}								
	}
	
	/**
	 * Submits a new order to be stored in the order book and performs
	 * order executions if necessary, reducing or eliminating volume from orders 
	 * in both the bid and ask order books.
	 * 
	 * 
	 * @param order The current order.
	 * @param orderBook The current order book.
	 * @param oppositeBook The order book with the opposite limit to the current 
	 * book.
	 * @param orderAggregation A map to keep track of the prices and aggregated 
	 * volumes of the current book.
	 * @param oppositeAggregation A map to keep track of the prices and 
	 * aggregated volumes of the order book with the opposite limit to the 
	 * current book.
	 * @param masterBook A master order book which contains orders for every
	 * order regardless of instrument.
	 */
	private void newOrder(Order order, Set<Order> orderBook,
			              Set<Order> oppositeBook,
			              Map<Double, Integer> orderAggregation,
			              Map<Double, Integer> oppositeAggregation,
			              MasterBook masterBook) {
				
		Iterator<Order> iterator =  oppositeBook.iterator();
		Order oppositeOrder;

        while (order.getVolume() > 0 && iterator.hasNext() &&
               (oppositeOrder = iterator.next()).getPrice() <= order.getPrice())
        {
            
            int tradeVolume = Math.min(order.getVolume(), 
                                       oppositeOrder.getVolume());

            order.setVolume(order.getVolume() - tradeVolume);
            oppositeOrder.setVolume(oppositeOrder.getVolume() - tradeVolume);
            
            orderAggregation.merge(order.getPrice(), tradeVolume, 
                                                     (a,b) -> Math.abs((a-b)));
            oppositeAggregation.merge(order.getPrice(), tradeVolume, 
                                                     (a,b) -> Math.abs((a-b)));
            
            if(oppositeOrder.getVolume() <= 0) iterator.remove();
        }
        
        if (order.getVolume() > 0) {
        	orderBook.add(order);
        	orderAggregation.merge(order.getPrice(), order.getVolume(), 
        			                                          (a,b) -> (a+b));
        	masterBook.addOrder(order);
        }
	}
	
	/**
	 * Cancels an order in the order book and reduces corresponding volume from
	 * bid and ask aggregation maps.
	 * 
	 * 
	 * @param order The current order.
	 * @param orderBook The current order book.
	 * @param orderAggregation A map to keep track of the prices and aggregated 
	 * volumes of the current book.
	 * @param masterBook A master order book which contains orders for every
	 * order regardless of instrument.
	 */
	private void cancelOrder(Order order, Set<Order> orderBook, 
			                 Map<Double, Integer> orderAggregation,
			                 MasterBook masterbook) {
		
	    Iterator<Order> itr = orderBook.iterator();
	    
	    while(itr.hasNext()) {
	    	itr.next().getTradeID();
	    }

		orderBook.removeIf(o -> o.getTradeID().equals(order.getTradeID()));
		orderAggregation.merge(order.getPrice(), order.getVolume(), 
                                               (a,b) -> Math.abs((a-b)));
	}
	
	/**
	 * Amends an order by simply cancelling the order and submitting the new
	 * order with the amended details.
	 * 
	 * 
	 * @param order The current order.
	 * @param orderBook The current order book.
	 * @param oppositeBook The order book with the opposite limit to the current 
	 * book.
	 * @param orderAggregation A map to keep track of the prices and aggregated 
	 * volumes of the current book.
	 * @param oppositeAggregation A map to keep track of the prices and 
	 * aggregated volumes of the order book with the opposite limit to the 
	 * current book.
	 * @param masterBook A master order book which contains orders for every
	 * order regardless of instrument.
	 */
	private void amendOrder(Order order, Set<Order> orderBook,
                            Set<Order> oppositeBook,
                            Map<Double, Integer> orderAggregation,
                            Map<Double, Integer> oppositeAggregation,
                            MasterBook masterBook) {
		
		cancelOrder(order, orderBook, orderAggregation, masterBook);

    	newOrder(order, orderBook, oppositeBook, 
   			 orderAggregation, oppositeAggregation, masterBook);
	}
	
}
