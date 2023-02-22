package com.orderBook;

import java.time.*;

import com.constant.Types.*;

/**
* A domain class to represent orders, which are created from order messages to 
* the trading venue.
*  
* @author Zane Ali
* 
*/
public class Order implements Comparable<Order> {
		
	private String tradeId;
	private String bbgCode;
	private String currency;
	private SideEnum side;
	private Double price;
	private int volume;
	private String portfolio;
	private ActionEnum action;
	private String account;
	private String strategy;
	private String user;
	private LocalDateTime tradeTimeUTC;
	
	public Order (String tradeId, String bbgCode, String currency, 
			SideEnum side, Double price, int volume, String portfolio, 
			ActionEnum action, String account, String strategy, String user,
			LocalDateTime tradeTimeUTC)
	{
		this.tradeId = tradeId;
		this.bbgCode = bbgCode;
		this.currency = currency;
		this.side = side;
		this.price = price;
		this.volume = volume;
		this.portfolio = portfolio;
		this.action = action;
		this.account = account;
		this.strategy = strategy;
		this.user = user;
		this.tradeTimeUTC = tradeTimeUTC;
	}
	
    public String getTradeID() {
		return tradeId;
	}
    
    public String getBbgCode() {
		return bbgCode;
	}
    
    public String getCurrency() {
		return currency;
	}
    
    public SideEnum getSide() {
		return side;
    }
    
    public Double getPrice() {
    	return price;
    }

    public int getVolume() {
    	return volume;
    }
    
    public String getPortfolio() {
    	return portfolio;
    }
    
    public ActionEnum getAction() {
    	return action;
    }
    
    public String getAccount() {
		return account;
	}
    
    public String getStrategy() {
		return strategy;
	}
    
    public String getUser() {
		return user;
	}
    
    public LocalDateTime getTradeTimeUTC() {
		return tradeTimeUTC;
	}
    
    public void setVolume(int volume) {
    	this.volume = volume;
    }

	@Override
	public int compareTo(Order o) {
 	   if (this.side == SideEnum.B && this.price > o.price)  
 	      return -1;  
 	   else if (this.side == SideEnum.B && this.price < o.price)  
 	      return 1;  
 	   else if (this.side == SideEnum.S && this.price > o.price)  
 	      return 1;  
 	   else if (this.side == SideEnum.S && this.price < o.price)  
 	      return -1;  	   
 	   else {
 		   if (this.tradeTimeUTC.isBefore(o.tradeTimeUTC))
 			   return -1;
 		   else if (this.tradeTimeUTC.isAfter(o.tradeTimeUTC))
 			   return 1;
 		   else
 			   return o.getTradeID().compareTo(this.getTradeID());
			    		   
 	   }
 	        
	}
    
    
    
}
