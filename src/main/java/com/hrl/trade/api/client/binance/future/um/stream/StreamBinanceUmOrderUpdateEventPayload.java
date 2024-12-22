package com.hrl.trade.api.client.binance.future.um.stream;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.hrl.trade.domain.orderupdate.OrderUpdateEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class StreamBinanceUmOrderUpdateEventPayload {

    @JsonAlias("s")
    public String symbol;

    @JsonAlias("c")
    public String clientOrderId;

    @JsonAlias("S")
    public String side;

    @JsonAlias("o")
    public String orderType;

    @JsonAlias("f")
    public String timeInForce;

    @JsonAlias("q")
    public BigDecimal originalQuantity;

    @JsonAlias("p")
    public BigDecimal originalPrice;

    @JsonAlias("ap")
    public BigDecimal averagePrice;

    @JsonAlias("sp")
    public BigDecimal stopPrice;

    @JsonAlias("x")
    public String executionType;

    @JsonAlias("X")
    public String orderStatus;

    @JsonAlias("i")
    public Long orderId;

    @JsonAlias("l")
    public BigDecimal orderLastFilledQuantity;

    @JsonAlias("z")
    public BigDecimal orderFilledAccumulatedQuantity;

    @JsonAlias("L")
    public BigDecimal lastFilledPrice;

    @JsonAlias("N")
    public String commissionAsset;

    @JsonAlias("n")
    public String commission;

    @JsonAlias("T")
    public Long orderTradeTime;

    @JsonAlias("t")
    public Long tradeId;

    @JsonAlias("b")
    public String bidsNotional;

    @JsonAlias("a")
    public String askNotional;

    @JsonAlias("m")
    public Boolean tradeMakerSide;

    @JsonAlias("R")
    public Boolean reduceOnly;


    @JsonAlias("wt")
    public String stopPriceWorkingType;

    @JsonAlias("ot")
    public String originalOrderType;

    @JsonAlias("ps")
    public String positionSide;

    @JsonAlias("cp")
    public Boolean closeAllPush;

    @JsonAlias("AP")
    public String activationPrice;

    @JsonAlias("cr")
    public String callbackRate;

    @JsonAlias("pP")
    public Boolean priceProtection;

    @JsonAlias("rp")
    public String realizedProfit;

    @JsonAlias("V")
    public String stpMode;

    @JsonAlias("pm")
    public String priceMatchMode;

    @JsonAlias("gtd")
    public Long tifGtdOrderAutoCancelTime;


    public OrderUpdateEvent toOrderUpdateEvent(){
       return OrderUpdateEvent.builder()
                .clientOrderId(clientOrderId)
                .orderId(orderId)
                .orderFilledAccumulatedQuantity(orderFilledAccumulatedQuantity)
                .orderLastFilledQuantity(orderLastFilledQuantity)
                .side(side)
                .symbol(symbol)
                .orderStatus(orderStatus)
                .orderType(orderType)
                .lastFilledPrice(lastFilledPrice)
                .build();
    }
}
