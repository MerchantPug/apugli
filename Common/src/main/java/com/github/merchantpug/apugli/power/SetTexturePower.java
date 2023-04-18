package net.merchantpug.apugli.power;

<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/power/SetTexturePower.java
import net.merchantpug.apugli.Apugli;
========
import the.great.migration.merchantpug.apugli.Apugli;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/power/SetTexturePower.java
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/power/SetTexturePower.java
import net.merchantpug.apugli.util.ApugliDataTypes;
import net.merchantpug.apugli.util.PlayerModelType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
========
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import com.github.merchantpug.apugli.util.ApugliDataTypes;
import com.github.merchantpug.apugli.util.PlayerModelType;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/power/SetTexturePower.java

public class SetTexturePower extends Power {
    public final ResourceLocation textureLocation;
    public final PlayerModelType model;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(Apugli.identifier("set_texture"),
                new SerializableData()
                        .add("texture_location", SerializableDataTypes.IDENTIFIER, null)
                        .add("player_model", ApugliDataTypes.PLAYER_MODEL_TYPE, null),
                data ->
                        (type, player) ->
                                new SetTexturePower(type, player, data.getId("texture_location"), (PlayerModelType)data.get("player_model")))
                .allowCondition();
    }

    public SetTexturePower(PowerType<?> type, LivingEntity entity, ResourceLocation textureLocation, PlayerModelType model) {
        super(type, entity);
        this.textureLocation = textureLocation;
        this.model = model;
    }
}
