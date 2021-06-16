package io.github.merchantpug.apugli.mixin;

import com.google.common.collect.ImmutableMap;
import io.github.merchantpug.apugli.registry.ApugliEntityModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModels;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(EntityModels.class)
public class EntityModelsMixin {
    @Shadow @Final private static Dilation HAT_DILATION;

    @Inject(method = "getModels", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void addPlayerOverlay(CallbackInfoReturnable<Map<EntityModelLayer, TexturedModelData>> cir, ImmutableMap.Builder<EntityModelLayer, TexturedModelData> builder) {
        builder.put(ApugliEntityModelLayers.PLAYER_ARMOR, TexturedModelData.of(PlayerEntityModel.getTexturedModelData(HAT_DILATION, false), 64, 64));
        builder.put(ApugliEntityModelLayers.PLAYER_SLIM_ARMOR, TexturedModelData.of(PlayerEntityModel.getTexturedModelData(HAT_DILATION, true), 64, 64));
    }
}
