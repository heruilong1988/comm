package com.hrl.trade.domain.orderupdate;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderUpdateEventPayload {

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
    public String originalQuantity;

    @JsonAlias("p")
    public String originalPrice;

    @JsonAlias("ap")
    public String averagePrice;

    @JsonAlias("sp")
    public String stopPrice;

    @JsonAlias("x")
    public String executionType;

    @JsonAlias("X")
    public String orderStatus;

    @JsonAlias("i")
    public Long orderId;

    @JsonAlias("l")
    public String orderLastFilledQuantity;

    @JsonAlias("z")
    public String orderFilledAccumulatedQuantity;

    @JsonAlias("L")
    public String lastFilledPrice;

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
}
