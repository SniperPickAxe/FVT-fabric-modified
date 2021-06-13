package me.flourick.fvt.settings;

import java.util.ArrayList;
import java.util.List;

import me.flourick.fvt.FVT;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

/**
 * Custom cycling button for the FVT settings menu. Clicking the button cycles through the list of possible values.
 * 
 * @author Flourick
 */
public class FVTCyclingOption extends FVTOption<Text>
{
	private List<Text> values;
	private Text currentValue;
	private Text defaultValue;

	private Text tooltipText;
	private List<OrderedText> tooltip;

	public FVTCyclingOption(String key, String tooltipKey, List<Text> values)
	{
		super(key);
		this.values = values;
		this.currentValue = values.isEmpty() ? null : values.get(0);
		this.defaultValue = currentValue;
		this.tooltipText = new TranslatableText(tooltipKey);
	}

	@Override
	public List<OrderedText> getTooltip()
	{
		return tooltip;
	}

	@Override
	public ClickableWidget createButton(GameOptions options, int x, int y, int width)
	{
		tooltip = new ArrayList<OrderedText>();
		tooltip.addAll(FVT.MC.textRenderer.wrapLines(tooltipText, 220));
		tooltip.add(new TranslatableText("fvt.feature.default", defaultValue).formatted(Formatting.GRAY).asOrderedText());

		return new FVTOptionButtonWidget(x, y, width, 20, this, getButtonLabel(), (button) -> {
			cycle();
			button.setMessage(getButtonLabel());
		});
	}

	@Override
	public String getValueAsString()
	{
		return String.valueOf(values.indexOf(currentValue));
	}

	@Override
	public boolean setValueFromString(String newValue)
	{
		boolean ret = true;

		try {
			currentValue = values.get(Integer.parseInt(newValue));
		}
		catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
			ret = false;
		}
		
		return ret;
	}

	@Override
	public void setValueDefault()
	{
		currentValue = values.isEmpty() ? null : values.get(0);
	}

	@Override
	public Text getValueRaw()
	{
		return currentValue;
	}

	public void cycle()
	{
		currentValue = values.get((values.indexOf(currentValue) + 1) % values.size());
	}

	private Text getButtonLabel()
	{
		return getGenericLabel(currentValue);
	}
}
