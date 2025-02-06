package cn.ksmcbrigade.iui_nf.mixin;

import cn.ksmcbrigade.iui_nf.interfaces.FontManagerAccessor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(FontManager.class)
public abstract class MixinFontManager implements FontManagerAccessor {

    @Shadow @Final private Map<ResourceLocation, FontSet> fontSets;
    @Shadow @Final private FontSet missingFontSet;

    @Override
    public Font createRenderer(ResourceLocation fontId) {
        return new Font(id -> this.fontSets.getOrDefault(fontId, this.missingFontSet), false);
    }
}
