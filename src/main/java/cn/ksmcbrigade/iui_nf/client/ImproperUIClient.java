package cn.ksmcbrigade.iui_nf.client;

import net.minecraft.client.gui.Font;

public class ImproperUIClient {

    private static final ImproperUIClient system = new ImproperUIClient();
    public static ImproperUIClient getInstance() {
        return system;
    }

    public Font codeRenderer;

    public ImproperUIClient() {
    }
}
