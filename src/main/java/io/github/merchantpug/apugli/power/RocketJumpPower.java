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
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RocketJumpPower extends ActiveCooldownPower {
    private Key key;
    private DamageSource source;
    private float amount;
    private double speed;
    private boolean useCharged;
    private final List<EntityAttributeModifier> modifiers = new LinkedList<>();

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<RocketJumpPower>(Apugli.identifier("rocket_jump"),
                new SerializableData()
                        .add("cooldown", SerializableDataTypes.INT)
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
                            RocketJumpPower power = new RocketJumpPower(type, entity, data.getInt("cooldown"), (HudRender)data.get("hud_render"), (DamageSource)data.get("source"), data.getFloat("amount"), data.getDouble("speed"), data.getBoolean("use_charged"));
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
                boolean isCharged = tmoCharged || cursedCharged;
                if (entityHitResult != null && entityHitResult.getType() == HitResult.Type.ENTITY) {
                    this.handleRocketJump(entityHitResult, isCharged);
                    this.use();
                } else if (blockHitResult != null && blockHitResult.getType() == HitResult.Type.BLOCK) {
                    this.handleRocketJump(blockHitResult, isCharged);
                    this.use();
                }
            }
        }
    }

    private void handleRocketJump(HitResult hitResult, boolean isCharged) {
        double speed = this.speed;
        if (isCharged && this.useCharged && !this.getChargedModifiers().isEmpty()) {
            speed = modifyCharged(entity, RocketJumpPower.class, this.speed, null, null);
        }
        float e = isCharged && this.useCharged ? 2.0F : 1.5F;
        if (this.source != null && this.amount != 0.0F) {
            entity.damage(this.source, this.amount);
        }
        float f = MathHelper.sin(entity.getYaw() * 0.017453292F) * MathHelper.cos(entity.getPitch() * 0.017453292F);
        float g = MathHelper.sin(entity.getPitch() * 0.017453292F);
        float h = -MathHelper.cos(entity.getYaw() * 0.017453292F) * MathHelper.cos(entity.getPitch() * 0.017453292F);

        entity.world.createExplosion(entity, ApugliDamageSources.jumpExplosion((LivingEntity) entity), null, hitResult.getPos().getX(), hitResult.getPos().getY(), hitResult.getPos().getZ(), e, false, Explosion.DestructionType.NONE);
        entity.addVelocity(f * speed, g * speed, h * speed);
        entity.velocityModified = true;
        entity.fallDistance = 0;
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

    private static <T extends RocketJumpPower> double modifyCharged(Entity entity, Class<T> powerClass, double baseValue, Predicate<T> powerFilter, Consumer<T> powerAction) {
        List<EntityAttributeModifier> modifiers = PowerHolderComponent.KEY.get(entity).getPowers(powerClass).stream()
                .flatMap(p -> p.getChargedModifiers().stream()).collect(Collectors.toList());
        return (float) AttributeUtil.sortAndApplyModifiers(modifiers, baseValue);
    }
}
