package net.merchantpug.apugli.mixin.xplatform.common.accessor;

import net.minecraft.commands.arguments.SlotArgument;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SlotArgument.class)
public interface SlotArgumentAccessor {
    @Accessor("SLOTS")
    static Map<String, Integer> apugli$getSlots() {
        throw new RuntimeException("");
    }
}
