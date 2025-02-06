package cn.ksmcbrigade.iui_nf.render.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import cn.ksmcbrigade.iui_nf.config.ConfigKey;
import cn.ksmcbrigade.iui_nf.config.PropertyCache;
import cn.ksmcbrigade.iui_nf.render.KeyHolderElement;
import cn.ksmcbrigade.iui_nf.render.constants.BackgroundClip;
import cn.ksmcbrigade.iui_nf.render.constants.Visibility;
import cn.ksmcbrigade.iui_nf.render.math.Color;
import cn.ksmcbrigade.iui_nf.render.math.Dimensions;
import cn.ksmcbrigade.iui_nf.util.RenderUtils;
import cn.ksmcbrigade.iui_nf.util.StringUtils;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.function.Function;

public class TextBox extends KeyHolderElement {

    public String defaultComponent, pattern;
    private boolean selectionBlinking;
    private int selectionBlink;

    public TextBox() {
        super();
        queueProperty("size: 90 12");
        queueProperty("border: 1 0 white");
        queueProperty("background-color: dark_gray");
        innerText = "";
    }

    @Override
    public void init() {
        super.init();
        registerProperty("default-text", args -> defaultComponent = args.getQuote());
        registerProperty("placeholder", args -> defaultComponent = args.getQuote());
        registerProperty("text-pattern", args -> pattern = args.getQuote());
        registerProperty("text-format", args -> pattern = args.getQuote());
        registerProperty("text-mask", args -> pattern = args.getQuote());
        registerProperty("input-pattern", args -> pattern = args.getQuote());
        registerProperty("input-format", args -> pattern = args.getQuote());
        registerProperty("input-mask", args -> pattern = args.getQuote());
        registerProperty("pattern", args -> pattern = args.getQuote());
        registerProperty("format", args -> pattern = args.getQuote());
        registerProperty("mask", args -> pattern = args.getQuote());
    }

    @Override
    public void onRender(GuiGraphics context, int mx, int my, float delta) {
        int x = getPosX();
        int y = getPosY();

        if (visibility == Visibility.INVISIBLE)
            return;

        context.pose().pushPose();
        int cx = x + width / 2;
        int cy = y + height / 2;
        context.pose().rotateAround(Axis.XP.rotationDegrees(rotateX), cx, cy, 0);
        context.pose().rotateAround(Axis.YP.rotationDegrees(rotateY), cx, cy, 0);
        context.pose().rotateAround(Axis.ZP.rotationDegrees(rotateZ), cx, cy, 0);

        if (visibility != Visibility.ONLY_CHILDREN) {
            boolean notOpaque = opacity < 1.0F;
            boolean focused = parentPanel != null && parentPanel.focused == this;
            if (notOpaque)
                RenderSystem.setShaderColor(1, 1, 1, opacity);

            RenderUtils.fillRoundShadow(context,
                    x + marginLeft - paddingLeft - borderThickness,
                    y + marginTop - paddingTop - borderThickness,
                    width + paddingLeft + paddingRight + borderThickness * 2,
                    height + paddingTop + paddingBottom + borderThickness * 2,
                    borderRadius,
                    shadowDistance,
                    shadowColor.getHex(),
                    shadowColor.getHexCustomAlpha(0)
            );
            RenderUtils.fillRoundShadow(context,
                    x + marginLeft - paddingLeft,
                    y + marginTop - paddingTop,
                    width + paddingLeft + paddingRight,
                    height + paddingTop + paddingBottom,
                    borderRadius,
                    borderThickness,
                    focused ? borderColor.getHex() : borderColor.darker().getHex(),
                    focused ? borderColor.getHex() : borderColor.darker().getHex()
            );
            RenderUtils.fillRoundRect(context,
                    x + marginLeft - paddingLeft,
                    y + marginTop - paddingTop,
                    width + paddingLeft + paddingRight,
                    height + paddingTop + paddingBottom,
                    borderRadius,
                    focused ? fillColor.getHex() : fillColor.darker().getHex()
            );
            if (backgroundImage != null) {
                RenderUtils.drawRoundComponenture(context,
                        backgroundImage,
                        x + marginLeft - paddingLeft,
                        y + marginTop - paddingTop,
                        width + paddingLeft + paddingRight,
                        height + paddingTop + paddingBottom,
                        borderRadius
                );
            }

            if (parentPanel != null) {
                String text = innerText != null ? innerText : "";
                while (!text.isEmpty() && mc.font.width(text) * 0.9F > width - height - 4) {
                    text = text.substring(1);
                }

                Component display = Component.nullToEmpty(text);
                if (!queryMatchesPattern())
                    RenderUtils.drawDefaultScaledComponent(context, display, x + height / 2 + 2, y + height / 3, 0.9F, false, Color.ORANGE.getHex());
                else if (parentPanel.focused == this && !text.isEmpty())
                    RenderUtils.drawDefaultScaledComponent(context, display, x + height / 2 + 2, y + height / 3, 0.9F, false, textColor.getHex());
                else if (!text.isEmpty())
                    RenderUtils.drawDefaultScaledComponent(context, display, x + height / 2 + 2, y + height / 3, 0.9F, false, textColor.darker().darker().getHex());
                else
                    RenderUtils.drawDefaultScaledComponent(context, Component.nullToEmpty(getDefaultComponent()), x + height / 2 + 2, y + height / 3, 0.9F, false, textColor.darker().darker().getHex());

                if (selectionBlinking) {
                    int tx = (int)(x + height / 2 + 2 + mc.font.width(text) * 0.9);
                    int ty = y + 2;
                    RenderUtils.drawVerLine(context, tx, ty, height - 4, 0xE0FFFFFF);
                }
            }

            if (notOpaque)
                RenderSystem.setShaderColor(1, 1, 1, 1);
        }

        if (visibility != Visibility.ONLY_SELF) {
            boolean shouldClip = backgroundClip != BackgroundClip.NONE;

            if (shouldClip) {
                Dimensions shape;
                switch (backgroundClip) {
                    case PADDING -> shape = getPaddedDimensions();
                    case BORDER -> shape = getBorderedDimensions();
                    case MARGIN -> shape = getMarginalDimensions();
                    default -> shape = getDimensions();
                }
                context.enableScissor(shape.x, shape.y, shape.x + shape.width, shape.y + shape.height);
            }

            onRenderChildren(context, mx, my, delta);

            if (shouldClip)
                context.disableScissor();
        }

        context.pose().popPose();
    }

