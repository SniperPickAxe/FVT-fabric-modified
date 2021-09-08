package me.flourick.fvt;

import java.util.UUID;

import org.lwjgl.glfw.GLFW;

import me.flourick.fvt.settings.FVTOptions;
import me.flourick.fvt.settings.FVTSettingsScreen;
import me.flourick.fvt.utils.CrowdinTranslations;
import me.flourick.fvt.utils.FVTVars;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.MessageType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.MathHelper;

/**
 * Mod initializer, registers keybinds & their listeners and several tick callbacks.
 * 
 * @author Flourick, jtenner
 */
public class FVT implements ClientModInitializer
{
	public static FVT INSTANCE;
	public static MinecraftClient MC;
	public static FVTOptions OPTIONS;
	public static final FVTVars VARS = new FVTVars();

	private KeyBinding toolBreakingOverrideKeybind;

	@Override
	public void onInitializeClient()
	{
		INSTANCE = this;
		MC = MinecraftClient.getInstance();
		OPTIONS = new FVTOptions();

		// downloads all available translations from Crowdin
		CrowdinTranslations.download();

		registerKeys();
		registerCallbacks();
	}

	public boolean isToolBreakingOverriden()
	{
		return toolBreakingOverrideKeybind.isPressed();
	}

	private void registerKeys()
	{
		KeyBinding openSettingsMenuKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.options.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		toolBreakingOverrideKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.feature.name.tool_breaking_override", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_ALT, "FVT"));
		KeyBinding fullbrightKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.feature.name.fullbright", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		KeyBinding freecamKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.feature.name.freecam", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		KeyBinding randomPlacementKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.feature.name.random_placement", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		KeyBinding entityOutlineKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.feature.name.entity_outline", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		KeyBinding autoAttackKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.feature.name.trigger_autoattack", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		KeyBinding placementLockKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.feature.name.placement_lock", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		KeyBinding invisibleOffhandKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.feature.name.invisible_offhand", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));

		ClientTickEvents.END_CLIENT_TICK.register(client ->
		{
			while(openSettingsMenuKeybind.wasPressed()) {
				FVT.MC.setScreen(new FVTSettingsScreen(FVT.MC.currentScreen));
			}

			while(fullbrightKeybind.wasPressed()) {
				FVT.OPTIONS.fullbright.toggle();

				if(FVT.OPTIONS.featureToggleMessages.getValueRaw()) {
					if(FVT.OPTIONS.fullbright.getValueRaw()) {
						FVT.MC.inGameHud.addChatMessage(MessageType.CHAT, new TranslatableText("fvt.chat_messages_prefix", new TranslatableText("fvt.feature.enabled", new TranslatableText("fvt.feature.name.fullbright"))), UUID.fromString("00000000-0000-0000-0000-000000000000"));
					}
					else {
						FVT.MC.inGameHud.addChatMessage(MessageType.CHAT, new TranslatableText("fvt.chat_messages_prefix", new TranslatableText("fvt.feature.disabled", new TranslatableText("fvt.feature.name.fullbright"))), UUID.fromString("00000000-0000-0000-0000-000000000000"));
					}
				}
			}

			while(entityOutlineKeybind.wasPressed()) {
				FVT.OPTIONS.entityOutline.toggle();

				if(FVT.OPTIONS.featureToggleMessages.getValueRaw()) {
					if(FVT.OPTIONS.entityOutline.getValueRaw()) {
						FVT.MC.inGameHud.addChatMessage(MessageType.CHAT, new TranslatableText("fvt.chat_messages_prefix", new TranslatableText("fvt.feature.enabled", new TranslatableText("fvt.feature.name.entity_outline"))), UUID.fromString("00000000-0000-0000-0000-000000000000"));
					}
					else {
						FVT.MC.inGameHud.addChatMessage(MessageType.CHAT, new TranslatableText("fvt.chat_messages_prefix", new TranslatableText("fvt.feature.disabled", new TranslatableText("fvt.feature.name.entity_outline"))), UUID.fromString("00000000-0000-0000-0000-000000000000"));
					}
				}
			}

			while(autoAttackKeybind.wasPressed()) {
				FVT.OPTIONS.triggerBot.toggle();

				if(FVT.OPTIONS.featureToggleMessages.getValueRaw()) {
					if(FVT.OPTIONS.triggerBot.getValueRaw()) {
						FVT.MC.inGameHud.addChatMessage(MessageType.CHAT, new TranslatableText("fvt.chat_messages_prefix", new TranslatableText("fvt.feature.enabled", new TranslatableText("fvt.feature.name.trigger_autoattack"))), UUID.fromString("00000000-0000-0000-0000-000000000000"));
					}
					else {
						FVT.MC.inGameHud.addChatMessage(MessageType.CHAT, new TranslatableText("fvt.chat_messages_prefix", new TranslatableText("fvt.feature.disabled", new TranslatableText("fvt.feature.name.trigger_autoattack"))), UUID.fromString("00000000-0000-0000-0000-000000000000"));
					}
				}
			}

			while(freecamKeybind.wasPressed()) {
				FVT.OPTIONS.freecam.toggle();

				if(FVT.OPTIONS.featureToggleMessages.getValueRaw()) {
					if(FVT.OPTIONS.freecam.getValueRaw()) {
						FVT.MC.inGameHud.addChatMessage(MessageType.CHAT, new TranslatableText("fvt.chat_messages_prefix", new TranslatableText("fvt.feature.enabled", new TranslatableText("fvt.feature.name.freecam"))), UUID.fromString("00000000-0000-0000-0000-000000000000"));
					}
					else {
						FVT.MC.inGameHud.addChatMessage(MessageType.CHAT, new TranslatableText("fvt.chat_messages_prefix", new TranslatableText("fvt.feature.disabled", new TranslatableText("fvt.feature.name.freecam"))), UUID.fromString("00000000-0000-0000-0000-000000000000"));
					}
				}
			}

			while(randomPlacementKeybind.wasPressed()) {
				FVT.OPTIONS.randomPlacement.toggle();

				if(FVT.OPTIONS.featureToggleMessages.getValueRaw()) {
					if(FVT.OPTIONS.randomPlacement.getValueRaw()) {
						FVT.MC.inGameHud.addChatMessage(MessageType.CHAT, new TranslatableText("fvt.chat_messages_prefix", new TranslatableText("fvt.feature.enabled", new TranslatableText("fvt.feature.name.random_placement"))), UUID.fromString("00000000-0000-0000-0000-000000000000"));
					}
					else {
						FVT.MC.inGameHud.addChatMessage(MessageType.CHAT, new TranslatableText("fvt.chat_messages_prefix", new TranslatableText("fvt.feature.disabled", new TranslatableText("fvt.feature.name.random_placement"))), UUID.fromString("00000000-0000-0000-0000-000000000000"));
					}
				}
			}

			while(placementLockKeybind.wasPressed()) {
				FVT.OPTIONS.placementLock.toggle();

				if(FVT.OPTIONS.featureToggleMessages.getValueRaw()) {
					if(FVT.OPTIONS.placementLock.getValueRaw()) {
						FVT.MC.inGameHud.addChatMessage(MessageType.CHAT, new TranslatableText("fvt.chat_messages_prefix", new TranslatableText("fvt.feature.enabled", new TranslatableText("fvt.feature.name.placement_lock"))), UUID.fromString("00000000-0000-0000-0000-000000000000"));
					}
					else {
						FVT.MC.inGameHud.addChatMessage(MessageType.CHAT, new TranslatableText("fvt.chat_messages_prefix", new TranslatableText("fvt.feature.disabled", new TranslatableText("fvt.feature.name.placement_lock"))), UUID.fromString("00000000-0000-0000-0000-000000000000"));
					}
				}
			}

			while(invisibleOffhandKeybind.wasPressed()) {
				FVT.OPTIONS.invisibleOffhand.toggle();

				if(FVT.OPTIONS.featureToggleMessages.getValueRaw()) {
					if(FVT.OPTIONS.invisibleOffhand.getValueRaw()) {
						FVT.MC.inGameHud.addChatMessage(MessageType.CHAT, new TranslatableText("fvt.chat_messages_prefix", new TranslatableText("fvt.feature.enabled", new TranslatableText("fvt.feature.name.invisible_offhand"))), UUID.fromString("00000000-0000-0000-0000-000000000000"));
					}
					else {
						FVT.MC.inGameHud.addChatMessage(MessageType.CHAT, new TranslatableText("fvt.chat_messages_prefix", new TranslatableText("fvt.feature.disabled", new TranslatableText("fvt.feature.name.invisible_offhand"))), UUID.fromString("00000000-0000-0000-0000-000000000000"));
					}
				}
			}
		});
	}

