package net.merchantpug.apugli.util;

import io.github.edwinmindcraft.apoli.api.power.IActivePower;

public class ActiveKeyUtil {
    public static boolean equals(IActivePower.Key key, IActivePower.Key otherKey) {
        return otherKey.key().equals(key.key()) && otherKey.continuous() == key.continuous();
    }
}
