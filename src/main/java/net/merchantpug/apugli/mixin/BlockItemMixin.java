package net.merchantpug.apugli.mixin;

import net.merchantpug.apugli.power.ActionOnBlockPlacedPower;
import net.merchantpug.apugli.power.ModifyBlockPlacedPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.block.BlockState;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(BlockItem.class)
public class BlockItemMixin extends Item {
    @Unique
    Pair<ModifyBlockPlacedPower, BlockState> pair;

    public BlockItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z", at = @At("HEAD"), cancellable = true)
    private void onPlaced(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        PowerHolderComponent.getPowers(context.getPlayer(), ActionOnBlockPlacedPower.class).stream().filter(power -> power.itemCondition.test(context.getStack())).forEach(power -> power.executeAction(Optional.ofNullable(context.getBlockPos())));

        List<ModifyBlockPlacedPower> powers = PowerHolderComponent.getPowers(context.getPlayer(), ModifyBlockPlacedPower.class)
                .stream()
                .filter(power -> power.itemCondition.test(context.getStack()))
                .toList();
        List<Pair<ModifyBlockPlacedPower, BlockState>> pairs = new ArrayList<>();
        for (ModifyBlockPlacedPower modifyBlockPlacedPower : powers) {
            modifyBlockPlacedPower.getBlockStates().forEach(blockState -> {
                pairs.add(new Pair<>(modifyBlockPlacedPower, blockState));
            });
        }

        if (powers.isEmpty() || pairs.isEmpty()) return;

        int random = new Random(powers.get(0).getSeed()).nextInt(pairs.size());
        BlockState blockState = pairs.get(random).getRight();

        this.pair = pairs.get(random);

        powers.get(0).generateSeed();

        cir.setReturnValue(context.getWorld().setBlockState(context.getBlockPos(), blockState, 11));
    }

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemPlacementContext;getBlockPos()Lnet/minecraft/util/math/BlockPos;"))
    private void executeActionAfterPlaced(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (this.pair == null) return;
        this.pair.getLeft().executeAction(Optional.ofNullable(context.getBlockPos()));
        this.pair = null;
    }
}
