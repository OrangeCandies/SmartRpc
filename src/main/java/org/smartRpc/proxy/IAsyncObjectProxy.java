package org.smartRpc.proxy;

import org.smartRpc.bean.RpcResult;

public interface IAsyncObjectProxy {
    public RpcResult call(String methodName,Object...arg);
}
