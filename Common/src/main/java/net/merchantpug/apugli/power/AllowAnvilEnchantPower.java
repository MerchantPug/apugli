package net.merchantpug.apugli.power;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class AllowAnvilEnchantPower extends Power {
    @Nullable
    private final Predicate<ItemStack> itemCondition;
    private final List<Enchantment> enchantments = new ArrayList<>();
    public Comparison comparison;
    public int compareTo;
    
    public AllowAnvilEnchantPower(PowerType<?> type, LivingEntity entity,
                                  @Nullable Predicate<ItemStack> itemCondition,
                                  Comparison comparison, int compareTo) {
        super(type, entity);
        this.itemCondition = itemCondition;
        this.compareTo = compareTo;
        this.comparison = comparison;
    }
    
    public boolean doesApply(Enchantment enchantment, ItemStack stackA, ItemStack stackB) {
        if(!enchantments.contains(enchantment)) return false;
        if(itemCondition != null && !itemCondition.test(stackA)) return false;
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stackB);
        if(!map.containsKey(enchantment)) return false;
        return comparison.compare(map.get(enchantment), compareTo);
    }
    
    public static class Factory extends SimplePowerFactory<AllowAnvilEnchantPower> {
        
        public Factory() {
            super("allow_anvil_enchant",
                new SerializableData()
                    .add("enchantment", SerializableDataTypes.ENCHANTMENT, null)
                    .add("enchantments", SerializableDataType.list(SerializableDataTypes.ENCHANTMENT), null)
                    .add("compare_to", SerializableDataTypes.INT, 0)
                    .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN)
                    .add("item_condition", Services.CONDITION.itemDataType()),
                data -> (type, player) -> {
                    AllowAnvilEnchantPower power = new AllowAnvilEnchantPower(type, player,
                        Services.CONDITION.itemPredicate(data, "item_condition"),
                        data.get("comparison"),
                        data.getInt("compare_to")
                    );
                    if(data.isPresent("enchantment")) {
                        power.enchantments.add(data.get("enchantment"));
                    }
                    if(data.isPresent("enchantments")) {
                        power.enchantments.addAll(data.get("enchantments"));
                    }
                    return power;
                });
            allowCondition();
        }
    
        @Override
        public Class<AllowAnvilEnchantPower> getPowerClass() {
            return AllowAnvilEnchantPower.class;
        }
        
    }

}
