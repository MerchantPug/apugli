package com.github.merchantpug.apugli;

import com.github.merchantpug.apugli.networking.ApugliPacketsC2S;
import com.github.merchantpug.apugli.registry.ApugliPowerFactories;
import com.github.merchantpug.apugli.util.ApugliNamespaceAlias;
import io.github.apace100.origins.power.Active;
import com.github.merchantpug.apugli.registry.action.ApugliBlockActions;
import com.github.merchantpug.apugli.registry.action.ApugliEntityActions;
import com.github.merchantpug.apugli.registry.action.ApugliItemActions;
import com.github.merchantpug.apugli.registry.condition.ApugliBlockConditions;
import com.github.merchantpug.apugli.registry.condition.ApugliDamageConditions;
import com.github.merchantpug.apugli.registry.condition.ApugliEntityConditions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Apugli {
    public static final String MODID = "apugli";
    public static final Logger LOGGER = LogManager.getLogger(Apugli.class);

    public static String VERSION = "";

    public static HashMap<UUID, HashSet<Active.Key>> keysToCheck = new HashMap<>();
    public static HashMap<UUID, HashSet<Active.Key>> currentlyUsedKeys = new HashMap<>();

    public static void init() {
        LOGGER.info("Apugli " + VERSION + " is initializing. Powering up your powered up game.");

        ApugliBlockActions.register();
        ApugliEntityActions.register();
        ApugliItemActions.register();

        ApugliBlockConditions.register();
        ApugliDamageConditions.register();
        ApugliEntityConditions.register();

        ApugliPowerFactories.register();

        ApugliPacketsC2S.register();

        ApugliNamespaceAlias.addAlias("ope");
    }

    public static Identifier identifier(String path) {
        return new Identifier(MODID, path);
    }
}
