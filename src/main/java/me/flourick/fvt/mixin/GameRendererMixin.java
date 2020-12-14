package me.flourick.fvt.mixin;

import net.minecraft.client.render.GameRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.flourick.fvt.FVT;

@Mixin(GameRenderer.class)
public class GameRendererMixin
{
	@Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void removeHandRendering(CallbackInfo info)
    {
        if(FVT.VARS.freecam) {
            info.cancel();
        }
    }
}
