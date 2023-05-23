**Rewrite**
- Apugli is now a multiloader mod. This means that it is now able to be simultaneously developed for Fabric and Forge. (PR #25 - RaymondBlaze)
  - Origins Fabric based development should not end up waiting for Origins Forge to update as I can detach the Forge module at any time.

**Licensing**
- Apugli is now licensed as MIT again. (Previously LGPL-3.0-only).

**Power Types**
- Added `item_action` field to `apugli:edible_item` power type.
- Added `apugli:action_when_projectile_hit` power type.
- `apugli:bunny_hop` now stacks when you have multiple powers with this power type.
- Re-added `slot` field to `apugli:action_on_durability_change` power type.
- Added `increase_item_action`, `decrease_item_action`, and `break_item_action` to `apugli:action_on_durability_change` power type.
- Internally rewrote `apugli:action_on_durability_change` power type.
- `apugli:action_on_durability_change` now only executes one action per power per tick to prevent recursion.
- Added `entity_action` and `damage_condition` fields to `apugli:action_on_harm` and `apugli:action_when_harmed` power types.
- Added `item_condition` field to `apugli:modify_enchantment_level` power type.
- Removed `target_condition`, `self_action` and `target_action` fields from `apugli:modify_enchantment_damage_taken` as they didn't do anything and weren't present in `apugli:modify_enchantment_damage_dealt`.

**Entity Action Types**
- `apugli:explode` no longer removes effects within the `apugli:charged` tag mob effect tag upon being triggered. Please do this yourself.
- `apugli:raycast` will now act upon entity raycasts before block raycasts, block raycasts will no longer run upon a successful entity raycast.

**Entity Condition Types**
- `apugli:raycast` now prioritises an entity raycast if one is found.

**Config**
- Reset Timer Ticks is now a serverside setting within the Apugli config.

**Bugfixes**
- Fixed `apugli:modify_enchantment_damage_taken` running more times than what was necessary.
- Fixed `apugli:velocity` comparing the `compare_to` value to the velocity value instead of the other way around. Please update your conditions if you are using this one.
- Fixed `performVersionCheck` not mattering in the serversided config. #29
- Fixed `apugli:prevent_breeding` bientity actions setting the love ticks value to too little.
- Fixed `apugli:modify_enchantment_level` crashing if used with an `apoli:enchantment` entity condition and item condition. #21 (PR #30 - Jarva)