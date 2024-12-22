package com.hrl.trade.api.client.binance.spot;

import com.hrl.trade.api.client.HClient;
import com.hrl.trade.api.client.Platforms;
import com.hrl.trade.api.client.binance.spot.http.HBinanceSpotRestClient;
import com.hrl.trade.domain.depth.Depth;
import com.hrl.trade.domain.order.Order;
import com.hrl.trade.domain.orderbook.OrderBook;
import com.hrl.trade.domain.symbol.Symbol;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class HBinanceSpotClient implements HClient {

    public HBinanceSpotRestClient hBinanceSpotRestClient;

    @Override
    public Order newOrder(Order order) {
        return hBinanceSpotRestClient.newOrder(order);
    }

    @Override
    public Order newTestOrder(Order order) {
        return hBinanceSpotRestClient.newTestOrder(order);
    }

    @Override
    public List<Order> currentAllOpenOrders(Order order) {
        return hBinanceSpotRestClient.currentAllOpenOrders(order);
    }

    @Override
    public Order cancelOrder(Order order) {
        return hBinanceSpotRestClient.cancelOrder(order);
    }

    @Override
    public Order queryOrder(Order order) {
        return hBinanceSpotRestClient.queryOrder(order);
    }

    @Override
    public AtomicReference<OrderBook> getOrderBook(Symbol symbol) {
        return null;
    }

    @Override
    public Depth depth(String symbol) {
        return hBinanceSpotRestClient.depth(symbol);
    }

    @Override
    public String getPlatform() {
        return Platforms.BINANCE_SPOT.name();
    }
}
