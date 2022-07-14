/*
MIT License

Copyright (c) 2020 apace100

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package com.github.merchantpug.apugli.action.entity;

import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.util.ModComponents;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.TogglePower;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class ToggleAction {
    public static void action(SerializableData.Instance data, Entity entity) {
        if(entity instanceof PlayerEntity) {
            OriginComponent component = ModComponents.getOriginComponent((PlayerEntity) entity);
            Power p = component.getPower((PowerType<?>)data.get("power"));
            if(p instanceof TogglePower) {
                ((TogglePower)p).onUse();
            }
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apugli.identifier("toggle"), new SerializableData()
                .add("power", SerializableDataType.POWER_TYPE),
                ToggleAction::action
        );
    }
}
