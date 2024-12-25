package com.hrl.trade.common.api.client.binance.future.um;

import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.binance.connector.futures.client.impl.futures.Account;
import com.binance.connector.futures.client.impl.futures.Market;
import com.binance.connector.futures.client.impl.futures.UserData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrl.trade.common.api.client.ClientConfig;
import com.hrl.trade.common.api.client.HClient;
import com.hrl.trade.common.api.client.Platforms;
import com.hrl.trade.common.api.client.binance.future.proxy.Utils;
import com.hrl.trade.common.api.client.binance.future.um.stream.HBinanceFutureStreamClient;
import com.hrl.trade.common.api.proxy.MyProxyAuth;
import com.hrl.trade.common.domain.depth.Depth;
import com.hrl.trade.common.domain.order.Order;
import com.hrl.trade.common.domain.orderbook.OrderBook;
import com.hrl.trade.common.domain.orderupdate.OrderUpdateEventListener;
import com.hrl.trade.common.domain.symbol.Symbol;
import com.hrl.trade.common.json.Serialization;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


@Slf4j
public class HBinanceUmFutureClient implements HClient {

    public static String platform = "BinanceUmFuture";


    //////////////// WEB SOCKEET STREAM ///////////////////////////////////////////
    HBinanceFutureStreamClient hBinanceFutureStreamClient;


    public OrderUpdateEventListener orderUpdateEventListener;

    ///////////////////////////// REST FUTURE ////////////////////////////////////

    public UMFuturesClientImpl umFuturesClient;
    public Account account;
    public Market market;
    public UserData userData;



    ///////////////////////////// client //////////////////////////
    //public ScheduledExecutorService listenKeyScheduled = Executors.newSingleThreadScheduledExecutor();
    //public List<String> listenSymbols = Collections.synchronizedList(new ArrayList<String>());

    public HBinanceUmFutureClient(OrderUpdateEventListener orderUpdateEventListener) {

        this.umFuturesClient = new UMFuturesClientImpl(ClientConfig.BINANCE_UM_FUTURE_ACCESS_KEY, ClientConfig.BINANCE_UM_FUTURE_SECRET_KEY);

        this.account = umFuturesClient.account();
        this.market = umFuturesClient.market();
        this.userData = umFuturesClient.userData();
        this.orderUpdateEventListener = orderUpdateEventListener;


        this.hBinanceFutureStreamClient = new HBinanceFutureStreamClient(this, this.umFuturesClient, orderUpdateEventListener);

    }

    @Override
    public void setProxy(MyProxyAuth myProxyAuth) {
        umFuturesClient.setProxy(Utils.fromMyProxy(myProxyAuth));
    }


    @Override
    public Order newOrder(Order order) {

        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        parameters.put("symbol", order.getSymbol());
        parameters.put("side", order.getSide());
        parameters.put("type", order.getType());

        parameters.put("quantity", order.getOrigQty().doubleValue());
        if(order.getPrice() != null) {
            parameters.put("price", order.getPrice().doubleValue());
        }
        if(order.getTimeInForce() != null) {
            parameters.put("timeInForce", order.getTimeInForce());
        }

        String result = account.newOrder(parameters);

        log.info("newOrder result:{},order:{}", result,order);

        ObjectMapper objectMapper = Serialization.serializationInstance.getObjectMapper();
        Order rspOrder = null;
        try {
            rspOrder = objectMapper.readValue(result, Order.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return rspOrder;

    }

    @Override
    public Order newTestOrder(Order order) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        parameters.put("symbol", order.getSymbol());
        parameters.put("side", order.getSide());
        parameters.put("type", order.getType());
        parameters.put("quantity", order.getOrigQty().doubleValue());

        if(order.getPrice() != null) {
            parameters.put("price", order.getPrice().doubleValue());
        }
        if(order.getTimeInForce() != null) {
            parameters.put("timeInForce", order.getTimeInForce());
        }

        String result = account.newTestOrder(parameters);

        log.info("newTestOrder.order:{},result:{}", order, result);

        ObjectMapper objectMapper = Serialization.serializationInstance.getObjectMapper();
        Order rspOrder = null;
        try {
            rspOrder = objectMapper.readValue(result, Order.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return rspOrder;
    }

  /*  @Override
    public Order queryCurrentOpenOrder(Order order) {
        LinkedHashMap param = new LinkedHashMap();
        param.put("clientOrderId", order.getClientOrderId());
        String orderStr = account.queryOrder(param);
        ObjectMapper objectMapper = Serialization.serializationInstance.getObjectMapper();
        Order rspOrder = null;
        try {
            rspOrder = objectMapper.readValue(orderStr, Order.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return rspOrder;
    }*/

    @Override
    public List<Order> currentAllOpenOrders(Order order) {
        LinkedHashMap param = new LinkedHashMap();
        param.put("symbol", order.getSymbol());
        String orderStr = account.currentAllOpenOrders(param);

        ObjectMapper objectMapper = Serialization.serializationInstance.getObjectMapper();
        try {
            return objectMapper.readValue(orderStr, new TypeReference<List<Order>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Order cancelOrder(Order order) {
        LinkedHashMap param = new LinkedHashMap();

        param.put("orderId", order.getOrderId());
        param.put("clientOrderId", order.getClientOrderId());
        String orderStr = account.cancelOrder(param);

        ObjectMapper objectMapper = Serialization.serializationInstance.getObjectMapper();
        Order rspOrder = null;
        try {
            rspOrder = objectMapper.readValue(orderStr, Order.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return rspOrder;
    }

    @Override
    public Order queryOrder(Order order) {
        LinkedHashMap param = new LinkedHashMap();

        param.put("symbol", order.getSymbol());

        if(order.getClientOrderId() != null) {
            param.put("origClientOrderId", order.getClientOrderId());
        }

        if(order.getOrderId() != null) {
            param.put("orderId", order.getOrderId());
        }

        String orderStr = account.queryOrder(param);

        ObjectMapper objectMapper = Serialization.serializationInstance.getObjectMapper();
        Order rspOrder = null;
        try {
            rspOrder = objectMapper.readValue(orderStr, Order.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return rspOrder;
    }


    @Override
    public Depth depth(String symbol) {
        LinkedHashMap param = new LinkedHashMap();
        param.put("symbol", symbol);
        String depthString = market.depth(param);

        Depth depth = null;
        try {
            depth = Serialization.serializationInstance.getObjectMapper().readValue(depthString, Depth.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize depthString", e);
        }

        return depth;
    }

    public AtomicReference<OrderBook> getOrderBook(Symbol symbol){

       return this.hBinanceFutureStreamClient.getOrderBook(symbol);

    }


    @Override
    public String getPlatform() {
        return Platforms.BINANCE_SPOT.name();
    }
}
