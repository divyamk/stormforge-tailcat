package com.divyamk.stormforge.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/** Implements the three slot-view methods added after EMI 1.1.24's JEI target.
 * Generic signatures are erased here so this small bridge needs no JEI binary dependency.
 */
@Mixin(targets = "dev.emi.emi.jemi.impl.JemiRecipeSlot", remap = false)
public abstract class JemiRecipeSlotMixin {
    @Shadow public abstract Stream<?> getAllIngredients();

    public List<?> getAllIngredientsList() {
        return getAllIngredients().toList();
    }

    public Stream<?> getDisplayedIngredients() {
        // JEMI has one ingredient group; preserve every alternative, not just
        // the currently animated item, so recipe filling can use substitutes.
        return getAllIngredients();
    }

    public Optional<?> getTagKey() {
        // JEMI does not retain an authoritative JEI group tag. No tag is valid.
        return Optional.empty();
    }
}
