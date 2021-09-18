package me.flourick.fvt.mixin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import me.flourick.fvt.FVT;
import me.flourick.fvt.utils.Color;
import me.flourick.fvt.utils.FVTButtonWidget;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;

/**
 * FEATURES: Horse Info
 * 
 * @author Flourick
 */
@Mixin(HorseScreen.class)
abstract class HorseScreenMixin extends HandledScreen<HorseScreenHandler>
{
	@Final
	@Shadow
	private HorseBaseEntity entity;

	private int FVT_buttonWidth;
	private int FVT_buttonHeight;

	List<OrderedText> FVT_tooltip;

	@Override
	protected void init()
	{
		super.init();

		Text header = new TranslatableText("fvt.feature.name.horses.button");

		this.FVT_buttonHeight = 12;
		this.FVT_buttonWidth = FVT.MC.textRenderer.getWidth(header) + 6;

		FVT_tooltip = new ArrayList<>();
		FVT_tooltip.add(new TranslatableText("fvt.feature.name.horses.button.tooltip.health", FVT_getHorseHealth()).asOrderedText());
		FVT_tooltip.add(new TranslatableText("fvt.feature.name.horses.button.tooltip.speed", FVT_getHorseSpeed()).asOrderedText());
		FVT_tooltip.add(new TranslatableText("fvt.feature.name.horses.button.tooltip.jump_height", FVT_getHorseJumpHeight()).asOrderedText());

		int baseX = ((this.width - this.backgroundWidth) / 2) + this.backgroundWidth - FVT_buttonWidth - 7;
		int baseY = ((this.height - this.backgroundHeight) / 2) + 4;

		FVTButtonWidget button = new FVTButtonWidget(baseX, baseY, FVT_buttonWidth, FVT_buttonHeight, header, null
		, (buttonWidget, matrixStack, i, j) -> {
			this.renderOrderedTooltip(matrixStack, FVT_tooltip, i, j + 8);
		}, new Color(120, 255, 255, 255), new Color(220, 255, 255, 255));
		button.active = false;

		this.addDrawableChild(button);
	}

	private String FVT_getHorseHealth()
	{
		double horseHealth = entity.getMaxHealth();
		return String.format("%s%.0f", FVT_getColorCodeByBounds(15.0D, 30.0D, horseHealth), horseHealth);
	}

	private String FVT_getHorseSpeed()
	{
		double horseSpeedBlocks = entity.getAttributes().getValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 42.157787584D;
		return String.format("%s%.02f", FVT_getColorCodeByBounds(4.742751103D, 14.228253309D, horseSpeedBlocks), horseSpeedBlocks);
	}

	private String FVT_getHorseJumpHeight()
	{
		double jumpStrength = entity.getJumpStrength();
		double jumpStrengthBlocks = -0.1817584952D * jumpStrength * jumpStrength * jumpStrength + 3.689713992D * jumpStrength * jumpStrength + 2.128599134D * jumpStrength - 0.343930367D;
		return String.format("%s%.02f", FVT_getColorCodeByBounds(1.08623D, 5.29262D, jumpStrengthBlocks), jumpStrengthBlocks);
	}

	private String FVT_getColorCodeByBounds(double min, double max, double value)
	{
		double third = (max - min) / 3.0D;

		if(value + 2*third < max) {
			return "§c";
		}
		else if(value + third < max) {
			return "§6";
		}
		else {
			return "§a";
		}
	}

	@Override
	public void renderOrderedTooltip(MatrixStack matrices, List<? extends OrderedText> lines, int x, int y)
	{
		// basically a copy paste just to adjust some annoying spacing, yeah
		if(!lines.isEmpty() && lines.size() > 2) {
			int i = 0;
			Iterator<? extends OrderedText> linesIterator = lines.iterator();

			while(linesIterator.hasNext()) {
				OrderedText orderedText = (OrderedText)linesIterator.next();
				int j = this.textRenderer.getWidth(orderedText);
				if (j > i) {
					i = j;
				}
			}

			int k = x + 12;
			int l = y - 12;
			int n = 8;
			if(lines.size() > 1) {
				n += 2 + (lines.size() - 1) * 10;
			}

			if(k + i > this.width) {
				k -= 28 + i;
			}

			if(l + n + 6 > this.height) {
				l = this.height - n - 6;
			}

			matrices.push();

			float f = this.itemRenderer.zOffset;
			this.itemRenderer.zOffset = 400.0F;

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			Matrix4f matrix4f = matrices.peek().getModel();
			fillGradient(matrix4f, bufferBuilder, k - 3, l - 4, k + i + 3, l - 3, 400, -267386864, -267386864);
			fillGradient(matrix4f, bufferBuilder, k - 3, l + n + 3, k + i + 3, l + n + 4, 400, -267386864, -267386864);
			fillGradient(matrix4f, bufferBuilder, k - 3, l - 3, k + i + 3, l + n + 3, 400, -267386864, -267386864);
			fillGradient(matrix4f, bufferBuilder, k - 4, l - 3, k - 3, l + n + 3, 400, -267386864, -267386864);
			fillGradient(matrix4f, bufferBuilder, k + i + 3, l - 3, k + i + 4, l + n + 3, 400, -267386864, -267386864);
			fillGradient(matrix4f, bufferBuilder, k - 3, l - 3 + 1, k - 3 + 1, l + n + 3 - 1, 400, 1347420415, 1344798847);
			fillGradient(matrix4f, bufferBuilder, k + i + 2, l - 3 + 1, k + i + 3, l + n + 3 - 1, 400, 1347420415, 1344798847);
			fillGradient(matrix4f, bufferBuilder, k - 3, l - 3, k + i + 3, l - 3 + 1, 400, 1347420415, 1347420415);
			fillGradient(matrix4f, bufferBuilder, k - 3, l + n + 2, k + i + 3, l + n + 3, 400, 1344798847, 1344798847);
			RenderSystem.enableDepthTest();
			RenderSystem.disableTexture();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			bufferBuilder.end();
			BufferRenderer.draw(bufferBuilder);
			RenderSystem.disableBlend();
			RenderSystem.enableTexture();
			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
			matrices.translate(0.0D, 0.0D, 400.0D);

			for(int s = 0; s < lines.size(); ++s) {
				OrderedText orderedText2 = (OrderedText)lines.get(s);
				if(orderedText2 != null) {
					this.textRenderer.draw(orderedText2, (float)k, (float)l, -1, true, matrix4f, immediate, false, 0, 15728880);
				}

				l += 10;
			}

			immediate.draw();
			matrices.pop();

			this.itemRenderer.zOffset = f;
		}
		else {
			super.renderOrderedTooltip(matrices, lines, x, y);
		}
	}

	public HorseScreenMixin(HorseScreenHandler handler, PlayerInventory inventory, Text title) { super(handler, inventory, title); } // IGNORED
}