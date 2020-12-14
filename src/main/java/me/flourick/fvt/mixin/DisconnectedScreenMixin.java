package me.flourick.fvt.mixin;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.flourick.fvt.FVT;
import me.flourick.fvt.utils.Color;

@Mixin(DisconnectedScreen.class)
public class DisconnectedScreenMixin
{
	@Shadow
    private int reasonHeight;

	@Inject(method = "init", at = @At("HEAD"))
    private void onInit(CallbackInfo info)
    {
        if(FVT.OPTIONS.autoReconnect) {
			FVT.VARS.autoReconnectTicks = FVT.OPTIONS.autoReconnectTimeout * 20;
			FVT.VARS.autoReconnectTries += 1;
		}
	}
	
	@Inject(method = "render", at = @At("RETURN"))
    private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info)
    {
        if(FVT.OPTIONS.autoReconnect && FVT.MC.currentScreen != null && (FVT.VARS.autoReconnectTries <= FVT.OPTIONS.autoReconnectMaxTries && FVT.VARS.autoReconnectTries > 0)) {
			DrawableHelper.drawCenteredString(matrices, FVT.MC.textRenderer, "Reconnecting in: " + MathHelper.ceil(FVT.VARS.autoReconnectTicks / 20.0f) + "s (" + (FVT.OPTIONS.autoReconnectMaxTries + 1 - FVT.VARS.autoReconnectTries) + " tries left)", FVT.MC.currentScreen.width / 2, FVT.MC.currentScreen.height - this.reasonHeight / 2 - 2*FVT.MC.textRenderer.fontHeight, Color.WHITE.getPacked());
		}
    }
}
