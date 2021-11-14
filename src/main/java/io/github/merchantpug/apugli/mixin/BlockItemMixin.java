package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.ActionOnLandPower;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.power.ActionOnBlockPlacedPower;
import io.github.merchantpug.apugli.power.ModifyBlockPlacedPower;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(BlockItem.class)
public class BlockItemMixin extends Item {

    public BlockItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z", at = @At("HEAD"), cancellable = true)
    private void onPlaced(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        PowerHolderComponent.getPowers(context.getPlayer(), ActionOnBlockPlacedPower.class).stream().filter(power -> power.itemCondition.test(context.getStack())).forEach(power -> power.executeAction(Optional.ofNullable(context.getBlockPos())));

        List<ModifyBlockPlacedPower> powers = PowerHolderComponent.getPowers(context.getPlayer(), ModifyBlockPlacedPower.class)
                .stream()
                .filter(power -> power.itemCondition.test(context.getStack()))
                .collect(Collectors.toList());
        List<BlockState> blockStates = powers.stream()
                .flatMap(power -> power.getBlockStates().stream())
                .collect(Collectors.toList());
        if (powers.isEmpty() || blockStates.isEmpty()) return;

        int random = new Random(powers.get(0).getSeed()).nextInt(blockStates.size());
        BlockState blockState = blockStates.get(random);

        powers.get(0).generateSeed();
        cir.setReturnValue(context.getWorld().setBlockState(context.getBlockPos(), blockState, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD));
    }

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemPlacementContext;getBlockPos()Lnet/minecraft/util/math/BlockPos;"))
    private void executeActionAfterPlaced(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
        List<ModifyBlockPlacedPower> powers = PowerHolderComponent.getPowers(context.getPlayer(), ModifyBlockPlacedPower.class)
                .stream()
                .filter(power -> power.itemCondition.test(context.getStack()))
                .collect(Collectors.toList());
        List<BlockState> blockStates = powers.stream()
                .flatMap(power -> power.getBlockStates().stream())
                .collect(Collectors.toList());
        if (powers.isEmpty() || blockStates.isEmpty()) return;

        int random = new Random(powers.get(0).getSeed()).nextInt(blockStates.size());
        int powerIndex = 0;
        for (ModifyBlockPlacedPower power : powers) {
            if (powerIndex < powers.size()) {
                if (powerIndex == random) {
                    power.executeAction(Optional.ofNullable(context.getBlockPos()));
                    break;
                } else if (powerIndex < power.getBlockStates().size()) {
                    powerIndex++;
                }
            }
        }
    }
}
