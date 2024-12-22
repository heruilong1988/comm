package com.hrl.trade.api.client;

import com.hrl.trade.domain.depth.Depth;
import com.hrl.trade.domain.order.Order;
import com.hrl.trade.domain.orderbook.OrderBook;
import com.hrl.trade.domain.symbol.Symbol;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public interface HClient {


    /***********order******************/
    Order newOrder(Order order);

    Order newTestOrder(Order order);

    //Order queryCurrentOpenOrder(Order order);

    List<Order> currentAllOpenOrders(Order order);

    Order cancelOrder(Order order);

    Order queryOrder(Order order);

    AtomicReference<OrderBook> getOrderBook(Symbol symbol);

    Depth depth(String symbol);


    String getPlatform();




}
