/* MIT License

Copyright (c) 2021 apace100

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

package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public class ClientActionOverTime extends Power {
    private final int interval;
    private final Consumer<Entity> entityAction;
    private final Consumer<Entity> risingAction;
    private final Consumer<Entity> fallingAction;

    private boolean wasActive = false;

    private Integer initialTicks = null;

    public ClientActionOverTime(PowerType<?> type, LivingEntity entity,
                                Consumer<Entity> entityAction,
                                Consumer<Entity> risingAction,
                                Consumer<Entity> fallingAction,
                                int interval) {
        super(type, entity);
        this.entityAction = entityAction;
        this.risingAction = risingAction;
        this.fallingAction = fallingAction;
        this.interval = interval;
    }

    public void clientTick() {
        if (initialTicks == null) {
            initialTicks = entity.tickCount % interval;
        }
        else if (entity.tickCount % interval == initialTicks) {
            if (isActive()) {
                if (!wasActive && risingAction != null) {
                    risingAction.accept(entity);
                }
                if (entityAction != null) {
                    entityAction.accept(entity);
                }
                wasActive = true;
            }
            else {
                if (wasActive && fallingAction != null) {
                    fallingAction.accept(entity);
                }
                wasActive = false;
            }
        }
    }

    public static class Factory extends SimplePowerFactory<ClientActionOverTime> {

        public Factory() {
            super("client_action_over_time",
                    new SerializableData()
                            .add("entity_action", Services.ACTION.entityDataType(), null)
                            .add("rising_action", Services.ACTION.entityDataType(), null)
                            .add("falling_action", Services.ACTION.entityDataType(), null)
                            .add("interval", SerializableDataTypes.INT, 20),
                    data -> (type, entity) -> new ClientActionOverTime(type, entity,
                            Services.ACTION.entityConsumer(data, "entity_action"),
                            Services.ACTION.entityConsumer(data, "rising_action"),
                            Services.ACTION.entityConsumer(data, "falling_action"),
                            data.getInt("interval"))
            );
            allowCondition();
        }

        @Override
        public Class<ClientActionOverTime> getPowerClass() {
            return ClientActionOverTime.class;
        }

    }

}
