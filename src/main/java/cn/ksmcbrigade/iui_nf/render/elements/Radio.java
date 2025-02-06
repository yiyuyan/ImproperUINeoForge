package cn.ksmcbrigade.iui_nf.render.elements;

import cn.ksmcbrigade.iui_nf.config.ConfigKey;
import cn.ksmcbrigade.iui_nf.config.PropertyCache;
import cn.ksmcbrigade.iui_nf.render.KeyHolderElement;
import cn.ksmcbrigade.iui_nf.render.math.Color;
import cn.ksmcbrigade.iui_nf.script.ScriptParser;

public class Radio extends KeyHolderElement {

    private Color radioFill;

    public Radio() {
        super();
        queueProperty("size: 6");
        queueProperty("background-color: white");
        queueProperty("border: 2 360 black");
        queueProperty("shadow: 1 white");
        queueProperty("shadow-fade-color: white");
        queueProperty("margin: 2");
    }

    @Override
    public void init() {
        super.init();
        registerProperty("active", args -> setActive(args.get(0).toBool(), false));
        registerProperty("fill-color", args -> fillColor = radioFill = args.get(0).toColor());
        registerProperty("background-color", args -> fillColor = radioFill = args.get(0).toColor());
    }

    public boolean isActive() {
        return classList.contains("active");
    }

    public void setActive(boolean active) {
        setActive(active, true);
    }

    public void setActive(boolean active, boolean deep) {
        if (active) {
            if (parent != null && deep)
                for (var child : parent.getChildren())
                    if (child instanceof Radio radio)
                        radio.setActive(false);
            classList.add("active");
            fillColor = radioFill;
        }
        else {
            classList.remove("active");
            fillColor = borderColor;
        }

        var key = getConfigKey();
        if (key != null)
            onSaveKey(ScriptParser.getCache(key.modId), key);
    }

    @Override
    public void onLeftClick(int mx, int my, boolean release) {
        if (!release)
            setActive(!isActive());
    }

    @Override
    public void onLoadKey(PropertyCache cache, ConfigKey key) {
        var property = cache.getProperty(key);
        setActive(property != null && property.get(0).toBool(), true);
    }

    @Override
    public void style() {
        super.style();
        if (getConfigKey() == null)
            setActive(isActive(), false); // fix for defaulting to active
    }

    @Override
    public void onSaveKey(PropertyCache cache, ConfigKey key) {
        cache.setProperty(key, isActive(), true);
    }
}
