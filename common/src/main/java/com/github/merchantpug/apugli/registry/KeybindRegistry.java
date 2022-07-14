package com.github.merchantpug.apugli.registry;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.OriginsClient;
import com.github.merchantpug.apugli.ApugliClient;
import me.shedaniel.architectury.platform.Platform;
import me.shedaniel.architectury.registry.KeyBindings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class KeybindRegistry {
    public static KeyBinding useTernaryActivePowerKeybind;
    public static KeyBinding useQuaternaryActivePowerKeybind;
    public static KeyBinding useQuinaryActivePowerKeybind;
    public static KeyBinding useSenaryActivePowerKeybind;
    public static KeyBinding useSeptenaryActivePowerKeybind;
    public static KeyBinding useOctonaryActivePowerKeybind;
    public static KeyBinding useNonaryActivePowerKeybind;
    public static KeyBinding useDenaryActivePowerKeybind;

    public static void register() {
        if (Platform.isModLoaded("extrakeybinds") || !ApugliClient.config.keybindConfig.shouldRegisterKeybinds) return;
        useTernaryActivePowerKeybind = registerKeybind("key.origins.ternary_active", "ternary");
        useQuaternaryActivePowerKeybind = registerKeybind("key.origins.quaternary_active", "quaternary");
        useQuinaryActivePowerKeybind = registerKeybind("key.origins.quinary_active", "quinary");
        useSenaryActivePowerKeybind = registerKeybind("key.origins.senary_active", "senary");
        useSeptenaryActivePowerKeybind = registerKeybind("key.origins.septenary_active", "septenary");
        useOctonaryActivePowerKeybind = registerKeybind("key.origins.octonary_active", "octonary");
        useNonaryActivePowerKeybind = registerKeybind("key.origins.nonary_active", "nonary");
        useDenaryActivePowerKeybind = registerKeybind("key.origins.denary_active", "denary");
    }

    private static KeyBinding registerKeybind(String primaryKeyId, String secondaryKeyId) {
        KeyBinding keyMapping = new KeyBinding(primaryKeyId, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category." + Origins.MODID);
        OriginsClient.registerPowerKeybinding(primaryKeyId, keyMapping);
        OriginsClient.registerPowerKeybinding(secondaryKeyId, keyMapping);
        KeyBindings.registerKeyBinding(keyMapping);
        return keyMapping;
    }
}