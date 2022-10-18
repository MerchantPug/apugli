package com.github.merchantpug.apugli.platform;

import com.github.merchantpug.apugli.platform.services.IPlatformHelper;
import com.google.auto.service.AutoService;
import io.github.apace100.apoli.util.AttributeUtil;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.extensions.IForgePlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.List;

@SuppressWarnings("unchecked")
@AutoService(IPlatformHelper.class)
public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }
    
    @Override
    public SerializableDataType<?> getModifierDataType() {
        return SerializableDataTypes.ATTRIBUTE_MODIFIER;
    }
    
    @Override
    public SerializableDataType<?> getModifiersDataType() {
        return SerializableDataTypes.ATTRIBUTE_MODIFIERS;
    }
    
    @Override
    public double applyModifiers(LivingEntity living, List<?> modifiers, double value) {
        return AttributeUtil.applyModifiers((List<AttributeModifier>) modifiers, value);
    }
    
    @Override
    public double getReachDistance(Entity entity) {
        return entity instanceof IForgePlayer player ? player.getReachDistance() : 4.5;
    }
    
    @Override
    public double getAttackRange(Entity entity) {
        return entity instanceof IForgePlayer player ? player.getAttackRange() : 3;
    }
    
}
