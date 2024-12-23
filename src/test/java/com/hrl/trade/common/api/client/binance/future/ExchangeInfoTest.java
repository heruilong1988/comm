package com.hrl.trade.common.api.client.binance.future;

import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.hrl.trade.api.client.IMyFuturesClient;
import com.hrl.trade.api.proxy.MyProxyAuth;
import com.hrl.trade.config.PrivateConfig;
import com.hrl.trade.platform.binance.client.future.BinanceUMFuturesClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.LinkedHashMap;

@Slf4j
public class ExchangeInfoTest {


    @Test
    public void test() {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        IMyFuturesClient client = new BinanceUMFuturesClient(PrivateConfig.apiKey, PrivateConfig.secretKey);


        Proxy proxyConn = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1",  7890));
        MyProxyAuth proxy = new MyProxyAuth(proxyConn, null);

        client.setProxy(proxy);


        try {
            String result = client.market().exchangeInfo();;
            log.info(result);
        } catch (BinanceConnectorException e) {
            log.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            log.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                    e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }
    }
}
