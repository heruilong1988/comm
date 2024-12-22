package com.hrl.trade.domain.orderbook;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SellingQuoteCoinTransactionOrder {

    private List<OrderBookEntry> orderBookEntryList = new ArrayList<>();

    private double sellingQuoteCoinQty;

    private double gettingBaseCoinQty;

    private boolean fillOneShare = false;

    public void addOrderBookEntry(OrderBookEntry entry) {
        orderBookEntryList.add(entry);
    }

}
