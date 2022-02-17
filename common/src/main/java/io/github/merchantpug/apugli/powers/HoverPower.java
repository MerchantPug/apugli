package io.github.merchantpug.apugli.powers;

import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.power.Active;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.ResourcePower;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.util.HudRender;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

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
                        .add("min", SerializableDataType.INT)
                        .add("max", SerializableDataType.INT)
                        .addFunctionedDefault("start_value", SerializableDataType.INT, data -> data.getInt("min"))
                        .add("hud_render", SerializableDataType.HUD_RENDER)
                        .add("min_action", SerializableDataType.ENTITY_ACTION, null)
                        .add("max_action", SerializableDataType.ENTITY_ACTION, null)
                        .add("decrease_while_using", SerializableDataType.BOOLEAN, true)
                        .add("tick_rate", SerializableDataType.INT, 1)
                        .add("time_until_recharge", SerializableDataType.INT, 10)
                        .add("recharge_amount", SerializableDataType.INT, 2)
                        .add("recharge_while_inactive", SerializableDataType.BOOLEAN, false)
                        .add("recharge_rate", SerializableDataType.INT, 1)
                        .add("key", SerializableDataType.BACKWARDS_COMPATIBLE_KEY, new Active.Key()),
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
                                    data.getInt("recharge_amount"),
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
        if (this.canUse() && player.getVelocity().y < 0.0D && !player.isOnGround()) {
            player.setVelocity(player.getVelocity().multiply(1.0, 0.0, 1.0));
            player.fallDistance = 0.0F;
            if (decreaseWhileUsing && player.age % tickRate == 0) {
                this.decrement();
                this.rechargeTimer = 0;
            }
            this.lastUseTime = player.world.getTime();
            this.isInUse = true;
        }
    }

    @Override
    public void tick() {
        if (!rechargeWhileInactive && !isActive()) return;
        if (this.lastUseTime != player.world.getTime()) {
            isInUse = false;
            OriginComponent.sync(player);
        }
        if (decreaseWhileUsing && !isInUse && rechargeTimer < timeUntilRecharge) {
            if (player.isOnGround() || player.hasVehicle()) {
                rechargeTimer += 1;
            } else {
                rechargeTimer = 0;
            }
            OriginComponent.sync(player);
        }
        if (rechargeTimer == timeUntilRecharge && player.age % rechargeRate == 0) {
            if (this.getValue() != this.getMax()) {
                this.setValue(Math.min(this.getValue() + this.rechargeAmount, this.getMax()));
            } else {
                rechargeTimer += 1;
            }
            OriginComponent.sync(player);
        }
    }

    public boolean canUse() {
        return this.getValue() > this.getMin() && this.isActive() && !player.isClimbing() && !player.isTouchingWater() && !player.isFallFlying() && !player.hasVehicle();
    }

    @Override
    public Tag toTag() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("CurrentValue", this.currentValue);
        nbt.putBoolean("IsInUse", this.isInUse);
        nbt.putLong("LastUseTime", this.lastUseTime);
        nbt.putInt("RechargeTimer", this.rechargeTimer);
        return nbt;
    }

    @Override
    public void fromTag(Tag tag) {
        if (!(tag instanceof CompoundTag)) return;
        currentValue = ((CompoundTag)tag).getInt("CurrentValue");
        isInUse = ((CompoundTag)tag).getBoolean("IsInUse");
        lastUseTime = ((CompoundTag)tag).getLong("LastUseTime");
        rechargeTimer = ((CompoundTag)tag).getInt("RechargeTimer");
    }

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public void setKey(Key key) {
        this.key = key;
    }

    public HoverPower(PowerType<?> type, PlayerEntity player, HudRender hudRender, int startValue, int min, int max, Consumer<Entity> actionOnMin, Consumer<Entity> actionOnMax, boolean decreaseWhileUsing, int tickRate, int rechargeAmount, int rechargeRate, int timeUntilRecharge, boolean rechargeWhileInactive) {
        super(type, player, hudRender, startValue, min, max, actionOnMin, actionOnMax);
        this.decreaseWhileUsing = decreaseWhileUsing;
        this.tickRate = tickRate;
        this.rechargeAmount = rechargeAmount;
        this.rechargeRate = rechargeRate;
        this.timeUntilRecharge = timeUntilRecharge;
        this.rechargeWhileInactive = rechargeWhileInactive;
        this.setTicking(true);
    }
}
