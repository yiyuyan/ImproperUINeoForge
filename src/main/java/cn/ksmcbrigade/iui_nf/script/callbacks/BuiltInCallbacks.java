package cn.ksmcbrigade.iui_nf.script.callbacks;

import cn.ksmcbrigade.iui_nf.ImproperUIAPI;
import cn.ksmcbrigade.iui_nf.script.CallbackHandler;
import cn.ksmcbrigade.iui_nf.script.CallbackListener;
import cn.ksmcbrigade.iui_nf.script.events.KeyEvent;
import cn.ksmcbrigade.iui_nf.script.events.MouseEvent;
import cn.ksmcbrigade.iui_nf.util.ChatUtils;
import net.minecraft.Util;
import org.lwjgl.glfw.GLFW;

public class BuiltInCallbacks implements CallbackListener {

    @CallbackHandler
    public void openGithub(MouseEvent e) {
        if (e.input.isDown())
            Util.getPlatform().openUri("https://github.com/yiyuyan/ImproperUINeoForge");
    }

    @CallbackHandler
    public void openModrinth(MouseEvent e) {
        if (e.input.isDown())
            Util.getPlatform().openUri("https://modrinth.com/mod/improperui");
    }

    @CallbackHandler
    public void openDiscord(MouseEvent e) {
        if (e.input.isDown())
            Util.getPlatform().openUri("https://discord.gg/tMaShNzNtP");
    }

    @CallbackHandler
    public void openWiki(MouseEvent e) {
        if (e.input.isDown())
            Util.getPlatform().openUri("https://github.com/itzispyder/iui_nf/wiki");
    }

    @CallbackHandler
    public void openExampleScreen(MouseEvent e) {
        if (e.input.isDown())
            ImproperUIAPI.parseAndRunFile("iui_nf", "example.ui");
    }

    @CallbackHandler
    public void sendHelloWorld(MouseEvent e) {
        if (e.input.isDown())
            ChatUtils.sendMessage("Hello World");
    }

    @CallbackHandler
    public void sendHelloWorld(KeyEvent e) {
        if (e.input.isDown())
            ChatUtils.sendFormatted("Hello World + %s", GLFW.glfwGetKeyName(e.key, e.scan));
    }

    @CallbackHandler
    public void printSelf(MouseEvent e) {
        if (e.input.isDown())
            ChatUtils.sendMessage("target: " + e.target);
    }

    @CallbackHandler
    public void printSelf(KeyEvent e) {
        if (e.input.isDown())
            ChatUtils.sendMessage("target: " + e.target);
    }
}
