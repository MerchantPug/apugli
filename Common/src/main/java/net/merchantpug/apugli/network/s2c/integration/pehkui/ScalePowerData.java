package net.merchantpug.apugli.network.s2c.integration.pehkui;

import net.merchantpug.apugli.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public record ScalePowerData(ResourceLocation powerId,
                             List<?> modifiers,
                             Optional<Integer> lerpTicks,
                             Optional<Integer> lerpTickMax,
                             Optional<Float> previousScale) {

        public void encode(FriendlyByteBuf buf) {
            buf.writeResourceLocation(this.powerId());
            Services.PLATFORM.getModifiersDataType().send(buf, this.modifiers());
            buf.writeBoolean(this.lerpTicks().isPresent());
            this.lerpTicks().ifPresent(buf::writeInt);
            buf.writeBoolean(this.lerpTickMax().isPresent());
            this.lerpTickMax().ifPresent(buf::writeInt);
            buf.writeBoolean(this.previousScale().isPresent());
            this.previousScale().ifPresent(buf::writeFloat);
        }

        public static ScalePowerData decode(FriendlyByteBuf buf) {
            ResourceLocation powerId = buf.readResourceLocation();
            List<?> modifiers = Services.PLATFORM.getModifiersDataType().receive(buf);
            Optional<Integer> lerpTicks = Optional.empty();
            if (buf.readBoolean()) {
                lerpTicks = Optional.of(buf.readInt());
            }
            Optional<Integer> lerpTickMax = Optional.empty();
            if (buf.readBoolean()) {
                lerpTickMax = Optional.of(buf.readInt());
            }
            Optional<Float> previousScale = Optional.empty();
            if (buf.readBoolean()) {
                previousScale = Optional.of(buf.readFloat());
            }

            return new ScalePowerData(powerId, modifiers, lerpTicks, lerpTickMax, previousScale);
        }

    }