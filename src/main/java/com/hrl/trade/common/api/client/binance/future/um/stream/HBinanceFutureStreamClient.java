package com.hrl.trade.common.api.client.binance.future.um.stream;

import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.binance.connector.futures.client.impl.UMWebsocketClientImpl;
import com.binance.connector.futures.client.utils.WebSocketCallback;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrl.trade.common.api.client.Platforms;
import com.hrl.trade.common.api.client.HClient;
import com.hrl.trade.common.api.client.binance.future.um.http.UserDataListenKey;
import com.hrl.trade.common.api.client.binance.future.um.stream.orderbook.OrderBookMaintainer;
import com.hrl.trade.common.domain.orderbook.OrderBook;
import com.hrl.trade.common.domain.orderupdate.OrderUpdateEvent;
import com.hrl.trade.common.domain.orderupdate.OrderUpdateEventListener;
import com.hrl.trade.common.domain.symbol.Symbol;
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
public class HBinanceFutureStreamClient {

    public HClient hClient;
    public UMFuturesClientImpl umFuturesClient;
    public UMWebsocketClientImpl umWebsocketClient;
    public String userDataListenKey;
    public OrderUpdateEventListener orderUpdateEventListener;
    public String platform = Platforms.BINANCE_UM_FUTURE.name();

    public List<Symbol> diffDepthStreamSymbols = new ArrayList<>();

    public Map<Symbol, OrderBookMaintainer> symbol2OrderBookMaintainerMap = new HashMap<>();

    public int diffDepthSpeed = 100;

    ///////////////////////USER DATA STREAM ////////////////////////////////
    //订单更新
    public WebSocketCallback userDataStreamCallback = new WebSocketCallback(){

        @Override
        public void onReceive(String data) {
            log.info("userDataStreamCallback.onReceive: {}", data);

            JSONObject object = new JSONObject(data);
            String event = object.getString("e");
            if(event.equalsIgnoreCase("ACCOUNT_UPDATE")){

            }else if(event.equalsIgnoreCase("ORDER_TRADE_UPDATE")){
                StreamBinanceUmOrderUpdateEvent streamBinanceUmOrderUpdateEvent = StreamBinanceUmOrderUpdateEvent.fromJson(data);
                OrderUpdateEvent orderUpdateEvent = streamBinanceUmOrderUpdateEvent.getPayload().toOrderUpdateEvent();
                orderUpdateEventListener.listen(orderUpdateEvent);
            }


        }
    };

    public WebSocketCallback onOpenCallbackUserDataStream = (response) -> log.info("platform:{}.UserDataStream open,rsp:{}", platform,response);;


    public WebSocketCallback onFailureCallbackUserDataStream = (data) -> {
        log.error("[Platform:{}] UserDataStream failure,data:{}", platform, data);

       // this.reinit();

        //System.exit(1);
    } ;


    public WebSocketCallback onClosedCallbackUserDataStream  = (data) -> {
        log.error("[Platform:{}] UserDataStream closing,data:{}",platform, data);
        //reInitWebSocketStreamClientImpl();
    };

    @SneakyThrows
    public int initUserDataStream(){
        /*listenKeyScheduled.scheduleAtFixedRate(this::extendUserDateListenKey,
                30, 30, TimeUnit.MINUTES);*/

        extendUserDateListenKey();

        userDataKeyScheduledExecutor.scheduleAtFixedRate(this::extendUserDateListenKey,
                30, 30, TimeUnit.MINUTES);

        log.info("begin userDataKeyScheduledExecutor");


        int result = umWebsocketClient.listenUserStream(this.userDataListenKey,onOpenCallbackUserDataStream,userDataStreamCallback,onClosedCallbackUserDataStream,onFailureCallbackUserDataStream);

        log.info("listenUserStream.result:{}", result);

        return result;

    }


    ///////USER DATA KEY///////////////////////
    public ScheduledExecutorService userDataKeyScheduledExecutor = Executors.newSingleThreadScheduledExecutor();


    @SneakyThrows
    public void extendUserDateListenKey()   {
        String userDataListenKeyFromServer = Serialization.serializationInstance.getObjectMapper().readValue(this.umFuturesClient.userData().createListenKey(), UserDataListenKey.class).getListenKey();

        log.info("userDataListenKey create.platform:{},userDataListenKeyFromServer:{}",platform, userDataListenKeyFromServer);


        if(this.userDataListenKey != null) {
            if(!this.userDataListenKey.equals(userDataListenKeyFromServer)){
                log.error("userDataListenKey no same.platform:{}",platform);
            }
        } else {
            log.info("set userDataListenKey:{}", userDataListenKeyFromServer);
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
            log.info("HBinanceFutureStreamClient.umWebsocketClient closeAllConnections");
        }

        this.umWebsocketClient = new UMWebsocketClientImpl();
        log.info("HBinanceFutureStreamClient new UMWebsocketClientImpl");

        if(webSocketReconnectScheduled != null) {
            webSocketReconnectScheduled.shutdown();

            log.info("webSocketReconnectScheduled shutdown");


            webSocketReconnectScheduled = Executors.newSingleThreadScheduledExecutor();
            webSocketReconnectScheduled.scheduleAtFixedRate(()->reinit(),
                    12, 12, TimeUnit.HOURS);

            log.info("webSocketReconnectScheduled executed.");

        }


        if(this.orderUpdateEventListener != null) {
            // this.createUserDateListenKey();
            int result = this.initUserDataStream();
        }else {
            log.info("userDataStreamCallback is null");
        }

        reinitDiffDepthStream();

    }


    public void reinitDiffDepthStream(){
        symbol2OrderBookMaintainerMap.clear();
        log.info("symbol2OrderBookMaintainerMap clear");
        //监听depth
        this.diffDepthStreamSymbols.stream().forEach(symbol ->{
            {
                this.reinitDiffDepthStream(symbol);
            }
        });
    }

    public void reinitDiffDepthStream(Symbol symbol){
        OrderBookMaintainer orderBookMaintainer = new OrderBookMaintainer(this.hClient, Platforms.BINANCE_UM_FUTURE, symbol);
        symbol2OrderBookMaintainerMap.put(symbol, orderBookMaintainer);
        this.umWebsocketClient.diffDepthStream(symbol.getPair(), diffDepthSpeed, onOpenCallbackDiffDepthStream,
                orderBookMaintainer.getOnDiffDepthEventMessageCallback(),
                onClosedCallbackDiffDepthStream,
                onFailureCallbackDiffDepthStream);
        log.info("listen on diffDepthStream.symbol:{}", symbol);
    }

    public void close(){
        this.umWebsocketClient.closeAllConnections();
    }

    public AtomicReference<OrderBook> getOrderBook(Symbol symbol) {

        if(symbol2OrderBookMaintainerMap.containsKey(symbol)) {
            return this.symbol2OrderBookMaintainerMap.get(symbol).getOrderBookAtomicReference();
        }



        reinitDiffDepthStream(symbol);
        return symbol2OrderBookMaintainerMap.get(symbol).getOrderBookAtomicReference();

    }

}
