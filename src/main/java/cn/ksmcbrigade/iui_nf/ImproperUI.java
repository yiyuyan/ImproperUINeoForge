package cn.ksmcbrigade.iui_nf;

import cn.ksmcbrigade.iui_nf.script.callbacks.BuiltInCallbacks;
import cn.ksmcbrigade.iui_nf.util.ClientRegistry;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

public class ImproperUI {

    public static final KeyMapping BIND = ClientRegistry.register(new KeyMapping(
            "binds.iui_nf.menu",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "binds.iui_nf"
    ));
    
    public void onInitialize() {
        ImproperUIAPI.init("iui_nf", ImproperUI.class,
                "assets/iui_nf/iui_nf/homescreen.ui",
                "assets/iui_nf/iui_nf/example.ui"
        );
    }
    
    @SubscribeEvent
    public void tick(ClientTickEvent.Post event){
        while (BIND.isDown()) {
            ImproperUIAPI.parseAndRunFile("iui_nf", "homescreen.ui", new BuiltInCallbacks());
        }
    }
}
