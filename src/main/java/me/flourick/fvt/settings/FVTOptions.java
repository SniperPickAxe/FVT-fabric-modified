package me.flourick.fvt.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.io.Files;

import org.apache.logging.log4j.LogManager;

import me.flourick.fvt.FVT;

import net.minecraft.text.TranslatableText;

public class FVTOptions
{
	private File optionsFile;
	private Map<String, SaveableValue> saveableFeatures;

	public FVTOptions()
	{
		this.optionsFile = new File(FVT.MC.runDirectory, "config/fvt.properties");
		this.saveableFeatures = new HashMap<String, SaveableValue>();

		// FEATURES CREATION
		buttonPosition = new FVTCyclingOption(
			"fvt.feature.name.button_position",
			"fvt.feature.name.button_position.tooltip",
			Arrays.asList(new TranslatableText[] {new TranslatableText("fvt.feature.name.button_position.right"), new TranslatableText("fvt.feature.name.button_position.left"), new TranslatableText("fvt.feature.name.button_position.center")})
		);
		saveableFeatures.put("buttonPosition", buttonPosition);

		featureToggleMessages = new FVTBooleanOption(
			"fvt.feature.name.feature_toggle_messages",
			"fvt.feature.name.feature_toggle_messages.tooltip",
			true
		);
		saveableFeatures.put("featureToggleMessages", featureToggleMessages);

		crosshairStaticColor = new FVTBooleanOption(
			"fvt.feature.name.crosshair_static_color",
			"fvt.feature.name.crosshair_static_color.tooltip",
			true
		);
		saveableFeatures.put("crosshairStaticColor", crosshairStaticColor);

		crosshairRedComponent = new FVTDoubleOption(
			"fvt.feature.name.crosshair_static_color.red_component",
			"fvt.feature.name.crosshair_static_color.red_component.tooltip",
			0.0d, 255.0d, 1.0d, 255.0d, FVTDoubleOption.Mode.WHOLE
		);
		saveableFeatures.put("crosshairRedComponent", crosshairRedComponent);

		crosshairGreenComponent = new FVTDoubleOption(
			"fvt.feature.name.crosshair_static_color.green_component",
			"fvt.feature.name.crosshair_static_color.green_component.tooltip",
			0.0d, 255.0d, 1.0d, 255.0d, FVTDoubleOption.Mode.WHOLE
		);
		saveableFeatures.put("crosshairGreenComponent", crosshairGreenComponent);

		crosshairBlueComponent = new FVTDoubleOption(
			"fvt.feature.name.crosshair_static_color.blue_component",
			"fvt.feature.name.crosshair_static_color.blue_component.tooltip",
			0.0d, 255.0d, 1.0d, 255.0d, FVTDoubleOption.Mode.WHOLE
		);
		saveableFeatures.put("crosshairBlueComponent", crosshairBlueComponent);

		crosshairScale = new FVTDoubleOption(
			"fvt.feature.name.crosshair_scale",
			"fvt.feature.name.crosshair_scale.tooltip",
			0.0d, 3.0d, 0.01d, 1.0d, FVTDoubleOption.Mode.PERCENT
		);
		saveableFeatures.put("crosshairScale", crosshairScale);

		disableWToSprint = new FVTBooleanOption(
			"fvt.feature.name.disable_w_to_sprint",
			"fvt.feature.name.disable_w_to_sprint.tooltip",
			true
		);
		saveableFeatures.put("disableWToSprint", disableWToSprint);

		sendDeathCoordinates = new FVTBooleanOption(
			"fvt.feature.name.send_death_coordinates",
			"fvt.feature.name.send_death_coordinates.tooltip",
			true
		);
		saveableFeatures.put("sendDeathCoordinates", sendDeathCoordinates);

		coordinatesPosition = new FVTBooleanOption(
			"fvt.feature.name.hud_coordinates",
			"fvt.feature.name.hud_coordinates.tooltip",
			true,
			new TranslatableText("fvt.feature.name.hud_coordinates.vertical"),
			new TranslatableText("fvt.feature.name.hud_coordinates.horizontal")
		);
		saveableFeatures.put("coordinatesPosition", coordinatesPosition);

		showHUDInfo = new FVTBooleanOption(
			"fvt.feature.name.show_hud_info",
			"fvt.feature.name.show_hud_info.tooltip",
			true,
			new TranslatableText("fvt.feature.name.show_hud_info.visible"),
			new TranslatableText("fvt.feature.name.show_hud_info.hidden")
		);
		saveableFeatures.put("showHUDInfo", showHUDInfo);

		noToolBreaking = new FVTBooleanOption(
			"fvt.feature.name.no_tool_breaking",
			"fvt.feature.name.no_tool_breaking.tooltip",
			false
		);
		saveableFeatures.put("noToolBreaking", noToolBreaking);

		toolWarning = new FVTBooleanOption(
			"fvt.feature.name.tool_warning",
			"fvt.feature.name.tool_warning.tooltip",
			true
		);
		saveableFeatures.put("toolWarning", toolWarning);

		toolWarningScale = new FVTDoubleOption(
			"fvt.feature.name.tool_warning.scale",
			"fvt.feature.name.tool_warning.scale.tooltip",
			0.0d, 4.0d, 0.01d, 1.5d, FVTDoubleOption.Mode.PERCENT
		);
		saveableFeatures.put("toolWarningScale", toolWarningScale);

		toolWarningPosition = new FVTBooleanOption(
			"fvt.feature.name.tool_warning.position",
			"fvt.feature.name.tool_warning.position.tooltip",
			false,
			new TranslatableText("fvt.feature.name.tool_warning.position.top"),
			new TranslatableText("fvt.feature.name.tool_warning.position.bottom")
		);
		saveableFeatures.put("toolWarningPosition", toolWarningPosition);

		cloudHeight = new FVTDoubleOption(
			"fvt.feature.name.cloud_height",
			"fvt.feature.name.cloud_height.tooltip",
			0.0d, 256.0d, 1.0d, 128.0d, FVTDoubleOption.Mode.WHOLE
		);
		saveableFeatures.put("cloudHeight", cloudHeight);

		entityOutline = new FVTBooleanOption(
			"fvt.feature.name.entity_outline",
			"fvt.feature.name.entity_outline.tooltip",
			false
		);
		saveableFeatures.put("entityOutline", entityOutline);

		fullbright = new FVTBooleanOption(
			"fvt.feature.name.fullbright",
			"fvt.feature.name.fullbright.tooltip",
			false
		);
		saveableFeatures.put("fullbright", fullbright);

		randomPlacement = new FVTBooleanOption(
			"fvt.feature.name.random_placement",
			"fvt.feature.name.random_placement.tooltip",
			false
		);
		saveableFeatures.put("randomPlacement", randomPlacement);

		noNetherFog = new FVTBooleanOption(
			"fvt.feature.name.no_nether_fog",
			"fvt.feature.name.no_nether_fog.tooltip",
			true
		);
		saveableFeatures.put("noNetherFog", noNetherFog);

		noBlockBreakParticles = new FVTBooleanOption(
			"fvt.feature.name.no_block_break_particles",
			"fvt.feature.name.no_block_break_particles.tooltip",
			false
		);
		saveableFeatures.put("noBlockBreakParticles", noBlockBreakParticles);

		refillHand = new FVTBooleanOption(
			"fvt.feature.name.refill_hand",
			"fvt.feature.name.refill_hand.tooltip",
			false
		);
		saveableFeatures.put("refillHand", refillHand);

		autoReconnect = new FVTBooleanOption(
			"fvt.feature.name.autoreconnect",
			"fvt.feature.name.autoreconnect.tooltip",
			false
		);
		saveableFeatures.put("autoReconnect", autoReconnect);

		autoReconnectMaxTries = new FVTDoubleOption(
			"fvt.feature.name.autoreconnect.tries",
			"fvt.feature.name.autoreconnect.tries.tooltip",
			0.0d, 50.0d, 1.0d, 3.0d, FVTDoubleOption.Mode.WHOLE
		);
		saveableFeatures.put("autoReconnectMaxTries", autoReconnectMaxTries);

		autoReconnectTimeout = new FVTDoubleOption(
			"fvt.feature.name.autoreconnect.timeout",
			"fvt.feature.name.autoreconnect.timeout.tooltip",
			1.0d, 300.0d, 1.0d, 5.0d, FVTDoubleOption.Mode.WHOLE
		);
		saveableFeatures.put("autoReconnectTimeout", autoReconnectTimeout);

		autoEat = new FVTBooleanOption(
			"fvt.feature.name.autoeat",
			"fvt.feature.name.autoeat.tooltip",
			false
		);
		saveableFeatures.put("autoEat", autoEat);

		triggerBot = new FVTBooleanOption(
			"fvt.feature.name.trigger_autoattack",
			"fvt.feature.name.trigger_autoattack.tooltip",
			false
		);
		saveableFeatures.put("triggerBot", triggerBot);

		freecam = new FVTBooleanOption(
			"fvt.feature.name.freecam",
			"fvt.feature.name.freecam.tooltip",
			false
		);
		//saveableFeatures.put("freecam", freecam);

		autoTotem = new FVTBooleanOption(
			"fvt.feature.name.autototem",
			"fvt.feature.name.autototem.tooltip",
			false
		);
		saveableFeatures.put("autoTotem", autoTotem);

		init();
	}

