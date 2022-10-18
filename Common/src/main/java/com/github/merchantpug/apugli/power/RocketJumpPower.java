package com.github.merchantpug.apugli.power;

import the.great.migration.merchantpug.apugli.networking.ApugliPackets;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.github.merchantpug.apugli.access.ExplosionAccess;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.ActiveCooldownPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import the.great.migration.merchantpug.apugli.Apugli;
import the.great.migration.merchantpug.apugli.registry.ApugliDamageSources;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedList;
import java.util.List;

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
                        .add("key", ApoliDataTypes.KEY, new Active.Key()),
                (data) ->
                        (type, entity) ->  {
                            RocketJumpPower power = new RocketJumpPower(type, entity, data.getInt("cooldown"), (HudRender)data.get("hud_render"), data.getDouble("distance"), (DamageSource)data.get("source"), data.getFloat("amount"), data.getDouble("horizontal_velocity"), data.getDouble("vertical_velocity"), data.getDouble("velocity_clamp_multiplier"), data.getBoolean("use_charged"));
                            power.setKey((Active.Key)data.get("key"));
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

    public RocketJumpPower(PowerType<?> type, LivingEntity entity, int cooldownDuration, HudRender hudRender, double distance, DamageSource source, float amount, double horizontalVelocity, double verticalVelocity, double velocityClampMultiplier, boolean useCharged) {
        super(type, entity, cooldownDuration, hudRender, null);
        this.distance = distance;
        this.source = source;
        this.amount = amount;
        this.horizontalVelocity = horizontalVelocity;
        this.verticalVelocity = verticalVelocity;
        this.velocityClampMultiplier = velocityClampMultiplier;
        this.useCharged = useCharged;
    }

    @Override
    public void onUse() {
        if(canUse()) {
            if(!entity.level.isClientSide()) {
                double baseReach = (entity instanceof Player && ((Player) entity).getAbilities().instabuild) ? 5.0D : 4.5D;
                double reach = FabricLoader.getInstance().isModLoaded("reach-entity-attributes") ? ReachEntityAttributes.getReachDistance(entity, baseReach) : baseReach;
                double distance = !Double.isNaN(this.distance) ? this.distance : reach;
                Vec3 eyePosition = entity.getEyePosition(0);
                Vec3 lookVector = entity.getViewVector(0).scale(distance);
                Vec3 traceEnd = eyePosition.add(lookVector);

                ClipContext context = new ClipContext(eyePosition, traceEnd, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity);
                BlockHitResult blockHitResult = entity.level.clip(context);

                double baseEntityAttackRange = (entity instanceof Player && ((Player)entity).getAbilities().instabuild) ? 6.0D : 3.0D;
                double entityAttackRange = FabricLoader.getInstance().isModLoaded("reach-entity-attributes") ? ReachEntityAttributes.getAttackRange(entity, baseEntityAttackRange) : baseEntityAttackRange;
                double entityDistance = !Double.isNaN(this.distance) ? this.distance : entityAttackRange;
                Vec3 entityLookVector = entity.getViewVector(0).scale(entityDistance);
                Vec3 entityTraceEnd = eyePosition.add(entityLookVector);
                AABB entityBox = entity.getBoundingBox().expandTowards(lookVector).inflate(1.0D);

                double blockHitResultSquaredDistance = blockHitResult != null ? blockHitResult.getBlockPos().distToLowCornerSqr(eyePosition.x, eyePosition.y, eyePosition.z) : entityDistance * entityDistance;
                double entityReach = Math.min(blockHitResultSquaredDistance, entityDistance * entityDistance);
                EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(entity, eyePosition, entityTraceEnd, entityBox, (traceEntity) -> !traceEntity.isSpectator() && traceEntity.isPickable() && (!(traceEntity instanceof ArmorStand) || !((ArmorStand)traceEntity).isMarker()), entityReach);

                HitResult.Type blockHitResultType = blockHitResult.getType();
                HitResult.Type entityHitResultType = entityHitResult != null ? entityHitResult.getType() : null;
                boolean tmoCharged = FabricLoader.getInstance().isModLoaded("toomanyorigins") && entity.hasEffect(Registry.MOB_EFFECT.get(new ResourceLocation("toomanyorigins", "charged")));
                boolean cursedCharged = FabricLoader.getInstance().isModLoaded("cursedorigins") && entity.hasEffect(Registry.MOB_EFFECT.get(new ResourceLocation("cursedorigins", "charged")));
                boolean isCharged = tmoCharged || cursedCharged;

                if(blockHitResultType == HitResult.Type.MISS && entityHitResultType == HitResult.Type.MISS) return;

                if(entityHitResultType == HitResult.Type.ENTITY) {
                    this.handleRocketJump(entityHitResult, isCharged);
                    return;
                }

                if(blockHitResultType == HitResult.Type.BLOCK) {
                    this.handleRocketJump(blockHitResult, isCharged);
                }
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
        explosion.explode();
        explosion.finalizeExplosion(true);

        sendExplosionToClient(hitResult, e);

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
}
