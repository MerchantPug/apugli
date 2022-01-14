package me.jarva.origins_power_expansion.powers.factory;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import me.jarva.origins_power_expansion.OriginsPowerExpansion;
import me.jarva.origins_power_expansion.powers.CustomFootstepPower;
import net.minecraft.sounds.SoundEvent;

@SuppressWarnings({"unchecked", "UnstableApiUsage", "deprecation"})
public class PowerFactories {
    public static void register() {
        register(new PowerFactory<CustomFootstepPower>(OriginsPowerExpansion.identifier("custom_footstep"),
                new SerializableData()
                    .add("sound", SerializableDataType.SOUND_EVENT)
                    .add("volume", SerializableDataType.FLOAT, 1F)
                    .add("pitch", SerializableDataType.FLOAT, 1F),
                data -> (type, player) -> {
                    return new CustomFootstepPower(type, player, data.<SoundEvent>get("sound"), data.getFloat("volume"), data.getFloat("pitch"));
                })
                .allowCondition());
    }

    @ExpectPlatform
    private static void register(PowerFactory<?> serializer) {
        throw new AssertionError();
    }
}
