package org.smartRpc.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.smartRpc.util.SerializationUtil;

public class RpcEncoder extends MessageToByteEncoder {


    private Class<?> jsonClass = null;
    public RpcEncoder(Class<?> jsonClass){
        this.jsonClass = jsonClass;
    }
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if(jsonClass.isInstance(msg)){
            byte[] dates = SerializationUtil.toJson(msg).getBytes();
            out.writeInt(dates.length);
            out.writeBytes(dates);
        }
    }
}
