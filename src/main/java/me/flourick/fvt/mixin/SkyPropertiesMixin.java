package me.flourick.fvt.mixin;

import net.minecraft.client.render.SkyProperties;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import me.flourick.fvt.FVT;

@Mixin(SkyProperties.class)
public class SkyPropertiesMixin
{
	@Shadow
	private float cloudsHeight;

	@Overwrite
	public float getCloudsHeight()
	{
		if(Float.isNaN(cloudsHeight)) {
			return cloudsHeight;
		}
		else {
			return (float)FVT.OPTIONS.cloudHeight;
		}
	}	
}
