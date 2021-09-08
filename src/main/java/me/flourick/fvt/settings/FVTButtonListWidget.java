package me.flourick.fvt.settings;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import me.flourick.fvt.FVT;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.option.Option;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

/**
 * Custom button list that allows for custom amount of buttons in each entry.
 * 
 * @author Flourick
 */
public class FVTButtonListWidget extends ElementListWidget<FVTButtonListWidget.FVTButtonEntry>
{
	public FVTButtonListWidget(MinecraftClient mc, int width, int height, int top, int bottom, int entryHeight)
	{
		super(mc, width, height, top, bottom, entryHeight);
	}

	public void addSingleOptionEntry(Option first)
	{
		this.addEntry(FVTButtonEntry.create(this.width, first));
	}

	public void addDualOptionEntry(Option first, Option second)
	{
		this.addEntry(FVTButtonEntry.create(this.width, first, second));
	}

	public void addEntry(Option[] options)
	{
		this.addEntry(FVTButtonEntry.create(this.width, options));
	}

	@Override
	public int getRowWidth()
	{
		return 400;
	}

	@Override
	protected int getScrollbarPositionX()
	{
		return super.getScrollbarPositionX() + 32;
	}

	public Optional<ClickableWidget> getHoveredButton(double mouseX, double mouseY) {
		Iterator<FVTButtonEntry> entryIterator = this.children().iterator();

		while(entryIterator.hasNext()) {
			FVTButtonEntry buttonEntry = entryIterator.next();
			Iterator<ClickableWidget> clickableIterator = buttonEntry.buttons.iterator();

			while(clickableIterator.hasNext()) {
				ClickableWidget clickableWidget = clickableIterator.next();

				if(clickableWidget.isMouseOver(mouseX, mouseY)) {
					return Optional.of(clickableWidget);
				}
			}
		}

		return Optional.empty();
	}

	// basically a button row in this list widget
	final static class FVTButtonEntry extends ElementListWidget.Entry<FVTButtonListWidget.FVTButtonEntry>
	{
		final Map<Option, ClickableWidget> optionsToButtons;
		final List<ClickableWidget> buttons;

		private FVTButtonEntry(Map<Option, ClickableWidget> optionsToButtons)
		{
			this.optionsToButtons = optionsToButtons;
			this.buttons = ImmutableList.copyOf(optionsToButtons.values());
		}

		// single button row
		public static FVTButtonEntry create(int width, Option option)
		{
			return new FVTButtonEntry(ImmutableMap.of(option, option.createButton(FVT.MC.options, width / 2 - 155, 0, 310)));
		}

		// two buttons next to each other, kept for simplicity so that the dynamic create does not have to be used for every row
		public static FVTButtonEntry create(int width, Option firstOption, Option secondOption)
		{
			return new FVTButtonEntry(ImmutableMap.of(firstOption, firstOption.createButton(FVT.MC.options, width / 2 - 155, 0, 150), secondOption, secondOption.createButton(FVT.MC.options, width / 2 - 155 + 160, 0, 150)));
		}

		// dynamically makes a button entry based on how many options are given, null values can be used to make empty spaces
		public static FVTButtonEntry create(int width, Option[] options)
		{
			final ImmutableMap.Builder<Option, ClickableWidget> builder = ImmutableMap.builder();

			float buttonWidth = (310.0f - 10.0f*(options.length - 1)) / (float)options.length;
			float pixelAdjustment = options.length * (buttonWidth - MathHelper.floor(buttonWidth));

			for(int i = 0; i < options.length; i++) {
				Option option = options[i];

				if(option != null) {
					int adjustment = pixelAdjustment > 0.5f && i == options.length - 1 ? Math.round(pixelAdjustment) : 0;

					builder.put(option, option.createButton(FVT.MC.options, width / 2 - 155 + (i * (MathHelper.floor(buttonWidth) + 10)) + adjustment, 0, MathHelper.floor(buttonWidth)));
				}
			}

			return new FVTButtonEntry(builder.build());
		}

		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta)
		{
			this.buttons.forEach((button) -> {
				button.y = y;
				button.render(matrices, mouseX, mouseY, tickDelta);
			});
		}

		public List<? extends Element> children()
		{
			return this.buttons;
		}

		public List<? extends Selectable> selectableChildren()
		{
			return this.buttons;
		}
	}
}
