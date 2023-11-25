package net.merchantpug.apugli.power.integration.pehkui;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.util.modifier.Modifier;
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

import java.util.ArrayList;
import java.util.List;
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
    public List<?> getDelayModifiers(Instance power, Entity entity) {
        return power.delayModifiers;
    }

    @Override
    public Set<ResourceLocation> getCachedScaleIds(Instance power, Entity entity) {
        return power.cachedScaleIds;
    }


    public static class Instance extends AbstractValueModifyingPower.Instance {
        private final Object apoliScaleModifier;
        private final Set<ResourceLocation> cachedScaleIds;
        private final List<Modifier> delayModifiers = new ArrayList<>();

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
            data.<Modifier>ifPresent("delay_modifier", delayModifiers::add);
            data.<List<Modifier>>ifPresent("delay_modifiers", delayModifiers::addAll);
        }

        @Override
        public void onAdded() {
            PehkuiUtil.onAddedOrRespawnedScalePower(this, this.entity);
        }

        @Override
        public void onLost() {
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
