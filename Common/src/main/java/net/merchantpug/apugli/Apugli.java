package net.merchantpug.apugli;

import net.merchantpug.apugli.registry.action.ApugliBiEntityActions;
import net.merchantpug.apugli.registry.action.ApugliBlockActions;
import net.merchantpug.apugli.registry.action.ApugliEntityActions;
import net.merchantpug.apugli.registry.action.ApugliItemActions;
import net.merchantpug.apugli.registry.condition.ApugliBiEntityConditions;
import net.merchantpug.apugli.registry.condition.ApugliBlockConditions;
import net.merchantpug.apugli.registry.condition.ApugliDamageConditions;
import net.merchantpug.apugli.registry.condition.ApugliEntityConditions;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Apugli {
    public static final String ID = "apugli";
    public static final String NAME = "Apugli";
    public static final Logger LOG = LoggerFactory.getLogger(NAME);
    public static String VERSION = "";
    public static int[] SEMVER;

    private static MinecraftServer server;
    
    public static void init() {
        ApugliBiEntityActions.registerAll();
        ApugliBlockActions.registerAll();
        ApugliEntityActions.registerAll();
        ApugliItemActions.registerAll();

        ApugliBiEntityConditions.registerAll();
        ApugliBlockConditions.registerAll();
        ApugliEntityConditions.registerAll();
        ApugliDamageConditions.registerAll();

        ApugliPowers.registerAll();
    }

    public static MinecraftServer getServer() {
        return server;
    }

    protected static void setServer(MinecraftServer server) {
        if (Apugli.server == null) {
            Apugli.server = server;
        }
    }

    public static ResourceLocation asResource(String name) {
        return new ResourceLocation(ID, name);
    }
    
}