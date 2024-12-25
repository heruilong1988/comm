package com.hrl.trade.common.api.client.binance.future;

import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.hrl.trade.common.api.client.HClient;
import com.hrl.trade.common.api.client.binance.future.um.HBinanceUmFutureClient;
import com.hrl.trade.common.api.proxy.MyProxyAuth;
import com.hrl.trade.common.domain.order.Order;
import com.hrl.trade.common.domain.orderupdate.OrderUpdateEvent;
import com.hrl.trade.common.domain.orderupdate.OrderUpdateEventListener;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.LinkedHashMap;

public class AccountTest {

    public AccountTest() {
    }

    private static final double quantity = 30;
    private static final double price = 50000;

    private static final Logger logger = LoggerFactory.getLogger(AccountTest.class);



    public static void main(String[] args) {

        System.setProperty("http:proxyHost","localhost");
        System.setProperty("http.proxyPort","7890");
        System.setProperty("https.proxyHost","localhost");
        System.setProperty("https.proxyPort","7890");

        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        HClient client = new HBinanceUmFutureClient(null);


        Proxy proxyConn = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1",  7890));
        MyProxyAuth proxy = new MyProxyAuth(proxyConn, null);

        client.setProxy(proxy);


        Order order = Order.builder()
                .symbol("DOGEUSDT")
                .side("BUY")
                .type( "MARKET")
                .origQty(new BigDecimal(100))
                .build();


        parameters.put("symbol", "DOGEUSDT");
        parameters.put("side", "BUY");
        parameters.put("type", "MARKET");
        //parameters.put("timeInForce", "GTC");
        parameters.put("quantity", quantity);
        //parameters.put("price", price);

        try {
            Order result = client.newTestOrder(order);
            logger.info("re:{}",result);
        } catch (BinanceConnectorException e) {
            logger.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            logger.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                    e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }
    }


    @Test
    public void buy2() throws InterruptedException {
       System.setProperty("http:proxyHost","localhost");
        System.setProperty("http.proxyPort","7890");
        System.setProperty("https.proxyHost","localhost");
        System.setProperty("https.proxyPort","7890");

        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();


        OrderUpdateEventListener  orderUpdateEventListener = new OrderUpdateEventListener() {
            @Override
            public void listen(OrderUpdateEvent orderUpdateEvent) {
                logger.info("orderUpdateEvent:{}", orderUpdateEvent);
            }
        };

        HClient client = new HBinanceUmFutureClient(orderUpdateEventListener);


        Thread.sleep(10000);

        Order order = Order.builder()
                .symbol("DOGEUSDT")
                .side("BUY")
                .type( "MARKET")
                .origQty(new BigDecimal(20))
                .build();


        try {
            Order result = client.newOrder(order);
            logger.info("re:{}",result);
        } catch (BinanceConnectorException e) {
            logger.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            logger.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                    e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }

        Thread.sleep(100000000);
    }

    @Test
    public void sell() throws InterruptedException {
        System.setProperty("http:proxyHost","localhost");
        System.setProperty("http.proxyPort","7890");
        System.setProperty("https.proxyHost","localhost");
        System.setProperty("https.proxyPort","7890");

        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();


        OrderUpdateEventListener  orderUpdateEventListener = new OrderUpdateEventListener() {
            @Override
            public void listen(OrderUpdateEvent orderUpdateEvent) {
                logger.info("orderUpdateEvent:{}", orderUpdateEvent);
            }
        };

        HClient client = new HBinanceUmFutureClient(orderUpdateEventListener);


        Thread.sleep(10000);

        Order order = Order.builder()
                .symbol("DOGEUSDT")
                .side("SELL")
                .type( "MARKET")
                .origQty(new BigDecimal(20))
                .build();


        try {
            Order result = client.newOrder(order);
            logger.info("sell result:{}",result);
        } catch (BinanceConnectorException e) {
            logger.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            logger.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                    e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }

        Thread.sleep(100000000);
    }

}
