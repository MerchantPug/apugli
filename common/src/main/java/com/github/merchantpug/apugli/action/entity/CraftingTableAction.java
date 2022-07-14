package com.github.merchantpug.apugli.action.entity;

import com.github.merchantpug.apugli.Apugli;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.util.SerializableData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class CraftingTableAction {
    private static final Text TITLE = new TranslatableText("container.crafting");

    public static void action(SerializableData.Instance data, Entity entity) {
        if (!(entity instanceof PlayerEntity)) return;

        PlayerEntity player = (PlayerEntity)entity;

        player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inventory, _player) ->
            new CraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(_player.world, _player.getBlockPos())), TITLE));

        player.incrementStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apugli.identifier("crafting_table"),
                new SerializableData(),
                CraftingTableAction::action
        );
    }
}