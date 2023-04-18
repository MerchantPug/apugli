package com.github.merchantpug.apugli.condition;

import com.github.merchantpug.apugli.condition.configuration.FabricConditionConfiguration;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.power.factory.ItemCondition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiPredicate;

@ParametersAreNonnullByDefault
public class FabricItemCondition extends ItemCondition<FabricConditionConfiguration<ItemStack>> {
    
    public FabricItemCondition(SerializableData data, BiPredicate<SerializableData.Instance, ItemStack> condition) {
        super(FabricConditionConfiguration.codec(data, condition));
    }
    
    @Override
    protected boolean check(FabricConditionConfiguration<ItemStack> config, @Nullable Level level, ItemStack stack) {
        return config.condition().test(stack);
    }
    
}
