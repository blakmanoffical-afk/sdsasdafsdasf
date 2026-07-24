package duck.menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class DuckButton extends ClickableWidget {
    private final Runnable action;
    private final String icon;
    private final boolean compact;

    public DuckButton(int x, int y, int width, int height, Text message, String icon, boolean compact, Runnable action) {
        super(x, y, width, height, message);
        this.action = action;
        this.icon = icon;
        this.compact = compact;
    }

    @Override
    protected void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        boolean hovered = this.isHovered();
        int x = this.getX();
        int y = this.getY();
        int w = this.getWidth();
        int h = this.getHeight();
        int radius = h / 2;

        // Görseldeki gri buton gövdesi
        int baseColor = hovered ? 0xE68A8A8A : 0xB86E6E6E;
        DuckMenuRender.roundedRect(ctx, x, y, w, h, radius, baseColor);

        if (hovered) {
            DuckMenuRender.roundedRectOutline(ctx, x, y, w, h, radius, 0x88FFFFFF);
        }

        int iconColor = 0xFF1A1A1A;
        int textColor = 0xFF262626;

        if (this.compact) {
            // Sağ alttaki Discord butonu stili
            int badgeW = h - 4;
            int badgeX = x + w - badgeW - 2;
            int badgeY = y + 2;
            DuckMenuRender.roundedRect(ctx, badgeX, badgeY, badgeW, badgeW, badgeW / 2, 0x40FFFFFF);
            if (this.icon != null) {
                DuckMenuRender.drawIcon(ctx, this.icon, badgeX + 4, badgeY + 4, badgeW - 8, iconColor);
            }

            String label = this.getMessage().getString();
            int textX = x + (w - badgeW) / 2 - MinecraftClient.getInstance().textRenderer.getWidth(label) / 2;
            int textY = y + (h - 8) / 2;
            ctx.drawText(MinecraftClient.getInstance().textRenderer, label, textX, textY, textColor, false);
        } else {
            // Sol ana menü buton stili (ikon solundaki koyu daire rozet ile)
            int badgeRadius = radius;
            int badgeW = h;
            DuckMenuRender.roundedRect(ctx, x, y, badgeW, h, badgeRadius, 0x33000000);

            int iconSize = h - 12;
            int iconX = x + (badgeW - iconSize) / 2;
            int iconY = y + (h - iconSize) / 2;
            if (this.icon != null) {
                DuckMenuRender.drawIcon(ctx, this.icon, iconX, iconY, iconSize, iconColor);
            }

            String label = this.getMessage().getString();
            int textX = x + badgeW + 12;
            int textY = y + (h - 8) / 2;
            ctx.drawText(MinecraftClient.getInstance().textRenderer, label, textX, textY, textColor, false);
        }
    }

    @Override
    public void onClick(Click click, boolean doubled) {
        if (this.action != null) this.action.run();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}
}
