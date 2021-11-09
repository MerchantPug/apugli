package io.github.merchantpug.apugli.power;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.ActiveCooldownPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.AttributeUtil;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.registry.ApugliDamageSources;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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
import java.util.stream.Collectors;

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
                        .add("cooldown", SerializableDataTypes.INT)
                        .add("distance", SerializableDataTypes.DOUBLE, Double.NaN)
                        .add("source", SerializableDataTypes.DAMAGE_SOURCE, null)
                        .add("amount", SerializableDataTypes.FLOAT, 0.0F)
                        .add("speed", SerializableDataTypes.DOUBLE, 1.0D)
                        .add("use_charged", SerializableDataTypes.BOOLEAN, false)
                        .add("charged_modifier", SerializableDataTypes.ATTRIBUTE_MODIFIER, null)
                        .add("charged_modifiers", SerializableDataTypes.ATTRIBUTE_MODIFIERS, null)
                        .add("hud_render", ApoliDataTypes.HUD_RENDER)
                        .add("key", ApoliDataTypes.KEY, new Active.Key()),
                (data) ->
                        (type, entity) ->  {
                            RocketJumpPower power = new RocketJumpPower(type, entity, data.getInt("cooldown"), (HudRender)data.get("hud_render"), data.getDouble("distance"), (DamageSource)data.get("source"), data.getFloat("amount"), data.getDouble("speed"), data.getBoolean("use_charged"));
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

    public RocketJumpPower(PowerType<?> type, LivingEntity entity, int cooldownDuration, HudRender hudRender, double distance, DamageSource source, float amount, double speed, boolean useCharged) {
        super(type, entity, cooldownDuration, hudRender, null);
        this.distance = distance;
        this.source = source;
        this.amount = amount;
        this.speed = speed;
        this.useCharged = useCharged;
    }

    @Override
    public void onUse() {
        if (canUse()) {
            if (!entity.world.isClient()) {
                double baseReach = (entity instanceof PlayerEntity && ((PlayerEntity) entity).getAbilities().creativeMode) ? 5.0D : 4.5D;
                double reach = FabricLoader.getInstance().isModLoaded("reach-entity-attributes") ? ReachEntityAttributes.getReachDistance(entity, baseReach) : baseReach;
                double distance = !Double.isNaN(this.distance) ? this.distance : reach;
                Vec3d eyePosition = entity.getCameraPosVec(0);
                Vec3d lookVector = entity.getRotationVec(0).multiply(distance);
                Vec3d traceEnd = eyePosition.add(lookVector);
                Box box = entity.getBoundingBox().stretch(lookVector).expand(1.0D);

                RaycastContext context = new RaycastContext(eyePosition, traceEnd, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, entity);
                BlockHitResult blockHitResult = entity.world.raycast(context);

                double entityReach = blockHitResult != null ? blockHitResult.getBlockPos().getSquaredDistance(eyePosition.x, eyePosition.y, eyePosition.z, true) : distance * distance;
                EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, eyePosition, traceEnd, box, (traceEntity) -> !traceEntity.isSpectator() && traceEntity.collides(), entityReach);

                HitResult.Type blockHitResultType = blockHitResult.getType();
                HitResult.Type entityHitResultType = entityHitResult != null ? entityHitResult.getType() : null;
                boolean tmoCharged = FabricLoader.getInstance().isModLoaded("toomanyorigins") && entity.hasStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("toomanyorigins", "charged")));
                boolean cursedCharged = FabricLoader.getInstance().isModLoaded("cursedorigins") && entity.hasStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("cursedorigins", "charged")));
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

    private void handleRocketJump(HitResult hitResult, boolean isCharged) {
        double speed = isCharged && this.useCharged && !this.getChargedModifiers().isEmpty() ? modifyCharged(entity, RocketJumpPower.class, this.speed) : this.speed;
        float e = isCharged && this.useCharged ? 2.0F : 1.5F;
        if (this.source != null && this.amount != 0.0F) entity.damage(this.source, this.amount);
        float f = MathHelper.sin(entity.getYaw() * 0.017453292F) * MathHelper.cos(entity.getPitch() * 0.017453292F);
        float g = MathHelper.sin(entity.getPitch() * 0.017453292F);
        float h = -MathHelper.cos(entity.getYaw() * 0.017453292F) * MathHelper.cos(entity.getPitch() * 0.017453292F);

        entity.world.createExplosion(entity, ApugliDamageSources.jumpExplosion(entity), null, hitResult.getPos().getX(), hitResult.getPos().getY(), hitResult.getPos().getZ(), e, false, Explosion.DestructionType.NONE);
        entity.addVelocity(f * speed, g * speed, h * speed);
        entity.velocityModified = true;
        entity.fallDistance = 0;
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

    private static <T extends RocketJumpPower> double modifyCharged(Entity entity, Class<T> powerClass, double baseValue) {
        List<EntityAttributeModifier> modifiers = PowerHolderComponent.KEY.get(entity).getPowers(powerClass).stream()
                .flatMap(p -> p.getChargedModifiers().stream()).collect(Collectors.toList());
        return (float) AttributeUtil.sortAndApplyModifiers(modifiers, baseValue);
    }
}