	private void init()
	{
		if(!optionsFile.exists()) {
			write();
		}
		else {
			read();
		}
	}

	// all the FEATURES
	public final FVTCyclingOption buttonPosition;
	public final FVTBooleanOption featureToggleMessages;
	public final FVTBooleanOption crosshairStaticColor;
	public final FVTDoubleOption  crosshairRedComponent;
	public final FVTDoubleOption  crosshairGreenComponent;
	public final FVTDoubleOption  crosshairBlueComponent;
	public final FVTDoubleOption  crosshairScale;
	public final FVTBooleanOption disableWToSprint;
	public final FVTBooleanOption sendDeathCoordinates;
	public final FVTBooleanOption coordinatesPosition;
	public final FVTBooleanOption showHUDInfo;
	public final FVTBooleanOption noToolBreaking;
	public final FVTBooleanOption toolWarning;
	public final FVTDoubleOption  toolWarningScale;
	public final FVTBooleanOption toolWarningPosition;
	public final FVTDoubleOption  cloudHeight;
	public final FVTBooleanOption entityOutline;
	public final FVTBooleanOption fullbright;
	public final FVTBooleanOption randomPlacement;
	public final FVTBooleanOption noNetherFog;
	public final FVTBooleanOption noBlockBreakParticles;
	public final FVTBooleanOption refillHand;
	public final FVTBooleanOption autoReconnect;
	public final FVTDoubleOption  autoReconnectMaxTries;
	public final FVTDoubleOption  autoReconnectTimeout;
	public final FVTBooleanOption autoEat;
	public final FVTBooleanOption triggerBot;
	public final FVTBooleanOption freecam;
	public final FVTBooleanOption autoTotem;

