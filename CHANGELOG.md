**Power Types**
- Added new `apugli:player_model_type` power type.
- Modified `apugli:redirect_lightning` logic to make it not cancel out in specific situations.
- Reworked how `apugli:prevent_breeding`'s bi-entity action works.
- Slightly rewrote `apugli:modify_enchantment_level` based on new modifiers.
- Added new fields (`url`, `show_first_person`, `hide_player_model`, `rendering_power_compatible`) to `apugli:entity_texture_overlay`
- `apugli:entity_texture_overlay` no longer renders if the player is invisible.

**Config**
- Changed config to use MidnightLib Config (this resets your current config).
- Added new File Size Limit config option, this is for use with `apugli:entity_texture_overlay`.
- Added new Connection Timeout config option, this is for use with `apugli:entity_texture_overlay`.

**Bugfixes**
- Removed two accidentally left in debug loggers.
- `apugli:edible_item`'s entity action now runs after the food has been consumed.

**Miscellaneous**
- Added warning message for when a player has more than one player model setting power active.