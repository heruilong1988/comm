package com.hrl.trade.common.api.client.binance.future;

import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;

public final class BookTicker {
    private BookTicker() {
    }

    private static final Logger logger = LoggerFactory.getLogger(BookTicker.class);
    public static void main(String[] args) {
       /* LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        IMyFuturesClient client = new BinanceUMFuturesClient();


        System.setProperty("http:proxyHost","localhost");
        System.setProperty("http.proxyPort","7890");
        System.setProperty("https.proxyHost","localhost");
        System.setProperty("https.proxyPort","7890");

        *//*Proxy proxyConn = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1",  7890));
        MyProxyAuth proxy = new MyProxyAuth(proxyConn, null);

        client.setProxy(proxy);*//*


        try {
            String result = client.market().bookTicker(parameters);
            logger.info(result);
        } catch (BinanceConnectorException e) {
            logger.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            logger.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                    e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }*/
    }
}