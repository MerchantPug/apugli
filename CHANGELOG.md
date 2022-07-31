**Power Types**
- Modified `apugli:redirect_lightning` logic to make it not cancel out in specific situations.
- Reworked how `apugli:prevent_breeding`'s bi-entity action works.
- Slightly rewrote `apugli:modify_enchantment_level` based on new modifiers.
- Added new fields (`url`, `show_first_person` and `hide_player_model`) to `apugli:entity_texture_overlay`

**Bugfixes**
- Removed two accidentally left in debug loggers.
- `apugli:edible_item`'s entity action now runs after the food has been consumed.

**Miscellaneous**
- Changed config to use MidnightLib Config (this resets your current config).