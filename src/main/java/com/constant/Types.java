package com.constant;

/**
* The enumerated types which represent certain Order fields.
* 
* @author Zane Ali
* 
*/
public class Types {
	
	/**
	 * An enumerator type which represents the Order side i.e. B for buy-side
	 * orders or 'bids', and S for sell-side orders or 'asks'.
	 * 
	 */
	public enum SideEnum {
		 B,
		 S
	}
	/**
	 * 
	 * An enumerator type which represents the action to be taken by a trading
	 * venue when it processes an order.
	 */
	public enum ActionEnum {
		NEW,
		AMEND,
		CANCEL
	}
}
