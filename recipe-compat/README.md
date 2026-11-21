# Stormforge EMI compatibility fix

Client-only compatibility add-on for Stormforge 2.3.0's bundled EMI 1.1.24 and
TooManyRecipeViewers 0.9.2 (JEI API 15.62.0.219).

Opening a recipe while in a crafting-table/container screen calls JEI's recipe
transfer calculation through EMI's JEMI slot wrapper. That wrapper lacks the
newer interface methods, producing:

```
AbstractMethodError: JemiRecipeSlot ... getAllIngredientsList()
```

This mixin provides `getAllIngredientsList()`, `getDisplayedIngredients()`, and
`getTagKey()`. It preserves all of JEMI's ingredient alternatives for recipe
filling and does not invent a tag when JEMI has no authoritative JEI group tag.
It changes no recipes, items, worlds, or server behavior. The client-only mixin
is limited by mod dependency ranges to EMI 1.1.24 and TMRV 0.9.2.

Build with JDK 17 from this directory:

```
../forge/gradlew build
```

Install the resulting JAR into the same Modrinth instance's mods folder and
restart Minecraft. A running game cannot load newly added mods. Do not stop a
shared world without saving and warning connected players.

Validation so far: Forge release build, two ingredient-alternative/empty-slot
unit tests, package metadata and method-descriptor checks. The patched instance reached the title screen without a patch dependency or
mixin error. The original crafting-table interaction still needs in-game verification.

Upstream report: https://github.com/emilyploszaj/emi/issues/1264
