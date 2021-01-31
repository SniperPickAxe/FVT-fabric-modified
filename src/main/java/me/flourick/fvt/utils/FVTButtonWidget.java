package me.flourick.fvt.utils;

import com.mojang.blaze3d.systems.RenderSystem;

import me.flourick.fvt.FVT;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class FVTButtonWidget extends ButtonWidget
{
	private Color messageColor;
	private Color buttonColor;

	public FVTButtonWidget(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress, ButtonWidget.TooltipSupplier tooltipSupplier)
	{
		super(x, y, width, height, message, onPress, tooltipSupplier);
		this.messageColor = Color.WHITE;
		this.buttonColor = Color.WHITE;
	}

	public FVTButtonWidget(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress, ButtonWidget.TooltipSupplier tooltipSupplier, Color buttonColor, Color messageColor)
	{
		super(x, y, width, height, message, onPress, tooltipSupplier);
		this.messageColor = messageColor;
		this.buttonColor = buttonColor;
	}

	// yep, had to make it my own so not only custom width is supported but also height
	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		FVT.MC.getTextureManager().bindTexture(WIDGETS_LOCATION);
		int textureOffset = this.getYImage(this.isHovered());

		RenderSystem.color4f(buttonColor.getNormRed(), buttonColor.getNormGreen(), buttonColor.getNormBlue(), buttonColor.getNormAlpha());
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();

		// upper-left
		this.drawTexture(matrices, this.x                 , this.y                  , 0                   , 46 + textureOffset * 20                  , this.width / 2, this.height / 2);
		// upper-right
		this.drawTexture(matrices, this.x + this.width / 2, this.y                  , 200 - this.width / 2, 46 + textureOffset * 20                  , this.width / 2, this.height / 2);
		// lower-left
		this.drawTexture(matrices, this.x                 , this.y + this.height / 2, 0                   , 66 - this.height / 2 + textureOffset * 20, this.width / 2, this.height / 2);
		// lower-right
		this.drawTexture(matrices, this.x + this.width / 2, this.y + this.height / 2, 200 - this.width / 2, 66 - this.height / 2 + textureOffset * 20, this.width / 2, this.height / 2);

		 // title
		drawCenteredText(matrices, FVT.MC.textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, messageColor.getPacked());

		if(this.isHovered()) {
			this.renderToolTip(matrices, mouseX, mouseY);
		}
	}

	public Color getMessageColor()
	{
		return messageColor;
	}

	public void setMessageColor(Color messageColor)
	{
		this.messageColor = messageColor;
	}

	public Color getButtonColor()
	{
		return buttonColor;
	}

	public void setButtonColor(Color buttonColor)
	{
		this.buttonColor = buttonColor;
	}

	
}
