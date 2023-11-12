package net.merchantpug.apugli.mixin.xplatform.common;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.ModifyBlockPlacedPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Mixin(BlockItem.class)
public class BlockItemMixin extends Item {
    @Unique
    Tuple<ModifyBlockPlacedPower, BlockState> pair;

    public BlockItemMixin(Properties settings) {
        super(settings);
    }

    @Inject(method = "placeBlock", at = @At("HEAD"), cancellable = true)
    private void apugli$onPlaced(BlockPlaceContext context, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        Player player = context.getPlayer();
        ItemStack heldItem = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        Services.POWER.getPowers(player, ApugliPowers.ACTION_ON_BLOCK_PLACED.get()).stream()
            .filter(power -> power.itemCondition.test(new Tuple<>(context.getLevel(), heldItem)))
            .forEach(power -> power.executeAction(pos));

        List<ModifyBlockPlacedPower> powers = Services.POWER.getPowers(context.getPlayer(), ApugliPowers.MODIFY_BLOCK_PLACED.get())
                .stream()
                .filter(power -> power.itemCondition.test(new Tuple<>(context.getLevel(), context.getItemInHand())))
                .toList();
        List<Tuple<ModifyBlockPlacedPower, BlockState>> pairs = new ArrayList<>();
        for(ModifyBlockPlacedPower modifyBlockPlacedPower : powers) {
            modifyBlockPlacedPower.getBlockStates().forEach(blockState -> {
                pairs.add(new Tuple<>(modifyBlockPlacedPower, blockState));
            });
        }

        if(powers.isEmpty() || pairs.isEmpty()) return;

        int random = new Random(powers.get(0).getSeed()).nextInt(pairs.size());
        BlockState blockState = pairs.get(random).getB();

        this.pair = pairs.get(random);

        powers.get(0).generateSeed();

        cir.setReturnValue(context.getLevel().setBlock(context.getClickedPos(), blockState, 11));
    }

    @Inject(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/context/BlockPlaceContext;getClickedPos()Lnet/minecraft/core/BlockPos;"))
    private void apugli$executeActionAfterPlaced(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if(this.pair == null) return;
        this.pair.getA().executeAction(Optional.of(context.getClickedPos()));
        this.pair = null;
    }

}
