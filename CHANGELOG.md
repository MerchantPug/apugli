## Power Types
- Added `easing` and `priority` fields to `modify_scale`.

## Bugfixes
- Fixed `modify_scale` not forcing an update for scales that don't update frequently.
- Fixed a crash upon having an invalid scale type in the `scale_type` or `scale_types` fields in the `modify_scale` power.
- Fixed `modify_scale` not being removed when it should be.
- Fixed `modify_scale`s with `delay` fields not getting exact scale values when changing into a non divisible value.
- Made `modify_scale` `delay` smoothing far more stable.

## Miscellaneous
- Rewrote `modify_scale`'s scale modifiers.