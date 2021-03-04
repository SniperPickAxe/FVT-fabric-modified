package me.flourick.fvt.mixin;

import java.util.Random;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.flourick.fvt.FVT;
import me.flourick.fvt.utils.OnScreenText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.options.AttackIndicator;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

/**
 * <p>
 * FEATURES: Tool Breaking Warning, HUD Info, Mount Hunger, Crosshair
 * </p>
 * 
 * @author Flourick
 */
@Mixin(InGameHud.class)
abstract class InGameHudMixin extends DrawableHelper
{
	@Final
	@Shadow
	private MinecraftClient client;

	@Final
	@Shadow
	private Random random;

	@Shadow
	private int ticks;

	@Shadow
	abstract LivingEntity getRiddenEntity();

	@Shadow
	abstract int getHeartCount(LivingEntity entity);

	@Shadow
	abstract int getHeartRows(int heartCount);

	@Shadow
	abstract PlayerEntity getCameraPlayer();

	@Inject(method = "tick", at = @At("HEAD"))
	private void onTick(CallbackInfo info)
	{
		FVT.VARS.tickToolWarningTicks();
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void onRender(MatrixStack matrixStack, float f, CallbackInfo info)
	{
		// renders on screen text only if not in debug or hud is hidden or if options don't say so
		if(this.client.options.debugEnabled || this.client.options.hudHidden || !FVT.OPTIONS.showHUDInfo.getValueRaw()) {
			return;
		}

		// HUD info moves to the top if chat is open
		if(FVT.MC.currentScreen instanceof ChatScreen) {
			OnScreenText.drawCoordinatesTextUpper(matrixStack);
			OnScreenText.drawLightLevelTextUpper(matrixStack);
			OnScreenText.drawPFTextUpper(matrixStack);
		}
		else {
			OnScreenText.drawCoordinatesTextLower(matrixStack);
			OnScreenText.drawLightLevelTextLower(matrixStack);
			OnScreenText.drawPFTextLower(matrixStack);
		}

		if(FVT.VARS.getToolWarningTextTicksLeft() > 0) {
			matrixStack.push();
			matrixStack.translate((double)(this.client.getWindow().getScaledWidth() / 2.0d), (double)(this.client.getWindow().getScaledHeight() / 2.0d), (double)this.getZOffset());
			matrixStack.scale(FVT.OPTIONS.toolWarningScale.getValueRaw().floatValue(), FVT.OPTIONS.toolWarningScale.getValueRaw().floatValue(), 1.0f);
			OnScreenText.drawToolWarningText(matrixStack);
			matrixStack.pop();
		}
	}

	@Redirect(method = "renderStatusBars(Lnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartRows(I)I", ordinal = 0))
	private int hijackGetHeartRows(InGameHud igHud, int heartCount)
	{
		// super rare thing but the air bubbles would overlap mount health if shown (ex. popping out of water and straight onto a horse), so yeah this fixes that
		if(this.getCameraPlayer() != null && this.getHeartCount(this.getRiddenEntity()) != 0 && FVT.MC.interactionManager.hasStatusBars()) {
			return this.getHeartRows(heartCount) + 1;
		}
		else {
			return this.getHeartRows(heartCount);
		}
	}

