package com.hrl.trade.common.api.client.binance.future;

import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.hrl.trade.common.api.client.HClient;
import com.hrl.trade.common.api.client.binance.future.um.HBinanceUmFutureClient;
import com.hrl.trade.common.api.proxy.MyProxyAuth;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.LinkedHashMap;

public class AccountTest {

    private AccountTest() {
    }
    private static final double quantity = 30;
    private static final double price = 50000;

    private static final Logger logger = LoggerFactory.getLogger(AccountTest.class);



    public static void main(String[] args) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        HClient client = new HBinanceUmFutureClient(null);


        Proxy proxyConn = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1",  7890));
        MyProxyAuth proxy = new MyProxyAuth(proxyConn, null);

        client.setProxy(proxy);

        parameters.put("symbol", "DOGEUSDT");
        parameters.put("side", "BUY");
        parameters.put("type", "MARKET");
        //parameters.put("timeInForce", "GTC");
        parameters.put("quantity", quantity);
        //parameters.put("price", price);

        try {
            String result = client.newTestOrder(parameters);
            logger.info(result);
        } catch (BinanceConnectorException e) {
            logger.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            logger.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                    e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }
    }


    @Test
    public void sell(){
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        IMyFuturesClient client = new BinanceUMFuturesClient(PrivateConfig.apiKey, PrivateConfig.secretKey);


        Proxy proxyConn = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1",  7890));
        MyProxyAuth proxy = new MyProxyAuth(proxyConn, null);

        client.setProxy(proxy);

        parameters.put("symbol", "DOGEUSDT");
        parameters.put("side", "SELL");
        parameters.put("type", "MARKET");
        //parameters.put("timeInForce", "GTC");
        parameters.put("quantity", quantity);
        //parameters.put("price", price);

        try {
            String result = client.account().newTestOrder(parameters);
            logger.info(result);
        } catch (BinanceConnectorException e) {
            logger.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            logger.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                    e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }
    }

}
