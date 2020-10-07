package com.krews.plugin.nitro.websockets.codec;

import com.eu.habbo.Emulator;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class WebSocketFrameToHabboFrameDecoder extends MessageToMessageDecoder<WebSocketFrame> {
    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame in, List<Object> out) {
        out.add(in.content().retain());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // only allow websockets connections from the whitelist
        if(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            WebSocketServerProtocolHandler.HandshakeComplete handshake = (WebSocketServerProtocolHandler.HandshakeComplete)evt;
            String origin = getDomainName(handshake.requestHeaders().get("Origin"));

            if(!isWhitelisted(origin)) {
                ctx.channel().writeAndFlush(new CloseWebSocketFrame(403, "Origin forbidden")).addListener(ChannelFutureListener.CLOSE);
            }
        }
        else {
            super.userEventTriggered(ctx, evt);
        }
    }

    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    public static boolean isWhitelisted(String origin) {
        String[] allowedOrigins = Emulator.getConfig().getValue("websockets.whitelist", "localhost").split(",");
        for(String entry : allowedOrigins) {
            if(entry.startsWith("*")) {
                if(origin.endsWith(entry.substring(1)) || ("." + origin).equals(entry.substring(1))) return true;
            } else {
                if(origin.equals(entry)) return true;
            }
        }
        return false;
    }
}

