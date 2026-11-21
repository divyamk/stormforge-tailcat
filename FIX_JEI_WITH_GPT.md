# Give this prompt to your GPT / Codex

Fix “Error Rendering” when opening recipes from a crafting table in my existing Stormforge 2.3.0 Modrinth instance. If you have computer access, do the installation; otherwise guide me.

This patch targets Minecraft 1.20.1 / Forge, EMI 1.1.24, and Too Many Recipe Viewers 0.9.2. The error is `AbstractMethodError: JemiRecipeSlot ... getAllIngredientsList()`. It adds the three missing JEI slot-view methods without replacing the recipe viewers or changing recipes/worlds.

Release: https://github.com/divyamk/stormforge-tailcat/releases/tag/recipe-compat-0.1.0
JAR: https://github.com/divyamk/stormforge-tailcat/releases/download/recipe-compat-0.1.0/stormforge-emi-compat-1.20.1-0.1.0.jar
SHA-256: `69fe29874e84c53e12f9473b3355964406f64b15eb4bf351217f4c6d62517214`

1. Find my actual Stormforge instance in Modrinth. Check its Minecraft/loader and the installed EMI/TMRV versions. If they differ from the versions above, diagnose that combination instead of forcing this patch.
2. Have me save and quit Minecraft before relaunching. Do not force-kill a running world. Warn connected players if I am hosting.
3. Download the JAR above and verify the SHA-256. Add it using the instance's Content → Upload files control, or copy it into that instance's `mods` folder. If the identical fix is already installed, do not add a duplicate.
4. Preserve Tailcat, EMI, TMRV, all other mods, configs, and world saves. Do not enable the pack's disabled standalone JEI JAR alongside TMRV, and do not install the original Fabric Tailcat mod.
5. Launch the instance and verify that Forge detects `stormforge_emi_compat` version `0.1.0`, reaches the title screen, and reports no mixin application or dependency errors for the patch.
6. Have me enter the world, open a crafting table, and view the same recipe that showed “Error Rendering.” Check rendering and recipe filling, including recipes with ingredient alternatives. Inspect the new log for `AbstractMethodError`, `JemiRecipeSlot`, and `Error constructing recipe widgets` if it still fails.
7. Report exactly what was verified. Do not claim the crafting-table issue is fixed solely because the title screen loaded. If it fails, preserve the error details and disable only this add-on before further diagnosis.

This is a client-side recipe-viewer fix. Friends who see the error should install it on their own client; it changes no world content. A Minecraft restart ends Tailcat sharing, so the host needs to share again and send a fresh invitation privately. Keep invitations and account tokens out of public logs.
