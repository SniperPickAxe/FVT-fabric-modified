package me.flourick.fvt.mixin;

import net.minecraft.client.render.SkyProperties;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import me.flourick.fvt.FVT;

/**
 * <p>
 * FEATURES: Cloud Height
 * </p>
 * 
 * @author Flourick
 */
@Mixin(SkyProperties.class)
abstract class SkyPropertiesMixin
{
	@Final
	@Shadow
	private float cloudsHeight;

	@Overwrite
	public float getCloudsHeight()
	{
		if(Float.isNaN(cloudsHeight)) {
			return cloudsHeight;
		}
		else {
			return FVT.OPTIONS.cloudHeight.getValueRaw().floatValue();
		}
	}	
}
