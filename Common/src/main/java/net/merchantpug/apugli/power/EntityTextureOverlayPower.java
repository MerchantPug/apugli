package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.merchantpug.apugli.util.TextureUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class EntityTextureOverlayPower extends TextureOrUrlPower {
    private final boolean showFirstPerson;
    private final boolean usesRenderingPowers;
    private final boolean renderPlayerOuterLayer;
    private final boolean renderOriginalModel;

    public EntityTextureOverlayPower(PowerType<?> type, LivingEntity entity, ResourceLocation textureLocation, String textureUrl, boolean showFirstPerson, boolean usesRenderingPowers, boolean renderPlayerOuterLayer, boolean renderOriginalModel) {
        super(type, entity, textureLocation, textureUrl);
        this.showFirstPerson = showFirstPerson;
        this.usesRenderingPowers = usesRenderingPowers;
        this.renderPlayerOuterLayer = renderPlayerOuterLayer;
        this.renderOriginalModel = renderOriginalModel;
    }

    @Override
    public String getPowerClassString() {
        return "EntityTextureOverlayPower";
    }

    public boolean shouldShowFirstPerson() {
        return showFirstPerson;
    }

    public boolean shouldUseRenderingPowers() {
        return usesRenderingPowers;
    }

    public boolean shouldRenderPlayerOuterLayer() {
        return renderPlayerOuterLayer;
    }

    public boolean shouldRenderOriginalModel() {
        return renderOriginalModel;
    }

    public boolean shouldRenderOriginalModelClient() {
        return renderOriginalModel || !TextureUtil.getPowerIdToUrl().containsKey(this.getType().getIdentifier()) && textureLocation == null;
    }

    public static class Factory extends SimplePowerFactory<EntityTextureOverlayPower> {

        public Factory() {
            super("entity_texture_overlay",
                    TextureOrUrlPower.getSerializableData()
                            .add("show_first_person", SerializableDataTypes.BOOLEAN, false)
                            .add("use_rendering_powers", SerializableDataTypes.BOOLEAN,  false)
                            .add("render_player_outer_layer", SerializableDataTypes.BOOLEAN,  true)
                            .add("render_original_model", SerializableDataTypes.BOOLEAN,  true),
                    data -> (type, player) -> new EntityTextureOverlayPower(type, player,
                            data.getId("texture_location"),
                            data.getString("texture_url"),
                            data.getBoolean("show_first_person"),
                            data.getBoolean("use_rendering_powers"),
                            data.getBoolean("render_player_outer_layer"),
                            data.getBoolean("render_original_model")));

            allowCondition();
        }

        @Override
        public Class<EntityTextureOverlayPower> getPowerClass() {
            return EntityTextureOverlayPower.class;
        }

    }

}
