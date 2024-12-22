package com.hrl.trade.api.client.binance.future.um.stream;

import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.binance.connector.futures.client.impl.UMWebsocketClientImpl;
import com.binance.connector.futures.client.utils.WebSocketCallback;
import com.hrl.trade.api.client.HClient;
import com.hrl.trade.api.client.Platforms;
import com.hrl.trade.api.client.binance.future.um.stream.orderbook.OrderBookMaintainer;
import com.hrl.trade.domain.orderbook.OrderBook;
import com.hrl.trade.domain.orderupdate.OrderUpdateEvent;
import com.hrl.trade.domain.orderupdate.OrderUpdateEventListener;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class HBinanceFutureStreamClient {
    public HClient hClient;
    public UMFuturesClientImpl umFuturesClient;
    public UMWebsocketClientImpl umWebsocketClient;
    public String userDataListenKey;
    public OrderUpdateEventListener orderUpdateEventListener;
    public String platform;

    public List<String> diffDepthStreamSymbols = new ArrayList<>();

    public Map<String, OrderBookMaintainer> symbol2OrderBookMaintainerMap = new HashMap<>();

    public int diffDepthSpeed = 100;

    ///////////////////////USER DATA STREAM ////////////////////////////////
    //订单更新
    public WebSocketCallback userDataStreamCallback = new WebSocketCallback(){

        @Override
        public void onReceive(String data) {
            StreamBinanceUmOrderUpdateEvent streamBinanceUmOrderUpdateEvent = StreamBinanceUmOrderUpdateEvent.fromJson(data);
            OrderUpdateEvent orderUpdateEvent = streamBinanceUmOrderUpdateEvent.getPayload().toOrderUpdateEvent();
            orderUpdateEventListener.listen(orderUpdateEvent);
        }
    };

    public WebSocketCallback onOpenCallbackUserDataStream = (response) -> log.info("platform:{}.UserDataStream open,rsp:{}", platform,response);;


    public WebSocketCallback onFailureCallbackUserDataStream = (data) -> {
        log.error("[Platform:{}] UserDataStream failure,data:{}", platform, data);

        this.reinit();

        //System.exit(1);
    } ;


    public WebSocketCallback onClosedCallbackUserDataStream  = (data) -> {
        log.error("[Platform:{}] UserDataStream closing,data:{}",platform, data);
        //reInitWebSocketStreamClientImpl();
    };

    public int initUserDataStream(){
        /*listenKeyScheduled.scheduleAtFixedRate(this::extendUserDateListenKey,
                30, 30, TimeUnit.MINUTES);*/

        userDataKeyScheduledExecutor.scheduleAtFixedRate(this::extendUserDateListenKey,
                30, 30, TimeUnit.MINUTES);

        return umWebsocketClient.listenUserStream(this.userDataListenKey,onOpenCallbackUserDataStream,userDataStreamCallback,onClosedCallbackUserDataStream,onFailureCallbackUserDataStream);
    }


    ///////USER DATA KEY///////////////////////
    public ScheduledExecutorService userDataKeyScheduledExecutor = Executors.newSingleThreadScheduledExecutor();


    public void extendUserDateListenKey(){
        String userDataListenKeyFromServer = this.umFuturesClient.userData().createListenKey();

        if(this.userDataListenKey != null) {
            if(!this.userDataListenKey.equals(userDataListenKeyFromServer)){
                log.error("userDataListenKey no same.platform:{}",platform);
            }
        } else {
            this.userDataListenKey = userDataListenKeyFromServer;

        }

    }


    //////////////////////////////////DIFF DEPTH STREAM ////////////////////////////////////////////
    public WebSocketCallback onOpenCallbackDiffDepthStream = (response) -> log.info("platform:{}.diffDepthStream open,rsp:{}", platform,response);;

    public WebSocketCallback onClosingCallbackDiffDepthStream = (data) -> {
        log.error("[Platform:{}] diffDepthStream closing,data:{}", platform,data);
        //reInitWebSocketStreamClientImpl();
    };

    public WebSocketCallback onFailureCallbackDiffDepthStream = (data) -> {
        log.error("[Platform:{}] diffDepthStream failure,data:{}", platform, data);

        this.reinit();

        //System.exit(1);
    } ;


    public WebSocketCallback onClosedCallbackDiffDepthStream  = (data) -> {
        log.error("[Platform:{}] diffDepthStream closing,data:{}",platform, data);
        //reInitWebSocketStreamClientImpl();
    };


    public ScheduledExecutorService webSocketReconnectScheduled = Executors.newSingleThreadScheduledExecutor();


    public HBinanceFutureStreamClient(HClient hClient, UMFuturesClientImpl umFuturesClient, OrderUpdateEventListener orderUpdateEventListener) {
        this.orderUpdateEventListener = orderUpdateEventListener;
        this.umFuturesClient = umFuturesClient;
        this.hClient = hClient;
        this.reinit();
    }


    public void reinit() {

        if(this.umWebsocketClient != null) {
            this.umWebsocketClient.closeAllConnections();
        }

        this.umWebsocketClient = new UMWebsocketClientImpl();

        if(webSocketReconnectScheduled != null) {
            webSocketReconnectScheduled.shutdown();

            webSocketReconnectScheduled = Executors.newSingleThreadScheduledExecutor();
            webSocketReconnectScheduled.scheduleAtFixedRate(()->reinit(),
                    12, 12, TimeUnit.HOURS);
        }


        if(this.userDataStreamCallback != null) {
            // this.createUserDateListenKey();
            this.initUserDataStream();
        }else {
            log.info("userDataStreamCallback is null");
        }


        reinitDiffDepthStream();


    }


    public void reinitDiffDepthStream(){
        symbol2OrderBookMaintainerMap.clear();
        //监听depth
        this.diffDepthStreamSymbols.stream().forEach(symbol ->{
            {
                this.reinitDiffDepthStream(symbol);
            }
        });
    }

    public void reinitDiffDepthStream(String symbol){
        OrderBookMaintainer orderBookMaintainer = new OrderBookMaintainer(this.hClient, Platforms.BINANCE_UM_FUTURE);
        symbol2OrderBookMaintainerMap.put(symbol, orderBookMaintainer);
        this.umWebsocketClient.diffDepthStream(symbol, diffDepthSpeed, onOpenCallbackDiffDepthStream,
                orderBookMaintainer.getOnDiffDepthEventMessageCallback(),
                onClosedCallbackDiffDepthStream,
                onFailureCallbackDiffDepthStream);
    }

    public void close(){
        this.umWebsocketClient.closeAllConnections();
    }

    public AtomicReference<OrderBook> getOrderBook(String symbol) {

        if(symbol2OrderBookMaintainerMap.containsKey(symbol)) {
            return this.symbol2OrderBookMaintainerMap.get(symbol).getOrderBookAtomicReference();
        }



        reinitDiffDepthStream(symbol);
        return symbol2OrderBookMaintainerMap.get(symbol).getOrderBookAtomicReference();

    }

}
