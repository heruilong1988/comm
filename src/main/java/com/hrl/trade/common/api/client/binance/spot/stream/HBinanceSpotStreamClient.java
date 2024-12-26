package com.hrl.trade.common.api.client.binance.spot.stream;

import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.impl.WebSocketStreamClientImpl;
import com.binance.connector.client.utils.websocketcallback.*;
import com.hrl.trade.common.api.client.HClient;
import com.hrl.trade.common.api.client.Platforms;
import com.hrl.trade.common.api.client.binance.future.um.http.UserDataListenKey;
import com.hrl.trade.common.api.client.binance.future.um.stream.StreamBinanceUmOrderUpdateEvent;
import com.hrl.trade.common.api.client.binance.spot.stream.orderbook.BinanceSpotOrderBookMaintainer;
import com.hrl.trade.common.domain.orderbook.OrderBook;
import com.hrl.trade.common.domain.orderupdate.OrderUpdateEvent;
import com.hrl.trade.common.domain.orderupdate.OrderUpdateEventListener;
import com.hrl.trade.common.json.Serialization;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class HBinanceSpotStreamClient {

    public HClient hClient;
    public SpotClientImpl spotRestClient;
    public WebSocketStreamClientImpl websocketClient;
    public String userDataListenKey;
    public OrderUpdateEventListener orderUpdateEventListener;
    public String platform;

    public List<String> diffDepthStreamSymbols = new ArrayList<>();

    public Map<String, BinanceSpotOrderBookMaintainer> symbol2OrderBookMaintainerMap = new HashMap<>();

    public int diffDepthSpeed = 100;


    ///////////////////////USER DATA STREAM ////////////////////////////////
    //订单更新
    public WebSocketMessageCallback userDataStreamCallback = new WebSocketMessageCallback(){

        @Override
        public void onMessage(String data) {

            log.info("onMessage: {}" + data);

            JSONObject eventData = new JSONObject(data);
            String event = eventData.getString("event");
            if(event.equalsIgnoreCase("executionReport")) {
                BinanceSpotOrderUpdateEvent binanceSpotOrderUpdateEvent = BinanceSpotOrderUpdateEvent.fromJson(data);
                OrderUpdateEvent orderUpdateEvent = binanceSpotOrderUpdateEvent.toOrderUpdateEvent();
                orderUpdateEventListener.listen(orderUpdateEvent);
            }

        }
    };

    public WebSocketOpenCallback onOpenCallbackUserDataStream = (response) -> log.info("platform:{}.UserDataStream open,rsp:{}", platform,response);;


    public WebSocketFailureCallback onFailureCallbackUserDataStream = (t, data) -> {
        log.error("[Platform:{}] UserDataStream failure,data:{}", platform, data, t);

        this.reinit();

        //System.exit(1);
    } ;


    public WebSocketClosedCallback onClosedCallbackUserDataStream  = (code,data) -> {
        log.error("[Platform:{}] UserDataStream closed,code:{},data:{}",platform, code, data);
        //reInitWebSocketStreamClientImpl();
    };

    public WebSocketClosingCallback onClosingCallbackUserDataStream  = (code, data) -> {
        log.error("[Platform:{}] UserDataStream closing,code:{},data:{}",platform, code, data);
        //reInitWebSocketStreamClientImpl();
    };

    public int initUserDataStream(){
        /*listenKeyScheduled.scheduleAtFixedRate(this::extendUserDateListenKey,
                30, 30, TimeUnit.MINUTES);*/

        userDataKeyScheduledExecutor.scheduleAtFixedRate(this::extendUserDateListenKey,
                30, 30, TimeUnit.MINUTES);

        return websocketClient.listenUserStream(this.userDataListenKey,onOpenCallbackUserDataStream,userDataStreamCallback,onClosingCallbackUserDataStream,onClosedCallbackUserDataStream,onFailureCallbackUserDataStream);
    }


    ///////USER DATA KEY///////////////////////
    public ScheduledExecutorService userDataKeyScheduledExecutor = Executors.newSingleThreadScheduledExecutor();


    @SneakyThrows
    public void extendUserDateListenKey(){
        //String userDataListenKeyFromServer = this.spotRestClient.createUserData().createListenKey();

        String userDataListenKeyFromServer = Serialization.serializationInstance.getObjectMapper().readValue(this.spotRestClient.createUserData().createListenKey(), UserDataListenKey.class).getListenKey();


        if(this.userDataListenKey != null) {
            if(!this.userDataListenKey.equals(userDataListenKeyFromServer)){
                log.error("userDataListenKey no same.platform:{}",platform);
            }
        } else {
            log.info("set userDataListenKey={}", userDataListenKeyFromServer);
            this.userDataListenKey = userDataListenKeyFromServer;

        }

    }


    //////////////////////////////////DIFF DEPTH STREAM ////////////////////////////////////////////
    public WebSocketOpenCallback onOpenCallbackDiffDepthStream = (response) -> log.info("platform:{}.diffDepthStream open,rsp:{}", platform,response);;

    public WebSocketClosingCallback onClosingCallbackDiffDepthStream = (code,data) -> {
        log.error("[Platform:{}] diffDepthStream closing,code:{},data:{}", platform,code,data);
        //reInitWebSocketStreamClientImpl();
    };

    public WebSocketFailureCallback onFailureCallbackDiffDepthStream = (t, data) -> {
        log.error("[Platform:{}] diffDepthStream failure,data:{}", platform, data, t);

        //this.reinit();
        //System.exit(1);
    } ;


    public WebSocketClosedCallback onClosedCallbackDiffDepthStream  = (code,data) -> {
        log.error("[Platform:{}] diffDepthStream closed,code:{}, data:{}",platform, code, data);
        //reInitWebSocketStreamClientImpl();
    };

   /* public WebSocketCallback onClosedCallbackDiffDepthStream  = (data) -> {
        log.error("[Platform:{}] diffDepthStream closing,data:{}",platform, data);
        //reInitWebSocketStreamClientImpl();
    };*/


    public ScheduledExecutorService webSocketReconnectScheduled = Executors.newSingleThreadScheduledExecutor();


    public HBinanceSpotStreamClient(HClient hClient, SpotClientImpl spotRestClient, OrderUpdateEventListener orderUpdateEventListener) {
        this.orderUpdateEventListener = orderUpdateEventListener;
        this.spotRestClient = spotRestClient;
        this.hClient = hClient;
        this.reinit();
    }


    public void reinit() {

        if(this.websocketClient != null) {
            this.websocketClient.closeAllConnections();
        }

        this.websocketClient = new WebSocketStreamClientImpl();

        if(webSocketReconnectScheduled != null) {
            webSocketReconnectScheduled.shutdown();

            webSocketReconnectScheduled = Executors.newSingleThreadScheduledExecutor();
            webSocketReconnectScheduled.scheduleAtFixedRate(()->reinit(),
                    12, 12, TimeUnit.HOURS);
        }


        if(this.orderUpdateEventListener != null) {
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
        BinanceSpotOrderBookMaintainer orderBookMaintainer = new BinanceSpotOrderBookMaintainer(this.hClient, Platforms.BINANCE_UM_FUTURE);
        symbol2OrderBookMaintainerMap.put(symbol, orderBookMaintainer);
        this.websocketClient.diffDepthStream(symbol, diffDepthSpeed, onOpenCallbackDiffDepthStream,
                orderBookMaintainer.getOnDiffDepthEventMessageCallback(),
                onClosingCallbackDiffDepthStream,
                onClosedCallbackDiffDepthStream,
                onFailureCallbackDiffDepthStream);
    }

    public void close(){
        this.websocketClient.closeAllConnections();
    }

    public AtomicReference<OrderBook> getOrderBook(String symbol) {

        if(symbol2OrderBookMaintainerMap.containsKey(symbol)) {
            return this.symbol2OrderBookMaintainerMap.get(symbol).getOrderBookAtomicReference();
        }



        reinitDiffDepthStream(symbol);
        return symbol2OrderBookMaintainerMap.get(symbol).getOrderBookAtomicReference();

    }

}
