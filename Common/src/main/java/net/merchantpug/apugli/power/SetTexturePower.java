package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.merchantpug.apugli.data.ApugliDataTypes;
import net.merchantpug.apugli.util.PlayerModelType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class SetTexturePower extends Power {
    public final ResourceLocation textureLocation;
    public final PlayerModelType model;

    public SetTexturePower(PowerType<?> type, LivingEntity entity, ResourceLocation textureLocation, PlayerModelType model) {
        super(type, entity);
        this.textureLocation = textureLocation;
        this.model = model;
    }

    public static class Factory extends SimplePowerFactory<SetTexturePower> {

        public Factory() {
            super("prevent_label_render",
                    new SerializableData()
                            .add("texture_location", SerializableDataTypes.IDENTIFIER, null)
                            .add("player_model", ApugliDataTypes.PLAYER_MODEL_TYPE, null),
                    data ->
                            (type, player) ->
                                    new SetTexturePower(type, player, data.getId("texture_location"), data.get("player_model")));
            allowCondition();
        }

        @Override
        public Class<SetTexturePower> getPowerClass() {
            return SetTexturePower.class;
        }
    }

}
