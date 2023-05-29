package net.merchantpug.apugli.mixin.forge.common;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.apace100.calio.data.SerializableDataType;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.EncoderException;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Mixin(SerializableDataType.class)
public abstract class SerializableDataTypeMixin<T> {

    @Mutable @Shadow @Final private BiConsumer<FriendlyByteBuf, T> send;

    @Shadow @Final private Codec<T> codec;

    @Mutable @Shadow @Final private Function<FriendlyByteBuf, T> receive;

    @Inject(method = "<init>(Ljava/lang/Class;Lcom/mojang/serialization/Codec;)V", at = @At("TAIL"))
    private void manageInit(Class<T> dataClass, Codec<T> codec, CallbackInfo ci) {
        this.send = (buf, t) -> apugli$writeWithCodec(buf, this.codec, t);
        this.receive = buf -> apugli$readWithCodec(buf, this.codec);
    }

    @Unique
    private void apugli$writeWithCodec(FriendlyByteBuf buf, Codec<T> codec, T data) {
        DataResult<Tag> dataResult = codec.encodeStart(NbtOps.INSTANCE, data);
        dataResult.error().ifPresent(partialResult -> {
            throw new EncoderException("Failed to encode: " + partialResult.message() + " " + data);
        });
        try {
            NbtIo.writeUnnamedTag(dataResult.result().get(), new ByteBufOutputStream(buf));
        } catch (IOException ioexception) {
            throw new EncoderException("Failed to encode SerializableDataType: " + ioexception);
        }
    }

    @Unique
    private T apugli$readWithCodec(FriendlyByteBuf buf, Codec<T> codec) {
        Tag compoundTag = this.apugli$readAnySizeNbt(buf);
        DataResult<T> dataResult = codec.parse(NbtOps.INSTANCE, compoundTag);
        dataResult.error().ifPresent(partialResult -> {
            throw new EncoderException("Failed to decode: " + partialResult.message() + " " + compoundTag);
        });
        return dataResult.result().get();
    }


    @Nullable
    public Tag apugli$readAnySizeNbt(FriendlyByteBuf buf) {
        int i = buf.readerIndex();
        byte b0 = buf.readByte();
        if (b0 == 0) {
            return null;
        } else {
            buf.readerIndex(i);
            try {
                return NbtIo.readUnnamedTag(new ByteBufInputStream(buf), 0, NbtAccounter.UNLIMITED);
            } catch (IOException ioexception) {
                throw new EncoderException(ioexception);
            }
        }
    }

}
