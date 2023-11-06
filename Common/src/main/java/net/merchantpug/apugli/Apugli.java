package net.merchantpug.apugli;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import net.merchantpug.apugli.registry.ApugliEntityTypes;
import net.merchantpug.apugli.registry.action.ApugliBiEntityActions;
import net.merchantpug.apugli.registry.action.ApugliBlockActions;
import net.merchantpug.apugli.registry.action.ApugliEntityActions;
import net.merchantpug.apugli.registry.action.ApugliItemActions;
import net.merchantpug.apugli.registry.condition.*;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class Apugli {
    public static final String ID = "apugli";
    public static final String NAME = "Apugli";
    public static final Logger LOG = LoggerFactory.getLogger(NAME);

    private static final Set<SerializableData.Instance> ERROR_LOGGED_DATA = new HashSet<>();
    public static String VERSION = "";
    
    public static void init() {
        Apugli.LOG.info("Apugli " + Apugli.VERSION + " has initialized. Powering up your powered up game.");

        ApugliBiEntityActions.registerAll();
        ApugliBlockActions.registerAll();
        ApugliEntityActions.registerAll();
        ApugliItemActions.registerAll();

        ApugliBiEntityConditions.registerAll();
        ApugliBlockConditions.registerAll();
        ApugliEntityConditions.registerAll();
        ApugliDamageConditions.registerAll();
        ApugliItemConditions.registerAll();

        ApugliPowers.registerAll();

        ApugliEntityTypes.registerAll();
    }

    public static boolean hasDataBeenErrorLogged(SerializableData.Instance data) {
        return ERROR_LOGGED_DATA.contains(data);
    }

    public static void addToErrorLoggedData(SerializableData.Instance data) {
        ERROR_LOGGED_DATA.add(data);
    }

    public static void clearErrorLoggedDataSet() {
        ERROR_LOGGED_DATA.clear();
    }

    public static ResourceLocation asResource(String name) {
        return new ResourceLocation(ID, name);
    }
    
}