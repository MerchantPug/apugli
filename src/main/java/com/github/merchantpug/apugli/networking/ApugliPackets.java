package com.github.merchantpug.apugli.networking;

import com.github.merchantpug.apugli.Apugli;
import net.minecraft.util.Identifier;

public class ApugliPackets {
    public static final Identifier HANDSHAKE = Apugli.identifier("handshake");

    public static final Identifier REMOVE_STACK_FOOD_COMPONENT = Apugli.identifier("remove_stack_food_component");
    public static final Identifier SYNC_HITS_ON_TARGET = Apugli.identifier("sync_hits_on_target");
    public static final Identifier REMOVE_KEYS_TO_CHECK = Apugli.identifier("remove_keys_to_check");
    public static final Identifier SYNC_ACTIVE_KEYS_CLIENT = Apugli.identifier("sync_active_keys_client");
    public static final Identifier SYNC_ROCKET_JUMP_EXPLOSION = Apugli.identifier("sync_rocket_jump_explosion");

    public static final Identifier SYNC_ACTIVE_KEYS_SERVER = Apugli.identifier("sync_active_keys_server");
}
