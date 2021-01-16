package me.flourick.fvt.settings;

import net.minecraft.client.options.Option;

public abstract class FVTOption<T> extends Option implements SaveableValue
{
	public FVTOption(String key)
	{
		super(key);
	}

	public abstract T getValueRaw();
}
