package net.merchantpug.apugli.action;

import net.merchantpug.apugli.action.configuration.FabricActionConfiguration;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.power.factory.ItemAction;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiConsumer;

/**
 * We use {@link Mutable} here to match Origins (Forge)'s implementation on {@link ItemAction}.
 */
@ParametersAreNonnullByDefault
public class FabricItemAction extends ItemAction<FabricActionConfiguration<Tuple<Level, Mutable<ItemStack>>>> {
    
    public FabricItemAction(SerializableData data, BiConsumer<SerializableData.Instance, Tuple<Level, Mutable<ItemStack>>> action) {
        super(FabricActionConfiguration.codec(data, action));
    }
    
    @Override
    public void execute(FabricActionConfiguration<Tuple<Level, Mutable<ItemStack>>> config, Level level, Mutable<ItemStack> mutable) {
        config.action().accept(new Tuple<>(level, mutable));
    }
    
}