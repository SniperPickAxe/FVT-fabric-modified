package me.flourick.fvt.utils;

import java.io.File;
import java.io.IOException;

import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.metadata.ResourceMetadataReader;

/**
 * Resource pack with our languages, had to null parseMetadata so 'pack.mcmeta' is not loaded
 * 
 * @author Flourick
 */
public class FVTLanguagesPack extends DirectoryResourcePack
{
	public FVTLanguagesPack(File file)
	{
		super(file);
	}

	@Override
	public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException
	{
		return null;
	}
}
