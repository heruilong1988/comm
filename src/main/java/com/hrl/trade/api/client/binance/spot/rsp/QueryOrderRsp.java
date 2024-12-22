package com.hrl.trade.api.client.binance.spot.rsp;

import com.hrl.trade.domain.order.Order;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class QueryOrderRsp {

    public String symbol;
    public Long orderId;

    //Unless part of an order list, the value will always be -1.
    public Long orderListId;
    public String clientOrderId;
    public Double price;
    public Double origQty;
    public Double executedQty;
    public Double cummulativeQuoteQty;
    public String status;
    public String timeInForce;
    public String type;
    public String side;
    public String stopPrice;
    public String icebergQty;
    public Long time;
    public Long updateTime;
    public String isWorking;
    public Long workingTime;
    public String origQuoteOrderQty;
    public String selfTradePreventionMode;


    public Order toOrder() {
        Order order =  Order.builder()
                .symbol(symbol)
                .clientOrderId(clientOrderId)
                .orderId(orderId)
                .price(new BigDecimal(price))
                .origQty(new BigDecimal(origQty))
                .executedQty(new BigDecimal(executedQty))
                .cumQuote(new BigDecimal(cummulativeQuoteQty))
                .status(status)
                .timeInForce(timeInForce)
                .type(type)
                .side(side)
                .stopPrice(new BigDecimal(stopPrice))
                .updateTime(updateTime)
                .status(status)
                .build();

        return order;

    }
}
