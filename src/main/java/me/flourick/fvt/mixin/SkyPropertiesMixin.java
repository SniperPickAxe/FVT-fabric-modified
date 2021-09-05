package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.flourick.fvt.FVT;

import net.minecraft.client.render.SkyProperties;

/**
 * FEATURES: Cloud Height
 * 
 * @author Flourick
 */
@Mixin(SkyProperties.class)
abstract class SkyPropertiesMixin
{
	@Final
	@Shadow
	private float cloudsHeight;

	@Inject(method = "getCloudsHeight", at = @At("HEAD"), cancellable = true)
	private void onGetCloudsHeight(CallbackInfoReturnable<Float> info)
	{
		if(!Float.isNaN(cloudsHeight)) {
			info.setReturnValue(FVT.OPTIONS.cloudHeight.getValueRaw().floatValue());
		}
	}
}
