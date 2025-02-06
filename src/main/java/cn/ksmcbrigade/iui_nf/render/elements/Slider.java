package cn.ksmcbrigade.iui_nf.render.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import cn.ksmcbrigade.iui_nf.config.ConfigKey;
import cn.ksmcbrigade.iui_nf.config.PropertyCache;
import cn.ksmcbrigade.iui_nf.render.KeyHolderElement;
import cn.ksmcbrigade.iui_nf.script.ScriptParser;
import cn.ksmcbrigade.iui_nf.util.MathUtils;
import cn.ksmcbrigade.iui_nf.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;

import static cn.ksmcbrigade.iui_nf.util.RenderUtils.drawText;
import static cn.ksmcbrigade.iui_nf.util.RenderUtils.fillRect;

public class Slider extends KeyHolderElement {

    public double min, max, val;
    public int decimalPlaces;

    private int fillEnd;

    public Slider() {
        super();
        queueProperty("size: 100 17");
        queueProperty("range: 0 10");
        queueProperty("value: 10");
        queueProperty("decimal-places: 1");
        queueProperty("border-radius: 360");
        queueProperty("background-color: white");
        queueProperty("inner-text: \"Slider\"");
        this.fillEnd = x + width;
    }

    @Override
    public void init() {
        super.init();
        registerProperty("min", args -> min = args.get(0).toDouble());
        registerProperty("minimum", args -> min = args.get(0).toDouble());
        registerProperty("max", args -> max = args.get(0).toDouble());
        registerProperty("maximum", args -> max = args.get(0).toDouble());
        registerProperty("val", args -> val = args.get(0).toDouble());
        registerProperty("value", args -> val = args.get(0).toDouble());
        registerProperty("decimal-places", args -> decimalPlaces = args.get(0).toInt());
        registerProperty("range", args -> {
            min = args.get(0).toDouble();
            max = args.get(1).toDouble();
        });
    }

    @Override
    public void onRender(GuiGraphics context, int mx, int my, float delta) {
        int x = getPosX() + marginLeft - paddingLeft;
        int y = getPosY() + marginTop - paddingTop;

        boolean notOpaque = opacity < 1.0F;
        if (notOpaque)
            RenderSystem.setShaderColor(1, 1, 1, opacity);

        if (parentPanel != null && parentPanel.selected == this) {
            this.fillEnd = MathUtils.clamp(mx, x, x + width);
            double range = max - min;
            double ratio = (double)(fillEnd - x) / (double)width;
            double value = range * ratio;
            val = MathUtils.round(value + min, decimalPlaces);

            var configKey = getConfigKey();
            if (configKey != null)
                ScriptParser.getCache(configKey.modId).setProperty(configKey, val, true);
        }

        double range = max - min;
        double value = val - min;
        double ratio = value / range;
        int len = (int)(width * ratio);
        this.fillEnd = x + len;

        val = MathUtils.round(range * ratio + min, decimalPlaces);

        String text = "(%s)".formatted(val);
        drawText(context, text, x + width + 10, y + (height - 7) / 2, 0.9F, false);
        fillRect(context, x, y + height / 2 - 1, width, 2, borderColor.getHex());
        fillRect(context, x, y + height / 2 - 1, len, 2, fillColor.getHex());

        x = fillEnd - (height / 2 + paddingLeft + paddingRight) / 2;
        y = y + (height - (height / 2 + paddingTop + paddingBottom)) / 2;

        RenderUtils.fillRoundShadow(context,
                x - borderThickness,
                y - borderThickness,
                height / 2 + paddingLeft + paddingRight + borderThickness * 2,
                height / 2 + paddingTop + paddingBottom + borderThickness * 2,
                borderRadius,
                shadowDistance,
                shadowColor.getHex(),
                shadowColor.getHexCustomAlpha(0)
        );
        RenderUtils.fillRoundShadow(context, x, y,
                height / 2 + paddingLeft + paddingRight,
                height / 2 + paddingTop + paddingBottom,
                borderRadius,
                borderThickness,
                borderColor.getHex(),
                borderColor.getHex()
        );
        RenderUtils.fillRoundRect(context, x, y,
                height / 2 + paddingLeft + paddingRight,
                height / 2 + paddingTop + paddingBottom,
                borderRadius,
                fillColor.getHex()
        );
        if (backgroundImage != null) {
            RenderUtils.drawRoundComponenture(context,
                    backgroundImage, x, y,
                    height / 2 + paddingLeft + paddingRight,
                    height / 2 + paddingTop + paddingBottom,
                    borderRadius
            );
        }

        if (notOpaque)
            RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    @Override
    public void onLoadKey(PropertyCache cache, ConfigKey key) {
        var property = cache.getProperty(key);
        if (property != null)
            val = property.get(0).toDouble();
    }

    @Override
    public void onSaveKey(PropertyCache cache, ConfigKey key) {
        cache.setProperty(key, val, true);
    }
}