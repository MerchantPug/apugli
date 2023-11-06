package net.merchantpug.apugli;

import io.github.apace100.apoli.integration.PowerLoadEvent;
import io.github.edwinmindcraft.apoli.api.component.IPowerDataCache;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.registry.ApoliDynamicRegistries;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.edwinmindcraft.apoli.fabric.FabricPowerFactory;
import io.github.edwinmindcraft.calio.api.event.CalioDynamicRegistryEvent;
import net.merchantpug.apugli.action.configuration.FabricActionConfiguration;
import net.merchantpug.apugli.action.factory.entity.CustomProjectileAction;
import net.merchantpug.apugli.capability.entity.HitsOnTargetCapability;
import net.merchantpug.apugli.capability.entity.KeyPressCapability;
import net.merchantpug.apugli.capability.item.EntityLinkCapability;
import net.merchantpug.apugli.mixin.forge.common.accessor.FabricPowerFactoryAccessor;
import net.merchantpug.apugli.network.ApugliPacketHandler;
import net.merchantpug.apugli.network.s2c.UpdateUrlTexturesPacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.*;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.merchantpug.apugli.util.IndividualisedEmptyStackUtil;
import net.merchantpug.apugli.util.TextureUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
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
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
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
    public static void attachEntityCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player)
            event.addCapability(KeyPressCapability.ID, new KeyPressCapability(player));
        if (event.getObject() instanceof LivingEntity living)
            event.addCapability(HitsOnTargetCapability.ID, new HitsOnTargetCapability(living));
    }

    @SubscribeEvent
    public static void attachItemCapabilities(final AttachCapabilitiesEvent<ItemStack> event) {
        event.addCapability(EntityLinkCapability.ID, new EntityLinkCapability(event.getObject()));
    }

    @SubscribeEvent
    public static void onCalioDynamicRegistryLoadComplete(CalioDynamicRegistryEvent.LoadComplete event) {
        Registry<ConfiguredEntityAction<?, ?>> registry = event.getRegistryManager().get(ApoliDynamicRegistries.CONFIGURED_ENTITY_ACTION_KEY);
        registry.forEach(action -> {
            if (action.getConfiguration() instanceof FabricActionConfiguration<?> fabricConfig && action.getFactory() == ApoliRegistries.ENTITY_ACTION.get().getValue(Apugli.asResource("custom_projectile"))) {
                if (fabricConfig.data().isPresent("texture_url")) {
                    String url = fabricConfig.data().getString("texture_url");
                    ResourceLocation textureLocation = null;
                    if (fabricConfig.data().isPresent("texture_location")) {
                        textureLocation = ResourceLocation.of(fabricConfig.data().getString("texture_location"), ':');
                    }
                    TextureUtil.cacheOneOff(CustomProjectileAction.getTextureUrl(url), url, textureLocation);
                }
            }
        });
    }

    @SubscribeEvent
    public static void onStruckByLightning(EntityStruckByLightningEvent event) {
        if (event.getEntity() instanceof LivingEntity living)
            Services.POWER.getPowers(living, ApugliPowers.ACTION_WHEN_LIGHTNING_STRUCK.get()).forEach(p -> ApugliPowers.ACTION_WHEN_LIGHTNING_STRUCK.get().execute(p, living, event.getLightning()));
    }

    @SubscribeEvent
    public static void onGroundJump(LivingEvent.LivingJumpEvent event) {
        Services.POWER.getPowers(event.getEntity(), ApugliPowers.ACTION_ON_JUMP.get()).forEach(ActionOnJumpPower::executeAction);
    }

    @SubscribeEvent
    public static void onFinishUsing(LivingEntityUseItemEvent.Finish event) {
        ItemStack stack = event.getItem().copy();
        if (!(Services.PLATFORM.getEntityFromItemStack(stack) instanceof LivingEntity living)) return;
        Optional<EdibleItemPower> power = Services.POWER.getPowers(living, ApugliPowers.EDIBLE_ITEM.get()).stream().filter(p -> p.doesApply(living.level(), stack)).findFirst();
        if (power.isPresent()) {
            EdibleItemPower.executeEntityActions(event.getEntity(), stack);
            ItemStack newStack = event.getEntity().eat(event.getEntity().level(), stack);
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
        if (!Services.POWER.hasPower(event.getEntity(), ApugliPowers.AERIAL_AFFINITY.get()) || event.getEntity().onGround()) return;
        event.setNewSpeed(event.getOriginalSpeed() * 5.0F);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        event.getEntity().getCapability(KeyPressCapability.INSTANCE).ifPresent(KeyPressCapability::tick);

        if (event.getEntity().isDeadOrDying()) return;

        if (event.getEntity() instanceof Player player) {
            CrawlingPower.tickOnceForge(player);
        }

        IndividualisedEmptyStackUtil.addEntityToStack(event.getEntity());

        if (Services.POWER.hasPower(event.getEntity(), ApugliPowers.HOVER.get())) {
            event.getEntity().setDeltaMovement(event.getEntity().getDeltaMovement().multiply(1.0, 0.0, 1.0));
            event.getEntity().fallDistance = 0.0F;
        }

        if (event.getEntity().level().isClientSide)
            Services.POWER.getPowers(event.getEntity(), ApugliPowers.CLIENT_ACTION_OVER_TIME.get()).forEach(ClientActionOverTime::clientTick);

        if (!event.getEntity().level().isClientSide) {
            event.getEntity().getCapability(HitsOnTargetCapability.INSTANCE).ifPresent(HitsOnTargetCapability::serverTick);
            ApugliPowers.BUNNY_HOP.get().onTravel(event.getEntity(), new Vec3(event.getEntity().xxa, event.getEntity().yya, event.getEntity().zza));
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    public static void onMobTargetChange(LivingChangeTargetEvent event) {
        if(event.getEntity().level().isClientSide()) return;

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
        float finalAmount = amount + extraEnchantmentDamage;

        event.setAmount(finalAmount);

        if (finalAmount != amount && finalAmount == 0.0F) {
            event.setCanceled(true);
        }

        if (!event.isCanceled()) {
            if (source.getEntity() instanceof LivingEntity living) {
                Services.POWER.getPowers(living, ApugliPowers.ACTION_ON_HARM.get()).forEach(p -> ApugliPowers.ACTION_ON_HARM.get().execute(p, living, source, amount, target));
                Services.POWER.getPowers(living, ApugliPowers.DAMAGE_NEARBY_ON_HIT.get()).forEach(p -> ApugliPowers.DAMAGE_NEARBY_ON_HIT.get().execute(p, living, source, amount, target));
            }
            Services.POWER.getPowers(target, ApugliPowers.ACTION_WHEN_HARMED.get()).forEach(p -> ApugliPowers.ACTION_WHEN_HARMED.get().execute(p, target, source, amount));
            Services.POWER.getPowers(target, ApugliPowers.DAMAGE_NEARBY_WHEN_HIT.get()).forEach(p -> ApugliPowers.DAMAGE_NEARBY_WHEN_HIT.get().execute(p, target, source, amount));

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
        float extraEnchantmentDamage = calculateEnchantmentDamage(event.getEntity(), event.getSource(), event.getAmount());
        float finalAmount = amount + extraEnchantmentDamage;
        if (extraEnchantmentDamage > 0.0F && event.getSource().getEntity() instanceof Player attacker) {
            float enchantmentDamageBonus = EnchantmentHelper.getDamageBonus(attacker.getMainHandItem(), event.getEntity().getMobType());
            if (enchantmentDamageBonus <= 0.0F && !event.getEntity().level().isClientSide) {
                attacker.magicCrit(event.getEntity());
            }
        }

        if (finalAmount != event.getAmount() && finalAmount == 0.0F) {
            event.setCanceled(true);
        }
    }

    private static float calculateEnchantmentDamage(LivingEntity powerHolder, DamageSource source, float amount) {
        float additionalValue = 0.0F;

        if (source.getEntity() instanceof LivingEntity attacker && !source.isIndirect()) {
            additionalValue += ApugliPowers.MODIFY_ENCHANTMENT_DAMAGE_DEALT.get().applyModifiers(attacker, source, amount, powerHolder);
        }

        if (source.getEntity() instanceof LivingEntity attacker) {
            additionalValue += ApugliPowers.MODIFY_ENCHANTMENT_DAMAGE_TAKEN.get().applyModifiers(powerHolder, source, attacker, amount);
        }

        return additionalValue;
    }

    @SubscribeEvent
    public static void onLivingKilled(LivingDeathEvent event) {
        IPowerDataCache.get(event.getEntity()).map(IPowerDataCache::getDamage).ifPresent(x -> {
            DamageSource source = event.getSource();
            Entity attacker = source.getEntity();
            LivingEntity target = event.getEntity();
            LivingEntity killCredit = event.getEntity().getKillCredit();

            if (killCredit != null && (attacker == null || attacker != killCredit)) {
                ApugliPowers.ACTION_ON_TARGET_DEATH.get().onTargetDeath(killCredit, target, event.getSource(), x, true);
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
    public static void prePowerLoad(AddReloadListenerEvent event) {
        TextureUtil.getCache().clear();
    }

    @SubscribeEvent
    public static void postPowerLoad(PowerLoadEvent.Post event) {
        handleUrlPower(event.getId(), event.getPower());
    }

    private static void handleUrlPower(ResourceLocation id, ConfiguredPower<?, ?> power) {
        if (power.getFactory() instanceof FabricPowerFactory<?> && ((FabricPowerFactoryAccessor)power.getFactory()).invokeGetPower(power, null) instanceof TextureOrUrlPower texturePower && texturePower.getTextureUrl() != null) {
            TextureUtil.cachePower(id, texturePower);
        } else if (power.getFactory() instanceof CustomProjectilePower projectilePower && ApugliPowers.CUSTOM_PROJECTILE.get().getDataFromPower(power).isPresent("texture_url")) {
            ApugliPowers.CUSTOM_PROJECTILE.get().cacheTextureUrl(id, projectilePower);
        }
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) return;
        ApugliPacketHandler.sendS2C(new UpdateUrlTexturesPacket(TextureUtil.getCache()), event.getPlayer());
    }

}
