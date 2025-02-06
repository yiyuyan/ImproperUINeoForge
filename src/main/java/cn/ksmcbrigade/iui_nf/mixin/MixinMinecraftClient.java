package cn.ksmcbrigade.iui_nf.mixin;

import cn.ksmcbrigade.iui_nf.client.ImproperUIClient;
import cn.ksmcbrigade.iui_nf.interfaces.FontManagerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftClient {

    @Shadow @Final private FontManager fontManager;

    @Shadow @Final public static ResourceLocation UNIFORM_FONT;

    @Inject(method = "updateFontOptions", at = @At("TAIL"))
    public void initFont(CallbackInfo ci) {
        var fonts = ((FontManagerAccessor)this.fontManager);
        ImproperUIClient.getInstance().codeRenderer = fonts.createRenderer(UNIFORM_FONT);
    }
}
