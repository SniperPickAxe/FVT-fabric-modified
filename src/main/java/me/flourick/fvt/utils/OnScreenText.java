package me.flourick.fvt.utils;

import me.flourick.fvt.FVT;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;

public class OnScreenText
{
	public static void drawCoordinatesTextLower(MatrixStack matrixStack)
	{
		if(FVT.OPTIONS.coordinatesPosition.getValueRaw()) {
			final String X = String.format("X: %.01f", FVT.OPTIONS.freecam.getValueRaw() ? FVT.VARS.freecamX : FVT.MC.player.getX());	
			final String Y = String.format("Y: %.01f", FVT.OPTIONS.freecam.getValueRaw() ? FVT.VARS.freecamY : FVT.MC.player.getY());
			final String Z = String.format("Z: %.01f", FVT.OPTIONS.freecam.getValueRaw() ? FVT.VARS.freecamZ : FVT.MC.player.getZ());

			FVT.MC.textRenderer.drawWithShadow(matrixStack, X, 2, FVT.MC.getWindow().getScaledHeight() - 3*FVT.MC.textRenderer.fontHeight - 2, Color.WHITE.getPacked());
			FVT.MC.textRenderer.drawWithShadow(matrixStack, Y, 2, FVT.MC.getWindow().getScaledHeight() - 2*FVT.MC.textRenderer.fontHeight - 1, Color.WHITE.getPacked());
			FVT.MC.textRenderer.drawWithShadow(matrixStack, Z, 2, FVT.MC.getWindow().getScaledHeight() - FVT.MC.textRenderer.fontHeight, Color.WHITE.getPacked());
		}
		else {
			final String curLocText = String.format("XYZ: %.01f %.01f %.01f", FVT.MC.player.getX(), FVT.MC.player.getY(), FVT.MC.player.getZ());
			FVT.MC.textRenderer.drawWithShadow(matrixStack, curLocText, 2, FVT.MC.getWindow().getScaledHeight() - FVT.MC.textRenderer.fontHeight, Color.WHITE.getPacked());
		}
	}

	public static void drawCoordinatesTextUpper(MatrixStack matrixStack)
	{
		if(FVT.OPTIONS.coordinatesPosition.getValueRaw()) {
			final String X = String.format("X: %.01f", FVT.OPTIONS.freecam.getValueRaw() ? FVT.VARS.freecamX : FVT.MC.player.getX());		
			final String Y = String.format("Y: %.01f", FVT.OPTIONS.freecam.getValueRaw() ? FVT.VARS.freecamY : FVT.MC.player.getY());
			final String Z = String.format("Z: %.01f", FVT.OPTIONS.freecam.getValueRaw() ? FVT.VARS.freecamZ : FVT.MC.player.getZ());

			FVT.MC.textRenderer.drawWithShadow(matrixStack, X, 2, 2, Color.WHITE.getPacked());
			FVT.MC.textRenderer.drawWithShadow(matrixStack, Y, 2, 3 + FVT.MC.textRenderer.fontHeight, Color.WHITE.getPacked());
			FVT.MC.textRenderer.drawWithShadow(matrixStack, Z, 2, 4 + 2*FVT.MC.textRenderer.fontHeight, Color.WHITE.getPacked());
		}
		else {
			final String curLocText = String.format("XYZ: %.01f %.01f %.01f", FVT.MC.player.getX(), FVT.MC.player.getY(), FVT.MC.player.getZ());
			FVT.MC.textRenderer.drawWithShadow(matrixStack, curLocText, 2, 2, Color.WHITE.getPacked());
		}
	}

