package me.flourick.fvt.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Mixin(ItemStack.class)
abstract class ItemStackMixin
{
	@Final
	@Shadow
	private Item item;
	
	@Shadow
    private CompoundTag tag;

	@Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isSectionHidden(ILnet/minecraft/item/ItemStack$TooltipSection;)Z", ordinal = 3), locals = LocalCapture.CAPTURE_FAILHARD)
	private void onGetTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> info, List<Text> list)
	{
		if((item != Items.BEEHIVE && item != Items.BEE_NEST) || !context.isAdvanced()){
			return;
		}

		int beeCount = 0;
		int honeyLevel = 0;

		if(tag != null) {
			CompoundTag entityTag = tag.getCompound("BlockEntityTag");
			if(entityTag != null) {
				if(entityTag.getList("Bees", 10) != null) {
					beeCount = entityTag.getList("Bees", 10).size();
				}
			}

			CompoundTag stateTag = tag.getCompound("BlockStateTag");
			if(stateTag != null) {
				try {
					honeyLevel = Integer.parseInt(stateTag.getString("honey_level"));
				}
				catch(NumberFormatException e) {
					// I have no idea but sometimes, an int appears...
					honeyLevel = stateTag.getInt("honey_level");
				}
			}
		}

		list.add(new TranslatableText("fvt.feature.name.bee_info.honey", honeyLevel).formatted(Formatting.GRAY));
		list.add(new TranslatableText("fvt.feature.name.bee_info.bees", beeCount).formatted(Formatting.GRAY));
	}
}
