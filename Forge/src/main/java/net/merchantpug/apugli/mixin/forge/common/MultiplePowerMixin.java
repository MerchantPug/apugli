package net.merchantpug.apugli.mixin.forge.common;

import com.google.gson.JsonElement;
import io.github.apace100.apoli.integration.PowerLoadEvent;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.common.power.MultiplePower;
import net.merchantpug.apugli.access.PowerLoadEventPostAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiplePower.class)
public class MultiplePowerMixin {
    private static String apugli$suffix;

    @Inject(method = "reconfigure", at = @At(value = "HEAD"), remap = false)
    private static <C extends IDynamicFeatureConfiguration, F extends PowerFactory<C>> void getSuffix(String suffix, ConfiguredPower<C, F> source, JsonElement root, CallbackInfoReturnable<ConfiguredPower<C, ?>> cir) {
        apugli$suffix = suffix;
    }

    @ModifyArg(method = "reconfigure", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z"), remap = false)
    private static Event addFixedPowerId(Event event) {
        if (event instanceof PowerLoadEvent.Post post) {
            ((PowerLoadEventPostAccess)post).setFixedId(new ResourceLocation(SerializableData.CURRENT_NAMESPACE, SerializableData.CURRENT_PATH + "_" + apugli$suffix));
        }
        return event;
    }
}
