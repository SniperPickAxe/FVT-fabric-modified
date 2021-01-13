package me.flourick.fvt.utils;

import me.flourick.fvt.FVT;
import me.flourick.fvt.options.FVTOptions;
import me.flourick.fvt.options.SpacerOption;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class FVTSettingsScreen extends GameOptionsScreen
{
	private ButtonListWidget list;

	public FVTSettingsScreen(Screen parent, GameOptions gameOptions)
	{
		super(parent, gameOptions, new LiteralText("FVT Options"));
	}

	protected void init()
	{
		this.list = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
		this.list.addAll(new Option[] {FVTOptions.BUTTON_POSITION, FVTOptions.FEATURE_TOGGLE_MESSAGES});
		this.list.addSingleOptionEntry(new SpacerOption("Crosshair"));
		this.list.addAll(new Option[] {FVTOptions.CROSSHAIR_STATIC_COLOR, FVTOptions.CROSSHAIR_SCALE});
		this.list.addSingleOptionEntry(FVTOptions.CROSSHAIR_RED_COMPONENT);
		this.list.addSingleOptionEntry(FVTOptions.CROSSHAIR_GREEN_COMPONENT);
		this.list.addSingleOptionEntry(FVTOptions.CROSSHAIR_BLUE_COMPONENT);
		this.list.addSingleOptionEntry(new SpacerOption("HUD")); 
		this.list.addAll(new Option[] {FVTOptions.SHOW_HUD_INFO, FVTOptions.HUD_COORDINATES_POSITION});
		this.list.addSingleOptionEntry(new SpacerOption("Tools"));
		this.list.addAll(new Option[] {FVTOptions.NO_TOOL_BREAKING, FVTOptions.TOOL_WARNING});
		this.list.addAll(new Option[] {FVTOptions.TOOL_WARNING_POSITION, FVTOptions.TOOL_WARNING_SCALE});
		this.list.addSingleOptionEntry(new SpacerOption("Render"));
		this.list.addAll(new Option[] {FVTOptions.NO_NETHER_FOG, FVTOptions.NO_BLOCK_BREAK_PARTICLES});
		this.list.addSingleOptionEntry(FVTOptions.CLOUD_HEIGHT);
		this.list.addAll(new Option[] {FVTOptions.FULLBRIGHT, FVTOptions.ENTITY_OUTLINE});
		this.list.addSingleOptionEntry(new SpacerOption("Auto"));
		this.list.addAll(new Option[] {FVTOptions.AUTORECONNECT, FVTOptions.AUTORECONNECT_MAX_TRIES});
		this.list.addSingleOptionEntry(FVTOptions.AUTORECONNECT_TIMEOUT);
		this.list.addAll(new Option[] {FVTOptions.AUTOEAT, FVTOptions.TRIGGER_BOT});
		this.list.addSingleOptionEntry(new SpacerOption("Other"));
		this.list.addAll(new Option[] {FVTOptions.DISABLE_W_TO_SPRINT, FVTOptions.SEND_DEATH_COORDINATES, FVTOptions.RANDOM_PLACEMENT, FVTOptions.REFILL_HAND});
		this.children.add(this.list);
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, (buttonWidget) -> {
			FVT.OPTIONS.write();
			this.client.openScreen(this.parent);
		}));
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(matrixStack);
		this.list.render(matrixStack, mouseX, mouseY, delta);
		drawCenteredString(matrixStack, this.textRenderer, this.title.asString(), this.width / 2, 12, Color.WHITE.getPacked());

		super.render(matrixStack, mouseX, mouseY, delta);
	}

	@Override
	public void removed()
	{
		FVT.OPTIONS.write();
	}
}
