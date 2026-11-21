package com.divyamk.stormforge;
import com.divyamk.stormforge.mixin.JemiRecipeSlotMixin;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
class SlotCompatibilityTest {
    private JemiRecipeSlotMixin slot(List<?> alternatives) {
        return new JemiRecipeSlotMixin() {
            @Override public Stream<?> getAllIngredients() { return alternatives.stream(); }
        };
    }
    @Test void retainsAllRecipeSubstitutesOnRepeatedQueries() {
        var slot = slot(List.of("oak", "birch", "spruce"));
        assertEquals(List.of("oak", "birch", "spruce"), slot.getAllIngredientsList());
        assertEquals(List.of("oak", "birch", "spruce"), slot.getDisplayedIngredients().toList());
        assertEquals(3, slot.getAllIngredientsList().size());
        assertTrue(slot.getTagKey().isEmpty());
    }
    @Test void handlesEmptyRecipeSlot() {
        var slot = slot(List.of());
        assertTrue(slot.getAllIngredientsList().isEmpty());
        assertEquals(0, slot.getDisplayedIngredients().count());
    }
}
