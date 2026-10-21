package net.fabricmc.reporting;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.Color;

public class MainDashboardScreen extends Screen {

    private static boolean fullbrightEnabled = false;
    private static boolean customCrosshairEnabled = true;
    private static boolean motionBlurEnabled = false;
    private static boolean espStyleEnabled = false;

    public MainDashboardScreen() {
        super(Text.literal("Reporting Client - Visuals"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        long time = System.currentTimeMillis();

        // Переливание цветов (радуга/градиент)
        float hue = (time % 4500L) / 4500.0f;
        int accentColor = Color.HSBtoRGB(hue, 0.8f, 1.0f);

        // Плавная пульсация интерфейса
        float pulse = 1.0f + 0.012f * (float) Math.sin(time / 250.0);

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int panelW = 320;
        int panelH = 210;

        context.getMatrices().push();
        context.getMatrices().translate(centerX, centerY, 0);
        context.getMatrices().scale(pulse, pulse, 1.0f);
        context.getMatrices().translate(-centerX, -centerY, 0);

        RenderSystem.enableBlend();

        // Полупрозрачный глубокий фон
        int bgPanelColor = 0xEE0D0D14;
        context.fill(centerX - panelW / 2, centerY - panelH / 2, centerX + panelW / 2, centerY + panelH / 2, bgPanelColor);

        // Внешняя светящаяся рамка
        int borderColor = 0x55000000 | (accentColor & 0x00FFFFFF);
        context.drawBorder(centerX - panelW / 2, centerY - panelH / 2, panelW, panelH, borderColor);

        // Верхняя акцентная полоска
        context.fill(centerX - panelW / 2, centerY - panelH / 2, centerX + panelW / 2, centerY - panelH / 2 + 3, accentColor);

        // Заголовки
        context.drawTextWithShadow(this.textRenderer, Text.literal("REPORTING CLIENT"), centerX - panelW / 2 + 14, centerY - panelH / 2 + 12, accentColor);
        context.drawTextWithShadow(this.textRenderer, Text.literal("1.21.1"), centerX + panelW / 2 - 45, centerY - panelH / 2 + 12, 0xFF666666);

        // Кнопки модулей
        int startY = centerY - panelH / 2 + 40;
        int spacing = 32;

        renderToggle(context, centerX - panelW / 2 + 15, startY, panelW - 30, 26, "FullBright (Яркость)", fullbrightEnabled, mouseX, mouseY, accentColor);
        renderToggle(context, centerX - panelW / 2 + 15, startY + spacing, panelW - 30, 26, "Custom Crosshair", customCrosshairEnabled, mouseX, mouseY, accentColor);
        renderToggle(context, centerX - panelW / 2 + 15, startY + spacing * 2, panelW - 30, 26, "Motion Blur", motionBlurEnabled, mouseX, mouseY, accentColor);
        renderToggle(context, centerX - panelW / 2 + 15, startY + spacing * 3, panelW - 30, 26, "ESP Render Highlights", espStyleEnabled, mouseX, mouseY, accentColor);

        RenderSystem.disableBlend();
        context.getMatrices().pop();

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderToggle(DrawContext context, int x, int y, int w, int h, String name, boolean active, int mx, int my, int accent) {
        boolean hovered = mx >= x && mx <= x + w && my >= y && my <= y + h;

        int bg = hovered ? 0x77202030 : 0x4412121A;
        context.fill(x, y, x + w, y + h, bg);
        context.drawBorder(x, y, w, h, hovered ? 0x44FFFFFF : 0x22FFFFFF);

        int indicatorColor = active ? accent : 0xFF353545;
        context.fill(x + 4, y + 6, x + 8, y + h - 6, indicatorColor);

        context.drawTextWithShadow(this.textRenderer, Text.literal(name), x + 16, y + 9, 0xFFE0E0E0);

        String statusText = active ? "ВКЛ" : "ВЫКЛ";
        int statusColor = active ? 0xFF55FF77 : 0xFFFF5555;
        context.drawTextWithShadow(this.textRenderer, Text.literal(statusText), x + w - 35, y + 9, statusColor);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int panelW = 320;
        int panelH = 210;
        int startY = centerY - panelH / 2 + 40;
        int spacing = 32;
        int w = panelW - 30;
        int x = centerX - panelW / 2 + 15;

        if (button == 0) {
            if (isInside(mouseX, mouseY, x, startY, w, 26)) fullbrightEnabled = !fullbrightEnabled;
            if (isInside(mouseX, mouseY, x, startY + spacing, w, 26)) customCrosshairEnabled = !customCrosshairEnabled;
            if (isInside(mouseX, mouseY, x, startY + spacing * 2, w, 26)) motionBlurEnabled = !motionBlurEnabled;
            if (isInside(mouseX, mouseY, x, startY + spacing * 3, w, 26)) espStyleEnabled = !espStyleEnabled;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isInside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
