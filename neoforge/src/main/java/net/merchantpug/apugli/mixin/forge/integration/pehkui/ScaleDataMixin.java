package net.merchantpug.apugli.mixin.forge.integration.pehkui;

import com.google.common.collect.Sets;
import net.merchantpug.apugli.access.ScaleDataAccess;
import net.merchantpug.apugli.integration.pehkui.ApoliScaleModifier;
import net.merchantpug.apugli.integration.pehkui.PehkuiUtil;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleModifier;
import virtuoel.pehkui.api.ScaleType;

import java.util.Set;
import java.util.SortedSet;

@Pseudo
@Mixin(ScaleData.class)
public abstract class ScaleDataMixin implements ScaleDataAccess {
    @Shadow(remap = false) @Final private SortedSet<ScaleModifier> differingModifierCache;

    @Shadow(remap = false) public abstract Entity getEntity();

    @Unique private final Set<ResourceLocation> apugli$apoliScaleModifiers = Sets.newHashSet();

    @Inject(method = "invalidateCachedModifiers", at = @At("TAIL"), remap = false)
    private void apugli$dontCacheIfApoliScaleModifier(CallbackInfo ci) {
        this.differingModifierCache.removeIf(modifier -> modifier instanceof ApoliScaleModifier);
    }

    @Inject(method = "readNbt", at = @At(value = "INVOKE", target = "Ljava/util/SortedSet;addAll(Ljava/util/Collection;)Z"), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private void apugli$addAllApoliScaleModifiers(CompoundTag tag, CallbackInfo ci, ScaleType type, SortedSet<ScaleModifier> baseValueModifiers) {
        this.apugli$apoliScaleModifiers.forEach(location -> {
            Object apoliModifier = ApugliPowers.MODIFY_SCALE.get().getApoliScaleModifier(location, this.getEntity());
            if (apoliModifier instanceof ScaleModifier scaleModifier)
                baseValueModifiers.add(scaleModifier);
        });
    }

    @Override
    public void apugli$addToApoliScaleModifiers(ResourceLocation value) {
        this.apugli$apoliScaleModifiers.add(value);
    }

    @Override
    public void apugli$removeFromApoliScaleModifiers(ResourceLocation value) {
        this.apugli$apoliScaleModifiers.remove(value);
    }
}

