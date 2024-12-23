package com.hrl.trade.common.domain.orderbook;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SellingBaseCoinTransactionOrder {

    private List<OrderBookEntry> orderBookEntryList = new ArrayList<>();

    private double gettingQuoteCoinQty;

    private double sellingBaseCoinQty;

    //已经够填满一份额度
    private boolean fillOneShare = false;

    public void addOrderBookEntry(OrderBookEntry entry) {
        orderBookEntryList.add(entry);
    }

}
