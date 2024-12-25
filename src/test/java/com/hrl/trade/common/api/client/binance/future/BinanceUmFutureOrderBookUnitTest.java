package com.hrl.trade.common.api.client.binance.future;

import com.hrl.trade.common.api.client.binance.future.um.HBinanceUmFutureClient;
import com.hrl.trade.common.domain.orderbook.OrderBook;
import com.hrl.trade.common.domain.orderupdate.OrderUpdateEvent;
import com.hrl.trade.common.domain.orderupdate.OrderUpdateEventListener;
import com.hrl.trade.common.domain.symbol.Symbol;

import java.util.concurrent.atomic.AtomicReference;

public class BinanceUmFutureOrderBookUnitTest {

    public static void main(String[] args) throws InterruptedException {

        System.setProperty("http:proxyHost","localhost");
        System.setProperty("http.proxyPort","7890");
        System.setProperty("https.proxyHost","localhost");
        System.setProperty("https.proxyPort","7890");


        OrderUpdateEventListener orderUpdateEvent = new OrderUpdateEventListener() {
            @Override
            public void listen(OrderUpdateEvent orderUpdateEvent) {

            }
        };
        HBinanceUmFutureClient hBinanceUmFutureClient = new HBinanceUmFutureClient(null);

        Symbol symbol = new Symbol();
        symbol.setPair("BTCUSDT");

       AtomicReference<OrderBook> bookRef = hBinanceUmFutureClient.getOrderBook(symbol);

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
