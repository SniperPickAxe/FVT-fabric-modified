package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;

@Mixin(Screen.class)
public interface ScreenInvoker
{
	@Invoker("addDrawableChild")
	public <T extends Element & Drawable & Selectable> T invokeAddDrawableChild(T drawableElement);
}
