package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.power.ModifyDamageTakenPower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Mixin(ModifyDamageTakenPower.class)
public interface ModifyDamageTakenPowerAccessor {
    @Accessor
    Consumer<Entity> getSelfAction();

    @Accessor
    Consumer<Entity> getAttackerAction();

    @Accessor
    Predicate<Pair<DamageSource, Float>> getCondition();
}
