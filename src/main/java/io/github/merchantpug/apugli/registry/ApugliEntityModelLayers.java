package io.github.merchantpug.apugli.registry;

import io.github.merchantpug.apugli.Apugli;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ApugliEntityModelLayers {
    public static final EntityModelLayer PLAYER_ARMOR = new EntityModelLayer(new Identifier(Apugli.MODID, "player"), "armor");
    public static final EntityModelLayer PLAYER_SLIM_ARMOR = new EntityModelLayer(new Identifier(Apugli.MODID, "player_slim"), "armor");
}
