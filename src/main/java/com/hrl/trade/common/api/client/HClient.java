package com.hrl.trade.common.api.client;

import com.hrl.trade.common.api.proxy.MyProxyAuth;
import com.hrl.trade.common.domain.depth.Depth;
import com.hrl.trade.common.domain.order.Order;
import com.hrl.trade.common.domain.orderbook.OrderBook;
import com.hrl.trade.common.domain.symbol.Symbol;

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

    public void setProxy(MyProxyAuth myProxyAuth);



}
