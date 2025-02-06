package cn.ksmcbrigade.iui_nf.util;

import cn.ksmcbrigade.iui_nf.client.ImproperUIClient;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import static com.mojang.blaze3d.systems.RenderSystem.*;

public final class RenderUtils {

    private static final Minecraft mc = Minecraft.getInstance();
    private static final ImproperUIClient system = ImproperUIClient.getInstance();

    // fill

    public static void fillRect(GuiGraphics context, int x, int y, int w, int h, int color) {
        
        BufferBuilder buf = getTesselator().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = context.pose().last().pose();

        buf.addVertex(mat, (float)x, (float)y, 0).setColor(color);
        buf.addVertex(mat, (float)(x + w), (float)y, 0).setColor(color);
        buf.addVertex(mat, (float)(x + w), (float)(y + h), 0).setColor(color);
        buf.addVertex(mat, (float)x, (float)(y + h), 0).setColor(color);

        beginRendering();
        BufferUploader.drawWithShader(buf.buildOrThrow());
        finishRendering();
    }

    public static void fillArc(GuiGraphics context, int cX, int cY, int radius, int start, int end, int color) {
        BufferBuilder buf = getTesselator().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = context.pose().last().pose();

        buf.addVertex(mat, (float)cX, (float)cY, 0).setColor(color);

        for (int i = start - 90; i <= end - 90; i ++) {
            double angle = Math.toRadians(i);
            float x = (float)(Math.cos(angle) * radius) + cX;
            float y = (float)(Math.sin(angle) * radius) + cY;
            buf.addVertex(mat, x, y, 0).setColor(color);
        }

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    public static void fillCircle(GuiGraphics context, int cX, int cY, int radius, int color) {
        fillArc(context, cX, cY, radius, 0, 360, color);
    }

    public static void fillAnnulusArc(GuiGraphics context, int cx, int cy, int radius, int start, int end, int thickness, int color) {
        BufferBuilder buf = getTesselator().begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = context.pose().last().pose();

        for (int i = start - 90; i <= end - 90; i ++) {
            float angle = (float)Math.toRadians(i);
            float cos = (float)Math.cos(angle);
            float sin = (float)Math.sin(angle);
            float x1 = cx + cos * radius;
            float y1 = cy + sin * radius;
            float x2 = cx + cos * (radius + thickness);
            float y2 = cy + sin * (radius + thickness);
            buf.addVertex(mat, x1, y1, 0).setColor(color);
            buf.addVertex(mat, x2, y2, 0).setColor(color);
        }

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    public static void fillAnnulus(GuiGraphics context, int cx, int cy, int radius, int thickness, int color) {
        fillAnnulusArc(context, cx, cy, radius, 0, 360, thickness, color);
    }

    public static void fillRoundRect(GuiGraphics context, int x, int y, int w, int h, int r, int color) {
        r = MathUtils.clamp(r, 0, Math.min(w, h) / 2);

        BufferBuilder buf = getTesselator().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = context.pose().last().pose();

        buf.addVertex(mat, x + w / 2F, y + h / 2F, 0).setColor(color);

        int[][] corners = {
                { x + w - r, y + r },
                { x + w - r, y + h - r},
                { x + r, y + h - r },
                { x + r, y + r }
        };

        for (int corner = 0; corner < 4; corner++) {
            int cornerStart = (corner - 1) * 90;
            int cornerEnd = cornerStart + 90;
            for (int i = cornerStart; i <= cornerEnd; i += 10) {
                float angle = (float)Math.toRadians(i);
                float rx = corners[corner][0] + (float)(Math.cos(angle) * r);
                float ry = corners[corner][1] + (float)(Math.sin(angle) * r);
                buf.addVertex(mat, rx, ry, 0).setColor(color);
            }
        }

        buf.addVertex(mat, corners[0][0], y, 0).setColor(color); // connect last to first vertex

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    public static void fillRoundShadow(GuiGraphics context, int x, int y, int w, int h, int r, int thickness, int innerColor, int outerColor) {
        r = MathUtils.clamp(r, 0, Math.min(w, h) / 2);

        BufferBuilder buf = getTesselator().begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = context.pose().last().pose();

        int[][] corners = {
                { x + w - r, y + r },
                { x + w - r, y + h - r},
                { x + r, y + h - r },
                { x + r, y + r }
        };

        for (int corner = 0; corner < 4; corner++) {
            int cornerStart = (corner - 1) * 90;
            int cornerEnd = cornerStart + 90;
            for (int i = cornerStart; i <= cornerEnd; i += 10) {
                float angle = (float)Math.toRadians(i);
                float rx1 = corners[corner][0] + (float)(Math.cos(angle) * r);
                float ry1 = corners[corner][1] + (float)(Math.sin(angle) * r);
                float rx2 = corners[corner][0] + (float)(Math.cos(angle) * (r + thickness));
                float ry2 = corners[corner][1] + (float)(Math.sin(angle) * (r + thickness));
                buf.addVertex(mat, rx1, ry1, 0).setColor(innerColor);
                buf.addVertex(mat, rx2, ry2, 0).setColor(outerColor);
            }
        }

        buf.addVertex(mat, corners[0][0], y, 0).setColor(innerColor); // connect last to first vertex
        buf.addVertex(mat, corners[0][0], y - thickness, 0).setColor(outerColor); // connect last to first vertex

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    public static void fillRoundTabTop(GuiGraphics context, int x, int y, int w, int h, int r, int color) {
        r = MathUtils.clamp(r, 0, Math.min(w, h) / 2);

        BufferBuilder buf = getTesselator().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = context.pose().last().pose();

        buf.addVertex(mat, x + w / 2F, y + h / 2F, 0).setColor(color);

        int[][] corners = {
                { x + r, y + r },
                { x + w - r, y + r }
        };

        for (int corner = 0; corner < 2; corner++) {
            int cornerStart = (corner - 2) * 90;
            int cornerEnd = cornerStart + 90;
            for (int i = cornerStart; i <= cornerEnd; i += 10) {
                float angle = (float)Math.toRadians(i);
                float rx = corners[corner][0] + (float)(Math.cos(angle) * r);
                float ry = corners[corner][1] + (float)(Math.sin(angle) * r);
                buf.addVertex(mat, rx, ry, 0).setColor(color);
            }
        }

        buf.addVertex(mat, x + w, y + h, 0).setColor(color);
        buf.addVertex(mat, x, y + h, 0).setColor(color);
        buf.addVertex(mat, x, corners[0][1], 0).setColor(color); // connect last to first vertex

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    public static void fillRoundTabBottom(GuiGraphics context, int x, int y, int w, int h, int r, int color) {
        r = MathUtils.clamp(r, 0, Math.min(w, h) / 2);

        BufferBuilder buf = getTesselator().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = context.pose().last().pose();

        buf.addVertex(mat, x + w / 2F, y + h / 2F, 0).setColor(color);

        int[][] corners = {
                { x + w - r, y + h - r},
                { x + r, y + h - r }
        };

        for (int corner = 0; corner < 2; corner++) {
            int cornerStart = corner * 90;
            int cornerEnd = cornerStart + 90;
            for (int i = cornerStart; i <= cornerEnd; i += 10) {
                float angle = (float)Math.toRadians(i);
                float rx = corners[corner][0] + (float)(Math.cos(angle) * r);
                float ry = corners[corner][1] + (float)(Math.sin(angle) * r);
                buf.addVertex(mat, rx, ry, 0).setColor(color);
            }
        }

        buf.addVertex(mat, x, y, 0).setColor(color);
        buf.addVertex(mat, x + w, y, 0).setColor(color);
        buf.addVertex(mat, x + w, corners[0][1], 0).setColor(color); // connect last to first vertex

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    public static void fillRoundHoriLine(GuiGraphics context, int x, int y, int length, int thickness, int color) {
        fillRoundRect(context, x, y, length, thickness, thickness / 2, color);
    }

    public static void fillRoundVertLine(GuiGraphics context, int x, int y, int length, int thickness, int color) {
        fillRoundRect(context, x, y, thickness, length, thickness / 2, color);
    }
    
    // draw

    public static void drawRect(GuiGraphics context, int x, int y, int w, int h, int color) {
        drawHorLine(context, x, y, w, color);
        drawVerLine(context, x, y + 1, h - 2, color);
        drawVerLine(context, x + w - 1, y + 1, h - 2, color);
        drawHorLine(context, x, y + h - 1, w, color);
    }

    public static void drawBox(GuiGraphics context, int x, int y, int w, int h, int color) {
        drawLine(context, x, y, x + w, y, color);
        drawLine(context, x, y + h, x + w, y + h, color);
        drawLine(context, x, y, x, y + h, color);
        drawLine(context, x + w, y, x + w, y + h, color);
    }

    public static void drawHorLine(GuiGraphics context, int x, int y, int length, int color) {
        fillRect(context, x, y, length, 1, color);
    }

    public static void drawVerLine(GuiGraphics context, int x, int y, int length, int color) {
        fillRect(context, x, y, 1, length, color);
    }

    public static void drawLine(GuiGraphics context, int x1, int y1, int x2, int y2, int color) {
        BufferBuilder buf = getTesselator().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = context.pose().last().pose();

        buf.addVertex(mat, (float)x1, (float)y1, 0).setColor(color);
        buf.addVertex(mat, (float)x2, (float)y2, 0).setColor(color);

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    public static void drawArc(GuiGraphics context, int cX, int cY, int radius, int start, int end, int color) {
        BufferBuilder buf = getTesselator().begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = context.pose().last().pose();

        for (int i = start - 90; i <= end - 90; i++) {
            double angle = Math.toRadians(i);
            float x = (float)(Math.cos(angle) * radius) + cX;
            float y = (float)(Math.sin(angle) * radius) + cY;
            buf.addVertex(mat, x, y, 0).setColor(color);
        }

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    public static void drawCircle(GuiGraphics context, int cX, int cY, int radius, int color) {
        drawArc(context, cX, cY, radius, 0, 360, color);
    }

    public static void drawRoundRect(GuiGraphics context, int x, int y, int w, int h, int r, int color) {
        r = MathUtils.clamp(r, 0, Math.min(w, h) / 2);

        BufferBuilder buf = getTesselator().begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f mat = context.pose().last().pose();

        int[][] corners = {
                { x + w - r, y + r },
                { x + w - r, y + h - r},
                { x + r, y + h - r },
                { x + r, y + r }
        };

        for (int corner = 0; corner < 4; corner++) {
            int cornerStart = (corner - 1) * 90;
            int cornerEnd = cornerStart + 90;
            for (int i = cornerStart; i <= cornerEnd; i += 10) {
                float angle = (float)Math.toRadians(i);
                float rx = corners[corner][0] + (float)(Math.cos(angle) * r);
                float ry = corners[corner][1] + (float)(Math.sin(angle) * r);
                buf.addVertex(mat, rx, ry, 0).setColor(color);
            }
        }

        buf.addVertex(mat, corners[0][0], y, 0).setColor(color); // connect last to first vertex

        beginRendering();
        drawBuffer(buf);
        finishRendering();
    }

    public static void drawRoundHoriLine(GuiGraphics context, int x, int y, int length, int thickness, int color) {
        drawRoundRect(context, x, y, length, thickness, thickness / 2, color);
    }

    public static void drawRoundVertLine(GuiGraphics context, int x, int y, int length, int thickness, int color) {
        drawRoundRect(context, x, y, thickness, length, thickness / 2, color);
    }

    // default text

    public static void drawDefaultScaledComponent(GuiGraphics context, Component text, int x, int y, float scale, boolean shadow, int color) {
        PoseStack m = context.pose();
        m.scale(scale, scale, scale);

        float rescale = 1 / scale;
        x = (int)(x * rescale);
        y = (int)(y * rescale);

        drawDefaultComponent(context, text, x, y, shadow, color);
        m.scale(rescale, rescale, rescale);
    }

    public static void drawDefaultCenteredScaledComponent(GuiGraphics context, Component text, int centerX, int y, float scale, boolean shadow, int color) {
        PoseStack m = context.pose();
        m.scale(scale, scale, scale);

        float rescale = 1 / scale;
        centerX = (int)(centerX * rescale);
        centerX = centerX - (mc.font.width(text) / 2);
        y = (int)(y * rescale);

        drawDefaultComponent(context, text, centerX, y, shadow, color);
        m.scale(rescale, rescale, rescale);
    }

    public static void drawDefaultRightScaledComponent(GuiGraphics context, Component text, int rightX, int y, float scale, boolean shadow, int color) {
        PoseStack m = context.pose();
        m.scale(scale, scale, scale);

        float rescale = 1 / scale;
        rightX = (int)(rightX * rescale);
        rightX = rightX - mc.font.width(text);
        y = (int)(y * rescale);

        drawDefaultComponent(context, text, rightX, y, shadow, color);
        m.scale(rescale, rescale, rescale);
    }

    public static void drawDefaultScaledComponent(GuiGraphics context, Component text, int x, int y, float scale, boolean shadow) {
        drawDefaultScaledComponent(context, text, x, y, scale, shadow, 0xFFFFFFFF);
    }

    public static void drawDefaultCenteredScaledComponent(GuiGraphics context, Component text, int centerX, int y, float scale, boolean shadow) {
        drawDefaultCenteredScaledComponent(context, text, centerX, y, scale, shadow, 0xFFFFFFFF);
    }

    public static void drawDefaultRightScaledComponent(GuiGraphics context, Component text, int rightX, int y, float scale, boolean shadow) {
        drawDefaultRightScaledComponent(context, text, rightX, y, scale, shadow, 0xFFFFFFFF);
    }

    public static void drawDefaultComponent(GuiGraphics context, Component text, int x, int y, boolean shadow, int color) {
        context.drawString(mc.font, text, x, y, color, shadow);
    }

    public static void drawDefaultCode(GuiGraphics context, String code, int x, int y, boolean shadow, int color) {
        context.drawString(system.codeRenderer, code, x, y, color, shadow);
    }

    // non-default
    // draw normal text

    public static void drawText(GuiGraphics context, String text, int x, int y, float scale, boolean shadow) {
        drawDefaultScaledComponent(context, Component.literal(text), x, y, scale, shadow);
    }

    public static void drawText(GuiGraphics context, String text, int x, int y, boolean shadow) {
        drawDefaultScaledComponent(context, Component.literal(text), x, y, 1.0F, shadow);
    }

    // draw right-aligned text

    public static void drawRightComponent(GuiGraphics context, String text, int leftX, int y, float scale, boolean shadow) {
        drawDefaultRightScaledComponent(context, Component.literal(text), leftX, y, scale, shadow);
    }

    public static void drawRightComponent(GuiGraphics context, String text, int leftX, int y, boolean shadow) {
        drawDefaultRightScaledComponent(context, Component.literal(text), leftX, y, 1.0F, shadow);
    }

    public static void drawRightComponent(GuiGraphics context, Component text, int leftX, int y, float scale, boolean shadow) {
        drawDefaultRightScaledComponent(context, text, leftX, y, scale, shadow);
    }

    public static void drawRightComponent(GuiGraphics context, Component text, int leftX, int y, boolean shadow) {
        drawDefaultRightScaledComponent(context, text, leftX, y, 1.0F, shadow);
    }

    // draw centered text

    public static void drawCenteredComponent(GuiGraphics context, String text, int centerX, int y, float scale, boolean shadow) {
        drawDefaultCenteredScaledComponent(context, Component.literal(text), centerX, y, scale, shadow);
    }

    public static void drawCenteredComponent(GuiGraphics context, String text, int centerX, int y, boolean shadow) {
        drawDefaultCenteredScaledComponent(context, Component.literal(text), centerX, y, 1.0F, shadow);
    }

    public static void drawCenteredComponent(GuiGraphics context, Component text, int centerX, int y, float scale, boolean shadow) {
        drawDefaultCenteredScaledComponent(context, text, centerX, y, scale, shadow);
    }

    public static void drawCenteredComponent(GuiGraphics context, Component text, int centerX, int y, boolean shadow) {
        drawDefaultCenteredScaledComponent(context, text, centerX, y, 1.0F, shadow);
    }

    // misc

    public static void drawComponenture(GuiGraphics context, ResourceLocation texture, int x, int y, int w, int h) {
        BufferBuilder buf = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f mat = context.pose().last().pose();

        buf.addVertex(mat, x, y, 0).setUv(0, 0);
        buf.addVertex(mat, x, y + h, 0).setUv(0, 1);
        buf.addVertex(mat, x + w, y + h, 0).setUv(1, 1);
        buf.addVertex(mat, x + w, y, 0).setUv(1, 0);

        disableCull();
        setShader(GameRenderer::getPositionTexShader);
        setShaderTexture(0, texture);

        BufferUploader.drawWithShader(buf.buildOrThrow());

        enableCull();
    }

    public static void drawRoundComponenture(GuiGraphics context, ResourceLocation texture, int x, int y, int w, int h, int r) {
        r = MathUtils.clamp(r, 0, Math.min(w, h) / 2);

        BufferBuilder buf = getTesselator().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_TEX);
        Matrix4f mat = context.pose().last().pose();

        buf.addVertex(mat, x + w / 2F, y + h / 2F, 0).setUv(0.5F, 0.5F);

        int[][] corners = {
                { x + w - r, y + r },
                { x + w - r, y + h - r},
                { x + r, y + h - r },
                { x + r, y + r }
        };

        for (int corner = 0; corner < 4; corner++) {
            int cornerStart = (corner - 1) * 90;
            int cornerEnd = cornerStart + 90;
            for (int i = cornerStart; i <= cornerEnd; i += 10) {
                float angle = (float)Math.toRadians(i);
                float rx = corners[corner][0] + (float)(Math.cos(angle) * r);
                float ry = corners[corner][1] + (float)(Math.sin(angle) * r);
                float u = (rx - x) / w;
                float v = (ry - y) / h;
                buf.addVertex(mat, rx, ry, 0).setUv(u, v);
            }
        }

        buf.addVertex(mat, corners[0][0], y, 0).setUv(((float)corners[0][0] - x) / w, 0); // connect last to first vertex

        disableCull();
        setShader(GameRenderer::getPositionTexShader);
        setShaderTexture(0, texture);

        BufferUploader.drawWithShader(buf.buildOrThrow());

        enableCull();
    }

    public static void drawItem(GuiGraphics context, ItemStack item, int x, int y, float scale) {
        x = (int)(x / scale);
        y = (int)(y / scale);
        context.pose().pushPose();
        context.pose().scale(scale, scale, scale);
        context.renderItem(item, x, y);
        context.renderItemDecorations(mc.font, item, x, y);
        context.pose().popPose();
    }

    public static void drawItem(GuiGraphics context, ItemStack item, int x, int y, float scale, String text) {
        x = (int)(x / scale);
        y = (int)(y / scale);
        context.pose().pushPose();
        context.pose().scale(scale, scale, scale);
        context.renderItem(item, x, y);
        context.renderItemDecorations(mc.font, item, x, y, text);
        context.pose().popPose();
    }

    public static void drawItem(GuiGraphics context, ItemStack item, int x, int y, int size) {
        drawItem(context, item, x, y, size / 16.0F);
    }

    public static void drawItem(GuiGraphics context, ItemStack item, int x, int y) {
        drawItem(context, item, x, y, 1.0F);
    }

    // util

    public static void beginRendering() {
        disableCull();
        enableBlend();
        defaultBlendFunc();
        setShader(GameRenderer::getPositionColorShader);
    }

    public static void finishRendering() {
        enableCull();
        disableBlend();
        setShader(GameRenderer::getPositionTexShader);
    }

    public static void check(boolean check, String msg) {
        if (!check) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void drawBuffer(BufferBuilder buf) {
        BufferUploader.drawWithShader(buf.buildOrThrow());
    }

    public static Tesselator getTesselator() { return Tesselator.getInstance();
    }

    public static int width() {
        return mc.getWindow().getGuiScaledWidth();
    }

    public static int height() {
        return mc.getWindow().getGuiScaledHeight();
    }

    public static void setCursor(int x, int y) {
        Window win = mc.getWindow();
        int w1 = win.getWidth();
        int w2 = win.getGuiScaledWidth();
        int h1 = win.getHeight();
        int h2 = win.getGuiScaledWidth();
        double ratW = (double)w2 / (double)w1;
        double ratH = (double)h2 / (double)h1;
        GLFW.glfwSetCursorPos(win.getWindow(), x / ratW, y / ratH);
    }

    public static Point getCursor() {
        Window win = mc.getWindow();
        int w1 = win.getWidth();
        int w2 = win.getGuiScaledWidth();
        int h1 = win.getHeight();
        int h2 = win.getGuiScaledWidth();
        double rW = (double)w2 / (double)w1;
        double rH = (double)h2 / (double)h1;
        return new Point((int)(rW * mc.mouseHandler.xpos()), (int)(rH * mc.mouseHandler.ypos()));
    }
}
