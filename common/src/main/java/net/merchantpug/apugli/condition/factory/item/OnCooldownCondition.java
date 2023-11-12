package net.merchantpug.apugli.condition.factory.item;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class OnCooldownCondition implements IConditionFactory<Tuple<Level, ItemStack>> {

    @Override
    public boolean check(SerializableData.Instance data, Tuple<Level, ItemStack> levelAndStack) {
        if (Services.PLATFORM.getEntityFromItemStack(levelAndStack.getB()) instanceof Player player) {
            return player.getCooldowns().isOnCooldown(levelAndStack.getB().getItem());
        }
        return false;
    }

}
