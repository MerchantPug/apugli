- Ported the mod to 1.20.2.

# Notes on Apoli
`edible_item` and `modify_enchantment_level` from Apugli have made it into the base mod itself, so for these, we have a special case where the content will be aliased to their Apoli counterparts.
Consider this a backwards compatible solution, not a permanent feature, you're best to swap to Apoli's implementations as they'll continue to be supported.
All code for these two power types have been removed from Apugli.
- Aliased `apugli:edible_item` to `apoli:edible_item`.
- Aliased `apugli:modify_enchantment_level` to `apoli:modify_enchantment_level`.