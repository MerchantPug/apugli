**Rewrite**
- Apugli is now a multiloader mod. This means that it is now simultaneously being developed for Fabric and Forge.

**Power Types**
- Removed `target_condition`, `self_action` and `target_action` fields from `apugli:modify_enchantment_damage_taken` as they didn't do anything and weren't present in `apugli:modify_enchantment_damage_dealt`.
- Fixed `apugli:modify_damage_taken` running more times than what was necessary.

**Entity Action Types**
- `apugli:explode` no longer removes effects within the `apugli:charged` tag mob effect tag upon being triggered. Please do this yourself.
- `apugli:raycast` will now act upon entity raycasts before block raycasts, block raycasts no longer run upon a successful entity raycast.

**Entity Condition Types**
- `apugli:raycast` now prioritises an entity raycast if one is found.