package com.github.merchantpug.apugli.power;

import com.github.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.Pair;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionWhenTameHitPower extends CooldownPower {

    private final Predicate<Pair<DamageSource, Float>> damageCondition;
    private final Predicate<Pair<Entity, Entity>> bientityCondition;
    private final Consumer<Pair<Entity, Entity>> bientityAction;
    private final Predicate<Pair<Entity, Entity>> ownerBiEntityCondition;
    private final Consumer<Pair<Entity, Entity>> ownerBiEntityAction;

    public ActionWhenTameHitPower(PowerType<?> type, LivingEntity entity, int cooldownDuration, HudRender hudRender, Predicate<Pair<DamageSource, Float>> damageCondition, Consumer<Pair<Entity, Entity>> bientityAction, Predicate<Pair<Entity, Entity>> bientityCondition, Consumer<Pair<Entity, Entity>> ownerBiEntityAction, Predicate<Pair<Entity, Entity>> ownerBiEntityCondition) {
        super(type, entity, cooldownDuration, hudRender);
        this.damageCondition = damageCondition;
        this.bientityAction = bientityAction;
        this.bientityCondition = bientityCondition;
        this.ownerBiEntityAction = ownerBiEntityAction;
        this.ownerBiEntityCondition = ownerBiEntityCondition;
    }

    public void whenHit(TameableEntity tameable, Entity attacker, DamageSource damageSource, float damageAmount) {
        if (canUse()) {
            if (damageCondition == null || damageCondition.test(new Pair<>(damageSource, damageAmount))) {
                if ((bientityCondition == null || bientityCondition.test(new Pair<>(attacker, tameable))) && (ownerBiEntityCondition == null || ownerBiEntityCondition.test(new Pair<>(attacker, entity)))) {
                    if (this.bientityAction != null) {
                        this.bientityAction.accept(new Pair<>(attacker, tameable));
                    }
                    if (this.ownerBiEntityAction != null) {
                        this.ownerBiEntityAction.accept(new Pair<>(attacker, entity));
                    }
                    use();
                }
            }
        }
    }

    public static PowerFactory getFactory() {
        return new PowerFactory<>(Apugli.identifier("action_when_tame_hit"),
                new SerializableData()
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
                        .add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
                        .add("cooldown", SerializableDataTypes.INT, 1)
                        .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("owner_bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
                        .add("owner_bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
                data ->
                        (type, player) -> new ActionWhenTameHitPower(type, player, data.getInt("cooldown"),
                                (HudRender) data.get("hud_render"), (ConditionFactory<Pair<DamageSource, Float>>.Instance) data.get("damage_condition"),
                                (ActionFactory<Pair<Entity, Entity>>.Instance) data.get("bientity_action"),
                                (ConditionFactory<Pair<Entity, Entity>>.Instance) data.get("bientity_condition"),
                                (ActionFactory<Pair<Entity, Entity>>.Instance) data.get("owner_bientity_action"),
                                (ConditionFactory<Pair<Entity, Entity>>.Instance) data.get("owner_bientity_condition")))
                .allowCondition();
    }
}