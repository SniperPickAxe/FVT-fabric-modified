package me.flourick.fvt.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;

import com.google.common.io.Files;

import org.apache.logging.log4j.LogManager;

import me.flourick.fvt.FVT;
import me.flourick.fvt.utils.Color;
import net.minecraft.client.options.BooleanOption;
import net.minecraft.client.options.CyclingOption;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public class FVTOptions
{
	private File optionsFile;

	public FVTOptions()
	{
		this.optionsFile = new File(FVT.MC.runDirectory, "fvtoptions.txt");

		init();
	}

	private void init()
	{
		loadDefaults();

		if(!optionsFile.exists()) {
			write();
		}
		else {
			read();
		}
	}

	// all the FEATURES
	public ButtonPosition buttonPosition;
	public boolean featureToggleMessages;
	public boolean crosshairStaticColor;
	public Color crosshairColor;
	public double crosshairScale;
	public boolean disableWToSprint;
	public boolean sendDeathCoordinates;
	public boolean coordinatesPosition;
	public boolean showHUDInfo;
	public boolean noToolBreaking;
	public boolean toolWarning;
	public double toolWarningScale;
	public boolean toolWarningPosition;
	public double cloudHeight;
	public boolean entityOutline;
	public boolean fullbright;
	public boolean randomPlacement;
	public boolean noNetherFog;
	public boolean noBlockBreakParticles;
	public boolean refillHand;
	public boolean autoReconnect;
	public int autoReconnectMaxTries;
	public int autoReconnectTimeout;
	public boolean autoEat;
	public boolean triggerBot;
	public boolean freecam;

	public final ButtonPosition buttonPositionDefault = ButtonPosition.RIGHT;
	public final boolean featureToggleMessagesDefault = true;
	public final boolean crosshairStaticColorDefault = true;
	public final Color crosshairColorDefault = new Color(255, 255, 255);
	public final double crosshairScaleDefault = 1.0d;
	public final boolean disableWToSprintDefault = true;
	public final boolean sendDeathCoordinatesDefault = true;
	public final boolean coordinatesPositionDefault = true;
	public final boolean showHUDInfoDefault = true;
	public final boolean noToolBreakingDefault = false;
	public final boolean toolWarningDefault = true;
	public final double toolWarningScaleDefault = 1.5d;
	public final boolean toolWarningPositionDefault = false;
	public final double cloudHeightDefault = 128.0d;
	public final boolean entityOutlineDefault = false;
	public final boolean fullbrightDefault = false;
	public final boolean randomPlacementDefault = false;
	public final boolean noNetherFogDefault = false;
	public final boolean noBlockBreakParticlesDefault = false;
	public final boolean refillHandDefault = false;
	public final boolean autoReconnectDefault = false;
	public final int autoReconnectMaxTriesDefault = 5;
	public final int autoReconnectTimeoutDefault = 5;
	public final boolean autoEatDefault = false;
	public final boolean triggerBotDefault = false;
	public final boolean freecamDefault = false;

	//region OPTIONS

	public static final BooleanOption FREECAM = new BooleanOption("fvt.feature.name.freecam",
		(gameOptions) -> {
			return FVT.OPTIONS.freecam;
		},
		(gameOptions, bool) -> {
			FVT.OPTIONS.freecam = bool;
			FVT.INSTANCE.freecamToggleCheck();
		}
	);

	public static final BooleanOption TRIGGER_BOT = new BooleanOption("fvt.feature.name.trigger_autoattack",
		(gameOptions) -> {
			return FVT.OPTIONS.triggerBot;
		},
		(gameOptions, bool) -> {
			FVT.OPTIONS.triggerBot = bool;
		}
	);

	public static final BooleanOption AUTOEAT = new BooleanOption("fvt.feature.name.autoeat",
		(gameOptions) -> {
			return FVT.OPTIONS.autoEat;
		},
		(gameOptions, bool) -> {
			FVT.OPTIONS.autoEat = bool;
		}
	);

	public static final DoubleOption AUTORECONNECT_TIMEOUT = new DoubleOption("fvt.feature.name.autoreconnect.timeout", 3.0d, 300.0d, 1.0f,
		(gameOptions) -> {
			return (double)FVT.OPTIONS.autoReconnectTimeout;
		},
		(gameOptions, timeout) -> {
			FVT.OPTIONS.autoReconnectTimeout = MathHelper.ceil(timeout);
		},
		(gameOptions, doubleOption) -> {
			return new TranslatableText("fvt.feature.name.autoreconnect.timeout").append(": " + FVT.OPTIONS.autoReconnectTimeout + "s");
		}
	);

	public static final DoubleOption AUTORECONNECT_MAX_TRIES = new DoubleOption("fvt.feature.name.autoreconnect.tries", 1.0d, 100.0d, 1.0f,
		(gameOptions) -> {
			return (double)FVT.OPTIONS.autoReconnectMaxTries;
		},
		(gameOptions, tries) -> {
			FVT.OPTIONS.autoReconnectMaxTries = MathHelper.ceil(tries);
		},
		(gameOptions, doubleOption) -> {
			return new TranslatableText("fvt.feature.name.autoreconnect.tries").append(": " + FVT.OPTIONS.autoReconnectMaxTries);
		}
	);

	public static final BooleanOption AUTORECONNECT = new BooleanOption("fvt.feature.name.autoreconnect",
		(gameOptions) -> {
			return FVT.OPTIONS.autoReconnect;
		},
		(gameOptions, bool) -> {
			FVT.OPTIONS.autoReconnect = bool;
		}
	);

	public static final BooleanOption REFILL_HAND = new BooleanOption("fvt.feature.name.refill_hand",
		(gameOptions) -> {
			return FVT.OPTIONS.refillHand;
		},
		(gameOptions, bool) -> {
			FVT.OPTIONS.refillHand = bool;
		}
	);

	public static final BooleanOption NO_BLOCK_BREAK_PARTICLES = new BooleanOption("fvt.feature.name.no_block_break_particles",
		(gameOptions) -> {
			return FVT.OPTIONS.noBlockBreakParticles;
		},
		(gameOptions, bool) -> {
			FVT.OPTIONS.noBlockBreakParticles = bool;
		}
	);

	public static final BooleanOption NO_NETHER_FOG = new BooleanOption("fvt.feature.name.no_nether_fog",
		(gameOptions) -> {
			return FVT.OPTIONS.noNetherFog;
		},
		(gameOptions, bool) -> {
			FVT.OPTIONS.noNetherFog = bool;
		}
	);

	public static final BooleanOption RANDOM_PLACEMENT = new BooleanOption("fvt.feature.name.random_placement",
		(gameOptions) -> {
			return FVT.OPTIONS.randomPlacement;
		},
		(gameOptions, bool) -> {
			FVT.OPTIONS.randomPlacement = bool;
		}
	);

	public static final BooleanOption FULLBRIGHT = new BooleanOption("fvt.feature.name.fullbright",
		(gameOptions) -> {
			return FVT.OPTIONS.fullbright;
		},
		(gameOptions, bool) -> {
			FVT.OPTIONS.fullbright = bool;
		}
	);

	public static final BooleanOption ENTITY_OUTLINE = new BooleanOption("fvt.feature.name.entity_outline",
		(gameOptions) -> {
			return FVT.OPTIONS.entityOutline;
		},
		(gameOptions, bool) -> {
			FVT.OPTIONS.entityOutline = bool;
		}
	);

	public static final DoubleOption CLOUD_HEIGHT = new DoubleOption("fvt.feature.name.cloud_height", 0.0d, 256.0d, 1.0f,
		(gameOptions) -> {
			return FVT.OPTIONS.cloudHeight;
		},
		(gameOptions, height) -> {
			FVT.OPTIONS.cloudHeight = height;
		},
		(gameOptions, doubleOption) -> {
			return new TranslatableText("fvt.feature.name.cloud_height").append(": " + BigDecimal.valueOf(FVT.OPTIONS.cloudHeight).setScale(0, RoundingMode.HALF_UP));
		}
	);

	public static final CyclingOption TOOL_WARNING_POSITION = new CyclingOption("fvt.feature.name.tool_warning.position",
		(gameOptions, integer) -> {
			FVT.OPTIONS.toolWarningPosition = !FVT.OPTIONS.toolWarningPosition;
		},
		(gameOptions, cyclingOption) -> {
			return new TranslatableText("fvt.feature.name.tool_warning.position").append(": ").append(new TranslatableText(FVT.OPTIONS.toolWarningPosition ? "fvt.feature.name.tool_warning.position.top" : "fvt.feature.name.tool_warning.position.bottom"));
		}
	);

	public static final DoubleOption TOOL_WARNING_SCALE = new DoubleOption("fvt.feature.name.tool_warning.scale", 1.0d, 4.0d, 0.01f,
		(gameOptions) -> {
			return FVT.OPTIONS.toolWarningScale;
		},
		(gameOptions, scale) -> {
			FVT.OPTIONS.toolWarningScale = scale;
		},
		(gameOptions, doubleOption) -> {
			return new TranslatableText("fvt.feature.name.tool_warning.scale").append(": " + BigDecimal.valueOf(FVT.OPTIONS.toolWarningScale * 100).setScale(0, RoundingMode.HALF_UP)).append("%");
		}
	);

	public static final BooleanOption TOOL_WARNING = new BooleanOption("fvt.feature.name.tool_warning",
		(gameOptions) -> {
			return FVT.OPTIONS.toolWarning;
		},
		(gameOptions, bool) -> {
			FVT.OPTIONS.toolWarning = bool;
		}
	);

	public static final BooleanOption NO_TOOL_BREAKING = new BooleanOption("fvt.feature.name.no_tool_breaking",
		(gameOptions) -> {
			return FVT.OPTIONS.noToolBreaking;
		},
		(gameOptions, bool) -> {
			FVT.OPTIONS.noToolBreaking = bool;
		}
	);

	public static final CyclingOption SHOW_HUD_INFO = new CyclingOption("fvt.feature.name.show_hud_info",
		(gameOptions, integer) -> {
			FVT.OPTIONS.showHUDInfo = !FVT.OPTIONS.showHUDInfo;
		},
		(gameOptions, cyclingOption) -> {
			return new TranslatableText("fvt.feature.name.show_hud_info").append(": ").append(new TranslatableText(FVT.OPTIONS.showHUDInfo ? "fvt.feature.name.show_hud_info.visible" : "fvt.feature.name.show_hud_info.hidden"));
		}
	);

	public static final CyclingOption HUD_COORDINATES_POSITION = new CyclingOption("fvt.feature.name.hud_coordinates",
		(gameOptions, integer) -> {
			FVT.OPTIONS.coordinatesPosition = !FVT.OPTIONS.coordinatesPosition;
		},
		(gameOptions, cyclingOption) -> {
			return new TranslatableText("fvt.feature.name.hud_coordinates").append(": ").append(new TranslatableText(FVT.OPTIONS.coordinatesPosition ? "fvt.feature.name.hud_coordinates.vertical" : "fvt.feature.name.hud_coordinates.horizontal"));
		}
	);

	public static final BooleanOption SEND_DEATH_COORDINATES = new BooleanOption("fvt.feature.name.send_death_coordinates",
		(gameOptions) -> {
			return FVT.OPTIONS.sendDeathCoordinates;
		},
		(gameOptions, bool) -> {
			FVT.OPTIONS.sendDeathCoordinates = bool;
		}
	);

	public static final BooleanOption DISABLE_W_TO_SPRINT = new BooleanOption("fvt.feature.name.disable_w_to_sprint",
		(gameOptions) -> {
			return FVT.OPTIONS.disableWToSprint;
		},
		(gameOptions, bool) -> {
			FVT.OPTIONS.disableWToSprint = bool;
		}
	);

	public static final DoubleOption CROSSHAIR_RED_COMPONENT = new DoubleOption("fvt.feature.name.crosshair_static_color.red_component", 0.0d, 255.0d, 1.0f,
		(gameOptions) -> {
			return (double)FVT.OPTIONS.crosshairColor.getRed();
		},
		(gameOptions, red) -> {
			FVT.OPTIONS.crosshairColor = new Color(FVT.OPTIONS.crosshairColor.getAlpha(), red.intValue(), FVT.OPTIONS.crosshairColor.getGreen(), FVT.OPTIONS.crosshairColor.getBlue());
		},
		(gameOptions, doubleOption) -> {
			return new TranslatableText("fvt.feature.name.crosshair_static_color.red_component").append(": " + FVT.OPTIONS.crosshairColor.getRed());
		}
	);

	public static final DoubleOption CROSSHAIR_GREEN_COMPONENT = new DoubleOption("fvt.feature.name.crosshair_static_color.green_component", 0.0d, 255.0d, 1.0f,
		(gameOptions) -> {
			return (double)FVT.OPTIONS.crosshairColor.getGreen();
		},
		(gameOptions, green) -> {
			FVT.OPTIONS.crosshairColor = new Color(FVT.OPTIONS.crosshairColor.getAlpha(), FVT.OPTIONS.crosshairColor.getRed(), green.intValue(), FVT.OPTIONS.crosshairColor.getBlue());
		},
		(gameOptions, doubleOption) -> {
			return new TranslatableText("fvt.feature.name.crosshair_static_color.green_component").append(": " + FVT.OPTIONS.crosshairColor.getGreen());
		}
	);

	public static final DoubleOption CROSSHAIR_BLUE_COMPONENT = new DoubleOption("fvt.feature.name.crosshair_static_color.blue_component", 0.0d, 255.0d, 1.0f,
		(gameOptions) -> {
			return (double)FVT.OPTIONS.crosshairColor.getBlue();
		},
		(gameOptions, blue) -> {
			FVT.OPTIONS.crosshairColor = new Color(FVT.OPTIONS.crosshairColor.getAlpha(), FVT.OPTIONS.crosshairColor.getRed(), FVT.OPTIONS.crosshairColor.getGreen(), blue.intValue());
		},
		(gameOptions, doubleOption) -> {
			return new TranslatableText("fvt.feature.name.crosshair_static_color.blue_component").append(": " + FVT.OPTIONS.crosshairColor.getBlue());
		}
	);

	public static final DoubleOption CROSSHAIR_SCALE = new DoubleOption("fvt.feature.name.crosshair_scale", 0.0d, 2.0d, 0.01f,
		(gameOptions) -> {
			return FVT.OPTIONS.crosshairScale;
		},
		(gameOptions, scale) -> {
			FVT.OPTIONS.crosshairScale = scale;
		},
		(gameOptions, doubleOption) -> {
			return new TranslatableText("fvt.feature.name.crosshair_scale").append(": " + BigDecimal.valueOf(FVT.OPTIONS.crosshairScale * 100).setScale(0, RoundingMode.HALF_UP)).append("%");
		}
	);

	public static final BooleanOption CROSSHAIR_STATIC_COLOR = new BooleanOption("fvt.feature.name.crosshair_static_color",
		(gameOptions) -> {
			return FVT.OPTIONS.crosshairStaticColor;
		},
		(gameOptions, bool) -> {
			FVT.OPTIONS.crosshairStaticColor = !FVT.OPTIONS.crosshairStaticColor;
		}
	);

	public static final BooleanOption FEATURE_TOGGLE_MESSAGES = new BooleanOption("fvt.feature.name.feature_toggle_messages",
		(gameOptions) -> {
			return FVT.OPTIONS.featureToggleMessages;
		},
		(gameOptions, bool) -> {
			FVT.OPTIONS.featureToggleMessages = bool;
		}
	);

	public static final CyclingOption BUTTON_POSITION = new CyclingOption("fvt.feature.name.button_position",
		(gameOptions, integer) -> {
			FVT.OPTIONS.buttonPosition = ButtonPosition.getOption(FVT.OPTIONS.buttonPosition.getId() + integer);
		},
		(gameOptions, cyclingOption) -> {
			return new TranslatableText("fvt.feature.name.button_position").append(": ").append(FVT.OPTIONS.buttonPosition.toString());
		}
	);

	//endregion

	//region ENUMS

	public enum ButtonPosition
	{
		RIGHT(0, "fvt.feature.name.button_position.right"), LEFT(1, "fvt.feature.name.button_position.left"), CENTER(2, "fvt.feature.name.button_position.center");

		private static final ButtonPosition[] BUTTON_POSITIONS = (ButtonPosition[]) Arrays.stream(values()).sorted(Comparator.comparingInt(ButtonPosition::getId)).toArray((i) -> {
			return new ButtonPosition[i];
		});

		private TranslatableText position;
		private int id;

		private ButtonPosition(int id, String positionTranslationKey)
		{
			this.position = new TranslatableText(positionTranslationKey);
			this.id = id;
		}

		@Override
		public String toString()
		{
			return position.getString();
		}

		public int getId()
		{
			return id;
		}

		public static ButtonPosition getOption(int id)
		{
			return BUTTON_POSITIONS[MathHelper.floorMod(id, BUTTON_POSITIONS.length)];
		}
	}

	//endregion

	public void write()
	{
		try(PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(optionsFile), StandardCharsets.UTF_8));) {
			printWriter.println("buttonPosition:" + this.buttonPosition.getId());
			printWriter.println("featureToggleMessages:" + this.featureToggleMessages);
			printWriter.println("crosshairStaticColor:" + this.crosshairStaticColor);
			printWriter.println("crosshairScale:" + BigDecimal.valueOf(this.crosshairScale).setScale(2, RoundingMode.HALF_UP));
			printWriter.println("crosshairColor:" + this.crosshairColor.getPacked());
			printWriter.println("disableWToSprint:" + this.disableWToSprint);
			printWriter.println("sendDeathCoordinates:" + this.sendDeathCoordinates);
			printWriter.println("coordinatesPosition:" + this.coordinatesPosition);
			printWriter.println("showHUDInfo:" + this.showHUDInfo);
			printWriter.println("noToolBreaking:" + this.noToolBreaking);
			printWriter.println("toolWarning:" + this.toolWarning);
			printWriter.println("toolWarningScale:" + BigDecimal.valueOf(this.toolWarningScale).setScale(2, RoundingMode.HALF_UP));
			printWriter.println("toolWarningPosition:" + this.toolWarningPosition);
			printWriter.println("cloudHeight:" + this.cloudHeight);
			printWriter.println("fullbright:" + this.fullbright);
			printWriter.println("entityOutline:" + this.entityOutline);
			printWriter.println("randomPlacement:" + this.randomPlacement);
			printWriter.println("noNetherFog:" + this.noNetherFog);
			printWriter.println("noBlockBreakParticles:" + this.noBlockBreakParticles);
			printWriter.println("refillHand:" + this.refillHand);
			printWriter.println("autoReconnect:" + this.autoReconnect);
			printWriter.println("autoReconnectMaxTries:" + this.autoReconnectMaxTries);
			printWriter.println("autoReconnectTimeout:" + this.autoReconnectTimeout);
			printWriter.println("autoEat:" + this.autoEat);
			printWriter.println("triggerBot:" + this.triggerBot);
		}
		catch(FileNotFoundException e) {
			LogManager.getLogger().error("Failed to write to fvtoptions.txt", e);
		}
	}

	private void read()
	{
		try(BufferedReader bufferedReader = Files.newReader(this.optionsFile, StandardCharsets.UTF_8)) {
			bufferedReader.lines().forEach((line) -> {
				String[] v = line.split(":");
				String key = v[0];
				String value = v[1];

				switch(key) {
					case "buttonPosition":
						try {
							this.buttonPosition = ButtonPosition.getOption(Integer.parseInt(value));
						}
						catch(NumberFormatException e) {
							LogManager.getLogger().warn("Skipping bad option (" + value + ")" + " for " + key);
						}

						break;

					case "featureToggleMessages":
						this.featureToggleMessages = "true".equalsIgnoreCase(value);

						break;
					
					case "crosshairStaticColor":
						this.crosshairStaticColor = "true".equalsIgnoreCase(value);
						break;

					case "crosshairScale":
						try {
							this.crosshairScale = MathHelper.clamp(Double.parseDouble(value), 0.0d, 2.0d);
						}
						catch(NumberFormatException e) {
							LogManager.getLogger().warn("Skipping bad option (" + value + ")" + " for " + key);
						}

						break;

					case "crosshairColor":
						try {
							this.crosshairColor = new Color(Integer.parseInt(value));
						}
						catch(NumberFormatException e) {
							LogManager.getLogger().warn("Skipping bad option (" + value + ")" + " for " + key);
						}
						
						break;

					case "disableWToSprint":
						this.disableWToSprint = "true".equalsIgnoreCase(value);
						break;
					
					case "sendDeathCoordinates":
						this.sendDeathCoordinates = "true".equalsIgnoreCase(value);
						break;

					case "coordinatesPosition":
						this.coordinatesPosition = "true".equalsIgnoreCase(value);
						break;

					case "showHUDInfo":
						this.showHUDInfo = "true".equalsIgnoreCase(value);
						break;

					case "noToolBreaking":
						this.noToolBreaking = "true".equalsIgnoreCase(value);
						break;

					case "toolWarning":
						this.toolWarning = "true".equalsIgnoreCase(value);
						break;

					case "toolWarningScale":
						try {
							this.toolWarningScale = MathHelper.clamp(Double.parseDouble(value), 1.0d, 4.0d);
						}
						catch(NumberFormatException e) {
							LogManager.getLogger().warn("Skipping bad option (" + value + ")" + " for " + key);
						}

						break;

					case "toolWarningPosition":
						this.toolWarningPosition = "true".equalsIgnoreCase(value);
						break;

					case "cloudHeight":
						try {
							this.cloudHeight = MathHelper.clamp(Double.parseDouble(value), 0.0d, 256.0d);
						}
						catch(NumberFormatException e) {
							LogManager.getLogger().warn("Skipping bad option (" + value + ")" + " for " + key);
						}
						break;

					case "entityOutline":
						this.entityOutline = "true".equalsIgnoreCase(value);
						break;

					case "fullbright":
						this.fullbright = "true".equalsIgnoreCase(value);
						break;

					case "randomPlacement":
						this.randomPlacement = "true".equalsIgnoreCase(value);
						break;

					case "noNetherFog":
						this.noNetherFog = "true".equalsIgnoreCase(value);
						break;

					case "noBlockBreakParticles":
						this.noBlockBreakParticles = "true".equalsIgnoreCase(value);
						break;

					case "refillHand":
						this.refillHand = "true".equalsIgnoreCase(value);
						break;
					case "autoReconnect":
						this.autoReconnect = "true".equalsIgnoreCase(value);
						break;
					case "autoReconnectMaxTries":
						try {
							this.autoReconnectMaxTries = MathHelper.clamp(Integer.parseInt(value), 1, 100);
						}
						catch(NumberFormatException e) {
							LogManager.getLogger().warn("Skipping bad option (" + value + ")" + " for " + key);
						}
						break;
					case "autoReconnectTimeout":
						try {
							this.autoReconnectTimeout = MathHelper.clamp(Integer.parseInt(value), 3, 300);
						}
						catch(NumberFormatException e) {
							LogManager.getLogger().warn("Skipping bad option (" + value + ")" + " for " + key);
						}
						break;
					case "autoEat":
						this.autoEat = "true".equalsIgnoreCase(value);
						break;
					case "triggerBot":
						this.triggerBot = "true".equalsIgnoreCase(value);
						break;
				}
			});
		}
		catch(IOException e) {
			LogManager.getLogger().error("Failed to read to FVTOptions", e);
		}
	}

	private void loadDefaults()
	{
		this.buttonPosition = buttonPositionDefault;
		this.featureToggleMessages = featureToggleMessagesDefault;
		this.crosshairStaticColor = crosshairStaticColorDefault;
		this.crosshairScale = crosshairScaleDefault;
		this.crosshairColor = crosshairColorDefault;
		this.disableWToSprint = disableWToSprintDefault;
		this.sendDeathCoordinates = sendDeathCoordinatesDefault;
		this.coordinatesPosition = coordinatesPositionDefault;
		this.showHUDInfo = showHUDInfoDefault;
		this.noToolBreaking = noToolBreakingDefault;
		this.toolWarning = toolWarningDefault;
		this.toolWarningScale = toolWarningScaleDefault;
		this.toolWarningPosition = toolWarningPositionDefault;
		this.cloudHeight = cloudHeightDefault;
		this.entityOutline = entityOutlineDefault;
		this.fullbright = fullbrightDefault;
		this.randomPlacement = randomPlacementDefault;
		this.noNetherFog = noNetherFogDefault;
		this.noBlockBreakParticles = noBlockBreakParticlesDefault;
		this.refillHand = refillHandDefault;
		this.autoReconnect = autoReconnectDefault;
		this.autoReconnectMaxTries = autoReconnectMaxTriesDefault;
		this.autoReconnectTimeout = autoReconnectTimeoutDefault;
		this.autoEat = autoEatDefault;
		this.triggerBot = triggerBotDefault;
		this.freecam = freecamDefault;
	}
}