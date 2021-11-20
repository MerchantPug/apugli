package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.ModifyDamageTakenPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.mixin.ModifyDamageTakenPowerAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ApugliModifyDamageTakenPower extends ModifyDamageTakenPower {
    private final Predicate<Pair<Entity, Entity>> biEntityCondition;
    private Consumer<Pair<Entity, Entity>> biEntityAction;

    public ApugliModifyDamageTakenPower(PowerType<?> type, LivingEntity entity, Predicate<Pair<DamageSource, Float>> condition, Predicate<Pair<Entity, Entity>> biEntityCondition) {
        super(type, entity, condition);
        this.biEntityCondition = biEntityCondition;
    }

    @Override
    public boolean doesApply(DamageSource source, float damageAmount) {
        return source.getAttacker() == null ? ((ModifyDamageTakenPowerAccessor) this).getCondition().test(new Pair(source, damageAmount)) && biEntityCondition == null : ((ModifyDamageTakenPowerAccessor) this).getCondition().test(new Pair(source, damageAmount)) && (biEntityCondition == null || biEntityCondition.test(new Pair<>(entity, source.getAttacker())));
    }

    public void setBiEntityAction(Consumer<Pair<Entity, Entity>> biEntityAction) {
        this.biEntityAction = biEntityAction;
    }

    @Override
    public void executeActions(Entity attacker) {
        if(((ModifyDamageTakenPowerAccessor)this).getSelfAction() != null) {
            ((ModifyDamageTakenPowerAccessor)this).getSelfAction().accept(entity);
        }
        if(((ModifyDamageTakenPowerAccessor)this).getAttackerAction() != null) {
            ((ModifyDamageTakenPowerAccessor)this).getAttackerAction().accept(attacker);
        }
        if (biEntityAction != null) {
            biEntityAction.accept(new Pair<>(entity, attacker));
        }
    }


    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(Apugli.identifier("modify_damage_taken"),
                new SerializableData()
                        .add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("modifier", SerializableDataTypes.ATTRIBUTE_MODIFIER, null)
                        .add("modifiers", SerializableDataTypes.ATTRIBUTE_MODIFIERS, null)
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
                        .add("self_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("attacker_action", ApoliDataTypes.ENTITY_ACTION, null),
                data ->
                        (type, player) -> {
                            ApugliModifyDamageTakenPower power = new ApugliModifyDamageTakenPower(type, player,
                                    data.isPresent("damage_condition") ? (ConditionFactory<Pair<DamageSource, Float>>.Instance)data.get("damage_condition") : dmg -> true,
                                    data.isPresent("bientity_condition") ? (ConditionFactory<Pair<Entity, Entity>>.Instance)data.get("bientity_condition") : biEntity -> true);
                            if(data.isPresent("modifier")) {
                                power.addModifier(data.getModifier("modifier"));
                            }
                            if(data.isPresent("modifiers")) {
                                ((List<EntityAttributeModifier>)data.get("modifiers")).forEach(power::addModifier);
                            }
                            if(data.isPresent("bientity_action")) {
                                power.setBiEntityAction((ActionFactory<Pair<Entity, Entity>>.Instance)data.get("bientity_action"));
                            }
                            if(data.isPresent("self_action")) {
                                power.setSelfAction((ActionFactory<Entity>.Instance)data.get("self_action"));
                            }
                            if(data.isPresent("attacker_action")) {
                                power.setAttackerAction((ActionFactory<Entity>.Instance)data.get("attacker_action"));
                            }
                            return power;
                        })
                .allowCondition();
    }
}
