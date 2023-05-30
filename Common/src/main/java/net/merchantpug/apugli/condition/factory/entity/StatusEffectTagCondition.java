package net.merchantpug.apugli.condition.factory.entity;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class StatusEffectTagCondition implements IConditionFactory<Entity> {

    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("tag", SerializableDataType.tag(Registries.MOB_EFFECT))
                .add("min_amplifier", SerializableDataTypes.INT, 0)
                .add("max_amplifier", SerializableDataTypes.INT, Integer.MAX_VALUE)
                .add("min_duration", SerializableDataTypes.INT, 0)
                .add("max_duration", SerializableDataTypes.INT, Integer.MAX_VALUE);
    }

    @Override
    public boolean check(SerializableData.Instance data, Entity entity) {
        TagKey<MobEffect> tag = data.get("tag");
        if(entity instanceof LivingEntity living) {
            return BuiltInRegistries.MOB_EFFECT.getTag(tag).stream().anyMatch(holders -> holders.stream().anyMatch(holder -> {
                if (holder.isBound() && living.hasEffect(holder.value())) {
                    MobEffectInstance instance = living.getEffect(holder.value());
                    return instance.getDuration() <= data.getInt("max_duration") && instance.getDuration() >= data.getInt("min_duration")
                            && instance.getAmplifier() <= data.getInt("max_amplifier") && instance.getAmplifier() >= data.getInt("min_amplifier");
                }
                return false;
            }));
        }
        return false;
    }

}
