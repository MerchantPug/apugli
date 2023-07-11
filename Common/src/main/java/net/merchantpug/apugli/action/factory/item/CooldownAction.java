package net.merchantpug.apugli.action.factory.item;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;

public class CooldownAction implements IActionFactory<Tuple<Level, Mutable<ItemStack>>> {

    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("ticks", SerializableDataTypes.INT, 20);
    }

    @Override
    public void execute(SerializableData.Instance data, Tuple<Level, Mutable<ItemStack>> levelItemStack) {
        if (Services.PLATFORM.getEntityFromItemStack(levelItemStack.getB().getValue()) instanceof Player player) {
            player.getCooldowns().addCooldown(levelItemStack.getB().getValue().getItem(), data.getInt("ticks"));
        }
    }

}
