## Apugli v1.9.0 (Fabric/Quilt 1.19)
**Power Types**
- Modified `apugli:redirect_lightning` logic to make it not cancel out in specific situations.
- Reworked how `apugli:prevent_breeding`'s bi-entity action works.
- Slightly rewrote `apugli:modify_enchantment_level` based on new modifiers.

**Bugfixes**
- Removed two accidentally left in debug loggers.
- `apugli:edible_item`'s entity action now runs after the food has been consumed.

**Miscellaneous**
- Changed config to use MidnightLib Config (this resets your current config).