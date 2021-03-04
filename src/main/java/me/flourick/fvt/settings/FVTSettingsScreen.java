package me.flourick.fvt.settings;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;

import me.flourick.fvt.FVT;
import me.flourick.fvt.utils.Color;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.Option;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.OrderableTooltip;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Matrix4f;

public class FVTSettingsScreen extends Screen
{
	private final Screen parent;
	private ButtonListWidget list;

	public static Screen getScreen(Screen parent) {
        return new FVTSettingsScreen(parent);
    }

	public FVTSettingsScreen(Screen parent)
	{
		super(new TranslatableText("fvt.options_title"));
		this.parent = parent;
	}

	protected void init()
	{
		this.list = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
		this.list.addAll(new Option[] {FVT.OPTIONS.buttonPosition, FVT.OPTIONS.featureToggleMessages});
		this.list.addSingleOptionEntry(new FTVCategoryOption("fvt.feature_category.crosshair"));
		this.list.addAll(new Option[] {FVT.OPTIONS.crosshairStaticColor, FVT.OPTIONS.crosshairScale});
		this.list.addSingleOptionEntry(FVT.OPTIONS.crosshairRedComponent);
		this.list.addSingleOptionEntry(FVT.OPTIONS.crosshairGreenComponent);
		this.list.addSingleOptionEntry(FVT.OPTIONS.crosshairBlueComponent);
		this.list.addSingleOptionEntry(new FTVCategoryOption("fvt.feature_category.hud")); 
		this.list.addAll(new Option[] {FVT.OPTIONS.showHUDInfo, FVT.OPTIONS.coordinatesPosition});
		this.list.addSingleOptionEntry(new FTVCategoryOption("fvt.feature_category.tools"));
		this.list.addAll(new Option[] {FVT.OPTIONS.noToolBreaking, FVT.OPTIONS.toolWarning});
		this.list.addAll(new Option[] {FVT.OPTIONS.toolWarningPosition, FVT.OPTIONS.toolWarningScale});
		this.list.addSingleOptionEntry(new FTVCategoryOption("fvt.feature_category.render"));
		this.list.addAll(new Option[] {FVT.OPTIONS.noNetherFog, FVT.OPTIONS.noBlockBreakParticles});
		this.list.addSingleOptionEntry(FVT.OPTIONS.cloudHeight);
		this.list.addAll(new Option[] {FVT.OPTIONS.fullbright, FVT.OPTIONS.entityOutline});
		this.list.addSingleOptionEntry(new FTVCategoryOption("fvt.feature_category.auto"));
		this.list.addAll(new Option[] {FVT.OPTIONS.autoReconnect, FVT.OPTIONS.autoReconnectMaxTries});
		this.list.addSingleOptionEntry(FVT.OPTIONS.autoReconnectTimeout);
		this.list.addAll(new Option[] {FVT.OPTIONS.autoEat, FVT.OPTIONS.triggerBot, FVT.OPTIONS.autoTotem});
		this.list.addSingleOptionEntry(new FTVCategoryOption("fvt.feature_category.other"));
		this.list.addAll(new Option[] {FVT.OPTIONS.disableWToSprint, FVT.OPTIONS.sendDeathCoordinates, FVT.OPTIONS.randomPlacement, FVT.OPTIONS.refillHand, FVT.OPTIONS.freecam, FVT.OPTIONS.useDelay});
		this.children.add(this.list);
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, (buttonWidget) -> {
			FVT.OPTIONS.write();
			this.client.openScreen(parent);
		}));

		FVT.VARS.tooltipsActive = false;
		this.addButton(new ButtonWidget(this.width - 26, 6, 20, 20, new LiteralText("?"), (buttonWidget) -> {
			FVT.VARS.tooltipsActive = !FVT.VARS.tooltipsActive;

			if(FVT.VARS.tooltipsActive) {
				buttonWidget.setMessage(new LiteralText("-"));
			}
			else {
				buttonWidget.setMessage(new LiteralText("?"));
			}
		}, (buttonWidget, matrixStack, i, j) -> {
			this.renderTooltip(matrixStack, (FVT.VARS.tooltipsActive ? new TranslatableText("fvt.options.tooltips.hide") : new TranslatableText("fvt.options.tooltips.show")), i, j + 8);
		}));
	}

	

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(matrixStack);
		this.list.render(matrixStack, mouseX, mouseY, delta);
		drawCenteredText(matrixStack, this.textRenderer, this.title, this.width / 2, 12, Color.WHITE.getPacked());

		super.render(matrixStack, mouseX, mouseY, delta);

		List<OrderedText> tooltip = getHoveredButtonTooltip(mouseX, mouseY);
		if(tooltip != null && FVT.VARS.tooltipsActive) {
			this.renderOrderedTooltip(matrixStack, tooltip, mouseX, mouseY);
		}
	}

	public List<OrderedText> getHoveredButtonTooltip(int mouseX, int mouseY)
	{
		Optional<AbstractButtonWidget> button = list.getHoveredButton((double)mouseX, (double)mouseY);
		
		if(button.isPresent() && button.get() instanceof OrderableTooltip) {
		   Optional<List<OrderedText>> tooltip = ((OrderableTooltip)button.get()).getOrderedTooltip();
		   return tooltip.orElse(null);
		}
		else {
		   return null;
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
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
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
			RenderSystem.shadeModel(7425);
			bufferBuilder.end();
			BufferRenderer.draw(bufferBuilder);
			RenderSystem.shadeModel(7424);
			RenderSystem.disableBlend();
			RenderSystem.enableTexture();
			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
			matrices.translate(0.0D, 0.0D, 400.0D);

			for(int s = 0; s < lines.size(); ++s) {
				OrderedText orderedText2 = (OrderedText)lines.get(s);
				if(orderedText2 != null) {
					this.textRenderer.draw(orderedText2, (float)k, (float)l, -1, true, matrix4f, immediate, false, 0, 15728880);
				}

				if(s == lines.size() - 2) {
					l += 2;
				}

				l += 10;
			}

			immediate.draw();
			matrices.pop();
		}
		else {
			super.renderOrderedTooltip(matrices, lines, x, y);
		}
	}

	@Override
	public void removed()
	{
		FVT.OPTIONS.write();
	}

	@Override
	public void onClose()
	{
		this.client.openScreen(parent);
	}
}
