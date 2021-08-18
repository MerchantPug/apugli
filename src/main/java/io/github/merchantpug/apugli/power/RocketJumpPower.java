package io.github.merchantpug.apugli.power;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import io.github.apace100.apoli.power.ActiveCooldownPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.util.HudRender;
import io.github.merchantpug.apugli.registry.ApugliDamageSources;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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

import java.util.function.Predicate;

public class RocketJumpPower extends ActiveCooldownPower {
    private Key key;
    private DamageSource source;
    private float amount;
    private double speed;
    private boolean useCharged;

    public RocketJumpPower(PowerType<?> type, LivingEntity entity, int cooldownDuration, HudRender hudRender, DamageSource source, float amount, double speed, boolean useCharged) {
        super(type, entity, cooldownDuration, hudRender, null);
        this.source = source;
        this.amount = amount;
        this.speed = speed;
        this.useCharged = useCharged;
    }

    @Override
    public void onUse() {
        if (canUse()) {
            if (!entity.world.isClient()) {
                double baseReach = 4.5D;
                if (entity instanceof PlayerEntity) {
                    if (((PlayerEntity) entity).getAbilities().creativeMode) {
                        baseReach = 5.0D;
                    }
                }
                double reach;
                if (FabricLoader.getInstance().isModLoaded("reach-entity-attributes")) {
                    reach = ReachEntityAttributes.getReachDistance((LivingEntity) entity, baseReach);
                } else {
                    reach = baseReach;
                }
                Vec3d vec3d = entity.getCameraPosVec(0.0F);
                Vec3d vec3d2 = entity.getRotationVec(0.0F);
                Vec3d vec3d3 = vec3d.add(vec3d2.x * reach, vec3d2.y * reach, vec3d2.z * reach);
                Box box = entity.getBoundingBox().stretch(vec3d2).expand(1.0D);
                double entityReach = reach * reach;
                Predicate<Entity> predicate = (entityx) -> !entityx.isSpectator() && entityx.collides();
                EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, vec3d, vec3d3, box, predicate, entityReach);
                BlockHitResult blockHitResult = entity.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, entity));

                boolean tmoCharged;
                boolean cursedCharged;
                if (FabricLoader.getInstance().isModLoaded("toomanyorigins")) {
                    tmoCharged = ((LivingEntity) entity).hasStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("toomanyorigins", "charged")));
                } else tmoCharged = false;
                if (FabricLoader.getInstance().isModLoaded("cursedorigins")) {
                    cursedCharged = ((LivingEntity) entity).hasStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("cursedorigins", "charged")));
                } else cursedCharged = false;
                double d = (tmoCharged || cursedCharged) && this.useCharged ? 1.5D : 1.0D;
                float e = (tmoCharged || cursedCharged) && this.useCharged ? 2.0F : 1.5F;
                if (entityHitResult != null && entityHitResult.getEntity() instanceof LivingEntity && entityHitResult.getType() == HitResult.Type.ENTITY) {
                    if (this.source != null && this.amount != 0.0F) {
                        entity.damage(this.source, this.amount);
                    }
                    float f = MathHelper.sin(entity.getYaw() * 0.017453292F) * MathHelper.cos(entity.getPitch() * 0.017453292F);
                    float g = MathHelper.sin(entity.getPitch() * 0.017453292F);
                    float h = -MathHelper.cos(entity.getYaw() * 0.017453292F) * MathHelper.cos(entity.getPitch() * 0.017453292F);

                    entity.world.createExplosion(entity, ApugliDamageSources.jumpExplosion((LivingEntity) entity), null, blockHitResult.getPos().getX(), blockHitResult.getPos().getY(), blockHitResult.getPos().getZ(), e, false, Explosion.DestructionType.NONE);
                    entity.addVelocity(f * this.speed * d, g * this.speed * d, h * this.speed * d);
                    entity.velocityModified = true;
                    this.use();
                } else if (blockHitResult != null && blockHitResult.getType() == HitResult.Type.BLOCK) {
                    if (this.source != null && this.amount != 0.0F) {
                        entity.damage(this.source, this.amount);
                    }
                    float f = MathHelper.sin(entity.getYaw() * 0.017453292F) * MathHelper.cos(entity.getPitch() * 0.017453292F);
                    float g = MathHelper.sin(entity.getPitch() * 0.017453292F);
                    float h = -MathHelper.cos(entity.getYaw() * 0.017453292F) * MathHelper.cos(entity.getPitch() * 0.017453292F);

                    entity.world.createExplosion(entity, ApugliDamageSources.jumpExplosion((LivingEntity) entity), null, blockHitResult.getPos().getX(), blockHitResult.getPos().getY(), blockHitResult.getPos().getZ(), e, false, Explosion.DestructionType.NONE);
                    entity.addVelocity(f * this.speed * d, g * this.speed * d, h * this.speed * d);
                    entity.velocityModified = true;
                    this.use();
                }
            }
        }
    }

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public void setKey(Key key) {
        this.key = key;
    }
}
