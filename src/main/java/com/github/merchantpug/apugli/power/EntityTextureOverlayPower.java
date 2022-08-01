package com.github.merchantpug.apugli.power;

import com.github.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class EntityTextureOverlayPower extends Power {
    public final Identifier textureLocation;
    public final String textureUrl;
    public final boolean showFirstPerson;
    public final boolean hideEntityModel;
    public final boolean renderingPowerCompatible;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(Apugli.identifier("entity_texture_overlay"),
                new SerializableData()
                        .add("texture_location", SerializableDataTypes.IDENTIFIER, null)
                        .add("url", SerializableDataTypes.STRING, null)
                        .add("show_first_person", SerializableDataTypes.BOOLEAN, false)
                        .add("hide_player_model", SerializableDataTypes.BOOLEAN, false)
                        .add("rendering_power_compatible", SerializableDataTypes.BOOLEAN,  false),
                data ->
                        (type, player) ->
                                new EntityTextureOverlayPower(type, player, data.getId("texture_location"), data.getString("url"), data.getBoolean("show_first_person"), data.getBoolean("hide_player_model"), data.getBoolean("rendering_power_compatible")))
                .allowCondition();
    }

    public EntityTextureOverlayPower(PowerType<?> type, LivingEntity entity, Identifier textureLocation, String textureUrl, boolean showFirstPerson, boolean hideEntityModel, boolean renderingPowerCompatible) {
        super(type, entity);
        this.textureLocation = textureLocation;
        this.textureUrl = textureUrl;
        this.showFirstPerson = showFirstPerson;
        this.hideEntityModel = hideEntityModel;
        this.renderingPowerCompatible = renderingPowerCompatible;
    }

    public Identifier getUrlTextureIdentifier() {
        return new Identifier(Apugli.MODID, "entitytextureoverlaypower/" + this.getType().getIdentifier().getNamespace() + "/" + this.getType().getIdentifier().getPath());
    }
}