	public void write()
	{
		try(PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(optionsFile), StandardCharsets.UTF_8));) {
			// printing header
			printWriter.println("# FVT configuration. Do not edit here unless you know what you're doing!");
			printWriter.println("# Last save: " + DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy").format(LocalDateTime.now()));

			for(Entry<String, SaveableValue> feature : saveableFeatures.entrySet()) {
				printWriter.println(feature.getKey() + "=" + feature.getValue().getValueAsString());
			}
		}
		catch(Exception e) {
			LogManager.getLogger().error("[FVT] Failed to write to 'fvt.properties':", e.toString());
		}
	}

	private void read()
	{
		try(BufferedReader bufferedReader = Files.newReader(optionsFile, StandardCharsets.UTF_8)) {
			bufferedReader.lines().forEach((line) -> {
				if(line.startsWith("#")) {
					// skips header
					return;
				}

				String[] v = line.split("=");

				if(v.length != 2) {
					LogManager.getLogger().warn("[FVT] Skipping bad config option line!");
					return;
				}

				String key = v[0];
				String value = v[1];

				if(saveableFeatures.get(key) == null || !saveableFeatures.get(key).setValueFromString(value)) {
					LogManager.getLogger().warn("[FVT] Skipping bad config option (" + value + ")" + " for " + key);
				}
			});
		}
		catch(IOException e) {
			LogManager.getLogger().error("[FVT] Failed to read from 'fvt.properties':", e.toString());
		}
	}
}