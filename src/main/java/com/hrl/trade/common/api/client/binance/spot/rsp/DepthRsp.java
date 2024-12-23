package com.hrl.trade.common.api.client.binance.spot.rsp;

import com.hrl.trade.common.domain.depth.Depth;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class DepthRsp {

    public Long lastUpdateId;
    public List<List<BigDecimal>> bids;
    public List<List<BigDecimal>> asks;


    public Depth toDepth() {
        return  Depth.builder()
                .lastUpdateId(lastUpdateId)
                .bids(bids)
                .asks(asks)
                .build();
    }
}
