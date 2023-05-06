**Rewrite**
- Apugli is now a multiloader mod. This means that it is now able to be simultaneously developed for Fabric and Forge.
    - Origins Fabric based development should not end up waiting for Origins Forge to update as I can detatch the Forge module.

**Power Types**
- Added `apugli:action_when_projectile_hit` power type.
- `apugli:bunny_hop` now stacks when you have multiple powers with this power type.
- Added `entity_action` and `damage_condition` fields to `apugli:action_on_harm` and `apugli:action_when_harmed` powser types.
- Added `item_condition` field to `apugli:modify_enchantment_level`.
- Removed `target_condition`, `self_action` and `target_action` fields from `apugli:modify_enchantment_damage_taken` as they didn't do anything and weren't present in `apugli:modify_enchantment_damage_dealt`.

**Entity Action Types**
- `apugli:explode` no longer removes effects within the `apugli:charged` tag mob effect tag upon being triggered. Please do this yourself.
- `apugli:raycast` will now act upon entity raycasts before block raycasts, block raycasts no longer run upon a successful entity raycast.

**Entity Condition Types**
- `apugli:raycast` now prioritises an entity raycast if one is found.

**Config**
- Reset Timer Ticks is now a serverside setting within the Apugli config.

**Bugfixes**
- Fixed `apugli:modify_damage_taken` running more times than what was necessary.
- Fixed `performVersionCheck` not mattering in the serversided config.
- Fixed `apugli:prevent_breeding` bientity actions setting the love ticks value to too little.
- Fixed `apugli:modify_enchantment_level` crashing if used with an `apoli:enchantment` entity condition and item condition.