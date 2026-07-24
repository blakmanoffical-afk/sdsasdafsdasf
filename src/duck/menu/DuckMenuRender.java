package duck.menu;

import net.minecraft.client.gui.DrawContext;

public final class DuckMenuRender {
    private DuckMenuRender() {}

    // 3x3 grid logo as shown in the mockup (3 blocks separated by a cross)
    public static final String[] DUCK = {
        "111011101",
        "111011101",
        "111011101",
        "000111000",
        "000111000",
        "000111000",
        "111011101",
        "111011101",
        "111011101"
    };

    private static final String[] IC_SINGLE = {
        "00011111000",
        "00111111100",
        "00111111100",
        "00011111000",
        "00000000000",
        "01111111110",
        "11111111111",
        "11111111111",
        "01111111110"
    };

    private static final String[] IC_MULTI = {
        "00110001100",
        "01111011110",
        "01111011110",
        "00110001100",
        "00000000000",
        "11111111111",
        "11111111111",
        "11111111111"
    };

    // DÜZELTİLDİ: eskiden "#" gibi görünüyordu (iki dikey + iki tam yatay çizgi).
    // Şimdi köşeleri kesilmiş bir dişli (gear) şekli çiziyor.
    private static final String[] IC_OPTIONS = {
        "00011100000",
        "00011100000",
        "00111111100",
        "11100000111",
        "11000000011",
        "11100000111",
        "00111111100",
        "00011100000",
        "00011100000"
    };

    // DÜZELTİLDİ: eskiden tam simetrik "X" gibi görünüyordu.
    // Şimdi tek çapraz kılıç (bıçak + tutamak) şekli çiziyor.
    private static final String[] IC_CLICKGUI = {
        "00000000011",
        "00000000110",
        "00000001100",
        "00000011000",
        "00000110000",
        "00001111000",
        "00000110000",
        "00000110000",
        "00001111000"
    };

    private static final String[] IC_QUIT = {
        "01111110000",
        "01000010000",
        "01000010010",
        "01000010110",
        "01000011110",
        "01000010110",
        "01000010010",
        "01000010000",
        "01111110000"
    };

    private static final String[] IC_DISCORD = {
        "00111111100",
        "01111111110",
        "01011011010",
        "01111111110",
        "01111111110",
        "01011011010",
        "01111111110",
        "00111111100"
    };

    public static void drawIcon(DrawContext ctx, String type, int x, int y, int size, int color) {
        String[] map;
        switch (type) {
            case "single": map = IC_SINGLE; break;
            case "multi": map = IC_MULTI; break;
            case "options": map = IC_OPTIONS; break;
            case "clickgui": map = IC_CLICKGUI; break;
            case "quit": map = IC_QUIT; break;
            case "discord": map = IC_DISCORD; break;
            default: return;
        }
        drawBitmap(ctx, map, x, y, size, size, color);
    }

    public static void drawBitmap(DrawContext ctx, String[] map, int x, int y, int boxW, int boxH, int color) {
        int rows = map.length;
        int cols = map[0].length();
        int cell = Math.max(1, Math.min(boxW / cols, boxH / rows));
        int offX = x + (boxW - cell * cols) / 2;
        int offY = y + (boxH - cell * rows) / 2;
        for (int r = 0; r < rows; r++) {
            String row = map[r];
            for (int c = 0; c < cols; c++) {
                if (row.charAt(c) == '1') {
                    int px = offX + c * cell;
                    int py = offY + r * cell;
                    ctx.fill(px, py, px + cell, py + cell, color);
                }
            }
        }
    }

    public static void roundedRect(DrawContext ctx, int x, int y, int w, int h, int r, int color) {
        if (r * 2 > h) r = h / 2;
        if (r * 2 > w) r = w / 2;
        ctx.fill(x + r, y, x + w - r, y + h, color);
        ctx.fill(x, y + r, x + r, y + h - r, color);
        ctx.fill(x + w - r, y + r, x + w, y + h - r, color);
        fillCircleQuadrant(ctx, x + r, y + r, r, -1, -1, color);
        fillCircleQuadrant(ctx, x + w - r, y + r, r, 1, -1, color);
        fillCircleQuadrant(ctx, x + r, y + h - r, r, -1, 1, color);
        fillCircleQuadrant(ctx, x + w - r, y + h - r, r, 1, 1, color);
    }

    private static void fillCircleQuadrant(DrawContext ctx, int cx, int cy, int r, int dirX, int dirY, int color) {
        for (int dy = 0; dy < r; dy++) {
            for (int dx = 0; dx < r; dx++) {
                if (dx * dx + dy * dy <= r * r) {
                    int px = cx + (dirX < 0 ? -1 - dx : dx);
                    int py = cy + (dirY < 0 ? -1 - dy : dy);
                    ctx.fill(px, py, px + 1, py + 1, color);
                }
            }
        }
    }

    public static void roundedRectOutline(DrawContext ctx, int x, int y, int w, int h, int r, int color) {
        ctx.fill(x + r, y, x + w - r, y + 1, color);
        ctx.fill(x + r, y + h - 1, x + w - r, y + h, color);
        ctx.fill(x, y + r, x + 1, y + h - r, color);
        ctx.fill(x + w - 1, y + r, x + w, y + h - r, color);
    }
}
