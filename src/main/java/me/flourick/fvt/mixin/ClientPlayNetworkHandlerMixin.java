package me.flourick.fvt.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.CombatEventS2CPacket;
import net.minecraft.network.packet.s2c.play.CraftFailedResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

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

	@Inject(method = "onEntityStatus", at = @At("RETURN"))
	private void onOnEntityStatus(EntityStatusS2CPacket packet, CallbackInfo info)
	{
		if(FVT.OPTIONS.autoTotem.getValueRaw() && packet.getStatus() == 35 && packet.getEntity(FVT.MC.player.world).equals(FVT.MC.player)) {
			ClientPlayerEntity player  = FVT.MC.player;

			int activeIdx = -1;

			// TOTEM used in main hand (main hand first as it gets priority if totem in both hands)
			if(player.getMainHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
				activeIdx = player.inventory.selectedSlot + 36;
			} // TOTEM used in offhand
			else if(player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
				activeIdx = 45;
			}

			if(activeIdx != -1) {
				int sz = player.inventory.main.size();

				for(int i = 0; i < sz; i++) {
					if(player.inventory.main.get(i).getItem() == Items.TOTEM_OF_UNDYING && i != player.inventory.selectedSlot) {
						// works by clicking on the totem first and then on the last known used totem position (either a hotbar slot or offhand)
						if(i < 9) { // hotbar
							FVT.MC.interactionManager.clickSlot(player.playerScreenHandler.syncId, i + 36, 0, SlotActionType.PICKUP, player);
						} // rest of inventory
						else {
							FVT.MC.interactionManager.clickSlot(player.playerScreenHandler.syncId, i, 0, SlotActionType.PICKUP, player);
						}
						
						FVT.MC.interactionManager.clickSlot(player.playerScreenHandler.syncId, activeIdx, 0, SlotActionType.PICKUP, player);

						break;
					}
				}
			}
		}
	}

	@Inject(method = "onInventory", at = @At("RETURN"))
	public void onOnInventory(InventoryS2CPacket packet, CallbackInfo info)
	{
		System.out.println("ONINTENVOTYS");
	}

	@Inject(method = "onScreenHandlerSlotUpdate", at = @At("RETURN"))
	private void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo info)
	{
		System.out.println("SCREENHANDLER");

		// crafting window open or inventory
		if(FVT.MC.player.currentScreenHandler instanceof CraftingScreenHandler || FVT.MC.player.currentScreenHandler instanceof PlayerScreenHandler) {
			AbstractRecipeScreenHandler<?> handler = (AbstractRecipeScreenHandler<?>) FVT.MC.player.currentScreenHandler;
			int resultSlotIdx = handler.getCraftingResultSlotIndex();

			// we should autocraft and this packet updates the crafting output slot
			if(FVT.VARS.shouldAutocraft && packet.getSlot() == resultSlotIdx && packet.getSyncId() == handler.syncId) {
				if(itemStackEqual(FVT.VARS.AutocraftRecipe.getOutput(), handler.getSlot(resultSlotIdx).getStack())) {
					FVT.MC.interactionManager.clickSlot(packet.getSyncId(), handler.getCraftingResultSlotIndex(), 0, SlotActionType.QUICK_MOVE, FVT.MC.player);

					// TODO: update inventory, somehow... +SHIFT works but not without, wtf....

					FVT.VARS.shouldAutocraft = false;
					FVT.VARS.AutocraftRecipe = null;
				}
			}
		}
	}

	private boolean itemStackEqual(ItemStack one, ItemStack two)
	{
		return one.getItem() == two.getItem() && one.getCount() == two.getCount();
	}

	@Inject(method = "onCraftFailedResponse", at = @At("RETURN"))
	private void onCraftFailedResponse(CraftFailedResponseS2CPacket packet, CallbackInfo info)
	{
		// incase the recipe is no longer available to craft
		FVT.VARS.shouldAutocraft = false;
		FVT.VARS.AutocraftRecipe = null;
	}
}
