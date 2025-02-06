package cn.ksmcbrigade.iui_nf.render.elements;

import cn.ksmcbrigade.iui_nf.render.Element;
import cn.ksmcbrigade.iui_nf.util.StringUtils;
import net.minecraft.client.gui.GuiGraphics;

public class Label extends Element {

    public Label() {
        super();
        queueProperty("inner-text: \"Empty Label\"");
        queueProperty("padding: 2");
        queueProperty("background-color: none");
    }

    @Override
    public void init() {
        super.init();

        registerProperty("inner-text", args -> {
            innerText = StringUtils.color(args.getQuoteAndRemove());
            updateDimensions();
        });
        registerProperty("inner-text-prefix", args -> {
            innerTextPrefix = StringUtils.color(args.getQuoteAndRemove());
            updateDimensions();
        });
        registerProperty("inner-text-suffix", args -> {
            innerTextSuffix = StringUtils.color(args.getQuoteAndRemove());
            updateDimensions();
        });
    }

    @Override
    public void onRender(GuiGraphics context, int mx, int my, float delta) {
        super.onRender(context, mx, my, delta);
        updateDimensions();
    }

    private void updateDimensions() {
        var text = getText();
        if (mc != null && mc.font != null && text != null) {
            width = (int)(mc.font.width(text) * textScale);
            height = (int)(mc.font.lineHeight * textScale);
        }
    }
}
