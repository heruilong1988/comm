package com.hrl.trade.common.api.client.binance.spot.rsp;

import com.hrl.trade.common.domain.order.Order;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelOrderRsp {

    public String symbol;
    public String origClientOrderId;
    public Long orderId;

    //Unless part of an order list, the value will always be -1.
    public Long orderListId;
    public String clientOrderId;
    public Long transactTime;
    public Double price;
    public Double origQty;
    public Double executedQty;
    public Double cummulativeQuoteQty;
    public String status;
    public String timeInForce;
    public String type;
    public String side;
    public String selfTradePreventionMode;


    public Order toOrder() {
        Order order =  Order.builder()
                .symbol(symbol)
                .clientOrderId(clientOrderId)
                .orderId(orderId)
                .status(status)
                .build();

        return order;

    }
}