	public static void drawPFTextLower(MatrixStack matrixStack)
	{
		String direction = FVT.MC.getCameraEntity().getHorizontalFacing().asString();
		direction = direction.substring(0, 1).toUpperCase() + direction.substring(1);

		final String PFText = String.format("P: %.02f (%s)", FVT.MC.gameRenderer.getCamera().getPitch(), direction);
		FVT.MC.textRenderer.drawWithShadow(matrixStack, PFText, FVT.MC.getWindow().getScaledWidth() - FVT.MC.textRenderer.getWidth(PFText) - 1, FVT.MC.getWindow().getScaledHeight() - FVT.MC.textRenderer.fontHeight, Color.WHITE.getPacked());
	}

	public static void drawPFTextUpper(MatrixStack matrixStack)
	{
		String direction = FVT.MC.getCameraEntity().getHorizontalFacing().asString();
		direction = direction.substring(0, 1).toUpperCase() + direction.substring(1);

		final String PFText = String.format("P: %.02f (%s)", FVT.MC.gameRenderer.getCamera().getPitch(), direction);
		FVT.MC.textRenderer.drawWithShadow(matrixStack, PFText, FVT.MC.getWindow().getScaledWidth() - FVT.MC.textRenderer.getWidth(PFText) - 1, 2, Color.WHITE.getPacked());
	}

	public static void drawLightLevelTextLower(MatrixStack matrixStack)
	{
		int blockLightLevel = FVT.MC.world.getChunkManager().getLightingProvider().get(LightType.BLOCK).getLightLevel(FVT.MC.getCameraEntity().getBlockPos());

		final String curYPRText = String.format("BL: %d", blockLightLevel);
		FVT.MC.textRenderer.drawWithShadow(matrixStack, curYPRText, FVT.MC.getWindow().getScaledWidth() - FVT.MC.textRenderer.getWidth(curYPRText) - 1, FVT.MC.getWindow().getScaledHeight() - 2*FVT.MC.textRenderer.fontHeight - 1, Color.WHITE.getPacked());
	}

	public static void drawLightLevelTextUpper(MatrixStack matrixStack)
	{
		int blockLightLevel = FVT.MC.world.getChunkManager().getLightingProvider().get(LightType.BLOCK).getLightLevel(FVT.MC.getCameraEntity().getBlockPos());

		final String curYPRText = String.format("BL: %d", blockLightLevel);
		FVT.MC.textRenderer.drawWithShadow(matrixStack, curYPRText, FVT.MC.getWindow().getScaledWidth() - FVT.MC.textRenderer.getWidth(curYPRText) - 1, FVT.MC.textRenderer.fontHeight + 3, Color.WHITE.getPacked());
	}

	public static void drawToolWarningText(MatrixStack matrixStack)
	{
		// last half a second fade-out
		int alpha = MathHelper.clamp(MathHelper.ceil(25.5f * FVT.VARS.getToolWarningTextTicksLeft()), 0, 255);

		int y;
		if(FVT.OPTIONS.toolWarningPosition.getValueRaw()) {
			y = (int)((-(FVT.MC.getWindow().getScaledHeight() / 2 * 1/FVT.OPTIONS.toolWarningScale.getValueRaw())) + (2/FVT.OPTIONS.toolWarningScale.getValueRaw()));
		}
		else {
			y = (int)(((FVT.MC.getWindow().getScaledHeight() / 2 * 1/FVT.OPTIONS.toolWarningScale.getValueRaw())) - (FVT.MC.textRenderer.fontHeight + 60/FVT.OPTIONS.toolWarningScale.getValueRaw()));
		}

		final String ToolWarningText = FVT.VARS.toolHand.equals(Hand.MAIN_HAND) ? new TranslatableText("fvt.feature.name.tool_warning.text.main_hand", FVT.VARS.toolDurability).getString() : new TranslatableText("fvt.feature.name.tool_warning.text.offhand", FVT.VARS.toolDurability).getString();
		FVT.MC.textRenderer.drawWithShadow(matrixStack, ToolWarningText, (float) -(FVT.MC.textRenderer.getWidth(ToolWarningText) / 2), y, new Color(alpha, 255, 0, 0).getPacked());
	}
}