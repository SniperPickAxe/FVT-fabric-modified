package me.flourick.fvt.mixin;

import net.minecraft.client.render.SkyProperties;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.flourick.fvt.FVT;

@Mixin(SkyProperties.Nether.class)
public class SkyPropertiesNetherMixin
{
	@Inject(method = "useThickFog", at = @At("HEAD"), cancellable = true)
    private void disableNetherFog(CallbackInfoReturnable<Boolean> info)
    {
        if(FVT.OPTIONS.noNetherFog) {
			info.setReturnValue(false);
		}
    }
}
