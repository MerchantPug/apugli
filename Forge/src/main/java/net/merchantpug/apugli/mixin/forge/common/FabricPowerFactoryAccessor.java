package net.merchantpug.apugli.mixin.forge.common;

import io.github.apace100.apoli.power.Power;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.fabric.FabricPowerConfiguration;
import io.github.edwinmindcraft.apoli.fabric.FabricPowerFactory;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FabricPowerFactory.class)
public interface FabricPowerFactoryAccessor<P extends Power> {
    
    @Invoker(value = "getPower", remap = false)
    P invokeGetPower(ConfiguredPower<FabricPowerConfiguration<P>, ?> configuration, LivingEntity entity);
    
}
