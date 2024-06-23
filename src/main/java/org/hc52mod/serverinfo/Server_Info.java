package org.hc52mod.serverinfo;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server_Info implements ClientModInitializer {
    public static final String MOD_ID = "serverinfo";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("ServerInfo Mod initialized!");
    }
}