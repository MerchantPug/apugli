package net.merchantpug.apugli.power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Pair;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnHarmPower extends CooldownPower {
    private final Predicate<Pair<Entity, Entity>> biEntityCondition;
    private final Predicate<Pair<DamageSource, Float>> damageCondition;
    private final Consumer<Pair<Entity, Entity>> biEntityAction;
    private final float amountToTrigger;
    private final boolean overflow;

    public ActionOnHarmPower(PowerType<?> type, LivingEntity entity, int cooldownDuration, HudRender hudRender, Consumer<Pair<Entity, Entity>> biEntityAction, Predicate<Pair<DamageSource, Float>> damageCondition, Predicate<Pair<Entity, Entity>> biEntityCondition, float amountToTrigger, boolean overflow) {
        super(type, entity, cooldownDuration, hudRender);
        this.biEntityAction = biEntityAction;
        this.biEntityCondition = biEntityCondition;
        this.damageCondition = damageCondition;
        this.amountToTrigger = amountToTrigger;
        this.overflow = overflow;
    }

    public void onHurt(LivingEntity target, DamageSource source, float amount) {
        if (canUse() && (damageCondition == null || damageCondition.test(new Pair<>(source, amount))) && (biEntityCondition == null || biEntityCondition.test(new Pair<>(entity, target)))) {
            float triggerTimes = overflow ? amount / amountToTrigger : Math.min(target.getHealth(), amount) / amountToTrigger;
            for (int i = 0; i < triggerTimes; ++i) {
                this.biEntityAction.accept(new Pair<>(entity, target));
            }
            this.use();
        }
    }

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(Apugli.identifier("action_on_harm"),
                new SerializableData()
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION)
                        .add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("cooldown", SerializableDataTypes.INT, 1)
                        .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
                        .add("amount_to_trigger", SerializableDataTypes.FLOAT, 1.0F)
                        .add("overflow", SerializableDataTypes.BOOLEAN, false),
                data ->
                        (type, player) -> new ActionOnHarmPower(type, player,
                                data.getInt("cooldown"),
                                data.get("hud_render"),
                                data.get("bientity_action"),
                                data.get("damage_condition"),
                                data.get("bientity_condition"),
                                data.getFloat("amount_to_trigger"),
                                data.getBoolean("overflow"))
        ).allowCondition();
    }
}
