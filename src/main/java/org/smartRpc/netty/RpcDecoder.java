package org.smartRpc.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.smartRpc.util.SerializationUtil;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder{

    private Class<?> jsonClass  = null;

    public RpcDecoder(Class<?> decoderClass){
        jsonClass = decoderClass;
    }
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {

        if(in.readableBytes() < 4){
            return;
        }
        in.markReaderIndex();
        int messageLength = in.readInt();
        if(messageLength < 0){
            throw new RuntimeException(" data format is wrong");
        }
        if(in.readableBytes() < messageLength){
            in.resetReaderIndex();
            return;
        }
        byte[] jsonMessage = new byte[messageLength];
        in.readBytes(jsonMessage);
        Object o = SerializationUtil.fromJson(jsonMessage, jsonClass);
        out.add(o);
    }
}
