package com.krews.plugin.nitro;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.HabboPlugin;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import com.krews.plugin.nitro.websockets.NetworkChannelInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class main extends HabboPlugin implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(Emulator.class);

    public void onEnable() throws Exception {
        Emulator.getPluginManager().registerEvents(this, this);
        if(Emulator.isReady && !Emulator.isShuttingDown) {
            this.onEmulatorLoadedEvent(null);
        }
    }

    public void onDisable() throws Exception {

    }

    public boolean hasPermission(Habbo habbo, String s) {
        return false;
    }

    @EventHandler
    public void onEmulatorLoadedEvent (EmulatorLoadedEvent e) {
        //add missing db entry
        Emulator.getConfig().register("websockets.whitelist", "localhost");
        Emulator.getConfig().register("ws.nitro.host", "0.0.0.0");
        Emulator.getConfig().register("ws.nitro.port", "2096");

        Emulator.getGameServer().getServerBootstrap().childHandler(new NetworkChannelInitializer());

        Emulator.getGameServer().getServerBootstrap().bind(Emulator.getConfig().getValue("ws.nitro.host", "0.0.0.0"), Emulator.getConfig().getInt("ws.nitro.port", 2096)).syncUninterruptibly();

        LOGGER.info("OFFICIAL PLUGIN - Nitro Websockets has started!");
        LOGGER.info("Nitro Websockets Listening on " + Emulator.getConfig().getValue("ws.nitro.host", "0.0.0.0") + ":" + Emulator.getConfig().getInt("ws.nitro.port", 2096));
    }
}
