package com.hrl.trade.common.api.proxy;

import okhttp3.Authenticator;

import java.net.Proxy;

public final class MyProxyAuth {
    private Proxy proxy;
    private Authenticator auth;
    
    public MyProxyAuth(Proxy proxy, Authenticator auth) {
        this.proxy = proxy;
        this.auth = auth;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public Authenticator getAuth() {
        return auth;
    }
}
