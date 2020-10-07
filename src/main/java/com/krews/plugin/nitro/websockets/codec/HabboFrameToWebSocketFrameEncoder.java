package com.krews.plugin.nitro.websockets.codec;

import com.eu.habbo.messages.ServerMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.IllegalReferenceCountException;

import java.io.IOException;
import java.util.List;

public class HabboFrameToWebSocketFrameEncoder extends MessageToMessageEncoder<ServerMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ServerMessage message, List<Object> out) throws Exception {
        try {
            BinaryWebSocketFrame frame = new BinaryWebSocketFrame(message.get());
            try {
                out.add(frame.retain());
            } finally {
                // Release copied buffer.
                frame.release();
            }
        } catch (IllegalReferenceCountException e) {
            throw new IOException(String.format("IllegalReferenceCountException happened for ServerMessage with packet id %d.", message.getHeader()), e);
        }
    }
}

