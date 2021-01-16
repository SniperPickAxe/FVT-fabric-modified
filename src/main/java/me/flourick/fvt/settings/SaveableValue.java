package me.flourick.fvt.settings;

public interface SaveableValue
{
	public String getValueAsString();
	public boolean setValueFromString(String newValue);
}
