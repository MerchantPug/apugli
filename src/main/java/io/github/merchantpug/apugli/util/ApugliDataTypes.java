package io.github.merchantpug.apugli.util;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.apace100.calio.mixin.DamageSourceAccessor;
import io.github.merchantpug.apugli.registry.ApugliEntityGroups;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.FoodComponent;
import net.minecraft.util.UseAction;
import net.minecraft.world.explosion.Explosion;

public class ApugliDataTypes {
    public static final SerializableDataType<EntityGroup> APUGLI_ENTITY_GROUP =
            SerializableDataType.mapped(EntityGroup.class, HashBiMap.create(ImmutableMap.of(
                    "smiteable", ApugliEntityGroups.SMITEABLE,
                    "player_undead", ApugliEntityGroups.PLAYER_UNDEAD
            )));
    public static final SerializableDataType<Explosion.DestructionType> EXPLOSION_BEHAVIOR =
            SerializableDataType.mapped(Explosion.DestructionType.class, HashBiMap.create(ImmutableMap.of(
                    "none", Explosion.DestructionType.NONE,
                    "break", Explosion.DestructionType.BREAK,
                    "destroy", Explosion.DestructionType.DESTROY
            )));
    public static final SerializableDataType<UseAction> EAT_ACTION =
            SerializableDataType.mapped(UseAction.class, HashBiMap.create(ImmutableMap.of(
                    "none", UseAction.NONE,
                    "eat", UseAction.EAT,
                    "drink", UseAction.DRINK
            )));
    public static final SerializableDataType<FoodComponent> FOOD_COMPONENT = SerializableDataType.compound(FoodComponent.class, new SerializableData()
                    .add("food", SerializableDataTypes.INT)
                    .add("saturation", SerializableDataTypes.FLOAT)
                    .add("meat", SerializableDataTypes.BOOLEAN, false)
                    .add("always_edible", SerializableDataTypes.BOOLEAN, false)
                    .add("snack", SerializableDataTypes.BOOLEAN, false),
            (data) -> {
                FoodComponent.Builder builder = new FoodComponent.Builder().hunger(data.getInt("hunger")).saturationModifier(data.getFloat("saturation"));
                if (data.getBoolean("meat")) {
                    builder.meat();
                }
                if (data.getBoolean("always_edible")) {
                    builder.alwaysEdible();
                }
                if (data.getBoolean("snack")) {
                    builder.snack();
                }
                return builder.build();
            },
            (data, fc) -> {
                SerializableData.Instance inst = data.new Instance();
                inst.set("hunger", fc.getHunger());
                inst.set("saturation", fc.getSaturationModifier());
                inst.set("meat", fc.isMeat());
                inst.set("always_edible", fc.isAlwaysEdible());
                inst.set("snack", fc.isSnack());
                return inst;
            });
}
