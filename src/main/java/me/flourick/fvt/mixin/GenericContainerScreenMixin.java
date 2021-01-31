package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import me.flourick.fvt.FVT;
import me.flourick.fvt.utils.Color;
import me.flourick.fvt.utils.FVTButtonWidget;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Mixin(GenericContainerScreen.class)
public class GenericContainerScreenMixin extends HandledScreen<GenericContainerScreenHandler>
{
	private final int buttonWidth = 12;
	private final int buttonHeight = 10;

	@Override
	protected void init()
	{
		super.init();

		int baseX = ((this.width - this.backgroundWidth) / 2) + this.backgroundWidth - buttonWidth - 7;
		int baseY = ((this.height - this.backgroundHeight) / 2) + 5;

		this.addButton(new FVTButtonWidget(baseX, baseY, buttonWidth, buttonHeight, new LiteralText("⊽"), (buttonWidget) -> onDropButtonClick()
		, (buttonWidget, matrixStack, i, j) -> {
			this.renderTooltip(matrixStack, new TranslatableText("fvt.feature.name.containers.drop.tooltip"), i, j + 8);
		}, new Color(150, 255, 255, 255), new Color(220, 255, 255, 255)));

		this.addButton(new FVTButtonWidget(baseX - buttonWidth - 2, baseY, buttonWidth, buttonHeight, new LiteralText("⊻"), (buttonWidget) -> onGetButtonClick()
		, (buttonWidget, matrixStack, i, j) -> {
			this.renderTooltip(matrixStack, new TranslatableText("fvt.feature.name.containers.get.tooltip"), i, j + 8);
		}, new Color(150, 255, 255, 255), new Color(220, 255, 255, 255)));

		this.addButton(new FVTButtonWidget(baseX - 2*buttonWidth - 4, baseY, buttonWidth, buttonHeight, new LiteralText("⊼"), (buttonWidget) -> onStoreButtonClick()
		, (buttonWidget, matrixStack, i, j) -> {
			this.renderTooltip(matrixStack, new TranslatableText("fvt.feature.name.containers.store.tooltip"), i, j + 8);
		}, new Color(150, 255, 255, 255), new Color(220, 255, 255, 255)));
	}

	private void onDropButtonClick()
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

	private void onGetButtonClick()
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

	private void onStoreButtonClick()
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

	// I don't know either
	@Shadow
	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {}

	public GenericContainerScreenMixin(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {super(handler, inventory, title);} // IGNORED
}
