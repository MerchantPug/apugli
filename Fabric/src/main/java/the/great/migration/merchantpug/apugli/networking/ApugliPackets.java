package the.great.migration.merchantpug.apugli.networking;

import net.minecraft.resources.ResourceLocation;
import the.great.migration.merchantpug.apugli.Apugli;

public class ApugliPackets {
    public static final ResourceLocation HANDSHAKE = Apugli.identifier("handshake");

    public static final ResourceLocation REMOVE_STACK_FOOD_COMPONENT = Apugli.identifier("remove_stack_food_component");
    public static final ResourceLocation SYNC_HITS_ON_TARGET = Apugli.identifier("sync_hits_on_target");
    public static final ResourceLocation SYNC_ACTIVE_KEYS_CLIENT = Apugli.identifier("sync_active_keys_client");
    public static final ResourceLocation SYNC_ROCKET_JUMP_EXPLOSION = Apugli.identifier("sync_rocket_jump_explosion");

    public static final ResourceLocation SYNC_ACTIVE_KEYS_SERVER = Apugli.identifier("sync_active_keys_server");
}
