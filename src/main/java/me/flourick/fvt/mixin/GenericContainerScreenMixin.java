package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Mixin;

import me.flourick.fvt.FVT;
import me.flourick.fvt.utils.Color;
import me.flourick.fvt.utils.FVTButtonWidget;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

/**
 * FEATURES: Container Buttons
 * 
 * @author Flourick
 */
@Mixin(GenericContainerScreen.class)
abstract class GenericContainerScreenMixin extends HandledScreen<GenericContainerScreenHandler>
{
	private final int FVT_buttonWidth = 12;
	private final int FVT_buttonHeight = 10;

	@Override
	protected void init()
	{
		super.init();

		if(!FVT.OPTIONS.containerButtons.getValueRaw()) {
			return;
		}

		int baseX = ((this.width - this.backgroundWidth) / 2) + this.backgroundWidth - FVT_buttonWidth - 7;
		int baseY = ((this.height - this.backgroundHeight) / 2) + 5;

		this.addDrawableChild(new FVTButtonWidget(baseX, baseY, FVT_buttonWidth, FVT_buttonHeight, new LiteralText("⊽"), (buttonWidget) -> FVT_onDropButtonClick()
		, (buttonWidget, matrixStack, i, j) -> {
			this.renderTooltip(matrixStack, new TranslatableText("fvt.feature.name.containers.drop.tooltip"), i, j + 8);
		}, new Color(150, 255, 255, 255), new Color(220, 255, 255, 255)));

		this.addDrawableChild(new FVTButtonWidget(baseX - FVT_buttonWidth - 2, baseY, FVT_buttonWidth, FVT_buttonHeight, new LiteralText("⊻"), (buttonWidget) -> FVT_onGetButtonClick()
		, (buttonWidget, matrixStack, i, j) -> {
			this.renderTooltip(matrixStack, new TranslatableText("fvt.feature.name.containers.get.tooltip"), i, j + 8);
		}, new Color(150, 255, 255, 255), new Color(220, 255, 255, 255)));

		this.addDrawableChild(new FVTButtonWidget(baseX - 2*FVT_buttonWidth - 4, baseY, FVT_buttonWidth, FVT_buttonHeight, new LiteralText("⊼"), (buttonWidget) -> FVT_onStoreButtonClick()
		, (buttonWidget, matrixStack, i, j) -> {
			this.renderTooltip(matrixStack, new TranslatableText("fvt.feature.name.containers.store.tooltip"), i, j + 8);
		}, new Color(150, 255, 255, 255), new Color(220, 255, 255, 255)));
	}

	private void FVT_onDropButtonClick()
	{
		int sz = handler.getInventory().size();

		for(int i = 0; i < sz; i++) {
			Slot slot = handler.getSlot(i);

			if(slot.getStack().isEmpty()) {
				continue;
			}

			FVT.MC.interactionManager.clickSlot(this.handler.syncId, i, 1, SlotActionType.THROW, FVT.MC.player);
		}
	}

	private void FVT_onGetButtonClick()
	{
		int sz = handler.getInventory().size();

		for(int i = 0; i < sz; i++) {
			Slot slot = handler.getSlot(i);

			if(slot.getStack().isEmpty()) {
				continue;
			}

			FVT.MC.interactionManager.clickSlot(this.handler.syncId, i, 0, SlotActionType.QUICK_MOVE, FVT.MC.player);
		}
	}

	private void FVT_onStoreButtonClick()
	{
		int sz = handler.getInventory().size();

		for(int i = sz; i < sz + 36; i++) {
			Slot slot = handler.getSlot(i);

			if(slot.getStack().isEmpty()) {
				continue;
			}

			FVT.MC.interactionManager.clickSlot(this.handler.syncId, i, 0, SlotActionType.QUICK_MOVE, FVT.MC.player);
		}
	}

	public GenericContainerScreenMixin(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {super(handler, inventory, title);} // IGNORED
}
