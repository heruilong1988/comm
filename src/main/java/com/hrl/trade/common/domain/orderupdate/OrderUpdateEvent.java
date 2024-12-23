package com.hrl.trade.common.domain.orderupdate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;


/**
 *
 * Side
 *
 *     BUY
 *     SELL
 *
 * Order Type
 *
 *     MARKET
 *     LIMIT
 *     STOP
 *     TAKE_PROFIT
 *     LIQUIDATION
 *
 * Execution Type
 *
 *     NEW
 *     CANCELED
 *     CALCULATED - Liquidation Execution
 *     EXPIRED
 *     TRADE
 *     AMENDMENT - Order Modified
 *
 * Order Status
 *
 *     NEW
 *     PARTIALLY_FILLED
 *     FILLED
 *     CANCELED
 *     EXPIRED
 *     EXPIRED_IN_MATCH
 *
 * Time in force
 *
 *     GTC
 *     IOC
 *     FOK
 *     GTX
 *
 * Working Type
 *
 *     MARK_PRICE
 *     CONTRACT_PRICE
 *
 * Liquidation and ADL:
 *
 *     If user gets liquidated due to insufficient margin balance:
 *         c shows as "autoclose-XXX"，X shows as "NEW"
 *
 *     If user has enough margin balance but gets ADL:
 *         c shows as “adl_autoclose”，X shows as “NEW”
 */
@Getter
@Setter
@ToString
@Builder
public class OrderUpdateEvent {

    public String symbol;

    public String clientOrderId;

    public String side;

    public String orderType;

    public String orderStatus;

    public Long orderId;

    public BigDecimal orderLastFilledQuantity;

    public BigDecimal orderFilledAccumulatedQuantity;

    public BigDecimal lastFilledPrice;


    public boolean isLastFilled(){
        return orderLastFilledQuantity != null && orderLastFilledQuantity.doubleValue() > 0;
    }

}
