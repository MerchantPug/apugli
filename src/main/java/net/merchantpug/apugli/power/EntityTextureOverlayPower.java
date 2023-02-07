package net.merchantpug.apugli.power;

import net.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class EntityTextureOverlayPower extends Power {
    private final Identifier textureLocation;
    private final String textureUrl;
    private final boolean showFirstPerson;
    private final boolean usesRenderingPowers;
    private final boolean renderPlayerOuterLayer;
    private final boolean renderOriginalModel;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(Apugli.identifier("entity_texture_overlay"),
                new SerializableData()
                        .add("texture_location", SerializableDataTypes.IDENTIFIER, null)
                        .add("texture_url", SerializableDataTypes.STRING, null)
                        .add("show_first_person", SerializableDataTypes.BOOLEAN, false)
                        .add("use_rendering_powers", SerializableDataTypes.BOOLEAN,  false)
                        .add("render_player_outer_layer", SerializableDataTypes.BOOLEAN,  true)
                        .add("render_original_model", SerializableDataTypes.BOOLEAN,  true),
                data ->
                        (type, player) ->
                                new EntityTextureOverlayPower(type, player, data.getId("texture_location"), data.getString("texture_url"), data.getBoolean("show_first_person"), data.getBoolean("use_rendering_powers"), data.getBoolean("render_player_outer_layer"), data.getBoolean("render_original_model")))
                .allowCondition();
    }

    public EntityTextureOverlayPower(PowerType<?> type, LivingEntity entity, Identifier textureLocation, String textureUrl, boolean showFirstPerson, boolean usesRenderingPowers, boolean renderPlayerOuterLayer, boolean renderOriginalModel) {
        super(type, entity);
        if (textureLocation == null && textureUrl == null) {
            Apugli.LOGGER.warn("EntityTextureOverlayPower '" + this.getType().getIdentifier() + "' does not have a valid `texture_location` or `texture_url` field. This power will not render.");
        }
        this.textureLocation = textureLocation;
        this.textureUrl = textureUrl;
        this.showFirstPerson = showFirstPerson;
        this.usesRenderingPowers = usesRenderingPowers;
        this.renderPlayerOuterLayer = renderPlayerOuterLayer;
        this.renderOriginalModel = renderOriginalModel;
    }

    public Identifier getTextureLocation() {
        return textureLocation;
    }

    public String getTextureUrl() {
        return textureUrl;
    }

    public Identifier getUrlTextureIdentifier() {
        return new Identifier(Apugli.MODID, "entitytextureoverlaypower/" + this.getType().getIdentifier().getNamespace() + "/" + this.getType().getIdentifier().getPath());
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
}
