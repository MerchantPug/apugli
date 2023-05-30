**Power Types**
- Added new `apugli:client_action_over_time` power type.
- `apugli:modify_enchantment_damage_dealt`/`apugli:modify_enchantment_damage_taken` now creates enchantment crit particles if the damage is caused by a player and the particles wouldn't normally be present.

**Meta Action Types**
- Added new `apugli:packet` entity and bi-entity action types.

**Entity Action Types**
- Added new `apugli:explosion_raycast` entity action.
- Added new `apugli:add_velocity` entity action.
- Added `damage_modifier` and `damage_modifiers` field to `apugli:explode`.
- Added `knockback_modifier` and `knockback_modifiers` field to `apugli:explode`.
- Added `volume_modifier` and `volume_modifiers` field to `apugli:explode`.
- Added `pitch_modifier` and `pitch_modifiers` field to `apugli:explode`.
- Added `damage_bientity_condition` field to `apugli:explode`.

**Entity Condition Types**
- Added new `apugli:status_effect_tag` entity condition.

**Bugfixes**
- Fixed `apugli:raycast` entity condition using an entity condition for the `bientity_condition` field.
- Fixed `apugli:charged_effects` mob effect tag not being present.
- Fixed `apugli:modify_enchantment_damage_dealt` always being set to the `apugli:modify_enchantment_damage_taken` value. #33
- Fixed a crash involving `apugli:modify_enchantment_damage_taken`. #34