package net.merchantpug.apugli.data;

import com.mojang.serialization.Codec;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import io.github.edwinmindcraft.apoli.api.power.configuration.*;
import io.github.edwinmindcraft.calio.api.network.UnitListCodec;
import net.minecraft.core.Holder;
import net.minecraftforge.common.util.Lazy;
import org.apache.commons.lang3.Validate;

import java.util.List;

public class ApoliForgeDataTypes {

    public static final Lazy<SerializableDataType<ConfiguredPower<?, ?>>> POWER_TYPE = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredPower.class), ConfiguredPower.CODEC));
    
    public static final Lazy<SerializableDataType<ConfiguredBiEntityCondition<?, ?>>> BIENTITY_CONDITION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredBiEntityCondition.class), ConfiguredBiEntityCondition.CODEC));
    public static final Lazy<SerializableDataType<ConfiguredBiomeCondition<?, ?>>> BIOME_CONDITION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredBiomeCondition.class), ConfiguredBiomeCondition.CODEC));
    public static final Lazy<SerializableDataType<ConfiguredBlockCondition<?, ?>>> BLOCK_CONDITION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredBlockCondition.class), ConfiguredBlockCondition.CODEC));
    public static final Lazy<SerializableDataType<ConfiguredDamageCondition<?, ?>>> DAMAGE_CONDITION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredDamageCondition.class), ConfiguredDamageCondition.CODEC));
    public static final Lazy<SerializableDataType<ConfiguredEntityCondition<?, ?>>> ENTITY_CONDITION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredEntityCondition.class), ConfiguredEntityCondition.CODEC));
    public static final Lazy<SerializableDataType<ConfiguredFluidCondition<?, ?>>> FLUID_CONDITION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredFluidCondition.class), ConfiguredFluidCondition.CODEC));
    public static final Lazy<SerializableDataType<ConfiguredItemCondition<?, ?>>> ITEM_CONDITION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredItemCondition.class), ConfiguredItemCondition.CODEC));

    public static final Lazy<SerializableDataType<ConfiguredBiEntityAction<?, ?>>> BIENTITY_ACTION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredBiEntityAction.class), ConfiguredBiEntityAction.CODEC));
    public static final Lazy<SerializableDataType<ConfiguredBlockAction<?, ?>>> BLOCK_ACTION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredBlockAction.class), ConfiguredBlockAction.CODEC));
    public static final Lazy<SerializableDataType<ConfiguredEntityAction<?, ?>>> ENTITY_ACTION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredEntityAction.class), ConfiguredEntityAction.CODEC));
    public static final Lazy<SerializableDataType<ConfiguredItemAction<?, ?>>> ITEM_ACTION = Lazy.of(() -> new SerializableDataType<>(castClass(ConfiguredItemAction.class), ConfiguredItemAction.CODEC));

    public static final Lazy<SerializableDataType<IActivePower.Key>> KEY = Lazy.of(() -> new SerializableDataType<>(IActivePower.Key.class, IActivePower.Key.CODEC));

    private static <T> SerializableDataType<List<T>> list(Codec<T> codec) {
        return new SerializableDataType<>(castClass(List.class), listOf(codec));
    }

    private static <T> Codec<List<T>> listOf(Codec<T> codec) {
        Validate.notNull(codec, "Codec cannot be null");
        return new UnitListCodec<>(codec);
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> castClass(Class<?> aClass) {
        return (Class<T>)aClass;
    }
    
}
