package com.hrl.trade.common.api.client.binance.spot;

import com.hrl.trade.common.api.client.binance.spot.http.HBinanceSpotRestClient;
import com.hrl.trade.common.api.client.HClient;
import com.hrl.trade.common.api.client.Platforms;
import com.hrl.trade.common.api.client.binance.spot.constants.BinanceSpotPrivateConfig;
import com.hrl.trade.common.api.client.binance.spot.proxy.Utils;
import com.hrl.trade.common.api.client.binance.spot.stream.HBinanceSpotStreamClient;
import com.hrl.trade.common.api.proxy.MyProxyAuth;
import com.hrl.trade.common.domain.depth.Depth;
import com.hrl.trade.common.domain.order.Order;
import com.hrl.trade.common.domain.orderbook.OrderBook;
import com.hrl.trade.common.domain.orderupdate.OrderUpdateEventListener;
import com.hrl.trade.common.domain.symbol.Symbol;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class HBinanceSpotClient implements HClient {

    public HBinanceSpotRestClient hBinanceSpotRestClient;
    public HBinanceSpotStreamClient hBinanceSpotStreamClient;


    public HBinanceSpotClient(OrderUpdateEventListener orderUpdateEventListener) {
        this.hBinanceSpotRestClient = new HBinanceSpotRestClient(BinanceSpotPrivateConfig.apiKey, BinanceSpotPrivateConfig.secretKey);
        this.hBinanceSpotStreamClient = new HBinanceSpotStreamClient(this, hBinanceSpotRestClient.getSpotClient(),orderUpdateEventListener);
    }

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
        return hBinanceSpotStreamClient.getOrderBook(symbol.getPair());
    }

    @Override
    public Depth depth(String symbol) {
        return hBinanceSpotRestClient.depth(symbol);
    }

    @Override
    public String getPlatform() {
        return Platforms.BINANCE_SPOT.name();
    }

    @Override
    public void setProxy(MyProxyAuth myProxyAuth) {
        hBinanceSpotRestClient.setProxy(Utils.fromMyProxy(myProxyAuth));

    }
}
