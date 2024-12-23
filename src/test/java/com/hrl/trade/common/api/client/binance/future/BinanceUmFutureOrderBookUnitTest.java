package com.hrl.trade.common.api.client.binance.future;

import com.hrl.trade.config.PrivateConfig;
import com.hrl.trade.model.OrderBook;
import com.hrl.trade.model.order.OrderUpdateEvent;
import com.hrl.trade.model.order.OrderUpdateEventListener;
import com.hrl.trade.platform.binance.client.platform.BinanceUsdtBaseFuturePlatform;

import java.util.concurrent.atomic.AtomicReference;

public class BinanceUmFutureOrderBookUnitTest {

    public static void main(String[] args) throws InterruptedException {

        System.setProperty("http:proxyHost","localhost");
        System.setProperty("http.proxyPort","7890");
        System.setProperty("https.proxyHost","localhost");
        System.setProperty("https.proxyPort","7890");

        BinanceUsdtBaseFuturePlatform binanceSpotPlatform = new BinanceUsdtBaseFuturePlatform(PrivateConfig.apiKey,
                PrivateConfig.secretKey, new OrderUpdateEventListener() {
            @Override
            public void listen(OrderUpdateEvent orderUpdateEvent) {

            }
        });

        AtomicReference<OrderBook> bookRef =  binanceSpotPlatform.initOrderBook("btcusdt");

        while (true) {

            OrderBook orderBook = bookRef.get();
            if(!orderBook.isInit()) {
                System.out.println("wait");
                Thread.sleep(2000);
                continue;
            }
            int as = orderBook.getAsksTreeMap().size();
            int bs = orderBook.getBidsTreeMap().size();

             System.out.println("as:" + as + ",bs:" + bs + ",p:" + orderBook.getAveragePriceOfFistAskAndBid());

            Thread.sleep(500);
        }
    }
}