	private void registerCallbacks()
	{
		ClientTickEvents.END_CLIENT_TICK.register(client ->
		{
			if(FVT.MC.player == null && FVT.OPTIONS.freecam.getValueRaw()) {
				// disables freecam if leaving a world
				FVT.OPTIONS.freecam.setValueRaw(false);
			}

			if(FVT.VARS.autoReconnectTicks > 0 && FVT.OPTIONS.autoReconnect.getValueRaw()) {
				if(FVT.MC.currentScreen instanceof DisconnectedScreen) {
					FVT.VARS.autoReconnectTicks -= 1;

					if(FVT.VARS.autoReconnectTicks == 0 && FVT.VARS.lastJoinedServer != null) {
						if(FVT.VARS.autoReconnectTries < FVT.OPTIONS.autoReconnectMaxTries.getValueAsInteger() + 1) {
							ConnectScreen.connect(new TitleScreen(), client, ServerAddress.parse(FVT.VARS.lastJoinedServer.address), FVT.VARS.lastJoinedServer);
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

		ClientTickEvents.END_WORLD_TICK.register(clientWorld ->
		{
			if(FVT.OPTIONS.toolWarning.getValueRaw()) {
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

			if(FVT.OPTIONS.autoEat.getValueRaw()) {
				int foodLevel = FVT.MC.player.getHungerManager().getFoodLevel();

				// checks if we hungry and have food in your offhand
				if(foodLevel < 20 && FVT.MC.player.getOffHandStack().isFood()) {
					// either we low on health so eat anyway or hunger is low enough for the food to be fully utilized
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

		// the entirety of TRIGGER BOT
		ClientTickEvents.START_WORLD_TICK.register(clientWorld ->
		{
			if(FVT.OPTIONS.triggerBot.getValueRaw() && FVT.MC.currentScreen == null) {
				if(FVT.MC.crosshairTarget != null && FVT.MC.crosshairTarget.getType() == Type.ENTITY && FVT.MC.player.getAttackCooldownProgress(0.0f) >= 1.0f) {
					if(((EntityHitResult)FVT.MC.crosshairTarget).getEntity() instanceof LivingEntity) {
						LivingEntity livingEntity = (LivingEntity)((EntityHitResult)FVT.MC.crosshairTarget).getEntity();

						if(livingEntity.isAttackable() && (livingEntity.hurtTime == 0 || livingEntity instanceof WitherEntity) && livingEntity.isAlive() && !(livingEntity instanceof PlayerEntity)) {
							FVT.MC.interactionManager.attackEntity(FVT.MC.player, livingEntity);
							FVT.MC.player.swingHand(Hand.MAIN_HAND);
						}
					}
				}
			}
		});
	}
}
