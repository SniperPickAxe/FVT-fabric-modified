package me.flourick.fvt.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.flourick.fvt.FVT;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

/**
 * FEATURES: Prevent Tool Breaking, Freecam, Use Delay, Entity Outline, AutoReconnect, Placement Lock
 * 
 * @author Flourick
 */
@Mixin(MinecraftClient.class)
abstract class MinecraftClientMixin
{
	private List<BlockPos> placementHistory = new ArrayList<>();
	private boolean xAligned = false;
	private boolean yAligned = false;
	private boolean zAligned = false;
	private int xAlign = 0;
	private int yAlign = 0;
	private int zAlign = 0;

	@Shadow
	private ServerInfo currentServerEntry;

	@Shadow
	private int itemUseCooldown;

	@Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
	private void onHandleBlockBreaking(boolean bl, CallbackInfo info)
	{
		if(FVT.OPTIONS.noToolBreaking.getValueRaw() && !FVT.INSTANCE.isToolBreakingOverriden()) {
			ItemStack mainHandItem = FVT.MC.player.getMainHandStack();

			if(mainHandItem.isDamaged()) {
				if(mainHandItem.getItem() instanceof SwordItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof TridentItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof MiningToolItem){
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
			}
		}

		if(FVT.OPTIONS.freecam.getValueRaw()) {
			info.cancel();
		}
	}

	@Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
	private void onDoAttack(CallbackInfo info)
	{
		if(FVT.OPTIONS.noToolBreaking.getValueRaw() && !FVT.INSTANCE.isToolBreakingOverriden()) {
			ItemStack mainHandItem = FVT.MC.player.getMainHandStack();

			if(mainHandItem.isDamaged()) {
				if(mainHandItem.getItem() instanceof SwordItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof TridentItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof MiningToolItem){
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
			}
		}

		if(FVT.OPTIONS.freecam.getValueRaw()) {
			info.cancel();
		}
	}

	@Inject(method = "doItemUse", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;itemUseCooldown:I", ordinal = 0, shift = At.Shift.AFTER))
	private void onDoItemUseCooldown(CallbackInfo info)
	{
		itemUseCooldown = FVT.OPTIONS.useDelay.getValueAsInteger();
	}

	@Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doItemUse()V", ordinal = 0, shift = At.Shift.BEFORE))
	private void onHandleInputEvents(CallbackInfo info)
	{
		// called when user presses the use key (aka will clear the placement history for every fresh keypress)
		placementHistory.clear();
		xAligned = false;
		yAligned = false;
		zAligned = false;
	}

	@Inject(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I", ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
	private void onDoItemUsePlaceBlock(CallbackInfo info)
	{
		// user placed three blocks so that's our cue to limit his placement!
		if(FVT.OPTIONS.placementLock.getValueRaw() && placementHistory.size() == 3) {
			BlockPos expected = ((BlockHitResult) FVT.MC.crosshairTarget).getBlockPos().offset(((BlockHitResult) FVT.MC.crosshairTarget).getSide());
			
			if((xAligned && xAlign != expected.getX()) || (yAligned && yAlign != expected.getY()) || (zAligned && zAlign != expected.getZ())) {
				info.cancel();
			}
		}
	}

	@Redirect(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;interactBlock(Lnet/minecraft/client/network/ClientPlayerEntity;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;", ordinal = 0))
	private ActionResult onDoItemUse(ClientPlayerInteractionManager manager, ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult)
	{
		ActionResult result = manager.interactBlock(player, world, hand, hitResult);

		if(result.isAccepted() && placementHistory.size() < 3) {
			placementHistory.add(hitResult.getBlockPos().offset(hitResult.getSide()));

			if(placementHistory.size() == 3) {
				if(placementHistory.get(0).getX() == placementHistory.get(1).getX() && placementHistory.get(1).getX() == placementHistory.get(2).getX()) {
					xAligned = true;
					xAlign = placementHistory.get(0).getX();
				}

				if(placementHistory.get(0).getY() == placementHistory.get(1).getY() && placementHistory.get(1).getY() == placementHistory.get(2).getY()) {
					yAligned = true;
					yAlign = placementHistory.get(0).getY();
				}

				if(placementHistory.get(0).getZ() == placementHistory.get(1).getZ() && placementHistory.get(1).getZ() == placementHistory.get(2).getZ()) {
					zAligned = true;
					zAlign = placementHistory.get(0).getZ();
				}
			}
		}

		return result;
	}

	@Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
	private void onDoItemUseBefore(CallbackInfo info)
	{
		if(FVT.OPTIONS.noToolBreaking.getValueRaw() && !FVT.INSTANCE.isToolBreakingOverriden()) {
			ItemStack mainHandItem = FVT.MC.player.getStackInHand(Hand.MAIN_HAND).isEmpty() ? null : FVT.MC.player.getStackInHand(Hand.MAIN_HAND);
			ItemStack offHandItem = FVT.MC.player.getStackInHand(Hand.OFF_HAND).isEmpty() ? null : FVT.MC.player.getStackInHand(Hand.OFF_HAND);

			if(mainHandItem != null && mainHandItem.isDamaged()) {
				if(mainHandItem.getItem() instanceof MiningToolItem){
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof CrossbowItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 10) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof TridentItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof BowItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
			}

			if(offHandItem != null && offHandItem.isDamaged()) {
				if(offHandItem.getItem() instanceof MiningToolItem) {
					if(offHandItem.getMaxDamage() - offHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(offHandItem.getItem() instanceof CrossbowItem) {
					if(offHandItem.getMaxDamage() - offHandItem.getDamage() < 10) {
						info.cancel();
					}
				}
				else if(offHandItem.getItem() instanceof TridentItem) {
					if(offHandItem.getMaxDamage() - offHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(offHandItem.getItem() instanceof BowItem) {
					if(offHandItem.getMaxDamage() - offHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
			}
		}

		if(FVT.OPTIONS.freecam.getValueRaw()) {
			info.cancel();
		}
	}

	@Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
	private void onHasOutline(Entity entity, CallbackInfoReturnable<Boolean> info)
	{
		if(FVT.OPTIONS.entityOutline.getValueRaw() && entity.getType() != EntityType.PLAYER || (FVT.OPTIONS.freecam.getValueRaw() && entity.equals(FVT.MC.player))) {
			info.setReturnValue(true);
		}
	}

	@Inject(method = "disconnect", at = @At("HEAD"), cancellable = true)
	private void onDisconnect(CallbackInfo info)
	{
		if(this.currentServerEntry != null) {
			FVT.VARS.lastJoinedServer = this.currentServerEntry;
		}
	}
}
