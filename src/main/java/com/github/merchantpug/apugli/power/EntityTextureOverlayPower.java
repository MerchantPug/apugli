package com.github.merchantpug.apugli.power;

import com.github.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class EntityTextureOverlayPower extends Power {
    public final Identifier textureLocation;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(Apugli.identifier("entity_texture_overlay"),
                new SerializableData()
                        .add("texture_location", SerializableDataTypes.IDENTIFIER),
                data ->
                        (type, player) ->
                                new EntityTextureOverlayPower(type, player, data.getId("texture_location")))
                .allowCondition();
    }

    public EntityTextureOverlayPower(PowerType<?> type, LivingEntity entity, Identifier textureLocation) {
        super(type, entity);
        this.textureLocation = textureLocation;
    }
}
