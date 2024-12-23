package com.hrl.trade.common.api.client.binance.spot.http;

import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.impl.spot.Market;
import com.binance.connector.client.impl.spot.Trade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrl.trade.common.api.client.binance.spot.constants.ConstantKey;
import com.hrl.trade.common.api.client.binance.spot.rsp.CancelOrderRsp;
import com.hrl.trade.common.api.client.binance.spot.rsp.DepthRsp;
import com.hrl.trade.common.api.client.binance.spot.rsp.QueryOrderRsp;
import com.hrl.trade.common.domain.depth.Depth;
import com.hrl.trade.common.domain.order.Order;
import com.hrl.trade.common.json.Serialization;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
public class HBinanceSpotRestClient {

    public SpotClientImpl spotClient;

    public Trade trade;

    public Market market;

    public HBinanceSpotRestClient(String apiKey, String secretKey) {
        this.spotClient = new SpotClientImpl(apiKey,secretKey);
        this.trade = spotClient.createTrade();
        this.market = spotClient.createMarket();
    }

    public HBinanceSpotRestClient(String apiKey, String secretKey, String baseUrl) {
        this.spotClient =new  SpotClientImpl(apiKey, secretKey, baseUrl);
        this.trade = spotClient.createTrade();
        this.market = spotClient.createMarket();


    }
    public Order newOrder(Order order) {

        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        parameters.put("symbol", order.getSymbol());
        parameters.put("side", order.getSide());
        parameters.put("type", order.getType());
        parameters.put("quantity", order.getOrigQty().doubleValue());
        parameters.put("price", order.getPrice().doubleValue());

        if(order.getTimeInForce() != null) {
            parameters.put("timeInForce", order.getTimeInForce());
        }

        String  rspOrderStr = trade.newOrder(parameters);

        ObjectMapper objectMapper = Serialization.serializationInstance.getObjectMapper();
        Order rspOrder = null;
        try {
            rspOrder = objectMapper.readValue(rspOrderStr, Order.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return rspOrder;
    }

    public Order newTestOrder(Order order) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        parameters.put("symbol", order.getSymbol());
        parameters.put("side", order.getSide());
        parameters.put("type", order.getType());
        parameters.put("quantity", order.getOrigQty().doubleValue());
        parameters.put("price", order.getPrice().doubleValue());

        if(order.getTimeInForce() != null) {
            parameters.put("timeInForce", order.getTimeInForce());
        }

        String  rspOrderStr = trade.testNewOrder(parameters);

        ObjectMapper objectMapper = Serialization.serializationInstance.getObjectMapper();
        Order rspOrder = null;
        try {
            rspOrder = objectMapper.readValue(rspOrderStr, Order.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return rspOrder;
    }

   /* @Override
    public Order queryCurrentOpenOrder(Order order) {
        LinkedHashMap param = new LinkedHashMap();
        param.put("clientOrderId", order.getClientOrderId());
        String orderStr = trade.getOpenOrders(param);
        ObjectMapper objectMapper = Serialization.serializationInstance.getObjectMapper();
        Order rspOrder = null;
        try {
            rspOrder = objectMapper.readValue(orderStr, Order.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return rspOrder;
    }*/

    public List<Order> currentAllOpenOrders(Order order) {

        LinkedHashMap param = new LinkedHashMap();
        param.put(ConstantKey.SYMBOL, order.getSymbol());
        String orderStr = trade.getOpenOrders(param);
        ObjectMapper objectMapper = Serialization.serializationInstance.getObjectMapper();
        List<Order> rspOrderList = null;
        try {
            rspOrderList = objectMapper.readValue(orderStr, new TypeReference<List<Order>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return rspOrderList;
    }

    public Order cancelOrder(Order order) {
        LinkedHashMap param = new LinkedHashMap();
        param.put(ConstantKey.ORDER_ID, order.getOrderId());
        String orderStr = trade.cancelOrder(param);

        ObjectMapper objectMapper = Serialization.serializationInstance.getObjectMapper();
        CancelOrderRsp rspOrder = null;
        try {
            rspOrder = objectMapper.readValue(orderStr, CancelOrderRsp.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return rspOrder.toOrder();
    }

    public Order queryOrder(Order order) {
        LinkedHashMap param = new LinkedHashMap();
        param.put(ConstantKey.SYMBOL, order.getSymbol());
        if(order.getClientOrderId() != null) {
            param.put("origClientOrderId", order.getClientOrderId());
        }

        if(order.getOrderId() != null) {
            param.put(ConstantKey.ORDER_ID, order.getOrderId());
        }
        String orderStr = trade.getOrder(param);

        ObjectMapper objectMapper = Serialization.serializationInstance.getObjectMapper();
        QueryOrderRsp rspOrder = null;
        try {
            rspOrder = objectMapper.readValue(orderStr, QueryOrderRsp.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return rspOrder.toOrder();
    }

    public Depth depth(String symbol) {
        LinkedHashMap param = new LinkedHashMap();
        param.put(ConstantKey.SYMBOL, symbol);

        String depthStr = market.depth(param);

        ObjectMapper objectMapper = Serialization.serializationInstance.getObjectMapper();
        DepthRsp depthRsp = null;
        try {
            depthRsp = objectMapper.readValue(depthStr, DepthRsp.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return depthRsp.toDepth();

    }

    }
