package me.flourick.fvt.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import me.flourick.fvt.FVT;
import me.flourick.fvt.utils.Color;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Matrix4f;

public class FVTSettingsScreen extends GameOptionsScreen
{
	private ButtonListWidget list;

	public FVTSettingsScreen(Screen parent, GameOptions gameOptions)
	{
		super(parent, gameOptions, new TranslatableText("fvt.options_title"));
	}

	protected void init()
	{
		this.list = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
		this.list.addAll(new Option[] {FVTOptions.BUTTON_POSITION, FVTOptions.FEATURE_TOGGLE_MESSAGES});
		this.list.addSingleOptionEntry(new SpacerOption("fvt.feature_category.crosshair"));
		this.list.addAll(new Option[] {FVTOptions.CROSSHAIR_STATIC_COLOR, FVTOptions.CROSSHAIR_SCALE});
		this.list.addSingleOptionEntry(FVTOptions.CROSSHAIR_RED_COMPONENT);
		this.list.addSingleOptionEntry(FVTOptions.CROSSHAIR_GREEN_COMPONENT);
		this.list.addSingleOptionEntry(FVTOptions.CROSSHAIR_BLUE_COMPONENT);
		this.list.addSingleOptionEntry(new SpacerOption("fvt.feature_category.hud")); 
		this.list.addAll(new Option[] {FVTOptions.SHOW_HUD_INFO, FVTOptions.HUD_COORDINATES_POSITION});
		this.list.addSingleOptionEntry(new SpacerOption("fvt.feature_category.tools"));
		this.list.addAll(new Option[] {FVTOptions.NO_TOOL_BREAKING, FVTOptions.TOOL_WARNING});
		this.list.addAll(new Option[] {FVTOptions.TOOL_WARNING_POSITION, FVTOptions.TOOL_WARNING_SCALE});
		this.list.addSingleOptionEntry(new SpacerOption("fvt.feature_category.render"));
		this.list.addAll(new Option[] {FVTOptions.NO_NETHER_FOG, FVTOptions.NO_BLOCK_BREAK_PARTICLES});
		this.list.addSingleOptionEntry(FVTOptions.CLOUD_HEIGHT);
		this.list.addAll(new Option[] {FVTOptions.FULLBRIGHT, FVTOptions.ENTITY_OUTLINE});
		this.list.addSingleOptionEntry(new SpacerOption("fvt.feature_category.auto"));
		this.list.addAll(new Option[] {FVTOptions.AUTORECONNECT, FVTOptions.AUTORECONNECT_MAX_TRIES});
		this.list.addSingleOptionEntry(FVTOptions.AUTORECONNECT_TIMEOUT);
		this.list.addAll(new Option[] {FVTOptions.AUTOEAT, FVTOptions.TRIGGER_BOT});
		this.list.addSingleOptionEntry(new SpacerOption("fvt.feature_category.other"));
		this.list.addAll(new Option[] {FVTOptions.DISABLE_W_TO_SPRINT, FVTOptions.SEND_DEATH_COORDINATES, FVTOptions.RANDOM_PLACEMENT, FVTOptions.REFILL_HAND, FVTOptions.FREECAM});
		this.children.add(this.list);
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, (buttonWidget) -> {
			FVT.OPTIONS.write();
			this.client.openScreen(this.parent);
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

		this.createFeatureTooltips();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(matrixStack);
		this.list.render(matrixStack, mouseX, mouseY, delta);
		drawCenteredString(matrixStack, this.textRenderer, this.title.getString(), this.width / 2, 12, Color.WHITE.getPacked());

		super.render(matrixStack, mouseX, mouseY, delta);

		List<OrderedText> tooltip = getHoveredButtonTooltip(this.list, mouseX, mouseY);
		if(tooltip != null && FVT.VARS.tooltipsActive) {
			this.renderOrderedTooltip(matrixStack, tooltip, mouseX, mouseY);
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

	private void createFeatureTooltips()
	{
		createFeatureTooltip("fvt.feature.name.button_position.tooltip", FVT.OPTIONS.buttonPositionDefault, FVTOptions.BUTTON_POSITION);
		createFeatureTooltip("fvt.feature.name.feature_toggle_messages.tooltip", FVT.OPTIONS.featureToggleMessagesDefault, FVTOptions.FEATURE_TOGGLE_MESSAGES);
		createFeatureTooltip("fvt.feature.name.crosshair_static_color.tooltip", FVT.OPTIONS.crosshairStaticColorDefault, FVTOptions.CROSSHAIR_STATIC_COLOR);
		createFeatureTooltip("fvt.feature.name.crosshair_scale.tooltip", FVT.OPTIONS.crosshairScaleDefault, FVTOptions.CROSSHAIR_SCALE);
		createFeatureTooltip("fvt.feature.name.crosshair_static_color.blue_component.tooltip", FVT.OPTIONS.crosshairColorDefault.getBlue(), FVTOptions.CROSSHAIR_BLUE_COMPONENT);
		createFeatureTooltip("fvt.feature.name.crosshair_static_color.green_component.tooltip", FVT.OPTIONS.crosshairColorDefault.getGreen(), FVTOptions.CROSSHAIR_GREEN_COMPONENT);
		createFeatureTooltip("fvt.feature.name.crosshair_static_color.red_component.tooltip", FVT.OPTIONS.crosshairColorDefault.getRed(), FVTOptions.CROSSHAIR_RED_COMPONENT);
		createFeatureTooltip("fvt.feature.name.disable_w_to_sprint.tooltip", FVT.OPTIONS.disableWToSprintDefault, FVTOptions.DISABLE_W_TO_SPRINT);
		createFeatureTooltip("fvt.feature.name.send_death_coordinates.tooltip", FVT.OPTIONS.sendDeathCoordinatesDefault, FVTOptions.SEND_DEATH_COORDINATES);
		createFeatureTooltip("fvt.feature.name.hud_coordinates.tooltip", FVT.OPTIONS.coordinatesPositionDefault, FVTOptions.HUD_COORDINATES_POSITION);
		createFeatureTooltip("fvt.feature.name.show_hud_info.tooltip", FVT.OPTIONS.showHUDInfoDefault, FVTOptions.SHOW_HUD_INFO);
		createFeatureTooltip("fvt.feature.name.no_tool_breaking.tooltip", FVT.OPTIONS.noToolBreakingDefault, FVTOptions.NO_TOOL_BREAKING);
		createFeatureTooltip("fvt.feature.name.tool_warning.tooltip", FVT.OPTIONS.toolWarningDefault, FVTOptions.TOOL_WARNING);
		createFeatureTooltip("fvt.feature.name.tool_warning.scale.tooltip", FVT.OPTIONS.toolWarningScaleDefault, FVTOptions.TOOL_WARNING_SCALE);
		createFeatureTooltip("fvt.feature.name.tool_warning.position.tooltip", FVT.OPTIONS.toolWarningPositionDefault, FVTOptions.TOOL_WARNING_POSITION);
		createFeatureTooltip("fvt.feature.name.cloud_height.tooltip", FVT.OPTIONS.cloudHeightDefault, FVTOptions.CLOUD_HEIGHT);
		createFeatureTooltip("fvt.feature.name.entity_outline.tooltip", FVT.OPTIONS.entityOutlineDefault, FVTOptions.ENTITY_OUTLINE);
		createFeatureTooltip("fvt.feature.name.fullbright.tooltip", FVT.OPTIONS.fullbrightDefault, FVTOptions.FULLBRIGHT);
		createFeatureTooltip("fvt.feature.name.random_placement.tooltip", FVT.OPTIONS.randomPlacementDefault, FVTOptions.RANDOM_PLACEMENT);
		createFeatureTooltip("fvt.feature.name.no_nether_fog.tooltip", FVT.OPTIONS.noNetherFogDefault, FVTOptions.NO_NETHER_FOG);
		createFeatureTooltip("fvt.feature.name.no_block_break_particles.tooltip", FVT.OPTIONS.noBlockBreakParticlesDefault, FVTOptions.NO_BLOCK_BREAK_PARTICLES);
		createFeatureTooltip("fvt.feature.name.refill_hand.tooltip", FVT.OPTIONS.refillHandDefault, FVTOptions.REFILL_HAND);
		createFeatureTooltip("fvt.feature.name.autoreconnect.tooltip", FVT.OPTIONS.autoReconnectDefault, FVTOptions.AUTORECONNECT);
		createFeatureTooltip("fvt.feature.name.autoreconnect.tries.tooltip", FVT.OPTIONS.autoReconnectMaxTriesDefault, FVTOptions.AUTORECONNECT_MAX_TRIES);
		createFeatureTooltip("fvt.feature.name.autoreconnect.timeout.tooltip", FVT.OPTIONS.autoReconnectTimeoutDefault, FVTOptions.AUTORECONNECT_TIMEOUT);
		createFeatureTooltip("fvt.feature.name.autoeat.tooltip", FVT.OPTIONS.autoEatDefault, FVTOptions.AUTOEAT);
		createFeatureTooltip("fvt.feature.name.trigger_autoattack.tooltip", FVT.OPTIONS.triggerBotDefault, FVTOptions.TRIGGER_BOT);
		createFeatureTooltip("fvt.feature.name.freecam.tooltip", FVT.OPTIONS.freecamDefault, FVTOptions.FREECAM);
	}

	private <T> void createFeatureTooltip(String tooltipKey, T defaultValue, Option option)
	{
		List<OrderedText> tooltip = new ArrayList<OrderedText>();
		tooltip.addAll(FVT.MC.textRenderer.wrapLines(new TranslatableText(tooltipKey), 220));
		tooltip.add(new TranslatableText("fvt.feature.default", defaultValue).formatted(Formatting.GRAY).asOrderedText());
		option.setTooltip(tooltip);
	}
}
