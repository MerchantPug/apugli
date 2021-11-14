package io.github.merchantpug.apugli.action.entity;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class EnderChestAction {
    private static final Text TITLE = new TranslatableText("container.enderchest");

    public static void action(SerializableData.Instance data, Entity entity) {
        if (!(entity instanceof PlayerEntity player)) return;

        EnderChestInventory enderChestContainer = player.getEnderChestInventory();

        player.openHandledScreen(
                new SimpleNamedScreenHandlerFactory((i, inventory, _player) ->
                    GenericContainerScreenHandler.createGeneric9x3(i, inventory, enderChestContainer),
                    TITLE
                )
        );
        player.incrementStat(Stats.OPEN_ENDERCHEST);
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apugli.identifier("ender_chest"),
            new SerializableData(),
            EnderChestAction::action
        );
    }
}