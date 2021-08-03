package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.util.AttributedEntityAttributeModifier;
import io.github.apace100.calio.Calio;
import io.github.merchantpug.apugli.mixin.PlayerEntityAccessor;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class ItemAttributePower extends Power {
    private final List<AttributedEntityAttributeModifier> modifiers = new LinkedList<>();
    public final Predicate<ItemStack> predicate;

    public ItemAttributePower(PowerType<?> type, LivingEntity entity, Predicate<ItemStack> predicate) {
        super(type, entity);
        this.predicate = predicate;
    }

    public ItemAttributePower addModifier(EntityAttribute attribute, EntityAttributeModifier modifier) {
        AttributedEntityAttributeModifier mod = new AttributedEntityAttributeModifier(attribute, modifier);
        this.modifiers.add(mod);
        return this;
    }

    public ItemAttributePower addModifier(AttributedEntityAttributeModifier modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    @Override
    public void onGained() {
        if (entity instanceof PlayerEntity) {
            for (int i = 0; i < ((PlayerEntityAccessor)entity).getInventory().main.size(); i++) {
                ItemStack stack = ((PlayerEntityAccessor)entity).getInventory().main.get(i);
                if (predicate.test(stack)) {
                    addModifiersToItem(stack);
                }
            }
            for (int i = 0; i < ((PlayerEntityAccessor)entity).getInventory().armor.size(); i++) {
                ItemStack stack = ((PlayerEntityAccessor)entity).getInventory().getArmorStack(i);
                if (predicate.test(stack)) {
                    addModifiersToItem(stack);
                }
            }
            ItemStack stack = entity.getEquippedStack(EquipmentSlot.OFFHAND);
            if (predicate.test(stack)) {
                addModifiersToItem(stack);
            }
        }
    }

    @Override
    public void onRemoved() {
        if (entity instanceof PlayerEntity) {
            this.removeModifiersFromAllItems((PlayerEntity)entity);
        }
    }

    public void removeModifiersFromAllItems(PlayerEntity player) {
        if (entity instanceof PlayerEntity) {
            for (int i = 0; i < ((PlayerEntityAccessor)entity).getInventory().main.size(); i++) {
                ItemStack stack = ((PlayerEntityAccessor)entity).getInventory().main.get(i);
                if (predicate.test(stack)) {
                    removeModifiersFromItem(stack);
                }
            }
            for (int i = 0; i < ((PlayerEntityAccessor)entity).getInventory().armor.size(); i++) {
                ItemStack stack = ((PlayerEntityAccessor)entity).getInventory().getArmorStack(i);
                if (predicate.test(stack)) {
                    removeModifiersFromItem(stack);
                }
            }
            ItemStack stack = entity.getEquippedStack(EquipmentSlot.OFFHAND);
            if (predicate.test(stack)) {
                removeModifiersFromItem(stack);
            }
        }
    }

    public void addModifiersToItem(ItemStack stack) {
        modifiers.forEach(attributedEntityAttributeModifier -> {
            AttributedEntityAttributeModifier attributeModifier = attributedEntityAttributeModifier;
            if (attributedEntityAttributeModifier.getModifier().getName().equals("Unnamed EntityAttributeModifier")) {
                attributeModifier = new AttributedEntityAttributeModifier(attributedEntityAttributeModifier.getAttribute(),
                        new EntityAttributeModifier(
                                attributedEntityAttributeModifier.getModifier().getId(),
                                "ItemAttributePower EntityAttributeModifier",
                                attributedEntityAttributeModifier.getModifier().getValue(),
                                attributedEntityAttributeModifier.getModifier().getOperation()
                        ));
            }
            if (stack.getItem() instanceof ArmorItem) {
                stack.addAttributeModifier(attributeModifier.getAttribute(), attributeModifier.getModifier(), ((ArmorItem) stack.getItem()).getSlotType());
                Calio.setEntityAttributesAdditional(stack, true);
            } else if (stack.getItem() instanceof ShieldItem) {
                stack.addAttributeModifier(attributeModifier.getAttribute(), attributeModifier.getModifier(), EquipmentSlot.OFFHAND);
                Calio.setEntityAttributesAdditional(stack, true);
            } else {
                stack.addAttributeModifier(attributeModifier.getAttribute(), attributeModifier.getModifier(), EquipmentSlot.MAINHAND);
                Calio.setEntityAttributesAdditional(stack, true);
            }
        });
    }

    public void removeModifiersFromItem(ItemStack stack) {
        modifiers.forEach(attributedEntityAttributeModifier -> {
            NbtList nbtList = stack.getNbt().getList("AttributeModifiers", 10);
            EntityAttributeModifier modifier = attributedEntityAttributeModifier.getModifier();
            if (attributedEntityAttributeModifier.getModifier().getName().equals("Unnamed EntityAttributeModifier")) {
                modifier = new EntityAttributeModifier(
                        attributedEntityAttributeModifier.getModifier().getId(),
                        "ItemAttributePower EntityAttributeModifier",
                        attributedEntityAttributeModifier.getModifier().getValue(),
                        attributedEntityAttributeModifier.getModifier().getOperation()
                );
            }
            NbtCompound nbtCompound = modifier.toNbt();
            nbtList.removeIf(nbtElement -> {
                return ((NbtCompound)nbtElement).get("Name").equals(nbtCompound.get("Name")) || ((NbtCompound)nbtElement).get("UUID").equals(nbtCompound.get("UUID"));
            });
        });
    }
}
