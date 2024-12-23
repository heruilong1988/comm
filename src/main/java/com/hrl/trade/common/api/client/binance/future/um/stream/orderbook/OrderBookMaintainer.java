package com.hrl.trade.common.api.client.binance.future.um.stream.orderbook;

import com.binance.connector.futures.client.utils.WebSocketCallback;
import com.hrl.trade.common.api.client.Platforms;
import com.hrl.trade.common.api.client.HClient;
import com.hrl.trade.common.api.client.binance.future.um.stream.StreamBinanceUmFutureDiffDepthEvent;
import com.hrl.trade.common.domain.depth.Depth;
import com.hrl.trade.common.domain.orderbook.OrderBook;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Getter
@Setter
public class OrderBookMaintainer {

    public AtomicReference<OrderBook> orderBookAtomicReference;

    public String symbol;

    public Long lastUpdateIdInSnapshot;

    public StreamBinanceUmFutureDiffDepthEvent previousDiffDepthEvent;

    public boolean firstProcessed;

    //标识
    public volatile boolean orderBookReInitializing;

    public String platformName;


      public HClient restBinanceUmFutureClient;

    //public  UMFuturesClientImpl restBinanceUmFutureClient;

    public OrderBookMaintainer(HClient restBinanceUmFutureClient, Platforms platforms) {
        this.restBinanceUmFutureClient = restBinanceUmFutureClient;
        this.platformName = platforms.name();
    }

    /**
     * 1.Open a stream to wss://fstream.binance.com/stream?streams=btcusdt@depth.
     * 2.Buffer the events you receive from the stream. For same price, latest received update covers the previous one.
     * 3.Get a depth snapshot from https://fapi.binance.com/fapi/v1/depth?symbol=BTCUSDT&limit=1000 .
     * 4.Drop any event where u is < lastUpdateId in the snapshot.
     * 5.The first processed event should have U <= lastUpdateId**AND**u >= lastUpdateId
     * 6.While listening to the stream, each new event's pu should be equal to the previous event's u, otherwise initialize the process from step 3.
     * 7.The data in each event is the absolute quantity for a price level.
     * 8.If the quantity is 0, remove the price level.
     * 9.Receiving an event that removes a price level that is not in your local order book can happen and is normal.
     */
    public WebSocketCallback onDiffDepthEventMessageCallback = new WebSocketCallback() {
        @Override
        public void onReceive(String data) {

            //log.debug("onmsg,timestamp:{},thread:{}", LocalDateTime.now(),Thread.currentThread().getId());
            //orderBookAtomicReference.get().simplePrint();


            StreamBinanceUmFutureDiffDepthEvent streamBinanceUmFutureDiffDepthEvent = StreamBinanceUmFutureDiffDepthEvent.fromJson(data);

            if(lastUpdateIdInSnapshot == null) {
                // 没有snapshot
                reinitializingOrderBookSnapshot(streamBinanceUmFutureDiffDepthEvent);
            }

            if(streamBinanceUmFutureDiffDepthEvent.getLastUpdateId() < lastUpdateIdInSnapshot) {
                log.error("event lastUpdatedId:{} < lastUpdateIdInSnapshot:{}, ignore event.symbol:{}",
                        streamBinanceUmFutureDiffDepthEvent.getLastUpdateId(), lastUpdateIdInSnapshot,symbol);
                return;
            }


            if(!firstProcessed) {

                if(streamBinanceUmFutureDiffDepthEvent.getFirstUpdateId() < lastUpdateIdInSnapshot &&  lastUpdateIdInSnapshot < streamBinanceUmFutureDiffDepthEvent.getLastUpdateId() ) {
                    log.info("first processed event. depthEventFirstUpdateId:{} < lastUpdateIdInSnapshot:{} && " +
                                    "lastUpdateIdInSnapshot:{} < depthEvent.LastUpdateId:{}", streamBinanceUmFutureDiffDepthEvent.getFirstUpdateId(),
                            lastUpdateIdInSnapshot, lastUpdateIdInSnapshot, streamBinanceUmFutureDiffDepthEvent.getLastUpdateId());

                    //first processed
                    firstProcessed = true;
                    orderBookAtomicReference.get().addAskList(streamBinanceUmFutureDiffDepthEvent.getAsks());
                    orderBookAtomicReference.get().addBidList(streamBinanceUmFutureDiffDepthEvent.getBids());
                    previousDiffDepthEvent = streamBinanceUmFutureDiffDepthEvent;
                    orderBookReInitializing = false;
                    return;
                }


                //不在范围中，不处理此事件
                return;
            }

            //firstProcess已经处理过

            if (streamBinanceUmFutureDiffDepthEvent.getLastUpdateIdInlastStream().equals(previousDiffDepthEvent.getLastUpdateId())) {
                orderBookAtomicReference.get().addAskList(streamBinanceUmFutureDiffDepthEvent.getAsks());
                orderBookAtomicReference.get().addBidList(streamBinanceUmFutureDiffDepthEvent.getBids());
                previousDiffDepthEvent = streamBinanceUmFutureDiffDepthEvent;
            } else {
                // reinitializing
                log.info("lastUpdateIdInLastStream:{} != previousDiffDepthEvent.LastUpdateId:{}, then reset()", streamBinanceUmFutureDiffDepthEvent.getLastUpdateIdInlastStream(),
                        previousDiffDepthEvent.getLastUpdateId());
                resetData();
            }

        }
    };

    public void reinitializingOrderBookSnapshot(StreamBinanceUmFutureDiffDepthEvent event) {
        log.info("reinitializing platform:{},symbol:{}", platformName, event.getSymbol());

        Depth depth =  restBinanceUmFutureClient.depth(event.getSymbol());

        //save depth in orderbook
        OrderBook orderBook = orderBookAtomicReference.get();

        orderBook.initBidList(depth.getBids());
        orderBook.initAskList(depth.getAsks());
        orderBook.setInit(true);

        this.lastUpdateIdInSnapshot = depth.getLastUpdateId();

    }

    public void resetData(){
        this.orderBookAtomicReference.get().clear();
        this.firstProcessed = false;
        this.lastUpdateIdInSnapshot = null;
        this.previousDiffDepthEvent = null;
        this.orderBookReInitializing = true;
        //this.myWebsocketClient.
    }
}
