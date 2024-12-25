package com.hrl.trade.common.api.client.binance.future;


import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.LinkedHashMap;

@Slf4j
public class ExchangeInfoTest {


  /*  @Test
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
    }*/
}
