package net.merchantpug.apugli.mixin.xplatform.common;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Map;

@Mixin(AnvilMenu.class)
public abstract class AnvilScreenHandlerMixin extends ItemCombinerMenu {

    public AnvilScreenHandlerMixin(@Nullable MenuType<?> type, int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(type, syncId, playerInventory, context);
    }

    @Unique
    private Enchantment apugli$capturedEnchantment;
    @Unique
    private ItemStack apugli$capturedItemStack;

    @Inject(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;canEnchant(Lnet/minecraft/world/item/ItemStack;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void apugli$captureFieldsInAnvil(CallbackInfo ci, ItemStack itemStack, int i, int j, int k, ItemStack itemStack2, ItemStack itemStack3, Map map, boolean bl, Map map2, boolean bl2, boolean bl3, Iterator var12, Enchantment enchantment, int q, int r) {
        apugli$capturedEnchantment = enchantment;
        apugli$capturedItemStack = itemStack;
    }

    /*
    There is a Forge AnvilUpdateEvent but honestly, I'd prefer to not have to reimplement the
    entire anvil system in an event.
     */
    @ModifyVariable(method = "createResult", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/enchantment/Enchantment;canEnchant(Lnet/minecraft/world/item/ItemStack;)Z"), ordinal = 3)
    private boolean apugli$allowEnchantingThroughAnvil(boolean bl4) {
        if(Services.POWER.getPowers(this.player, ApugliPowers.ALLOW_ANVIL_ENCHANT.get()).stream().anyMatch(p -> p.doesApply(apugli$capturedEnchantment, apugli$capturedItemStack, this.inputSlots.getItem(1)))) {
            this.apugli$capturedEnchantment = null;
            this.apugli$capturedItemStack = null;
            return bl4 = true;
        }
        this.apugli$capturedEnchantment = null;
        this.apugli$capturedItemStack = null;
        return bl4;
    }

}
