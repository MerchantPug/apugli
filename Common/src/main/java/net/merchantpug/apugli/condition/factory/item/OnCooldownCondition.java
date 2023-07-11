package net.merchantpug.apugli.condition.factory.item;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class OnCooldownCondition implements IConditionFactory<ItemStack> {

    @Override
    public boolean check(SerializableData.Instance data, ItemStack stack) {
        if (Services.PLATFORM.getEntityFromItemStack(stack) instanceof Player player) {
            player.getCooldowns().isOnCooldown(stack.getItem());
        }
        return false;
    }

}
