package com.hrl.trade.common.api.client.binance.future.proxy;

import com.binance.connector.futures.client.utils.ProxyAuth;
import com.hrl.trade.common.api.proxy.MyProxyAuth;

public class Utils {
    public static ProxyAuth fromMyProxy(MyProxyAuth mproxyAuth){
        return new ProxyAuth(mproxyAuth.getProxy(),mproxyAuth.getAuth());
    }
}
