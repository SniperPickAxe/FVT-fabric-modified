package me.flourick.fvt.mixin;

import me.flourick.fvt.FVT;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookWidget.class)
public class RecipeBookWidgetMixin
{
	@Shadow
	private RecipeBookResults recipesArea;

	@Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;clickRecipe(ILnet/minecraft/recipe/Recipe;Z)V"))
	private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> info)
	{
		// schedule autocrafting on ctrl down and valid possible recipe
		if(Screen.hasControlDown() && recipesArea.getLastClickedResults().isCraftable(recipesArea.getLastClickedRecipe())) {
			FVT.VARS.shouldAutocraft = true;
			FVT.VARS.autocraftRecipe = recipesArea.getLastClickedRecipe();
		}
		else if(!Screen.hasControlDown()) {
			FVT.VARS.shouldAutocraft = false;
			FVT.VARS.autocraftRecipe = null;
		}
	}
}
