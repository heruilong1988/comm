package com.hrl.trade.common.api.client.binance.spot.rsp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class BinanceSpotDiffDepthEvent {


    @JsonProperty("e")
    private String eventType;

    @JsonProperty("E")
    private Long eventTime;

    @JsonProperty("s")
    private String symbol;

    @JsonProperty("U")
    private Long firstUpdateId;

    @JsonProperty("u")
    private Long lastUpdateId;

    @JsonProperty("pu")
    private Long lastUpdateIdInlastStream;

    @JsonProperty("b")
    private List<List<BigDecimal>> bids;

    @JsonProperty("a")
    private List<List<BigDecimal>> asks;

}
