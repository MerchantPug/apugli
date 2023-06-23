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
import net.merchantpug.apugli.network.s2c.UpdateUrlTexturesPacket;
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
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
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
        Optional<EdibleItemPower> power = Services.POWER.getPowers(living, ApugliPowers.EDIBLE_ITEM.get()).stream().filter(p -> p.doesApply(living.getLevel(), stack)).findFirst();
        if (power.isPresent()) {
            EdibleItemPower.executeEntityActions((LivingEntity) event.getEntity(), stack);
            ItemStack newStack = event.getEntityLiving().eat(event.getEntity().getLevel(), stack);
            if (event.getEntity() instanceof Player player && !player.getAbilities().instabuild) {
                if (power.get().getReturnStack() != null) {
                    ItemStack returnStack = power.get().getReturnStack().copy();
                    if (newStack.isEmpty()) {
                        event.setResultStack(EdibleItemPower.executeItemActions((LivingEntity) event.getEntity(), returnStack, stack));
                    } else {
                        ItemStack stack2 = EdibleItemPower.executeItemActions(event.getEntityLiving(), returnStack, stack);
                        if (!player.addItem(stack2)) {
                            player.drop(stack2, false);
                        }
                    }
                } else {
                    event.setResultStack(EdibleItemPower.executeItemActions(event.getEntityLiving(), newStack, stack));
                }
            }
        }
    }

    /*
    The one discrepancy from Fabric Apugli is that the action will always execute in the up direction, this should hopefully have no repercussions.
     */
    @SubscribeEvent
    public static void onBonemeal(BonemealEvent event) {
        if (Services.POWER.hasPower(event.getEntityLiving(), ApugliPowers.ACTION_ON_BONEMEAL.get()) && event.getBlock().getBlock() instanceof BonemealableBlock bonemeal
                && bonemeal.isValidBonemealTarget(event.getWorld(), event.getPos(), event.getBlock(), event.getWorld().isClientSide) && event.getWorld() instanceof ServerLevel
                && bonemeal.isBonemealSuccess(event.getWorld(), event.getWorld().random, event.getPos(), event.getBlock())) {
            Services.POWER.getPowers(event.getEntityLiving(), ApugliPowers.ACTION_ON_BONEMEAL.get())
                    .stream()
                    .filter(p -> p.doesApply(new BlockInWorld(event.getWorld(), event.getPos(), true)))
                    .forEach(p -> p.executeActions(event.getWorld(), event.getPos(), Direction.UP));
        }
    }

    @SubscribeEvent
    public static void modifyAerialBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (!Services.POWER.hasPower((LivingEntity) event.getEntity(), ApugliPowers.AERIAL_AFFINITY.get()) || event.getEntity().isOnGround()) return;
        event.setNewSpeed(event.getOriginalSpeed() * 5.0F);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingUpdateEvent event) {
        event.getEntity().getCapability(KeyPressCapability.INSTANCE).ifPresent(KeyPressCapability::tick);

        if (!event.getEntity().isAlive()) return;

        if (Services.POWER.hasPower(event.getEntityLiving(), ApugliPowers.HOVER.get())) {
            event.getEntity().setDeltaMovement(event.getEntity().getDeltaMovement().multiply(1.0, 0.0, 1.0));
            event.getEntity().fallDistance = 0.0F;
        }

        if (event.getEntity().level.isClientSide)
            Services.POWER.getPowers(event.getEntityLiving(), ApugliPowers.CLIENT_ACTION_OVER_TIME.get()).forEach(ClientActionOverTime::clientTick);

        if (!event.getEntity().level.isClientSide)
            ApugliPowers.BUNNY_HOP.get().onTravel(event.getEntityLiving(), new Vec3(event.getEntityLiving().xxa, event.getEntityLiving().yya, event.getEntityLiving().zza));
    }

    @SubscribeEvent(receiveCanceled = true)
    public static void onMobTargetChange(LivingSetAttackTargetEvent event) {
        if(event.getEntity().level.isClientSide()) return;

        List<MobsIgnorePower> powers = Services.POWER.getPowers(event.getTarget(), ApugliPowers.MOBS_IGNORE.get());
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
        LivingEntity target = event.getEntityLiving();
        DamageSource source = event.getSource();
        float amount = event.getAmount();

        float extraEnchantmentDamage = calculateEnchantmentDamage(target, source, amount);
        float finalAmount = amount + extraEnchantmentDamage;

        event.setAmount(finalAmount);

        if (finalAmount != amount && finalAmount == 0.0F) {
            event.setCanceled(true);
        }

        if (!event.isCanceled()) {
            if (source.getEntity() instanceof LivingEntity living) {
                Services.POWER.getPowers(living, ApugliPowers.ACTION_ON_HARM.get()).forEach(p -> ApugliPowers.ACTION_ON_HARM.get().execute(p, living, source, amount, target));
            }
            Services.POWER.getPowers(target, ApugliPowers.ACTION_WHEN_HARMED.get()).forEach(p -> ApugliPowers.ACTION_WHEN_HARMED.get().execute(p, target, source, amount));

            if (target.getLastHurtByMob() != null) {
                ApugliPowers.ACTION_ON_ATTACKER_HURT.get().execute(target, source, amount);
                ApugliPowers.ACTION_ON_TARGET_HURT.get().execute(target, source, amount);
            }

            if (target instanceof TamableAnimal tamable) {
                ApugliPowers.ACTION_WHEN_TAME_HIT.get().execute(tamable, source, amount);
            }

            if (source.getEntity() instanceof TamableAnimal tamable) {
                ApugliPowers.ACTION_ON_TAME_HIT.get().execute(tamable, source, amount, target);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onLivingAttack(LivingAttackEvent event) {
        float amount = event.getAmount();
        float extraEnchantmentDamage = calculateEnchantmentDamage(event.getEntityLiving(), event.getSource(), event.getAmount());
        float finalAmount = amount + extraEnchantmentDamage;
        if (extraEnchantmentDamage > 0.0F && event.getSource().getEntity() instanceof Player attacker) {
            float enchantmentDamageBonus = EnchantmentHelper.getDamageBonus(attacker.getMainHandItem(), event.getEntityLiving().getMobType());
            if (enchantmentDamageBonus <= 0.0F && !event.getEntity().level.isClientSide) {
                attacker.magicCrit(event.getEntity());
            }
        }

        if (finalAmount != event.getAmount() && finalAmount == 0.0F) {
            event.setCanceled(true);
        }
    }

    private static float calculateEnchantmentDamage(LivingEntity powerHolder, DamageSource source, float amount) {
        float additionalValue = 0.0F;

        if (source.getEntity() instanceof LivingEntity attacker && !source.isProjectile()) {
            additionalValue += ApugliPowers.MODIFY_ENCHANTMENT_DAMAGE_DEALT.get().applyModifiers(attacker, source, amount, powerHolder);
        }

        if (source.getEntity() instanceof LivingEntity attacker) {
            additionalValue += ApugliPowers.MODIFY_ENCHANTMENT_DAMAGE_TAKEN.get().applyModifiers(powerHolder, source, attacker, amount);
        }

        return additionalValue;
    }

    @SubscribeEvent
    public static void onLivingKilled(LivingDeathEvent event) {
        IPowerDataCache.get(event.getEntityLiving()).map(IPowerDataCache::getDamage).ifPresent(x -> {
            DamageSource source = event.getSource();
            Entity attacker = source.getEntity();
            LivingEntity target = event.getEntityLiving();
            LivingEntity kilLCredit = event.getEntityLiving().getKillCredit();

            if (kilLCredit != null && (attacker == null || attacker != kilLCredit)) {
                ApugliPowers.ACTION_ON_TARGET_DEATH.get().onTargetDeath(kilLCredit, target, event.getSource(), x, true);
                return;
            }

            if (!(attacker instanceof LivingEntity living)) return;
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
        List<PreventBreedingPower> preventBreedingPowerList = Services.POWER.getPowers(event.getEntityLiving(), ApugliPowers.PREVENT_BREEDING.get()).stream().filter(power -> power.doesApply(event.getTarget())).collect(Collectors.toList());
        if(!preventBreedingPowerList.isEmpty() && event.getTarget() instanceof Animal animal && animal.isFood(event.getItemStack())) {
            int i = animal.getAge();
            if(i == 0 && animal.canFallInLove()) {
                if(preventBreedingPowerList.stream().anyMatch(PreventBreedingPower::hasAction)) {
                    preventBreedingPowerList.forEach(power -> power.executeAction(event.getTarget()));
                    animal.setInLoveTime((int)Services.PLATFORM.applyModifiers(event.getEntityLiving(), ApugliPowers.MODIFY_BREEDING_COOLDOWN.get(), 6000));
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
        TextureUtil.cachePower(id, texturePower);
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) return;
        ApugliPacketHandler.sendS2C(new UpdateUrlTexturesPacket(TextureUtil.getCache()), event.getPlayer());
    }

}
