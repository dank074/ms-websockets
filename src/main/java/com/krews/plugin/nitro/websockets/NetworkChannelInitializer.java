package com.krews.plugin.nitro.websockets;

import com.eu.habbo.messages.PacketManager;
import com.eu.habbo.networking.gameserver.decoders.*;
import com.eu.habbo.networking.gameserver.encoders.GameServerMessageEncoder;
import com.eu.habbo.networking.gameserver.encoders.GameServerMessageLogger;
import com.eu.habbo.networking.gameserver.handlers.IdleTimeoutHandler;
import com.krews.plugin.nitro.websockets.handlers.MessageInterceptorHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class NetworkChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) {
        ch.pipeline().addLast("logger", new LoggingHandler());

        ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(60, 30, 0));
        ch.pipeline().addAfter("idleStateHandler", "idleEventHandler", new IdleTimeoutHandler());

        ch.pipeline().addLast("messageInterceptor", new MessageInterceptorHandler());

        // Decoders.
        ch.pipeline().addLast(new GamePolicyDecoder());
        ch.pipeline().addLast(new GameByteFrameDecoder());
        ch.pipeline().addLast(new GameByteDecoder());

        if (PacketManager.DEBUG_SHOW_PACKETS) {
            ch.pipeline().addLast(new GameClientMessageLogger());
        }

        ch.pipeline().addLast(new GameMessageRateLimit());
        ch.pipeline().addLast(new GameMessageHandler());

        // Encoders.
        ch.pipeline().addLast("messageEncoder", new GameServerMessageEncoder());

        if (PacketManager.DEBUG_SHOW_PACKETS) {
            ch.pipeline().addLast(new GameServerMessageLogger());
        }
    }
}
