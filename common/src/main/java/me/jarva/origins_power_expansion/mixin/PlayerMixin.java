package me.jarva.origins_power_expansion.mixin;

import io.github.apace100.origins.component.OriginComponent;
import me.jarva.origins_power_expansion.powers.CustomFootstepPower;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity {
    protected PlayerMixin(EntityType<?> player, Level level) {
        super(player, level);
        throw new AssertionError("PlayerMixin constructor called.");
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        super.playStepSound(pos, state);
        List<CustomFootstepPower> powers = OriginComponent.getPowers(this, CustomFootstepPower.class);
        if (!powers.isEmpty()) {
            powers.get(0).playFootstep(this);
        }
    }
}
