package net.merchantpug.apugli;

import io.github.apace100.apoli.integration.PowerLoadEvent;
import io.github.edwinmindcraft.apoli.api.component.IPowerDataCache;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.fabric.FabricPowerFactory;
import net.merchantpug.apugli.access.ItemStackAccess;
import net.merchantpug.apugli.access.PowerLoadEventPostAccess;
import net.merchantpug.apugli.capability.HitsOnTargetCapability;
import net.merchantpug.apugli.capability.KeyPressCapability;
import net.merchantpug.apugli.mixin.forge.common.accessor.FabricPowerFactoryAccessor;
import net.merchantpug.apugli.network.ApugliPacketHandler;
import net.merchantpug.apugli.networking.s2c.UpdateUrlTexturesPacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.*;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.merchantpug.apugli.util.TextureUtil;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Apugli.ID)
public class ApugliForgeEventHandler {

    @SubscribeEvent
    public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player)
            event.addCapability(KeyPressCapability.ID, new KeyPressCapability(player));
        if (event.getObject() instanceof LivingEntity living)
            event.addCapability(HitsOnTargetCapability.ID, new HitsOnTargetCapability(living));
    }

    @SubscribeEvent
    public static void onFinishUsing(LivingEntityUseItemEvent.Finish event) {
        ItemStack stack = event.getItem().copy();
        if (!(((ItemStackAccess)(Object)stack).getEntity() instanceof LivingEntity living)) return;
        Optional<EdibleItemPower> power = Services.POWER.getPowers(living, ApugliPowers.EDIBLE_ITEM.get()).stream().filter(p -> p.doesApply(stack)).findFirst();
        if (power.isPresent()) {
            EdibleItemPower.executeEntityActions(event.getEntity(), stack);
            ItemStack newStack = event.getEntity().eat(event.getEntity().getLevel(), stack);
            if (event.getEntity() instanceof Player player && !player.getAbilities().instabuild) {
                if (power.get().getReturnStack() != null) {
                    ItemStack returnStack = power.get().getReturnStack().copy();
                    if (newStack.isEmpty()) {
                        event.setResultStack(EdibleItemPower.executeItemActions(event.getEntity(), returnStack, stack));
                    } else {
                        ItemStack stack2 = EdibleItemPower.executeItemActions(event.getEntity(), returnStack, stack);
                        if (!player.addItem(stack2)) {
                            player.drop(stack2, false);
                        }
                    }
                } else {
                    event.setResultStack(EdibleItemPower.executeItemActions(event.getEntity(), newStack, stack));
                }
            }
        }
    }

    /*
    The one discrepancy from Fabric Apugli is that the action will always execute in the up direction, this should hopefully have no repercussions.
     */
    @SubscribeEvent
    public static void onBonemeal(BonemealEvent event) {
        if (Services.POWER.hasPower(event.getEntity(), ApugliPowers.ACTION_ON_BONEMEAL.get()) && event.getBlock().getBlock() instanceof BonemealableBlock bonemeal
                && bonemeal.isValidBonemealTarget(event.getLevel(), event.getPos(), event.getBlock(), event.getLevel().isClientSide) && event.getLevel() instanceof ServerLevel
                && bonemeal.isBonemealSuccess(event.getLevel(), event.getLevel().random, event.getPos(), event.getBlock())) {
            Services.POWER.getPowers(event.getEntity(), ApugliPowers.ACTION_ON_BONEMEAL.get())
                    .stream()
                    .filter(p -> p.doesApply(new BlockInWorld(event.getLevel(), event.getPos(), true)))
                    .forEach(p -> p.executeActions(event.getLevel(), event.getPos(), Direction.UP));
        }
    }

    @SubscribeEvent
    public static void modifyAerialBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (!Services.POWER.hasPower(event.getEntity(), ApugliPowers.AERIAL_AFFINITY.get()) || event.getEntity().isOnGround()) return;
        event.setNewSpeed(event.getOriginalSpeed() * 5.0F);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        event.getEntity().getCapability(KeyPressCapability.INSTANCE).ifPresent(KeyPressCapability::tick);

        if (event.getEntity().isDeadOrDying()) return;

        if (Services.POWER.hasPower(event.getEntity(), ApugliPowers.HOVER.get())) {
            event.getEntity().setDeltaMovement(event.getEntity().getDeltaMovement().multiply(1.0, 0.0, 1.0));
            event.getEntity().fallDistance = 0.0F;
        }

        if (!event.getEntity().level.isClientSide)
            ApugliPowers.BUNNY_HOP.get().onTravel(event.getEntity(), new Vec3(event.getEntity().xxa, event.getEntity().yya, event.getEntity().zza));
    }

    @SubscribeEvent(receiveCanceled = true)
    public static void onMobTargetChange(LivingChangeTargetEvent event) {
        if(event.getEntity().level.isClientSide()) return;

        List<MobsIgnorePower> powers = Services.POWER.getPowers(event.getOriginalTarget(), ApugliPowers.MOBS_IGNORE.get());
        if (powers.stream().anyMatch(power -> power.shouldIgnore(event.getEntity())))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getRayTraceResult().getType() != HitResult.Type.ENTITY) return;

        if (((EntityHitResult)event.getRayTraceResult()).getEntity() instanceof LivingEntity living)
            Services.POWER.getPowers(living, ApugliPowers.ACTION_WHEN_PROJECTILE_HIT.get()).forEach(power -> ApugliPowers.ACTION_WHEN_PROJECTILE_HIT.get().execute(power, living, event.getProjectile()));
        if ((event.getProjectile().getOwner() instanceof LivingEntity living))
            Services.POWER.getPowers(living, ApugliPowers.ACTION_ON_PROJECTILE_HIT.get()).forEach(power -> ApugliPowers.ACTION_ON_PROJECTILE_HIT.get().execute(power, living, ((EntityHitResult)event.getRayTraceResult()).getEntity(), event.getProjectile()));
    }

    // Lowest so it can execute after any damage modifications.
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        float amount = event.getAmount();

        float extraEnchantmentDamage = calculateEnchantmentDamage(target, source, amount);

        event.setAmount(extraEnchantmentDamage);

        if (extraEnchantmentDamage != amount || extraEnchantmentDamage == 0.0F) {
            event.setCanceled(true);
        }

        if (!event.isCanceled()) {
            if (source.getEntity() instanceof LivingEntity living)
                Services.POWER.getPowers(living, ApugliPowers.ACTION_ON_HARM.get()).forEach(p -> ApugliPowers.ACTION_ON_HARM.get().execute(p, living, source, amount, target));
            Services.POWER.getPowers(target, ApugliPowers.ACTION_WHEN_HARMED.get()).forEach(p -> ApugliPowers.ACTION_WHEN_HARMED.get().execute(p, target, source, amount));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onLivingAttack(LivingAttackEvent event) {
        float extraEnchantmentDamage = calculateEnchantmentDamage(event.getEntity(), event.getSource(), event.getAmount());

        if (extraEnchantmentDamage != event.getAmount() || extraEnchantmentDamage == 0.0F) {
            event.setCanceled(true);
        }
    }

    private static float calculateEnchantmentDamage(LivingEntity powerHolder, DamageSource source, float amount) {
        float additionalValue = 0.0F;

        if (source.getEntity() instanceof LivingEntity attacker && !source.isProjectile()) {
            additionalValue = ApugliPowers.MODIFY_ENCHANTMENT_DAMAGE_DEALT.get().applyModifiers(attacker, source, amount, powerHolder);
        }

        if (source.getEntity() instanceof LivingEntity) {
            additionalValue = ApugliPowers.MODIFY_ENCHANTMENT_DAMAGE_TAKEN.get().applyModifiers(powerHolder, source, amount);
        }

        return amount + additionalValue;
    }

    @SubscribeEvent
    public static void onLivingKilled(LivingDeathEvent event) {
        IPowerDataCache.get(event.getEntity()).map(IPowerDataCache::getDamage).ifPresent(x -> {

            DamageSource source = event.getSource();
            Entity attacker = source.getEntity();
            LivingEntity target = event.getEntity();
            LivingEntity kilLCredit = event.getEntity().getKillCredit();

            if (attacker == null || attacker != kilLCredit) {
                ApugliPowers.ACTION_ON_TARGET_DEATH.get().onTargetDeath(kilLCredit, target, event.getSource(), x, true);
                return;
            }

            LivingEntity living = (LivingEntity) attacker;
            ApugliPowers.ACTION_ON_TARGET_DEATH.get().onTargetDeath(living, target, source, x, false);
        });
    }

    @SubscribeEvent
    public static void onStartPlayerTrack(PlayerEvent.StartTracking event) {
        event.getTarget().getCapability(KeyPressCapability.INSTANCE).ifPresent(KeyPressCapability::sync);
        event.getTarget().getCapability(HitsOnTargetCapability.INSTANCE).ifPresent(HitsOnTargetCapability::sync);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onMobInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        List<PreventBreedingPower> preventBreedingPowerList = Services.POWER.getPowers(event.getEntity(), ApugliPowers.PREVENT_BREEDING.get()).stream().filter(power -> power.doesApply(event.getTarget())).collect(Collectors.toList());
        if(!preventBreedingPowerList.isEmpty() && event.getTarget() instanceof Animal animal && animal.isFood(event.getItemStack())) {
            int i = animal.getAge();
            if(i == 0 && animal.canFallInLove()) {
                if(preventBreedingPowerList.stream().anyMatch(PreventBreedingPower::hasAction)) {
                    preventBreedingPowerList.forEach(power -> power.executeAction(event.getTarget()));
                    animal.setInLoveTime((int)Services.PLATFORM.applyModifiers(event.getEntity(), ApugliPowers.MODIFY_BREEDING_COOLDOWN.get(), 6000));
                    event.setResult(Event.Result.ALLOW);
                    event.setCanceled(true);
                } else {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBabySpawn(BabyEntitySpawnEvent event) {
        if (!(event.getParentA() instanceof Animal parentA) || !(event.getParentB() instanceof Animal parentB)) return;
        parentA.setInLoveTime((int)Services.PLATFORM.applyModifiers(event.getCausedByPlayer(), ApugliPowers.MODIFY_BREEDING_COOLDOWN.get(), 6000));
        parentB.setInLoveTime((int)Services.PLATFORM.applyModifiers(event.getCausedByPlayer(), ApugliPowers.MODIFY_BREEDING_COOLDOWN.get(), 6000));
    }

    @SubscribeEvent
    public static void postPowerLoad(PowerLoadEvent.Post event) {
        ConfiguredPower<?, ?> power = event.getPower();
        ResourceLocation id = ((PowerLoadEventPostAccess)event).getFixedId() != null ? ((PowerLoadEventPostAccess)event).getFixedId() : event.getId();
        handleUrlPower(id, power);
    }

    private static void handleUrlPower(ResourceLocation id, ConfiguredPower<?, ?> power) {
        if (!(power.getFactory() instanceof FabricPowerFactory<?>) || !(((FabricPowerFactoryAccessor)power.getFactory()).invokeGetPower(power, null) instanceof TextureOrUrlPower texturePower) || texturePower.getTextureUrl() == null) return;
        TextureUtil.handleUrlTexture(id, texturePower);
    }

    @SubscribeEvent
    public static void postPowerReload(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) return;
        ApugliPacketHandler.sendS2C(new UpdateUrlTexturesPacket(TextureUtil.getTexturePowers()), event.getPlayer());
    }

}
