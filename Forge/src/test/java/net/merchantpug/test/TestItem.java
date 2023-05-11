package net.merchantpug.test;

import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.HashMap;
import java.util.Map;

public class TestItem extends PickaxeItem {
    public TestItem() {
        super(Tiers.GOLD, 3, -2.4F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS));
    }

    @Override
    public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        if (enchantment == Enchantments.SHARPNESS && EnchantmentHelper.getTagEnchantmentLevel(enchantment, stack) < 2) {
            return 2;
        }
        return super.getEnchantmentLevel(stack, enchantment);
    }

    @Override
    public Map<Enchantment, Integer> getAllEnchantments(ItemStack stack) {
        var map = new HashMap<>(super.getAllEnchantments(stack));
        if (EnchantmentHelper.getTagEnchantmentLevel(Enchantments.SHARPNESS, stack) < 2) {
            map.put(Enchantments.SHARPNESS, 2);
        }
        return map;
    }
}
