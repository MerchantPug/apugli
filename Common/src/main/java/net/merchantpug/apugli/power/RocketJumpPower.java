package net.merchantpug.apugli.power;

import net.merchantpug.apugli.access.ExplosionAccess;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.netty.buffer.Unpooled;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class RocketJumpPower extends ActiveCooldownPower {
    private Key key;
    private final double distance;
    private final DamageSource source;
    private final float amount;
    private final double horizontalVelocity;
    private final double verticalVelocity;
    private final double velocityClampMultiplier;
    private final boolean useCharged;
    private final List<Modifier> chargedModifiers = new LinkedList<>();
    private final List<Modifier> waterModifiers = new LinkedList<>();
    private final List<Modifier> damageModifiers = new LinkedList<>();
    private final Predicate<Tuple<Entity, Entity>> targetableBiEntityCondition;
    private final Predicate<Tuple<Entity, Entity>> damageBiEntityCondition;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<RocketJumpPower>(Apugli.identifier("rocket_jump"),
                new SerializableData()
                        .add("cooldown", SerializableDataTypes.INT, 1)
                        .add("distance", SerializableDataTypes.DOUBLE, Double.NaN)
                        .add("source", SerializableDataTypes.DAMAGE_SOURCE, null)
                        .add("amount", SerializableDataTypes.FLOAT, 0.0F)
                        .add("velocity", SerializableDataTypes.DOUBLE, 1.0D)
                        .addFunctionedDefault("horizontal_velocity", SerializableDataTypes.DOUBLE, data -> data.getDouble("velocity"))
                        .addFunctionedDefault("vertical_velocity", SerializableDataTypes.DOUBLE, data -> data.getDouble("velocity"))
                        .add("velocity_clamp_multiplier", SerializableDataTypes.DOUBLE, 1.8D)
                        .add("use_charged", SerializableDataTypes.BOOLEAN, false)
                        .add("charged_modifier", Modifier.DATA_TYPE, null)
                        .add("charged_modifiers", Modifier.LIST_TYPE, null)
                        .add("water_modifier", Modifier.DATA_TYPE, null)
                        .add("water_modifiers", Modifier.LIST_TYPE, null)
                        .add("damage_modifier", Modifier.DATA_TYPE, null)
                        .add("damage_modifiers", Modifier.LIST_TYPE, null)
                        .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
                        .add("targetable_bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("damage_bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("key", ApoliDataTypes.KEY, new Active.Key()),
                (data) ->
                        (type, entity) ->  {
                            RocketJumpPower power = new RocketJumpPower(
                                    type,
                                    entity,
                                    data.getInt("cooldown"),
                                    data.get("hud_render"),
                                    data.getDouble("distance"),
                                    data.get("source"),
                                    data.getFloat("amount"),
                                    data.getDouble("horizontal_velocity"),
                                    data.getDouble("vertical_velocity"),
                                    data.getDouble("velocity_clamp_multiplier"),
                                    data.getBoolean("use_charged"),
                                    data.get("damage_bientity_condition"),
                                    data.get("targetable_bientity_condition"));
                            power.setKey(data.get("key"));
                            if(data.isPresent("charged_modifier")) {
                                power.addChargedJumpModifier(data.get("charged_modifier"));
                            }
                            if(data.isPresent("charged_modifiers")) {
                                ((List<Modifier>)data.get("charged_modifiers")).forEach(power::addChargedJumpModifier);
                            }
                            if(data.isPresent("water_modifier")) {
                                power.addWaterJumpModifier(data.get("water_modifier"));
                            }
                            if(data.isPresent("water_modifiers")) {
                                ((List<Modifier>)data.get("water_modifiers")).forEach(power::addWaterJumpModifier);
                            }
                            if(data.isPresent("damage_modifier")) {
                                power.addDamageModifier(data.get("damage_modifier"));
                            }
                            if(data.isPresent("damage_modifiers")) {
                                ((List<Modifier>)data.get("damage_modifiers")).forEach(power::addDamageModifier);
                            }
                            return power;
                        })
                .allowCondition();
    }

    public RocketJumpPower(PowerType<?> type, LivingEntity entity, int cooldownDuration, HudRender hudRender, double distance, DamageSource source, float amount, double horizontalVelocity, double verticalVelocity, double velocityClampMultiplier, boolean useCharged, Predicate<Pair<Entity, Entity>> damageBiEntityCondition, Predicate<Pair<Entity, Entity>> targetableBiEntityCondition) {
        super(type, entity, cooldownDuration, hudRender, null);
        this.distance = distance;
        this.source = source;
        this.amount = amount;
        this.horizontalVelocity = horizontalVelocity;
        this.verticalVelocity = verticalVelocity;
        this.velocityClampMultiplier = velocityClampMultiplier;
        this.useCharged = useCharged;
        this.damageBiEntityCondition = damageBiEntityCondition;
        this.targetableBiEntityCondition = targetableBiEntityCondition;
    }

    @Override
    public void onUse() {
        if (canUse() && !entity.world.isClient()) {
            double baseReach = (entity instanceof PlayerEntity && ((PlayerEntity) entity).getAbilities().creativeMode) ? 5.0D : 4.5D;
            double reach = FabricLoader.getInstance().isModLoaded("reach-entity-attributes") ? ReachEntityAttributes.getReachDistance(entity, baseReach) : baseReach;
            double distance = !Double.isNaN(this.distance) ? this.distance : reach;
            Vec3d eyePosition = entity.getCameraPosVec(0);
            Vec3d lookVector = entity.getRotationVec(0).multiply(distance);
            Vec3d traceEnd = eyePosition.add(lookVector);

            RaycastContext context = new RaycastContext(eyePosition, traceEnd, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, entity);
            BlockHitResult blockHitResult = entity.world.raycast(context);

            double baseEntityAttackRange = (entity instanceof PlayerEntity && ((PlayerEntity) entity).getAbilities().creativeMode) ? 6.0D : 3.0D;
            double entityAttackRange = FabricLoader.getInstance().isModLoaded("reach-entity-attributes") ? ReachEntityAttributes.getAttackRange(entity, baseEntityAttackRange) : baseEntityAttackRange;
            double entityDistance = !Double.isNaN(this.distance) ? this.distance : entityAttackRange;
            Vec3d entityLookVector = entity.getRotationVec(0).multiply(entityDistance);
            Vec3d entityTraceEnd = eyePosition.add(entityLookVector);
            Box entityBox = entity.getBoundingBox().stretch(lookVector).expand(1.0D);

            double blockHitResultSquaredDistance = blockHitResult != null ? blockHitResult.getBlockPos().getSquaredDistance(eyePosition.x, eyePosition.y, eyePosition.z) : entityDistance * entityDistance;
            double entityReach = Math.min(blockHitResultSquaredDistance, entityDistance * entityDistance);
            EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, eyePosition, entityTraceEnd, entityBox, (traceEntity) -> !traceEntity.isSpectator() && traceEntity.collides() && (targetableBiEntityCondition == null || targetableBiEntityCondition.test(new Pair<>(entity, traceEntity))), entityReach);

            HitResult.Type blockHitResultType = blockHitResult.getType();
            HitResult.Type entityHitResultType = entityHitResult != null ? entityHitResult.getType() : null;

            boolean isCharged = entity.getStatusEffects().stream().anyMatch(effect -> Registry.STATUS_EFFECT.getKey(effect.getEffectType()).isPresent() && Registry.STATUS_EFFECT.entryOf(Registry.STATUS_EFFECT.getKey(effect.getEffectType()).get()).isIn(ApugliTags.CHARGED_EFFECTS));

            if (blockHitResultType == HitResult.Type.MISS && entityHitResultType == HitResult.Type.MISS) return;

            if (entityHitResultType == HitResult.Type.ENTITY) {
                this.handleRocketJump(entityHitResult, isCharged);
                return;
            }
            if (blockHitResultType == HitResult.Type.BLOCK) {
                this.handleRocketJump(blockHitResult, isCharged);
            }
        }
    }

    private void handleRocketJump(HitResult hitResult, boolean isCharged) {
        double horizontalVelocity = isCharged && this.useCharged && !this.getChargedModifiers().isEmpty() ? ModifierUtil.applyModifiers(entity, chargedModifiers, this.horizontalVelocity) : this.horizontalVelocity;
        double verticalVelocity = isCharged && this.useCharged && !this.getChargedModifiers().isEmpty() ? ModifierUtil.applyModifiers(entity, chargedModifiers, this.verticalVelocity) : this.verticalVelocity;
        float e = isCharged && this.useCharged ? 2.0F : 1.5F;
        if(this.source != null && this.amount != 0.0F) entity.hurt(this.source, this.amount);
        float f = Mth.sin(entity.getYRot() * 0.017453292F) * Mth.cos(entity.getXRot() * 0.017453292F);
        float g = Mth.sin(entity.getXRot() * 0.017453292F);
        float h = -Mth.cos(entity.getYRot() * 0.017453292F) * Mth.cos(entity.getXRot() * 0.017453292F);

        Explosion explosion = new Explosion(entity.level, entity, ApugliDamageSources.jumpExplosion(entity), null, hitResult.getLocation().x(), hitResult.getLocation().y(), hitResult.getLocation().z(), e, false, Explosion.BlockInteraction.NONE);
        ((ExplosionAccess)explosion).setRocketJump(true);
        ((ExplosionAccess)explosion).setExplosionDamageModifiers(this.getDamageModifiers());
        ((ExplosionAccess)explosion).setBiEntityPredicate(this.getDamageBiEntityCondition());
        explosion.collectBlocksAndDamageEntities();
        explosion.affectWorld(false);
        explosion.explode();
        explosion.finalizeExplosion(true);

        if(entity.isInWater()) {
            horizontalVelocity = !this.waterModifiers.isEmpty() ? ModifierUtil.applyModifiers(entity, waterModifiers, horizontalVelocity) : horizontalVelocity;
            verticalVelocity = !this.waterModifiers.isEmpty() ? ModifierUtil.applyModifiers(entity, waterModifiers, verticalVelocity) : verticalVelocity;
        }
        Vec3 vec = entity.getDeltaMovement().add(f * horizontalVelocity, g * verticalVelocity, h * horizontalVelocity);
        double horizontalClamp = isCharged ? ModifierUtil.applyModifiers(entity, getChargedModifiers(), horizontalVelocity * velocityClampMultiplier) : horizontalVelocity * velocityClampMultiplier;
        double verticalClamp = isCharged ? ModifierUtil.applyModifiers(entity, getChargedModifiers(), verticalVelocity * velocityClampMultiplier) : verticalVelocity * velocityClampMultiplier;
        entity.setDeltaMovement(Mth.clamp(vec.x, -horizontalClamp, horizontalClamp), Mth.clamp(vec.y, -verticalClamp, verticalClamp), Mth.clamp(vec.z, -horizontalClamp, horizontalClamp));
        entity.hurtMarked = true;
        entity.fallDistance = 0;
        this.use();
    }

    public void sendExplosionToClient(HitResult hitResult, float radius) {
        SyncRocketJumpExplosionPacket packet = new SyncRocketJumpExplosionPacket(entity.getId(), hitResult.getPos().getX(), hitResult.getPos().getY(), hitResult.getPos().getZ(), radius, this.getType().getIdentifier());

        for (ServerPlayerEntity player : PlayerLookup.tracking(entity)) {
            ApugliPackets.sendS2C(packet, player);
        }
        if (!(entity instanceof ServerPlayerEntity serverHolder)) return;
        ApugliPackets.sendS2C(packet, serverHolder);

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getId());
        buf.writeDouble(hitResult.getLocation().x());
        buf.writeDouble(hitResult.getLocation().y());
        buf.writeDouble(hitResult.getLocation().z());
        buf.writeFloat(radius);
        Modifier.LIST_TYPE.send(buf, this.getDamageModifiers());

        for(ServerPlayer player : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(player, ApugliPackets.SYNC_ROCKET_JUMP_EXPLOSION, buf);
        }
        if(!(entity instanceof ServerPlayer)) return;
        ServerPlayNetworking.send((ServerPlayer)entity, ApugliPackets.SYNC_ROCKET_JUMP_EXPLOSION, buf);
    }

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public void setKey(Key key) {
        this.key = key;
    }

    public void addChargedJumpModifier(Modifier modifier) {
        this.chargedModifiers.add(modifier);
    }

    public List<Modifier> getChargedModifiers() {
        return chargedModifiers;
    }

    public void addWaterJumpModifier(Modifier modifier) {
        this.waterModifiers.add(modifier);
    }

    public List<Modifier> getWaterModifiers() {
        return waterModifiers;
    }

    public void addDamageModifier(Modifier modifier) {
        this.damageModifiers.add(modifier);
    }

    public List<Modifier> getDamageModifiers() {
        return damageModifiers;
    }

    public Predicate<Tuple<Entity, Entity>> getTargetableBiEntityCondition() {
        return targetableBiEntityCondition;
    }

    public Predicate<Tuple<Entity, Entity>> getDamageBiEntityCondition() {
        return damageBiEntityCondition;
    }
}
