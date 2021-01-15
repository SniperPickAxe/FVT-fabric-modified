package me.flourick.fvt.settings;

import java.util.ArrayList;
import java.util.List;

import me.flourick.fvt.FVT;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class FVTCyclingOption extends Option
{
	private List<Text> values;
	private Text currentValue;
	private Text defaultValue;

	private Text tooltipText;

	public FVTCyclingOption(String key, String tooltipKey, List<Text> values)
	{
		super(key);
		this.values = values;
		this.currentValue = values.isEmpty() ? null : values.get(0);
		this.defaultValue = currentValue;
		this.tooltipText = new TranslatableText(tooltipKey);
	}

	@Override
	public AbstractButtonWidget createButton(GameOptions options, int x, int y, int width)
	{
		List<OrderedText> tooltip = new ArrayList<OrderedText>();
		tooltip.addAll(FVT.MC.textRenderer.wrapLines(tooltipText, 220));
		tooltip.add(new TranslatableText("fvt.feature.default", defaultValue).formatted(Formatting.GRAY).asOrderedText());
		this.setTooltip(tooltip);

		return new OptionButtonWidget(x, y, width, 20, this, this.getButtonLabel(), (button) -> {
			this.cycle();
			button.setMessage(this.getButtonLabel());
		});
	}

	public void cycle()
	{
		this.currentValue = values.get((values.indexOf(this.currentValue) + 1) % values.size());
	}

	public String getCurrentAsString()
	{
		return String.valueOf(values.indexOf(this.currentValue));
	}

	public boolean setCurrentFromString(String newValue)
	{
		boolean ret = true;

		try {
			this.currentValue = values.get(Integer.parseInt(newValue));
		}
		catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
			ret = false;
		}
		
		return ret;
	}

	private Text getButtonLabel()
	{
		return getGenericLabel(currentValue);
	}
}
