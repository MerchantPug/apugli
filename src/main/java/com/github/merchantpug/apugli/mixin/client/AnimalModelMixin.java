package com.github.merchantpug.apugli.mixin.client;

import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.access.AnimalModelAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalModel.class)
public abstract class AnimalModelMixin<E extends Entity> extends EntityModel<E> implements AnimalModelAccess {
    @Shadow protected abstract Iterable<ModelPart> getHeadParts();

    @Shadow protected abstract Iterable<ModelPart> getBodyParts();

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/AnimalModel;getHeadParts()Ljava/lang/Iterable;", ordinal = 1), cancellable = true)
    private void renderAllButHatLayer(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (this.apugli$hidden) {
            VertexConsumer emptyVertices = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().getBuffer(RenderLayer.getEntityTranslucent(Apugli.identifier("empty_player_texture")));
            this.getHeadParts().forEach(part -> part.render(matrices, emptyVertices, light, overlay, red, green, blue, alpha));
            this.getBodyParts().forEach(part -> part.render(matrices, emptyVertices, light, overlay, red, green, blue, alpha));
            ci.cancel();
        }
    }

    @Unique
    private boolean apugli$hidden = false;
    @Override
    public boolean apugli$isHidden() {
        return apugli$hidden;
    }

    @Override
    public void apugli$setHidden(boolean value) {
        apugli$hidden = value;
    }
}
