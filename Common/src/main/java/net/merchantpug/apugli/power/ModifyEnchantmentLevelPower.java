package net.merchantpug.apugli.power;

<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/power/ModifyEnchantmentLevelPower.java
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ItemStackAccess;
========
import the.great.migration.merchantpug.apugli.Apugli;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/power/ModifyEnchantmentLevelPower.java
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModifyEnchantmentLevelPower extends ValueModifyingPower {
    private static final ConcurrentHashMap<String, ConcurrentHashMap<ListTag, ListTag>> ENTITY_ITEM_ENCHANTS = new ConcurrentHashMap<>();
    private final Enchantment enchantment;

    public ModifyEnchantmentLevelPower(PowerType<?> type, LivingEntity entity, Enchantment enchantment) {
        super(type, entity);
        this.enchantment = enchantment;
    }

    public Enchantment getEnchantment() {
        return this.enchantment;
    }

    public boolean doesApply(Enchantment enchantment) {
        return enchantment.equals(this.enchantment);
    }

    private static Optional<Integer> findEnchantIndex(ResourceLocation id, ListTag enchants) {
        for(int i = 0; i < enchants.size(); ++i) {
            String string = enchants.getCompound(i).getString("id");
            ResourceLocation enchantId = ResourceLocation.tryParse(string);
            if(enchantId != null && enchantId.equals(id)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    public static ListTag generateEnchantments(ListTag enchants, ItemStack self) {
        Entity entity = ((ItemStackAccess) (Object) self).getEntity();

        if(!(entity instanceof Player)) return enchants;
        
        ListTag newEnchants = enchants.copy();

        for(ModifyEnchantmentLevelPower power : PowerHolderComponent.getPowers(entity, ModifyEnchantmentLevelPower.class)) {
            ResourceLocation id = Registry.ENCHANTMENT.getKey(power.getEnchantment());
            Optional<Integer> idx = findEnchantIndex(id, newEnchants);
            if(idx.isPresent()) {
                CompoundTag existingEnchant = newEnchants.getCompound(idx.get());
                int lvl = existingEnchant.getInt("lvl");
                int newLvl = (int) PowerHolderComponent.modify(entity, ModifyEnchantmentLevelPower.class, lvl, powerFilter -> powerFilter.doesApply(power.getEnchantment()));
                existingEnchant.putInt("lvl", newLvl);
                newEnchants.set(idx.get(), existingEnchant);
            } else {
                CompoundTag newEnchant = new CompoundTag();
                newEnchant.putString("id", id.toString());
                newEnchant.putInt("lvl", (int) PowerHolderComponent.modify(entity, ModifyEnchantmentLevelPower.class, 0, powerFilter -> powerFilter.doesApply(power.getEnchantment())));
                newEnchants.add(newEnchant);
            }
        }
        return newEnchants;
    }

    public static ListTag getEnchantments(ItemStack self) {
        Entity entity = ((ItemStackAccess) (Object) self).getEntity();
        if(entity == null) return self.getEnchantmentTags();
        ConcurrentHashMap<ListTag, ListTag> itemEnchants = ENTITY_ITEM_ENCHANTS.computeIfAbsent(entity.getStringUUID(), (_uuid) -> new ConcurrentHashMap<>());
        return itemEnchants.compute(self.getEnchantmentTags(), (tag, tag2) -> ModifyEnchantmentLevelPower.generateEnchantments(tag, self));
    }

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ModifyEnchantmentLevelPower>(
                Apugli.identifier("modify_enchantment_level"),
                new SerializableData()
                        .add("enchantment", SerializableDataTypes.ENCHANTMENT)
                        .add("modifier", Modifier.DATA_TYPE, null)
                        .add("modifiers", Modifier.LIST_TYPE, null),
                data -> (type, entity) -> {
                    ModifyEnchantmentLevelPower power = new ModifyEnchantmentLevelPower(type, entity, (Enchantment) data.get("enchantment"));
                    if(data.isPresent("modifier")) {
                        power.addModifier((Modifier) data.get("modifier"));
                    }
                    if(data.isPresent("modifiers")) {
                        ((List<Modifier>)data.get("modifiers")).forEach(power::addModifier);
                    }
                    return power;
                })
                .allowCondition();
    }
}