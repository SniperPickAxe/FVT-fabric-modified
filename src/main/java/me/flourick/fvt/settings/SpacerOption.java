package me.flourick.fvt.settings;

import me.flourick.fvt.FVT;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class SpacerOption extends Option
{
	private Text text;

	public SpacerOption(String key)
	{
		super(key);
		this.text = new TranslatableText(key).formatted(Formatting.BOLD);
	}

	@Override
	public AbstractButtonWidget createButton(GameOptions options, int x, int y, int width)
	{
		return new SpacerButtonWidget(x, y, width, 20, this.getDisplayPrefix(), (buttonWidget) -> {
			// nada
		});
	}

	private class SpacerButtonWidget extends ButtonWidget
	{
		public SpacerButtonWidget(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress)
		{
			super(x, y, width, height, message, onPress);
		}

		@Override
		public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
		{
			// nada
		}

		@Override
		public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY)
		{
			// nada
		}

		@Override
		public boolean isFocused()
		{
			return false;
		}

		@Override
		public boolean isHovered()
		{
			return false;
		}

		@Override
		public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
		{
			if(this.visible) {
				DrawableHelper.drawCenteredText(matrices, FVT.MC.textRenderer, text, this.x + this.width / 2, this.y + (this.height - 8) / 2, 16777215);
			}
		}

		@Override
		protected void renderBg(MatrixStack matrices, MinecraftClient client, int mouseX, int mouseY)
		{
			// nada
		}
	}
}