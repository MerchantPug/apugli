package com.github.merchantpug.apugli.registry;

import com.github.merchantpug.apugli.Constants;
import com.github.merchantpug.apugli.platform.Services;
import com.github.merchantpug.apugli.power.ActionOnEquipPower;
import com.github.merchantpug.apugli.power.HoverPower;
import com.github.merchantpug.apugli.power.data.HoverPowerData;
import io.github.apace100.apoli.power.factory.PowerFactory;
import net.minecraft.resources.ResourceLocation;

public class ExamplePowerFactories {
    public static final PowerFactory<ActionOnEquipPower> ACTION_ON_EQUIP = Services.PLATFORM.registerPowerFactory(new ResourceLocation(Constants.MOD_ID, "action_on_equip"), Services.ACTION_ON_EQUIP);
    public static final PowerFactory<HoverPower> HOVER = Services.PLATFORM.registerPowerFactory(new ResourceLocation(Constants.MOD_ID, "hover"), new HoverPowerData());

    public static void init() {

    }
}
