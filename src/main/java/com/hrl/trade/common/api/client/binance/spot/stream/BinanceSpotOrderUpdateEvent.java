package com.hrl.trade.common.api.client.binance.spot.stream;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hrl.trade.common.api.client.binance.future.um.stream.StreamBinanceUmOrderUpdateEvent;
import com.hrl.trade.common.domain.orderupdate.OrderUpdateEvent;

import java.math.BigDecimal;

import static com.hrl.trade.common.json.Serialization.serializationInstance;

/**
 * {
 *   "e": "executionReport",        // Event type
 *   "E": 1499405658658,            // Event time
 *   "s": "ETHBTC",                 // Symbol
 *   "c": "mUvoqJxFIILMdfAW5iGSOW", // Client order ID
 *   "S": "BUY",                    // Side
 *   "o": "LIMIT",                  // Order type
 *   "f": "GTC",                    // Time in force
 *   "q": "1.00000000",             // Order quantity
 *   "p": "0.10264410",             // Order price
 *   "P": "0.00000000",             // Stop price
 *   "F": "0.00000000",             // Iceberg quantity
 *   "g": -1,                       // OrderListId
 *   "C": "",                       // Original client order ID; This is the ID of the order being canceled
 *   "x": "NEW",                    // Current execution type
 *   "X": "NEW",                    // Current order status
 *   "r": "NONE",                   // Order reject reason; will be an error code.
 *   "i": 4293153,                  // Order ID
 *   "l": "0.00000000",             // Last executed quantity
 *   "z": "0.00000000",             // Cumulative filled quantity
 *   "L": "0.00000000",             // Last executed price
 *   "n": "0",                      // Commission amount
 *   "N": null,                     // Commission asset
 *   "T": 1499405658657,            // Transaction time
 *   "t": -1,                       // Trade ID
 *   "v": 3,                        // Prevented Match Id; This is only visible if the order expired due to STP
 *   "I": 8641984,                  // Ignore
 *   "w": true,                     // Is the order on the book?
 *   "m": false,                    // Is this trade the maker side?
 *   "M": false,                    // Ignore
 *   "O": 1499405658657,            // Order creation time
 *   "Z": "0.00000000",             // Cumulative quote asset transacted quantity
 *   "Y": "0.00000000",             // Last quote asset transacted quantity (i.e. lastPrice * lastQty)
 *   "Q": "0.00000000",             // Quote Order Quantity
 *   "W": 1499405658657,            // Working Time; This is only visible if the order has been placed on the book.
 *   "V": "NONE"                    // SelfTradePreventionMode
 * }
 */
public class BinanceSpotOrderUpdateEvent {

    @JsonAlias("e")
    public String eventType;

    @JsonAlias("E")
    public String eventTime;

    @JsonAlias("s")
    public String symbol;

    @JsonAlias("c")
    public String clientId;

    @JsonAlias("S")
    public String side;

    @JsonAlias("o")
    public String orderType;

    @JsonAlias("f")
    public String timeInForce;

    @JsonAlias("q")
    public String orderQuantity;

    @JsonAlias("p")
    public String orderPrice;

    @JsonAlias("P")
    public String stopPrice;

    @JsonAlias("F")
    public String icebergQuantity;

    @JsonAlias("g")
    public long orderListId;

    //Original client order ID; This is the ID of the order being canceled
    @JsonAlias("C")
    public String originalClientOrderId;

    @JsonAlias("x")
    public String currentExecutionType;

    @JsonAlias("X")
    public String currentOrderStatus;

    @JsonAlias("r")
    public String orderRejectReason;

    @JsonAlias("i")
    public Long orderId;

    @JsonAlias("l")
    public String lastExecutedQuantity;

    @JsonAlias("z")
    public String cumulativeFilledQuantity;

    @JsonAlias("L")
    public String lastExecutedPrice;

    @JsonAlias("n")
    public String commissionAmount;

    @JsonAlias("N")
    public String commissionAsset;

    @JsonAlias("T")
    public Long transactionTime;

    @JsonAlias("t")
    public Long tradeId;

    @JsonAlias("w")
    public Boolean orderOnBook;

    @JsonAlias("m")
    public Boolean tradeMakerSide;

    @JsonAlias("O")
    public Long orderCreationTime;

    // Last quote asset transacted quantity (i.e. lastPrice * lastQty)
    @JsonAlias("Z")
    public String cumulativeQuoteAssetTransactedQuantity;

    @JsonAlias("Q")
    public String quoteOrderQuantity;

    @JsonAlias("W")
    public Long workingTime;

    public static BinanceSpotOrderUpdateEvent fromJson(String orderUpdateEventPayloadStr) {
        try {
            return serializationInstance.getObjectMapper().readValue(orderUpdateEventPayloadStr, BinanceSpotOrderUpdateEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public OrderUpdateEvent toOrderUpdateEvent(){
        return OrderUpdateEvent.builder()
                .clientOrderId(clientId)
                .orderId(orderId)
                .orderFilledAccumulatedQuantity(new BigDecimal(cumulativeFilledQuantity))
                .orderLastFilledQuantity(new BigDecimal(lastExecutedQuantity))
                .side(side)
                .symbol(symbol)
                .orderStatus(currentOrderStatus)
                .orderType(orderType)
                .lastFilledPrice(new BigDecimal(lastExecutedPrice))
                .build();
    }
}
