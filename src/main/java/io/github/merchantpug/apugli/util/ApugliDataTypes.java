package io.github.merchantpug.apugli.util;

import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.model.BipedEntityModel;

import java.util.List;

public class ApugliDataTypes {
    public static final SerializableDataType<PlayerModelType> PLAYER_MODEL_TYPE =
            SerializableDataType.enumValue(PlayerModelType.class);
    public static final SerializableDataType<BipedEntityModel.ArmPose> ARM_POSE =
            SerializableDataType.enumValue(BipedEntityModel.ArmPose.class);

    public static final SerializableDataType<List<Block>> BLOCKS = SerializableDataType.list(SerializableDataTypes.BLOCK);

    public static final SerializableDataType<List<BlockState>> BLOCK_STATES = SerializableDataType.list(SerializableDataTypes.BLOCK_STATE);
}
