package com.hrl.trade.domain.order;

public enum OrderStatus
{
	NEW,
	PARTIALLY_FILLED,
	FILLED,

	CANCELED,
	REJECTED,
	EXPIRED,
	EXPIRED_IN_MATCH;


	public static boolean isTerminated(String status){
		return status.equalsIgnoreCase(FILLED.toString())
				|| status.equalsIgnoreCase(CANCELED.toString())
				|| status.equals(REJECTED.toString())
				|| status.equals(EXPIRED.toString())
				|| status.equals(EXPIRED_IN_MATCH.toString());
	}
}