    @Override
    public void onKey(int key, int scan, boolean release) {
        if (parentPanel != null && !release) {
            String typed = GLFW.glfwGetKeyName(key, scan);

            if (key == GLFW.GLFW_KEY_ESCAPE) {
                parentPanel.focused = null;
            }
            else if (key == GLFW.GLFW_KEY_BACKSPACE) {
                onInput(input -> {
                    if (!input.isEmpty()) {
                        return input.substring(0, input.length() - 1);
                    }
                    return input;
                }, false);
            }
            else if (key == GLFW.GLFW_KEY_SPACE) {
                onInput(input -> input.concat(" "), true);
            }
            else if (key == GLFW.GLFW_KEY_V && parentPanel.ctrlKeyPressed) {
                onInput(input -> input.concat(mc.keyboardHandler.getClipboard()), true);
            }
            else if (typed != null){
                onInput(input -> input.concat(parentPanel.shiftKeyPressed ? StringUtils.keyPressWithShift(typed) : typed), true);
            }
        }
    }

    public void onInput(Function<String, String> factory, boolean append) {
        innerText = factory.apply(innerText != null ? innerText : "");
    }

    @Override
    public void onTick() {
        super.onTick();

        if (parentPanel != null) {
            if (parentPanel.focused != this) {
                selectionBlinking = false;
                return;
            }

            if (selectionBlink++ >= 20) {
                selectionBlink = 0;
            }
            if (selectionBlink % 10 == 0 && selectionBlink > 0) {
                selectionBlinking = !selectionBlinking;
            }
        }
    }

    @Override
    public void onLoadKey(PropertyCache cache, ConfigKey key) {
        var property = cache.getProperty(key);
        if (property != null)
            innerText = property.getQuote();
    }

    @Override
    public void onSaveKey(PropertyCache cache, ConfigKey key) {
        cache.setProperty(key, "\"%s\"".formatted(innerText), true);
    }

    public String getQuery() {
        return innerText;
    }

    public String getLowercaseQuery() {
        return innerText.toLowerCase();
    }

    public void setQuery(String query) {
        this.innerText = query;
    }

    public String getDefaultComponent() {
        return defaultComponent == null ? "" : defaultComponent;
    }

    public void setDefaultComponent(String defaultComponent) {
        this.defaultComponent = defaultComponent;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean queryMatchesPattern() {
        return pattern == null || innerText.matches(pattern);
    }
}
