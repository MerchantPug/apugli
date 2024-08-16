## Bugfixes
- Fixed `damage_nearby_x_hit` power type running attacker, target bientity conditions without an attacker. ([toomanyorigins#87](https://github.com/MerchantPug/toomanyorigins/issues/87))
- Backported fix for a crash relating to explosions. #62
- [FORGE] Fixed `modify_breeding_cooldown` not functioning properly. #79
- [FORGE] Fixed `key_pressed` entity condition not being persistent across dimensions.
- [FORGE] Fixed `hits_on_target` not updating.
- Fixed the custom projectile power/action modifying the shooter's tag instead of the projectile's tag with the `tag` field.

## Miscellaneous
- Updated Mixin Extras to 0.2.1.
- Updated Calio and Apoli artifacts to use Ladysnake Maven instead of JitPack.
- Can now run on 1.19-1.19.2.