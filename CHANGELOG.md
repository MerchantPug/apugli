**Bugfixes**
- Fixed `apugli:modify_enchantment_damage_dealt` always being set to the `apugli:modify_enchantment_damage_taken` value. #33
- Fixed a crash involving `apugli:modify_enchantment_damage_taken`. #34
- `apugli:modify_enchantment_damage_dealt`/`apugli:modify_enchantment_damage_taken` now creates enchantment crit particles if the damage is caused by a player and the particles wouldn't normally be present.