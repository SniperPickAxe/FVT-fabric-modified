package me.flourick.fvt.settings;

import java.util.List;

import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.OrderableTooltip;
import net.minecraft.text.OrderedText;

/**
 * Slider part for FVTDoubleOption. This is what updates the actual internal double value and also the number that is shown on screen.
 * 
 * @author Flourick
 */
public class FVTDoubleOptionSliderWidget extends OptionSliderWidget implements OrderableTooltip
{
	FVTDoubleOption option;

	protected FVTDoubleOptionSliderWidget(GameOptions options, int x, int y, int width, int height, double value, FVTDoubleOption option)
	{
		super(options, x, y, width, height, value);
		this.option = option;
		updateMessage();
	}

	@Override
	protected void updateMessage()
	{
		setMessage(option.getButtonLabel());
	}

	@Override
	protected void applyValue()
	{
		option.setValue(value);
	}

	@Override
	public List<OrderedText> getOrderedTooltip()
	{
		return option.getTooltip();
	}
}
