package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ResourcePower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.function.Consumer;

public class HoverPower extends ResourcePower implements Active {
    private Key key;
    private final boolean decreaseWhileUsing;
    private final int tickRate;
    private final int rechargeAmount;
    private final int rechargeRate;
    private final int timeUntilRecharge;
    private final boolean rechargeWhileInactive;

    protected boolean isInUse;
    protected long lastUseTime;
    protected int rechargeTimer;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<HoverPower>(Apugli.identifier("hover"),
                new SerializableData()
                        .add("min", SerializableDataTypes.INT)
                        .add("max", SerializableDataTypes.INT)
                        .addFunctionedDefault("start_value", SerializableDataTypes.INT, data -> data.getInt("min"))
                        .add("hud_render", ApoliDataTypes.HUD_RENDER)
                        .add("min_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("max_action", ApoliDataTypes.ENTITY_ACTION, null)
                        .add("decrease_while_using", SerializableDataTypes.BOOLEAN, true)
                        .add("tick_rate", SerializableDataTypes.INT, 1)
                        .add("time_until_recharge", SerializableDataTypes.INT, 10)
                        .add("recharge_amount", SerializableDataTypes.INT, 2)
                        .add("recharge_while_inactive", SerializableDataTypes.BOOLEAN, false)
                        .add("recharge_rate", SerializableDataTypes.INT, 2)
                        .add("key", ApoliDataTypes.KEY, new Active.Key()),
                data ->
                        (type, entity) -> {
                                HoverPower power = new HoverPower(type, entity,
                                        (HudRender)data.get("hud_render"),
                                        data.getInt("start_value"),
                                        data.getInt("min"),
                                        data.getInt("max"),
                                        (ActionFactory<Entity>.Instance)data.get("min_action"),
                                        (ActionFactory<Entity>.Instance)data.get("max_action"),
                                        data.getBoolean("decrease_while_using"),
                                        data.getInt("tick_rate"),
                                        data.get("recharge_amount"),
                                        data.getInt("recharge_rate"),
                                        data.getInt("time_until_recharge"),
                                        data.getBoolean("recharge_while_inactive"));
                                power.setKey((Active.Key)data.get("key"));
                                return power;
                        })
                .allowCondition();
    }

    @Override
    public void onUse() {
        if (this.canUse() && entity.getVelocity().y < 0.0D && !entity.isOnGround()) {
            entity.setVelocity(entity.getVelocity().multiply(1.0, 0.0, 1.0));
            entity.fallDistance = 0.0F;
            if (decreaseWhileUsing && entity.age % tickRate == 0) {
                this.decrement();
                this.rechargeTimer = 0;
            }
            this.lastUseTime = entity.world.getTime();
            this.isInUse = true;
        }
    }

    @Override
    public void tick() {
        if (!rechargeWhileInactive && !isActive()) return;
        if (this.lastUseTime != entity.world.getTime()) {
            isInUse = false;
            PowerHolderComponent.syncPower(entity, this.getType());
        }
        if (decreaseWhileUsing && !isInUse && rechargeTimer < timeUntilRecharge) {
            if (entity.isOnGround() || entity.hasVehicle()) {
                rechargeTimer += 1;
            } else {
                rechargeTimer = 0;
            }
            PowerHolderComponent.syncPower(entity, this.getType());
        }
        if (rechargeTimer == timeUntilRecharge && entity.age % rechargeRate == 0) {
            if (this.getValue() != this.getMax()) {
                this.setValue(Math.min(this.getValue() + this.rechargeAmount, this.getMax()));
            } else {
                rechargeTimer += 1;
            }
            PowerHolderComponent.syncPower(entity, this.getType());
        }
    }

    public boolean canUse() {
        return this.getValue() > this.getMin() && this.isActive() && !entity.isClimbing() && !entity.isTouchingWater() && !entity.isFallFlying() && !entity.hasVehicle();
    }

    @Override
    public NbtElement toTag() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("CurrentValue", this.currentValue);
        nbt.putBoolean("IsInUse", this.isInUse);
        nbt.putLong("LastUseTime", this.lastUseTime);
        nbt.putInt("RechargeTimer", this.rechargeTimer);
        return nbt;
    }

    @Override
    public void fromTag(NbtElement tag) {
        if (!(tag instanceof NbtCompound)) return;
        currentValue = ((NbtCompound)tag).getInt("CurrentValue");
        isInUse = ((NbtCompound)tag).getBoolean("IsInUse");
        lastUseTime = ((NbtCompound)tag).getLong("LastUseTime");
        rechargeTimer = ((NbtCompound)tag).getInt("RechargeTimer");
    }

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public void setKey(Key key) {
        this.key = key;
    }

    public HoverPower(PowerType<?> type, LivingEntity entity, HudRender hudRender, int startValue, int min, int max, Consumer<Entity> actionOnMin, Consumer<Entity> actionOnMax, boolean decreaseWhileUsing, int tickRate, int rechargeAmount, int rechargeRate, int timeUntilRecharge, boolean rechargeWhileInactive) {
        super(type, entity, hudRender, startValue, min, max, actionOnMin, actionOnMax);
        this.decreaseWhileUsing = decreaseWhileUsing;
        this.tickRate = tickRate;
        this.rechargeAmount = rechargeAmount;
        this.rechargeRate = rechargeRate;
        this.timeUntilRecharge = timeUntilRecharge;
        this.rechargeWhileInactive = rechargeWhileInactive;
        this.setTicking(true);
    }
}