package net.merchantpug.apugli.data;

import io.github.apace100.calio.data.SerializableDataType;
import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import io.github.edwinmindcraft.apoli.api.power.configuration.*;
import net.minecraft.core.Holder;
import net.minecraftforge.common.util.Lazy;

public class ApoliForgeDataTypes {
    
    public static final SerializableDataType<Holder<ConfiguredPower<?, ?>>> POWER_TYPE = new SerializableDataType<>(castClass(Holder.class), ConfiguredPower.CODEC_SET.holderRef());
    
    public static final SerializableDataType<ConfiguredBiEntityCondition<?, ?>> BIENTITY_CONDITION = new SerializableDataType<>(castClass(ConfiguredBiEntityCondition.class), ConfiguredBiEntityCondition.CODEC);
    public static final SerializableDataType<ConfiguredBiomeCondition<?, ?>> BIOME_CONDITION = new SerializableDataType<>(castClass(ConfiguredBiomeCondition.class), ConfiguredBiomeCondition.CODEC);
    public static final SerializableDataType<ConfiguredBlockCondition<?, ?>> BLOCK_CONDITION = new SerializableDataType<>(castClass(ConfiguredBlockCondition.class), ConfiguredBlockCondition.CODEC);
    public static final SerializableDataType<ConfiguredDamageCondition<?, ?>> DAMAGE_CONDITION = new SerializableDataType<>(castClass(ConfiguredDamageCondition.class), ConfiguredDamageCondition.CODEC);
    public static final SerializableDataType<ConfiguredEntityCondition<?, ?>> ENTITY_CONDITION = new SerializableDataType<>(castClass(ConfiguredEntityCondition.class), ConfiguredEntityCondition.CODEC);
    public static final SerializableDataType<ConfiguredFluidCondition<?, ?>> FLUID_CONDITION = new SerializableDataType<>(castClass(ConfiguredFluidCondition.class), ConfiguredFluidCondition.CODEC);
    public static final SerializableDataType<ConfiguredItemCondition<?, ?>> ITEM_CONDITION = new SerializableDataType<>(castClass(ConfiguredItemCondition.class), ConfiguredItemCondition.CODEC);

    public static final SerializableDataType<ConfiguredBiEntityAction<?, ?>> BIENTITY_ACTION = new SerializableDataType<>(castClass(ConfiguredBiEntityAction.class), ConfiguredBiEntityAction.CODEC);
    public static final SerializableDataType<ConfiguredBlockAction<?, ?>> BLOCK_ACTION = new SerializableDataType<>(castClass(ConfiguredBlockAction.class), ConfiguredBlockAction.CODEC);
    public static final SerializableDataType<ConfiguredEntityAction<?, ?>> ENTITY_ACTION = new SerializableDataType<>(castClass(ConfiguredEntityAction.class), ConfiguredEntityAction.CODEC);
    public static final SerializableDataType<ConfiguredItemAction<?, ?>> ITEM_ACTION = new SerializableDataType<>(castClass(ConfiguredItemAction.class), ConfiguredItemAction.CODEC);

    public static final SerializableDataType<ConfiguredModifier<?>> MODIFIER = new SerializableDataType<>(ApoliForgeDataTypes.castClass(ConfiguredModifier.class), ConfiguredModifier.CODEC);

    public static final SerializableDataType<IActivePower.Key> KEY = new SerializableDataType<>(IActivePower.Key.class, IActivePower.Key.CODEC);

    @SuppressWarnings("unchecked")
    private static <T> Class<T> castClass(Class<?> aClass) {
        return (Class<T>)aClass;
    }
    
}
