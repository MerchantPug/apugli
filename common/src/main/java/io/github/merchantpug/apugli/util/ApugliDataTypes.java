package io.github.merchantpug.apugli.util;

import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

import static io.github.apace100.origins.util.SerializableDataType.PARTICLE_TYPE;
import static io.github.apace100.origins.util.SerializableDataType.STRING;

@SuppressWarnings("deprecation")
public class ApugliDataTypes {

    public static final SerializableDataType<PlayerModelType> PLAYER_MODEL_TYPE =
            SerializableDataType.enumValue(PlayerModelType.class);
}
