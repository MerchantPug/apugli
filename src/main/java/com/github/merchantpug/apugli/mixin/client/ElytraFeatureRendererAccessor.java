package com.github.merchantpug.apugli.mixin.client;

import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ElytraFeatureRenderer.class)
public interface ElytraFeatureRendererAccessor<T extends LivingEntity, M extends EntityModel<T>> {
    @Accessor
    @Mutable
    static Identifier getSKIN() {
        throw new AssertionError("");
    }

    @Accessor @Mutable
    ElytraEntityModel<T> getElytra();
}
