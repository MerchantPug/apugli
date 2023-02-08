package net.merchantpug.apugli.networking;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface ApugliPacket {
    default PacketByteBuf toBuf() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        encode(buf);
        return buf;
    }

    void encode(PacketByteBuf buf);

    Identifier getId();
}
