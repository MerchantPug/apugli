package io.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class EntityTextureOverlayPower extends Power {
    public final Identifier textureLocation;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(Apugli.identifier("entity_texture_overlay"),
                new SerializableData()
                        .add("texture_location", SerializableDataType.IDENTIFIER),
                data ->
                        (type, player) ->
                                new EntityTextureOverlayPower(type, player, data.getId("texture_location")))
                .allowCondition();
    }

    public EntityTextureOverlayPower(PowerType<?> type, PlayerEntity player, Identifier textureLocation) {
        super(type, player);
        this.textureLocation = textureLocation;
    }
}
