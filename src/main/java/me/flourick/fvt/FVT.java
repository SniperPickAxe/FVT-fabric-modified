package me.flourick.fvt;

import java.util.UUID;

import org.lwjgl.glfw.GLFW;

import me.flourick.fvt.options.FVTOptions;
import me.flourick.fvt.utils.FVTVars;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.MathHelper;

public class FVT implements ModInitializer
{
	public static final boolean DEBUG = false;

	public static FVT INSTANCE;
	public static MinecraftClient MC;
	public static FVTOptions OPTIONS;
	public static final FVTVars VARS = new FVTVars();

	private KeyBinding toolBreakingOverrideKeybind;

	@Override
	public void onInitialize()
	{
		INSTANCE = this;
		MC = MinecraftClient.getInstance();
		OPTIONS = new FVTOptions();

		registerKeys();
	}

	public boolean isToolBreakingOverriden()
	{
		return toolBreakingOverrideKeybind.isPressed();
	}

	private void registerKeys()
	{
		KeyBinding fullbrightKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("Fullbright", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		KeyBinding freecamKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("Freecam", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		KeyBinding randomPlacementKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("Random Placement", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		KeyBinding entityOutlineKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("Entity Outline", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		KeyBinding autoAttackKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("Trigger Autoattack", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		toolBreakingOverrideKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("Tool Breaking Override", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_ALT, "FVT"));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if(FVT.MC.player == null && FVT.VARS.freecam) {
				// disables freecam if leaving a world
				FVT.MC.chunkCullingEnabled = true;
				FVT.VARS.freecam = false;
			}

			while(fullbrightKeybind.wasPressed()) {
				FVT.VARS.fullbright = !FVT.VARS.fullbright;
			}

			while(entityOutlineKeybind.wasPressed()) {
				FVT.VARS.entityOutline = !FVT.VARS.entityOutline;
			}

			while(autoAttackKeybind.wasPressed()) {
				FVT.OPTIONS.triggerBot = !FVT.OPTIONS.triggerBot;
			}

			while(freecamKeybind.wasPressed()) {
				FVT.VARS.freecam = !FVT.VARS.freecam;

				if(FVT.VARS.freecam && FVT.MC.player != null) {
					FVT.MC.chunkCullingEnabled = false;

					FVT.VARS.freecamPitch = FVT.MC.player.pitch;
					FVT.VARS.freecamYaw = FVT.MC.player.yaw;

					FVT.VARS.playerPitch = FVT.MC.player.pitch;
					FVT.VARS.playerYaw = FVT.MC.player.yaw;

					FVT.VARS.freecamX = FVT.VARS.prevFreecamX = FVT.MC.gameRenderer.getCamera().getPos().getX();
					FVT.VARS.freecamY = FVT.VARS.prevFreecamY = FVT.MC.gameRenderer.getCamera().getPos().getY();
					FVT.VARS.freecamZ = FVT.VARS.prevFreecamZ = FVT.MC.gameRenderer.getCamera().getPos().getZ();
				}
				else {
					FVT.MC.chunkCullingEnabled = true;

					FVT.MC.player.pitch = (float) FVT.VARS.playerPitch;
					FVT.MC.player.yaw = (float) FVT.VARS.playerYaw;

					FVT.VARS.freecamForwardSpeed = 0.0f;
					FVT.VARS.freecamUpSpeed = 0.0f;
					FVT.VARS.freecamSideSpeed = 0.0f;
				}
			}

			while(randomPlacementKeybind.wasPressed()) {
				FVT.OPTIONS.randomPlacement = !FVT.OPTIONS.randomPlacement;

				if(FVT.OPTIONS.randomPlacement) {
					FVT.MC.inGameHud.addChatMessage(MessageType.CHAT, new LiteralText("Random Block Placement is now enabled!"), UUID.fromString("00000000-0000-0000-0000-000000000000"));
				}
				else {
					FVT.MC.inGameHud.addChatMessage(MessageType.CHAT, new LiteralText("Random Block Placement is now disabled!"), UUID.fromString("00000000-0000-0000-0000-000000000000"));
				}
			}

			if(FVT.VARS.autoReconnectTicks > 0 && FVT.OPTIONS.autoReconnect) {
				if(FVT.MC.currentScreen instanceof DisconnectedScreen) {
					FVT.VARS.autoReconnectTicks -= 1;

					if(FVT.VARS.autoReconnectTicks == 0 && FVT.VARS.lastJoinedServer != null) {
						if(FVT.VARS.autoReconnectTries < FVT.OPTIONS.autoReconnectMaxTries + 1) {
							FVT.MC.disconnect();
							FVT.MC.openScreen(new ConnectScreen(new TitleScreen(), FVT.MC, FVT.VARS.lastJoinedServer));
						}
						else {
							// we get here when all tries are due
							FVT.VARS.autoReconnectTries = 0;
						}
					}
				}
				else {
					// we get here when player decides to go back to multiplayer screen before all tries are due
					FVT.VARS.autoReconnectTicks = 0;
					FVT.VARS.autoReconnectTries = 0;
				}
			}
		});

		ClientTickEvents.END_WORLD_TICK.register(clientWorld -> {
			if(FVT.OPTIONS.toolWarning) {
				ItemStack mainHandItem = FVT.MC.player.getStackInHand(Hand.MAIN_HAND);
				ItemStack offHandItem = FVT.MC.player.getStackInHand(Hand.OFF_HAND);
	
				int mainHandDurability = mainHandItem.getMaxDamage() - mainHandItem.getDamage();;
				int offHandDurability = offHandItem.getMaxDamage() - offHandItem.getDamage();

				if(mainHandItem.isDamaged() && mainHandItem != FVT.VARS.mainHandToolItemStack) {
					if(MathHelper.floor(mainHandItem.getMaxDamage() * 0.9f) < mainHandItem.getDamage() + 1 && mainHandDurability < 13) {
						FVT.VARS.toolDurability = mainHandDurability;
						FVT.VARS.toolHand = Hand.MAIN_HAND;
						FVT.VARS.resetToolWarningTicks();
					}
				}

				if(offHandItem.isDamaged() && offHandItem != FVT.VARS.offHandToolItemStack) {
					if(MathHelper.floor(offHandItem.getMaxDamage() * 0.9f) < offHandItem.getDamage() + 1 && offHandDurability < 13) {
						if(mainHandDurability == 0 || offHandDurability < mainHandDurability) {
							FVT.VARS.toolDurability = offHandDurability;
							FVT.VARS.toolHand = Hand.OFF_HAND;
							FVT.VARS.resetToolWarningTicks();
						}
					}
				}

				FVT.VARS.mainHandToolItemStack = mainHandItem;
				FVT.VARS.offHandToolItemStack = offHandItem;
			}

			if(FVT.OPTIONS.autoEat) {
				int foodLevel = FVT.MC.player.getHungerManager().getFoodLevel();

				// checks if we hungry and have food in your offhand
				if(foodLevel < 20 && FVT.MC.player.getOffHandStack().isFood()) {
					// either we on low health so eat anyway or eat hunger is low enough for the food to be fully utilized
					if(FVT.MC.player.getOffHandStack().getItem().getFoodComponent().getHunger() + foodLevel <= 20 || FVT.MC.player.getHealth() <= 12.0f) {
						FVT.MC.options.keyUse.setPressed(true);
						FVT.VARS.autoEating = true;
					}
					else if(FVT.VARS.autoEating) {
						FVT.VARS.autoEating = false;
						FVT.MC.options.keyUse.setPressed(false);
					}
				}
				else if(FVT.VARS.autoEating) {
					FVT.VARS.autoEating = false;
					FVT.MC.options.keyUse.setPressed(false);
				}
			}
		});

		ClientTickEvents.START_WORLD_TICK.register(clientWorld -> {
			if(FVT.OPTIONS.triggerBot && FVT.MC.currentScreen == null) {
				if(FVT.MC.crosshairTarget != null && FVT.MC.crosshairTarget.getType() == Type.ENTITY && FVT.MC.player.getAttackCooldownProgress(0.0f) >= 1.0f) {
					if(((EntityHitResult)FVT.MC.crosshairTarget).getEntity() instanceof LivingEntity) {
						LivingEntity livingEntity = (LivingEntity)((EntityHitResult)FVT.MC.crosshairTarget).getEntity();

						if(livingEntity.isAttackable() && livingEntity.hurtTime == 0 && livingEntity.isAlive()) {
							FVT.MC.interactionManager.attackEntity(FVT.MC.player, livingEntity);
							FVT.MC.player.swingHand(Hand.MAIN_HAND);
						}
					}
				}
			}
		});
	}
}
