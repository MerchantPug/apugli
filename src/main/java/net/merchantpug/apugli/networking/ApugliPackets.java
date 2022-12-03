package net.merchantpug.apugli.networking;

import net.merchantpug.apugli.Apugli;
import net.minecraft.util.Identifier;

public class ApugliPackets {
    public static final Identifier HANDSHAKE = Apugli.identifier("handshake");

    public static final Identifier SEND_KEY_TO_CHECK = Apugli.identifier("send_key_to_check");
    public static final Identifier SYNC_ROCKET_JUMP_EXPLOSION = Apugli.identifier("sync_rocket_jump_explosion");

    public static final Identifier UPDATE_KEYS_PRESSED = Apugli.identifier("update_keys_pressed");
}
