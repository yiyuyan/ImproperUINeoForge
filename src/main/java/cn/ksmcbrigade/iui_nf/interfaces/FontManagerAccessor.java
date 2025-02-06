package cn.ksmcbrigade.iui_nf.interfaces;

import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;

public interface FontManagerAccessor {

    Font createRenderer(ResourceLocation fontId);
}
