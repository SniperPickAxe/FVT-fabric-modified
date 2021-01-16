package me.flourick.fvt.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.CombatEventS2CPacket;

import org.apache.commons.lang3.text.WordUtils;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.flourick.fvt.FVT;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin
{
	@Inject(method = "onCombatEvent", at = @At("HEAD"))
	private void onOnCombatEvent(CombatEventS2CPacket packet, CallbackInfo info)
	{
		if(packet.type == CombatEventS2CPacket.Type.ENTITY_DIED) {
			Entity entity = FVT.MC.world.getEntityById(packet.entityId);
			
			if(entity == FVT.MC.player) {
				FVT.VARS.setLastDeathCoordinates(FVT.MC.player.getX(), FVT.MC.player.getY(), FVT.MC.player.getZ(), WordUtils.capitalize(FVT.MC.player.clientWorld.getRegistryKey().getValue().toString().split(":")[1].replace('_', ' ')));
				FVT.VARS.isAfterDeath = true;
			}
		}
	}
}
