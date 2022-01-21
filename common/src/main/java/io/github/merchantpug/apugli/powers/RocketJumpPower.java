package io.github.merchantpug.apugli.powers;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.apace100.origins.power.Active;
import io.github.apace100.origins.power.ActiveCooldownPower;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.util.AttributeUtil;
import io.github.apace100.origins.util.HudRender;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.registry.ApugliDamageSources;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.explosion.Explosion;

import java.util.LinkedList;
import java.util.List;

public class RocketJumpPower extends ActiveCooldownPower {
    private Key key;
    private final double distance;
    private final DamageSource source;
    private final float amount;
    private final double speed;
    private final boolean useCharged;
    private final List<EntityAttributeModifier> modifiers = new LinkedList<>();

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<RocketJumpPower>(Apugli.identifier("rocket_jump"),
                new SerializableData()
                        .add("cooldown", SerializableDataType.INT)
                        .add("distance", SerializableDataType.DOUBLE, Double.NaN)
                        .add("source", SerializableDataType.DAMAGE_SOURCE, null)
                        .add("amount", SerializableDataType.FLOAT, 0.0F)
                        .add("speed", SerializableDataType.DOUBLE, 1.0D)
                        .add("use_charged", SerializableDataType.BOOLEAN, false)
                        .add("charged_modifier", SerializableDataType.ATTRIBUTE_MODIFIER, null)
                        .add("charged_modifiers", SerializableDataType.ATTRIBUTE_MODIFIERS, null)
                        .add("hud_render", SerializableDataType.HUD_RENDER)
                        .add("key", SerializableDataType.KEY, new Active.Key()),
                (data) ->
                        (type, player) ->  {
                            RocketJumpPower power = new RocketJumpPower(type, player, data.getInt("cooldown"), (HudRender)data.get("hud_render"), data.getDouble("distance"), (DamageSource)data.get("source"), data.getFloat("amount"), data.getDouble("speed"), data.getBoolean("use_charged"));
                            power.setKey((Active.Key)data.get("key"));
                            if(data.isPresent("charged_modifier")) {
                                power.addChargedJumpModifier(data.getModifier("charged_modifier"));
                            }
                            if(data.isPresent("charged_modifiers")) {
                                ((List<EntityAttributeModifier>)data.get("charged_modifiers")).forEach(power::addChargedJumpModifier);
                            }
                            return power;
                        })
                .allowCondition();
    }

    public RocketJumpPower(PowerType<?> type, PlayerEntity player, int cooldownDuration, HudRender hudRender, double distance, DamageSource source, float amount, double speed, boolean useCharged) {
        super(type, player, cooldownDuration, hudRender, null);
        this.distance = distance;
        this.source = source;
        this.amount = amount;
        this.speed = speed;
        this.useCharged = useCharged;
    }

    @Override
    public void onUse() {
        if (canUse()) {
            if (!player.world.isClient()) {
                double baseReach = player.abilities.creativeMode ? 5.0D : 4.5D;
                double reach = this.getReach(player, baseReach);
                double distance = !Double.isNaN(this.distance) ? this.distance : reach;
                Vec3d eyePosition = player.getCameraPosVec(0);
                Vec3d lookVector = player.getRotationVec(0).multiply(distance);
                Vec3d traceEnd = eyePosition.add(lookVector);

                RaycastContext context = new RaycastContext(eyePosition, traceEnd, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player);
                BlockHitResult blockHitResult = player.world.raycast(context);

                double baseEntityAttackRange = player.abilities.creativeMode ? 6.0D : 3.0D;
                double entityAttackRange = this.getAttackRange(player, baseEntityAttackRange);
                double entityDistance = !Double.isNaN(this.distance) ? this.distance : entityAttackRange;
                Vec3d entityLookVector = player.getRotationVec(0).multiply(entityDistance);
                Vec3d entityTraceEnd = eyePosition.add(entityLookVector);
                Box entityBox = player.getBoundingBox().stretch(lookVector).expand(1.0D);

                double blockHitResultSquaredDistance = blockHitResult != null ? blockHitResult.getBlockPos().getSquaredDistance(eyePosition.x, eyePosition.y, eyePosition.z, true) : entityDistance * entityDistance;
                double entityReach = Math.min(blockHitResultSquaredDistance, entityDistance * entityDistance);
                EntityHitResult entityHitResult = ProjectileUtil.raycast(player, eyePosition, entityTraceEnd, entityBox, (traceEntity) -> !traceEntity.isSpectator() && traceEntity.collides(), entityReach);

                HitResult.Type blockHitResultType = blockHitResult.getType();
                HitResult.Type entityHitResultType = entityHitResult != null ? entityHitResult.getType() : null;
                boolean tmoCharged = FabricLoader.getInstance().isModLoaded("toomanyorigins") && player.hasStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("toomanyorigins", "charged")));
                boolean cursedCharged = FabricLoader.getInstance().isModLoaded("cursedorigins") && player.hasStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("cursedorigins", "charged")));
                boolean isCharged = tmoCharged || cursedCharged;

                if (blockHitResultType == HitResult.Type.MISS && entityHitResultType == HitResult.Type.MISS) return;

                if (blockHitResultType == HitResult.Type.BLOCK) {
                    this.handleRocketJump(blockHitResult, isCharged);
                }

                if (entityHitResultType == HitResult.Type.ENTITY) {
                    this.handleRocketJump(entityHitResult, isCharged);
                }
            }
        }
    }

    @ExpectPlatform
    public static double getReach(Entity entity, double baseReach) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static double getAttackRange(Entity entity, double baseReach) {
        throw new AssertionError();
    }

    private void handleRocketJump(HitResult hitResult, boolean isCharged) {
        double speed = isCharged && this.useCharged && !this.getChargedModifiers().isEmpty() ? AttributeUtil.sortAndApplyModifiers(modifiers, this.speed) : this.speed;
        float e = isCharged && this.useCharged ? 2.0F : 1.5F;
        if (this.source != null && this.amount != 0.0F) player.damage(this.source, this.amount);
        float f = MathHelper.sin(player.yaw * 0.017453292F) * MathHelper.cos(player.pitch * 0.017453292F);
        float g = MathHelper.sin(player.pitch * 0.017453292F);
        float h = -MathHelper.cos(player.yaw * 0.017453292F) * MathHelper.cos(player.pitch * 0.017453292F);

        player.world.createExplosion(player, ApugliDamageSources.jumpExplosion(player), null, hitResult.getPos().getX(), hitResult.getPos().getY(), hitResult.getPos().getZ(), e, false, Explosion.DestructionType.NONE);
        player.addVelocity(f * speed, g * speed, h * speed);
        player.velocityModified = true;
        player.fallDistance = 0;
        this.use();
    }

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public void setKey(Key key) {
        this.key = key;
    }

    public void addChargedJumpModifier(EntityAttributeModifier modifier) {
        this.modifiers.add(modifier);
    }

    public List<EntityAttributeModifier> getChargedModifiers() {
        return modifiers;
    }
}
