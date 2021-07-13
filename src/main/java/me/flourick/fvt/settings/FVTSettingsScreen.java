package me.flourick.fvt.settings;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;

import me.flourick.fvt.FVT;
import me.flourick.fvt.utils.Color;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.Option;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.OrderableTooltip;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Matrix4f;

public class FVTSettingsScreen extends Screen
{
	private final Screen parent;
	private ButtonListWidget list;

	private boolean tooltipsActive = false;

	public static Screen getNewScreen(Screen parent) {
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
		this.list.addAll(new Option[] {FVT.OPTIONS.autoEat, FVT.OPTIONS.triggerBot, FVT.OPTIONS.autoTotem, FVT.OPTIONS.refillHand});
		this.list.addSingleOptionEntry(new FTVCategoryOption("fvt.feature_category.placement"));
		this.list.addAll(new Option[] {FVT.OPTIONS.randomPlacement, FVT.OPTIONS.useDelay, FVT.OPTIONS.creativeBreakDelay, FVT.OPTIONS.placementLock});
		this.list.addSingleOptionEntry(new FTVCategoryOption("fvt.feature_category.other"));
		this.list.addAll(new Option[] {FVT.OPTIONS.disableWToSprint, FVT.OPTIONS.sendDeathCoordinates, FVT.OPTIONS.freecam});
		this.addSelectableChild(this.list);

		// DEFAULTS button at the top left corner
		this.addDrawableChild(new ButtonWidget(6, 6, 55, 20, new TranslatableText("fvt.options.defaults"), (buttonWidget) -> {
			FVT.OPTIONS.reset();
			this.client.setScreen(getNewScreen(parent));
		}, (buttonWidget, matrixStack, i, j) -> {
			this.renderTooltip(matrixStack, new TranslatableText("fvt.options.defaults.tooltip").formatted(Formatting.YELLOW), i, j + 8);
		}));

		// ?/- button at the top right corner
		this.addDrawableChild(new ButtonWidget(this.width - 26, 6, 20, 20, new LiteralText("?"), (buttonWidget) -> {
			tooltipsActive = !tooltipsActive;

			if(tooltipsActive) {
				buttonWidget.setMessage(new LiteralText("-"));
			}
			else {
				buttonWidget.setMessage(new LiteralText("?"));
			}
		}, (buttonWidget, matrixStack, i, j) -> {
			this.renderTooltip(matrixStack, (tooltipsActive ? new TranslatableText("fvt.options.tooltips.hide") : new TranslatableText("fvt.options.tooltips.show")), i, j + 8);
		}));

		// DONE button at the bottom
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, (buttonWidget) -> {
			FVT.OPTIONS.write();
			this.client.setScreen(parent);
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
		if(tooltip != null && tooltipsActive) {
			this.renderOrderedTooltip(matrixStack, tooltip, mouseX, mouseY);
		}
	}

	public List<OrderedText> getHoveredButtonTooltip(int mouseX, int mouseY)
	{
		Optional<ClickableWidget> button = list.getHoveredButton((double)mouseX, (double)mouseY);
      	return (button.isPresent() && button.get() instanceof OrderableTooltip ? ((OrderableTooltip)button.get()).getOrderedTooltip() : ImmutableList.of());
	 }

	@Override
	public void renderOrderedTooltip(MatrixStack matrices, List<? extends OrderedText> lines, int x, int y)
	{
		// basically a copy paste just to adjust some annoying spacing, yeah
		List<TooltipComponent> components = lines.stream().map(TooltipComponent::of).collect(Collectors.toList());

		if(!components.isEmpty()) {
			int i = 0;
			int j = components.size() == 1 ? -2 : 0;
   
			TooltipComponent tooltipComponent;
			for(Iterator<TooltipComponent> var7 = components.iterator(); var7.hasNext(); j += tooltipComponent.getHeight()) {
			   tooltipComponent = var7.next();
			   int k = tooltipComponent.getWidth(this.textRenderer);
			   if (k > i) {
				  i = k;
			   }
			}
   
			int l = x + 12;
			int m = y - 12;
			if (l + i > this.width) {
			   l -= 28 + i;
			}
   
			if (m + j + 6 > this.height) {
			   m = this.height - j - 6;
			}
   
			matrices.push();

			float f = this.itemRenderer.zOffset;
			this.itemRenderer.zOffset = 400.0F;

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			Matrix4f matrix4f = matrices.peek().getModel();
			fillGradient(matrix4f, bufferBuilder, l - 3, m - 4, l + i + 3, m - 3, 400, -267386864, -267386864);
			fillGradient(matrix4f, bufferBuilder, l - 3, m + j + 3, l + i + 3, m + j + 4, 400, -267386864, -267386864);
			fillGradient(matrix4f, bufferBuilder, l - 3, m - 3, l + i + 3, m + j + 3, 400, -267386864, -267386864);
			fillGradient(matrix4f, bufferBuilder, l - 4, m - 3, l - 3, m + j + 3, 400, -267386864, -267386864);
			fillGradient(matrix4f, bufferBuilder, l + i + 3, m - 3, l + i + 4, m + j + 3, 400, -267386864, -267386864);
			fillGradient(matrix4f, bufferBuilder, l - 3, m - 3 + 1, l - 3 + 1, m + j + 3 - 1, 400, 1347420415, 1344798847);
			fillGradient(matrix4f, bufferBuilder, l + i + 2, m - 3 + 1, l + i + 3, m + j + 3 - 1, 400, 1347420415, 1344798847);
			fillGradient(matrix4f, bufferBuilder, l - 3, m - 3, l + i + 3, m - 3 + 1, 400, 1347420415, 1347420415);
			fillGradient(matrix4f, bufferBuilder, l - 3, m + j + 2, l + i + 3, m + j + 3, 400, 1344798847, 1344798847);
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
			int t = m;
   
			int v;
			TooltipComponent tooltipComponent3;
			for(v = 0; v < components.size(); ++v) {
			   tooltipComponent3 = components.get(v);
			   tooltipComponent3.drawText(this.textRenderer, l, t, matrix4f, immediate);
			   t += tooltipComponent3.getHeight() + (v == components.size() - 2 ? 2 : 0);
			}
   
			immediate.draw();
			matrices.pop();
			t = m;
   
			for(v = 0; v < components.size(); ++v) {
			   tooltipComponent3 = components.get(v);
			   tooltipComponent3.drawItems(this.textRenderer, l, t, matrices, this.itemRenderer, 400, this.client.getTextureManager());
			   t += tooltipComponent3.getHeight() + (v == components.size() - 2 ? 2 : 0);
			}
   
			this.itemRenderer.zOffset = f;
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
		this.client.setScreen(parent);
	}
}
