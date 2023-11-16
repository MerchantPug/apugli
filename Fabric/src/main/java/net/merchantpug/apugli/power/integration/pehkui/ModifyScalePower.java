package net.merchantpug.apugli.power.integration.pehkui;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.fabricmc.loader.api.FabricLoader;
import net.merchantpug.apugli.integration.pehkui.PehkuiUtil;
import net.merchantpug.apugli.power.AbstractValueModifyingPower;
import net.merchantpug.apugli.power.factory.ModifyScalePowerFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

@AutoService(ModifyScalePowerFactory.class)
public class ModifyScalePower extends AbstractValueModifyingPower<ModifyScalePower.Instance> implements ModifyScalePowerFactory<ModifyScalePower.Instance> {

    public ModifyScalePower() {
        super("modify_scale", ModifyScalePowerFactory.getSerializableData(),
            data -> (type, entity) -> new Instance(type, entity, data));
        allowCondition();
    }
    
    @Override
    public Class<Instance> getPowerClass() {
        return Instance.class;
    }

    @Override
    public ResourceLocation getPowerId(Instance power) {
        return power.getType().getIdentifier();
    }

    @Override
    public Object getApoliScaleModifier(Instance power, Entity entity) {
        return power.apoliScaleModifier;
    }

    @Override
    public Set<ResourceLocation> getCachedScaleIds(Instance power, Entity entity) {
        return power.cachedScaleIds;
    }

    @Override
    public boolean hasScaleModifier(Instance power, LivingEntity entity) {
        return power.apoliScaleModifier != null;
    }

    public static class Instance extends AbstractValueModifyingPower.Instance {
        private final Object apoliScaleModifier;
        private final Set<ResourceLocation> cachedScaleIds;

        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
            setTicking(true);
            if (FabricLoader.getInstance().isModLoaded("pehkui")) {
                this.cachedScaleIds = PehkuiUtil.getTypesFromCache(data);
                this.apoliScaleModifier = PehkuiUtil.createApoliScaleModifier(this, entity, data);
            } else {
                this.cachedScaleIds = Set.of();
                this.apoliScaleModifier = null;
            }
        }

        @Override
        public void onRemoved() {
            PehkuiUtil.onRemovedScalePower(this, this.entity);
        }

        @Override
        public Tag toTag() {
            return PehkuiUtil.serializeScalePower(this, this.entity, new CompoundTag());
        }

        @Override
        public void fromTag(Tag tag) {
            if (!(tag instanceof CompoundTag compoundTag)) return;
            PehkuiUtil.deserializeScalePower(this, this.entity, compoundTag);
        }
    }
}
