package me.flourick.fvt.mixin;

import net.minecraft.client.render.LightmapTextureManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.flourick.fvt.FVT;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin
{
	@Inject(method = "getBrightness", at = @At("HEAD"), cancellable = true)
	private void onGetBrightness(CallbackInfoReturnable<Float> info)
	{
		if(FVT.OPTIONS.fullbright) {
			info.setReturnValue(1f);
		}
	}
}
