package net.merchantpug.apugli.power;

<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/power/PlayerModelTypePower.java
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.util.ApugliDataTypes;
import net.merchantpug.apugli.util.PlayerModelType;
========
import the.great.migration.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.util.ApugliDataTypes;
import com.github.merchantpug.apugli.util.PlayerModelType;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/power/PlayerModelTypePower.java
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.LivingEntity;

public class PlayerModelTypePower extends Power {
    public final PlayerModelType model;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(Apugli.identifier("player_model_type"),
                new SerializableData()
                        .add("model", ApugliDataTypes.PLAYER_MODEL_TYPE),
                data ->
                        (type, player) ->
                                new PlayerModelTypePower(type, player, data.get("model")))
                .allowCondition();
    }

    public PlayerModelTypePower(PowerType<?> type, LivingEntity entity, PlayerModelType model) {
        super(type, entity);
        this.model = model;
    }
}
