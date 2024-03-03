## Power Types
- Added `easing`, `priority`, `delay_modifier`, and `delay_modifiers` fields to `modify_scale`.
- `modify_scale`s with `delay` and resource based `modifier` fields will now dynamically shift between scales

## Optimisation
- Optimised `modify_enchantment_level` power type for compatibility.
- Rewrote entity stack linking entirely, empty stacks do not tick, and Forge no longer uses capabilities for this due to it being totally overkill.

## Bugfixes
- Fixed `modify_scale` not forcing an update for scales that don't update frequently.
- Fixed a crash upon having an invalid scale type in the `scale_type` or `scale_types` fields in the `modify_scale` power.
- Fixed `modify_scale` not being removed when it should be.
- Fixed `modify_scale`s with `delay` fields not getting exact scale values when changing into a non divisible value.
- Made `modify_scale` `delay` smoothing far more stable.
- Fixed `modify_scale` unintentionally resetting when swapping dimensions.
- Fixed `compare_scales` crashing when `scale_type` is specified.
- Fixed `compare_scales`s `scale_types` field not being counted.
- Fixed crash due to `enchantment` item condition mixin not applying.
- Fixed `step_height` power types sometimes causing entities to fly way up into the air when falling.
- [FORGE] Fixed NPE with hits on target.
- [FORGE] Fixed item capabilities being broken in a player's inventory. #67
- [FORGE] Fixed `modify_scale` with modifiers with `resource` fields not functioning.

## Miscellaneous
- Rewrote `modify_scale`'s scale modifiers.
- `texture_url` fields will now tell you whether textures are loaded.