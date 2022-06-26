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
import net.minecraft.util.Pair;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnTargetDeathPower extends CooldownPower {
    private final Predicate<Pair<DamageSource, Float>> damageCondition;
    private final Predicate<Pair<Entity, Entity>> bientityCondition;
    private final Consumer<Pair<Entity, Entity>> bientityAction;
    public final boolean includesPrimeAdversary;

    public ActionOnTargetDeathPower(PowerType<?> type, LivingEntity entity, int cooldownDuration, HudRender hudRender, Predicate<Pair<DamageSource, Float>> damageCondition, Consumer<Pair<Entity, Entity>> bientityAction, Predicate<Pair<Entity, Entity>> bientityCondition, boolean includesPrimeAdversary) {
        super(type, entity, cooldownDuration, hudRender);
        this.damageCondition = damageCondition;
        this.bientityCondition = bientityCondition;
        this.bientityAction = bientityAction;
        this.includesPrimeAdversary = includesPrimeAdversary;
    }

    public void onTargetDeath(Entity target, DamageSource damageSource, float damageAmount) {
        if(canUse()) {
            if(bientityCondition == null || bientityCondition.test(new Pair<>(entity, target))) {
                if(damageCondition == null || damageCondition.test(new Pair<>(damageSource, damageAmount))) {
                    this.bientityAction.accept(new Pair<>(entity, target));
                    use();
                }
            }
        }
    }

    public static PowerFactory getFactory() {
        return new PowerFactory<>(Apugli.identifier("action_on_target_death"),
                new SerializableData()
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION)
                        .add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
                        .add("cooldown", SerializableDataTypes.INT, 1)
                        .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("includes_prime_adversary", SerializableDataTypes.BOOLEAN, true),
                data ->
                        (type, player) -> new ActionOnTargetDeathPower(type, player, data.getInt("cooldown"),
                                (HudRender)data.get("hud_render"), (ConditionFactory<Pair<DamageSource, Float>>.Instance)data.get("damage_condition"),
                                (ActionFactory<Pair<Entity, Entity>>.Instance)data.get("bientity_action"),
                                (ConditionFactory<Pair<Entity, Entity>>.Instance)data.get("bientity_condition"),
                                data.getBoolean("includes_prime_adversary")))
                .allowCondition();
    }
}
