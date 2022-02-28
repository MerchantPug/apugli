package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.behavior.MobBehavior;
import io.github.merchantpug.apugli.util.ApugliDataTypes;
import io.github.merchantpug.apugli.util.MobBehaviorUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class ModifyMobBehaviorPower extends Power {
    private final Predicate<Entity> entityCondition;
    private final Predicate<Pair<Entity, Entity>> bientityCondition;

    private final MobBehavior mobBehavior;
    public final List<MobEntity> modifiedEntities = new ArrayList<>();

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ModifyMobBehaviorPower>(Apugli.identifier("modify_mob_behavior"),
                new SerializableData()
                        .add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null)
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("behavior", ApugliDataTypes.MOB_BEHAVIOR),
                data ->
                        (type, player) -> new ModifyMobBehaviorPower(type, player, data.get("entity_condition"), data.get("bientity_condition"), data.get("behavior")))
                .allowCondition();
    }

    public ModifyMobBehaviorPower(PowerType<?> type, LivingEntity entity, Predicate<Entity> entityCondition, Predicate<Pair<Entity, Entity>> bientityCondition, MobBehavior mobBehavior) {
        super(type, entity);
        this.entityCondition = entityCondition;
        this.bientityCondition = bientityCondition;
        this.mobBehavior = mobBehavior;
        this.addMobPredicate(this::doesApply);
        this.addPowerHolderPredicate(livingEntity -> PowerHolderComponent.KEY.get(livingEntity).hasPower(this.getType()) && PowerHolderComponent.KEY.get(livingEntity).getPower(this.getType()).isActive());
        this.setTicking(true);
    }

    public boolean doesApply(Entity e) {
        if (entity.world.isClient) return false;
        return (entityCondition == null || entityCondition.test(e)) && (bientityCondition == null || bientityCondition.test(new Pair<>(entity, e)));
    }

    @Override
    public void tick() {
        if (entity.age % 10 != 0) return;

        if (this.isActive()) {
            MobBehaviorUtil.mobEntityMap.stream().filter(mob -> this.doesApply(mob) && !mobBehavior.hasAppliedGoals(mob)).forEach(mob -> {
                mobBehavior.initGoals(mob);
                this.modifiedEntities.add(mob);
            });
        }

        for (Iterator<MobEntity> iterator = modifiedEntities.stream().filter(mob -> !this.doesApply(mob) || !this.isActive()).iterator(); iterator.hasNext();) {
            MobEntity mob = iterator.next();
            if (this.getMobBehavior().isHostile(mob, entity) && (mob.getTarget() == this.entity || mob instanceof Angerable && ((Angerable) mob).getAngryAt() == entity.getUuid())) {
                if (mob instanceof Angerable && ((Angerable) mob).getTarget() == entity) {
                    ((Angerable) mob).stopAnger();
                }
                mob.setTarget(null);
            }
            this.mobBehavior.removeGoals(mob);
        }
        modifiedEntities.removeIf(mob -> !this.doesApply(mob) || !this.isActive());
    }

    @Override
    public void onAdded() {
        if (entity.world.isClient) return;
        MobBehaviorUtil.mobEntityMap.stream().filter(this::doesApply).forEach(mob -> {
            this.mobBehavior.initGoals(mob);
            this.modifiedEntities.add(mob);
        });
        MobBehaviorUtil.powerMap.add(this);
    }

    @Override
    public void onRemoved() {
        if (entity.world.isClient) return;
        for (Iterator<MobEntity> iterator = modifiedEntities.stream().iterator(); iterator.hasNext();) {
            MobEntity mob = iterator.next();
            if (mob.getTarget() == this.entity || mob instanceof Angerable && ((Angerable) mob).getAngryAt() == entity.getUuid()) {
                if (mob instanceof Angerable && ((Angerable) mob).getTarget() == entity) {
                    ((Angerable) mob).stopAnger();
                }
                mob.setTarget(null);
            }
            this.mobBehavior.removeGoals(mob);
        }
        MobBehaviorUtil.powerMap.remove(this);
    }

    public void addMobPredicate(Predicate<LivingEntity> predicate) {
        mobBehavior.addMobRelatedPredicate(predicate);
    }

    public void addPowerHolderPredicate(Predicate<LivingEntity> predicate) {
        mobBehavior.addEntityRelatedPredicate(predicate);
    }

    public MobBehavior getMobBehavior() {
        return mobBehavior;
    }
}