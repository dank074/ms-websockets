package org.krews.plugin.nitro.websockets;

import com.eu.habbo.messages.PacketManager;
import com.eu.habbo.networking.gameserver.decoders.*;
import com.eu.habbo.networking.gameserver.encoders.GameServerMessageEncoder;
import com.eu.habbo.networking.gameserver.encoders.GameServerMessageLogger;
import com.eu.habbo.networking.gameserver.handlers.IdleTimeoutHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;
import org.krews.plugin.nitro.websockets.codec.WebSocketCodec;
import org.krews.plugin.nitro.websockets.handlers.CustomHTTPHandler;
import org.krews.plugin.nitro.websockets.ssl.SSLCertificateLoader;

public class NetworkChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext context;
    private final boolean isSSL;

    public NetworkChannelInitializer() {
        context = SSLCertificateLoader.getContext();
        isSSL = context != null;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ch.pipeline().addLast("logger", new LoggingHandler());

        ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(60, 30, 0));
        ch.pipeline().addAfter("idleStateHandler", "idleEventHandler", new IdleTimeoutHandler());

        if(isSSL) {
            ch.pipeline().addLast(context.newHandler(ch.alloc()));
        }
        ch.pipeline().addLast("httpCodec", new HttpServerCodec());
        ch.pipeline().addLast("objectAggregator", new HttpObjectAggregator(65536));
        ch.pipeline().addLast("customHttpHandler", new CustomHTTPHandler());
        ch.pipeline().addLast("protocolHandler", new WebSocketServerProtocolHandler("/", true));
        ch.pipeline().addLast("websocketCodec", new WebSocketCodec());

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

    public boolean isSSL() {
        return isSSL;
    }
}
