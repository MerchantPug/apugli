package net.merchantpug.apugli.action.factory.entity;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.action.factory.IActionFactory;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.List;

public class ItemCooldownAction implements IActionFactory<Entity> {

    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("items", SerializableDataType.list(SerializableDataTypes.ITEM), null)
                .add("item_tags", SerializableDataType.list(SerializableDataTypes.ITEM_TAG), null)
                .add("ticks", SerializableDataTypes.INT, 20);
    }

    @Override
    public void execute(SerializableData.Instance data, Entity entity) {
        if (!entity.getLevel().isClientSide() && entity instanceof Player player) {
            data.<List<Item>>get("items").forEach(item -> player.getCooldowns().addCooldown(item, data.getInt("ticks")));
            data.<List<TagKey<Item>>>get("item_tags").forEach(tagKey -> {
                if (Registry.ITEM.getTag(tagKey).isPresent()) {
                    Registry.ITEM.getTag(tagKey).get().spliterator().forEachRemaining(itemHolder -> {
                        if (itemHolder.isBound()) {
                            player.getCooldowns().addCooldown(itemHolder.value(), data.getInt("ticks"));
                        }
                    });
                }
            });
        }
    }

}
