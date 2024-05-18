## Bugfixes
- Fixed `damage_nearby_x_hit` power type running attacker, target bientity conditions without an attacker. ([toomanyorigins#87](https://github.com/MerchantPug/toomanyorigins/issues/87))
- Fixed Explosion Sync packet desync. Which should affect the `apugli:explode` and `explosion_raycast` entity action types, and the `rocket_jump` power type.
- Fixed `key_pressed` entity condition type not functioning properly with `continuous` keys.