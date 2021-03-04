package me.flourick.fvt.settings;

/**
 * Value that can be read as a string and be set from a string.
 * 
 * @author Flourick
 */
public interface SaveableValue
{
	public String getValueAsString();
	public boolean setValueFromString(String newValue);
}
