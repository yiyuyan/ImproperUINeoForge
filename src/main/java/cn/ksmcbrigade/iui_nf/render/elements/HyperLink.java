package cn.ksmcbrigade.iui_nf.render.elements;

import cn.ksmcbrigade.iui_nf.render.Element;
import net.minecraft.Util;

public class HyperLink extends Element {

    public String link;

    public HyperLink() {
        super();
        queueProperty("text-color: aqua");
        queueProperty("height: 7");
        queueProperty("background-color: #00000000");
        queueProperty("hovered => { inner-text-prefix: \"&b&n\" }");
        queueProperty("selected => { inner-text-prefix: \"&3&n\" }");
        queueProperty("focused => { inner-text-prefix: \"&5&n\" }");
    }

    @Override
    public void init() {
        super.init();
        registerProperty("link", args -> link = args.get(0).toString());
        registerProperty("url", args -> link = args.get(0).toString());
        registerProperty("href", args -> link = args.get(0).toString());
    }

    @Override
    public void style() {
        super.style();
        innerText = innerText == null ? link : innerText;
        width = mc.font.width(innerText == null ? "" : innerText);

        if (selectStyle != null)
            selectStyle.innerText = innerText;
        if (hoverStyle != null)
            hoverStyle.innerText = innerText;
        if (focusStyle != null)
            focusStyle.innerText = innerText;
    }

    @Override
    public void onLeftClick(int mx, int my, boolean release) {
        super.onLeftClick(mx, my, release);
        if (link != null && !link.trim().isEmpty() && !release) {
            Util.getPlatform().openUri(link);
        }
    }
}