	@Inject(method = "renderMountHealth", at = @At("HEAD"), cancellable = true)
	private void onRenderMountHealth(MatrixStack matrices, CallbackInfo info)
	{
		PlayerEntity playerEntity = this.getCameraPlayer();
		LivingEntity livingEntity = this.getRiddenEntity();
		int riddenEntityHearts = this.getHeartCount(livingEntity);

		// custom behavior only if these, else use vanillas impl
		if(playerEntity != null && riddenEntityHearts != 0 && FVT.MC.interactionManager.hasStatusBars()) {
			int playerFoodLevel = playerEntity.getHungerManager().getFoodLevel();
			int foodRectY = FVT.MC.getWindow().getScaledHeight() - 39;
			int foodRectX = FVT.MC.getWindow().getScaledWidth() / 2 + 91;

			// PLAYER FOOD
			FVT.MC.getProfiler().swap("food");

			for(int i = 0; i < 10; ++i) {
				int currentRowY = foodRectY;
				int currentFoodX = foodRectX - i*8 - 9;
				int hungerEffectU = 16;
				int hungerEffectBackgroundU = 0;

				if(playerEntity.hasStatusEffect(StatusEffects.HUNGER)) {
					hungerEffectU += 36;
					hungerEffectBackgroundU = 13;
				}

				// hunger bar bobbing effect if no saturation
				if(playerEntity.getHungerManager().getSaturationLevel() <= 0.0F && this.ticks % (playerFoodLevel * 3 + 1) == 0) {
					currentRowY = foodRectY + (this.random.nextInt(3) - 1);
				}

				this.drawTexture(matrices, currentFoodX, currentRowY, 16 + hungerEffectBackgroundU * 9, 27, 9, 9);
				if(i*2 + 1 < playerFoodLevel) {
					this.drawTexture(matrices, currentFoodX, currentRowY, hungerEffectU + 36, 27, 9, 9);
				}

				if(i*2 + 1 == playerFoodLevel) {
					this.drawTexture(matrices, currentFoodX, currentRowY, hungerEffectU + 45, 27, 9, 9);
				}
			}

			// MOUNT HEALTH
			FVT.MC.getProfiler().swap("mountHealth");

			int subRiddenEntityHealth = riddenEntityHearts;
			int riddenEntityHealth = (int)Math.ceil((double)livingEntity.getHealth());
			int mountRectY = foodRectY - 10;
			int mountRectX = foodRectX;
			int currentRowY = mountRectY;

			for(int i = 0; subRiddenEntityHealth > 0; i += 20) {
				int riddenEntityHealthRowOffset = Math.min(subRiddenEntityHealth, 10);
				subRiddenEntityHealth -= riddenEntityHealthRowOffset;

				for(int j = 0; j < riddenEntityHealthRowOffset; ++j) {
					int currentHeartX = mountRectX - j * 8 - 9;

					this.drawTexture(matrices, currentHeartX, currentRowY, 52, 9, 9, 9);
					if(j*2 + 1 + i < riddenEntityHealth) {
						this.drawTexture(matrices, currentHeartX, currentRowY, 88, 9, 9, 9);
					}

					if(j*2 + 1 + i == riddenEntityHealth) {
						this.drawTexture(matrices, currentHeartX, currentRowY, 97, 9, 9, 9);
					}
				}

				currentRowY -= 10;
			}

			info.cancel();
		}
	}

	@Overwrite
	private void renderCrosshair(MatrixStack matrixStack)
	{
		final GameOptions gameOptions = this.client.options;

		int scaledWidth = this.client.getWindow().getScaledWidth();
		int scaledHeight = this.client.getWindow().getScaledHeight();

		if(gameOptions.getPerspective().isFirstPerson()) {
			if(gameOptions.debugEnabled && !gameOptions.hudHidden && !this.client.player.getReducedDebugInfo() && !gameOptions.reducedDebugInfo) {
				RenderSystem.pushMatrix();
				RenderSystem.translatef((float) (scaledWidth / 2), (float) (scaledHeight / 2), (float) this.getZOffset());
				Camera camera = this.client.gameRenderer.getCamera();
				RenderSystem.rotatef(camera.getPitch(), -1.0F, 0.0F, 0.0F);
				RenderSystem.rotatef(camera.getYaw(), 0.0F, 1.0F, 0.0F);
				RenderSystem.renderCrosshair(10);
				RenderSystem.popMatrix();
			}
			else {
				RenderSystem.pushMatrix();
				RenderSystem.translatef((float)(scaledWidth / 2), (float)(scaledHeight / 2), (float)this.getZOffset());

				RenderSystem.enableBlend();
				if(FVT.OPTIONS.crosshairStaticColor.getValueRaw()) {
					RenderSystem.blendColor(FVT.OPTIONS.crosshairRedComponent.getValueRawNormalized().floatValue(), FVT.OPTIONS.crosshairGreenComponent.getValueRawNormalized().floatValue(), FVT.OPTIONS.crosshairBlueComponent.getValueRawNormalized().floatValue(), 1.0f);
					RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.CONSTANT_COLOR, GlStateManager.DstFactor.ZERO, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
				}
				else {
					RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
				}
				
				RenderSystem.scaled(FVT.OPTIONS.crosshairScale.getValueRaw(), FVT.OPTIONS.crosshairScale.getValueRaw(), 1.0d);
				this.drawTexture(matrixStack, -15/2, -15/2, 0, 0, 15, 15);
				RenderSystem.disableBlend();
				RenderSystem.popMatrix();

				if(gameOptions.attackIndicator == AttackIndicator.CROSSHAIR) {
					float f = this.client.player.getAttackCooldownProgress(0.0F);
					boolean bl = false;

					if(this.client.targetedEntity != null && this.client.targetedEntity instanceof LivingEntity && f >= 1.0F) {
						bl = this.client.player.getAttackCooldownProgressPerTick() > 5.0F;
						bl &= this.client.targetedEntity.isAlive();
					}
					int j = scaledHeight / 2 - 7 + 16;
					int k = scaledWidth / 2 - 8;

					if(bl) {
						this.drawTexture(matrixStack, k, j, 68, 94, 16, 16);
					}
					else if(f < 1.0F) {
						int l = (int) (f * 17.0F);
						this.drawTexture(matrixStack, k, j, 36, 94, 16, 4);
						this.drawTexture(matrixStack, k, j, 52, 94, l, 4);
					}
				}
			}
		}
	}
}
