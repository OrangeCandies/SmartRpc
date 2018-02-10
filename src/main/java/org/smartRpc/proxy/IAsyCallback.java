package org.smartRpc.proxy;

public interface IAsyCallback {
    void success(Object result);

    void fail(Exception e);
}
