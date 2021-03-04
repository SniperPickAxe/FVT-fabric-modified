package me.flourick.fvt.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.flourick.fvt.FVT;
import me.flourick.fvt.settings.FVTSettingsScreen;

@Mixin(OptionsScreen.class)
abstract class OptionsScreenMixin extends Screen
{
	@Final
	@Shadow
	private Screen parent;

	@Final
	@Shadow
	private GameOptions settings;

	// IGNORED
	public OptionsScreenMixin(Screen parent, GameOptions gameOptions) { super(new TranslatableText("options.title", new Object[0])); }

	@Inject(method = "init", at = @At("RETURN"))
	private void onInit(CallbackInfo info)
	{
		int x, y, l;

		switch(FVT.OPTIONS.buttonPosition.getValueAsString()) {
			case "2": // CENTER
				x = this.width / 2 - 155;
				y = this.height / 6 + 24 - 6;
				l = 310;
				break;

			case "1": // LEFT
				x = this.width / 2 - 155;
				y = this.height / 6 + 24 - 6;
				l = 150;
				break;

			default:  // RIGHT
				x = this.width / 2 + 5;
				y = this.height / 6 + 24 - 6;
				l = 150;
				break;
		}

		this.addButton(new ButtonWidget(x, y, l, 20, new LiteralText("FVT..."), (buttonWidget) -> {
			this.client.openScreen(new FVTSettingsScreen(this));
		}));
	}
}
