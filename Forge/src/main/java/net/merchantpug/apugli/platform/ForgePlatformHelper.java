package net.merchantpug.apugli.platform;

import net.merchantpug.apugli.network.ApugliPacketHandler;
import net.merchantpug.apugli.networking.c2s.ApugliPacketC2S;
import net.merchantpug.apugli.networking.s2c.ApugliPacketS2C;
import net.merchantpug.apugli.platform.services.IPlatformHelper;
import com.google.auto.service.AutoService;
import io.github.apace100.apoli.util.AttributeUtil;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.server.level.ServerPlayer;
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

    @Override
    public void sendS2C(ApugliPacketS2C packet, ServerPlayer player) {
        ApugliPacketHandler.sendS2C(packet, player);
    }

    @Override
    public void sendS2CTrackingAndSelf(ApugliPacketS2C packet, ServerPlayer player) {
        ApugliPacketHandler.sendS2CTrackingAndSelf(packet, player);
    }

    @Override
    public void sendC2S(ApugliPacketC2S packet) {
        ApugliPacketHandler.sendC2S(packet);
    }

}
