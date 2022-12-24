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
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.Pair;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnProjectileHitPower extends CooldownPower {
    private final Predicate<Pair<Entity, Entity>> biEntityCondition;
    private final Consumer<Pair<Entity, Entity>> biEntityAction;
    private final Predicate<Pair<Entity, Entity>> ownerBiEntityCondition;
    private final Consumer<Pair<Entity, Entity>> ownerBiEntityAction;

    public ActionOnProjectileHitPower(PowerType<?> type, LivingEntity entity, int cooldownDuration, HudRender hudRender, Consumer<Pair<Entity, Entity>> biEntityAction, Predicate<Pair<Entity, Entity>> biEntityCondition, Consumer<Pair<Entity, Entity>> ownerBiEntityAction, Predicate<Pair<Entity, Entity>> ownerBiEntityCondition) {
        super(type, entity, cooldownDuration, hudRender);
        this.biEntityCondition = biEntityCondition;
        this.biEntityAction = biEntityAction;
        this.ownerBiEntityCondition = ownerBiEntityCondition;
        this.ownerBiEntityAction = ownerBiEntityAction;
    }

    public void onHit(Entity target, ProjectileEntity projectile) {
        if (canUse() && (biEntityCondition == null || biEntityCondition.test(new Pair<>(projectile, target))) && (ownerBiEntityCondition == null || ownerBiEntityCondition.test(new Pair<>(entity, target)))) {
            if (this.biEntityAction != null) {
                this.biEntityAction.accept(new Pair<>(projectile, target));
            }
            if (this.ownerBiEntityAction != null) {
                this.ownerBiEntityAction.accept(new Pair<>(entity, target));
            }
            this.use();
        }
    }

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(Apugli.identifier("action_on_projectile_hit"),
                new SerializableData()
                        .add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
                        .add("cooldown", SerializableDataTypes.INT, 1)
                        .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("owner_bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
                        .add("owner_bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
                data ->
                        (type, player) -> new ActionOnProjectileHitPower(type, player, data.getInt("cooldown"),
                                data.get("hud_render"),
                                data.get("bientity_action"),
                                data.get("bientity_condition"),
                                data.get("owner_bientity_action"),
                                data.get("owner_bientity_condition"))
        ).allowCondition();
    }
}
