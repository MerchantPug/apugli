## Power Types
- Added `easing`, `priority`, `delay_modifier`, and `delay_modifiers` fields to `modify_scale`.
- `modify_scale`s with `delay` and resource based `modifier` fields will now dynamically shift between scales.
- Added `limit` field to `action_on_harm` and `action_when_harmed`. if not set, this is equal to the entity's max health * 4.

## Bugfixes
- Fixed `modify_scale` not forcing an update for scales that don't update frequently.
- Fixed a crash upon having an invalid scale type in the `scale_type` or `scale_types` fields in the `modify_scale` power.
- Fixed `modify_scale` not being removed when it should be.
- Fixed `modify_scale`s with `delay` fields not getting exact scale values when changing into a non divisible value.
- Made `modify_scale` `delay` smoothing far more stable.
- Fixed `modify_scale` unintentionally resetting when swapping dimensions.
- Fixed `compare_scales` crashing when `scale_type` is specified.
- Fixed `compare_scales`s `scale_types` field not being counted.
- Fixed `step_height` power types sometimes causing entities to fly way up into the air when falling.

## Miscellaneous
- Rewrote `modify_scale`'s scale modifiers.
- `texture_url` fields will now tell you whether textures are loaded.