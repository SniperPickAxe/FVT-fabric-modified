package me.flourick.fvt.settings;

import java.util.ArrayList;
import java.util.List;

import me.flourick.fvt.FVT;

import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

/**
 * Custom boolean button for the FVT settings menu. Click the button cycles between true and false.
 * 
 * @author Flourick
 */
public class FVTBooleanOption extends FVTOption<Boolean>
{
	private boolean currentValue;
	private boolean defaultValue;

	private Text trueText;
	private Text falseText;

	private Text tooltipText;
	private List<OrderedText> tooltip;

	public FVTBooleanOption(String key, String tooltipKey, boolean defaultValue, Text trueText, Text falseText)
	{
		super(key);
		this.trueText = trueText;
		this.falseText = falseText;
		this.currentValue = defaultValue;
		this.defaultValue = defaultValue;
		this.tooltipText = new TranslatableText(tooltipKey);
	}

	public FVTBooleanOption(String key, String tooltipKey, boolean defaultValue)
	{
		super(key);
		this.trueText = ScreenTexts.ON;
		this.falseText = ScreenTexts.OFF;
		this.currentValue = defaultValue;
		this.defaultValue = defaultValue;
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
		tooltip = new ArrayList<>();
		tooltip.addAll(FVT.MC.textRenderer.wrapLines(tooltipText, 220));
		tooltip.add(new TranslatableText("fvt.feature.default", defaultValue ? trueText : falseText).formatted(Formatting.GRAY).asOrderedText());

		return new FVTOptionButtonWidget(x, y, width, 20, this, getButtonLabel(), (button) -> {
			currentValue = !currentValue;
			button.setMessage(getButtonLabel());
		});
	}

	@Override
	public String getValueAsString()
	{
		return String.valueOf(currentValue);
	}

	@Override
	public boolean setValueFromString(String newValue)
	{
		boolean ret = true;

		if(newValue.equalsIgnoreCase("true")) {
			currentValue = true;
		}
		else if(newValue.equalsIgnoreCase("false")) {
			currentValue = false;
		}
		else {
			ret = false;
		}

		return ret;
	}

	@Override
	public Boolean getValueRaw()
	{
		return currentValue;
	}

	public void setValueRaw(Boolean newValue)
	{
		currentValue = newValue;
	}

	@Override
	public void setValueDefault()
	{
		currentValue = defaultValue;
	}

	public void toggle()
	{
		currentValue = !currentValue;
	}

	private Text getButtonLabel()
	{
		return getGenericLabel(currentValue ? trueText : falseText);
	}
}
