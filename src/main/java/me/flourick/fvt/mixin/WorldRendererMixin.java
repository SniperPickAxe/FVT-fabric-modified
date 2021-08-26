package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import me.flourick.fvt.FVT;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Matrix4f;

/**
 * FEATURES: Freecam
 * 
 * @author Flourick
 */
@Mixin(WorldRenderer.class)
abstract class WorldRendererMixin
{
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getTeamColorValue()I", ordinal = 0))
	public int onPlayerGlow(Entity entity)
	{
		// NOTE: Does now work in Canvas as it replaces the WorldRenderer instance and it has it's own render method

		if(entity.equals(FVT.MC.player) && FVT.OPTIONS.freecam.getValueRaw()) {
			return 65280;
		}

		return entity.getTeamColorValue();
	}

	@Redirect(method = "renderClouds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/SkyProperties;getCloudsHeight()F", ordinal = 0))
	public float hijackGetCloudsHeight(SkyProperties SkyProperties, MatrixStack matrices, Matrix4f matrix4f, float f, double d, double e, double g)
	{
		if(Float.isNaN(f)) {
			return f;
		}
		else {
			return FVT.OPTIONS.cloudHeight.getValueRaw().floatValue();
		}
	}
}
