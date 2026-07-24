package duck.menu.mixin;

import duck.menu.DuckButton;
import duck.menu.DuckMenuRender;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(TitleScreen.class)
public abstract class DuckTitleScreenMixin extends Screen {
    private static final String DISCORD_URL = "https://discord.gg/duckware";

    private final List<DuckButton> duckButtons = new ArrayList<>();
    private DuckButton duckDiscord;
    private float[] starX;
    private float[] starY;
    private float[] starSize;
    private float[] starPhase;

    private DuckTitleScreenMixin(Text title) {
        super(title);
    }

    private void duckGenerateStars() {
        int count = 260;
        starX = new float[count];
        starY = new float[count];
        starSize = new float[count];
        starPhase = new float[count];
        Random rng = new Random(1337L);
        for (int i = 0; i < count; i++) {
            starX[i] = rng.nextFloat();
            starY[i] = rng.nextFloat();
            starSize[i] = rng.nextFloat() < 0.85f ? 1f : 2f;
            starPhase[i] = rng.nextFloat() * 6.2831853f;
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void duckInit(CallbackInfo ci) {
        this.clearChildren();
        this.duckButtons.clear();
        if (this.starX == null) duckGenerateStars();

        int W = this.width;
        int H = this.height;
        int btnW = clamp((int) (W * 0.22f), 150, 220);
        int btnH = clamp((int) (H * 0.08f), 28, 40);
        int gap = Math.max(10, (int) (H * 0.022f));
        int startX = Math.max(20, (int) (W * 0.05f));
        int totalBtn = 5 * btnH + 4 * gap;
        int startY = (int) (H * 0.55f);
        if (startY + totalBtn > H * 0.92f) startY = (int) (H * 0.92f - totalBtn);

        addButton(startX, startY + 0 * (btnH + gap), btnW, btnH, "Singleplayer", "single",
                () -> this.client.setScreen(new SelectWorldScreen(this)));
        addButton(startX, startY + 1 * (btnH + gap), btnW, btnH, "Multiplayer", "multi",
                () -> this.client.setScreen(new MultiplayerScreen(this)));
        addButton(startX, startY + 2 * (btnH + gap), btnW, btnH, "Options", "options",
                () -> this.client.setScreen(new OptionsScreen(this, this.client.options)));
        addButton(startX, startY + 3 * (btnH + gap), btnW, btnH, "ClickGUI", "clickgui",
                this::duckOpenClickGui);
        addButton(startX, startY + 4 * (btnH + gap), btnW, btnH, "Quit Game", "quit",
                () -> this.client.scheduleStop());

        int dW = clamp((int) (W * 0.15f), 110, 150);
        int dH = btnH;
        this.duckDiscord = new DuckButton(W - dW - startX, H - dH - Math.max(16, (int) (H * 0.05f)), dW, dH,
                Text.literal("Discord"), "discord", true, () -> {
            try {
                Util.getOperatingSystem().open(new URI(DISCORD_URL));
            } catch (Exception ignored) {}
        });
        this.addDrawableChild(this.duckDiscord);
    }

    private static int clamp(int v, int lo, int hi) {
        return v < lo ? lo : (v > hi ? hi : v);
    }

    private void addButton(int x, int y, int w, int h, String label, String icon, Runnable action) {
        DuckButton b = new DuckButton(x, y, w, h, Text.literal(label), icon, false, action);
        this.duckButtons.add(b);
        this.addDrawableChild(b);
    }

    private void duckOpenClickGui() {
        try {
            Class<?> clazz = Class.forName("namidevelopment.kiriyaga.nami.impl.gui.screen.ClickGuiScreen");
            Object screen = clazz.getConstructor().newInstance();
            this.client.setScreen((Screen) screen);
        } catch (Throwable ignored) {}
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void duckRender(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int w = this.width;
        int h = this.height;

        ctx.fill(0, 0, w, h, 0xFF050507);

        float time = (System.currentTimeMillis() % 100000L) / 1000f;
        if (starX != null) {
            for (int i = 0; i < starX.length; i++) {
                int sx = (int) (starX[i] * w);
                int sy = (int) (starY[i] * h);
                float tw = 0.55f + 0.45f * (float) Math.sin(time * 1.6f + starPhase[i]);
                int a = (int) (150 + 105 * tw);
                int col = (a << 24) | 0xFFFFFF;
                int s = (int) starSize[i];
                ctx.fill(sx, sy, sx + s, sy + s, col);
            }
        }

        duckDrawTitle(ctx);
        duckDrawLogo(ctx, w, h);

        String welcome = "Welcome back, [" + this.client.getSession().getUsername() + "]";
        int wcX = w / 2 - this.textRenderer.getWidth(welcome) / 2;
        int wcY = (int) (h * 0.55f);
        ctx.drawText(this.textRenderer, welcome, wcX + 1, wcY + 1, 0x80000000, false);
        ctx.drawText(this.textRenderer, welcome, wcX, wcY, 0xFFEDEDED, false);

        String version = "version 1.1.2";
        ctx.drawText(this.textRenderer, version, Math.max(20, (int) (w * 0.05f)), h - 20, 0xFFBFBFBF, false);

        for (DuckButton b : this.duckButtons) {
            b.render(ctx, mouseX, mouseY, delta);
        }
        if (this.duckDiscord != null) {
            this.duckDiscord.render(ctx, mouseX, mouseY, delta);
        }

        ci.cancel();
    }

    // DÜZELTİLDİ: eskiden 8 yönde (üst/alt/sol/sağ/çaprazlar) aynı alfa ile
    // metin tekrar tekrar basılıyordu -> pixel fontta bu "glitch/tarama" gibi
    // görünüyordu (2. ekran görüntüsündeki bozuk görünüm). Şimdi tek yönlü,
    // yumuşak bir gölge kullanılıyor; sonuç mockup'taki temiz glow'a çok daha yakın.
    private void duckDrawTitle(DrawContext ctx) {
        Matrix3x2fStack m = ctx.getMatrices();
        String title = "DUCK WARE";
        float scale = Math.max(2.0f, Math.min(3.2f, this.height / 140f));
        int baseX = Math.max(20, (int) (this.width * 0.05f));
        int baseY = (int) (this.height * 0.08f);
        m.pushMatrix();
        m.translate(baseX, baseY);
        m.scale(scale, scale);

        int[][] glow = {{1, 1}, {2, 2}};
        for (int[] g : glow) {
            ctx.drawText(this.textRenderer, title, g[0], g[1], 0x55000000, false);
        }
        ctx.drawText(this.textRenderer, title, 0, 0, 0xFFFFFFFF, false);

        m.popMatrix();
    }

    private void duckDrawLogo(DrawContext ctx, int w, int h) {
        int cols = DuckMenuRender.DUCK[0].length();
        int rows = DuckMenuRender.DUCK.length;
        int logoH = clamp((int) (h * 0.15f), 80, 200);
        int cell = Math.max(1, logoH / rows);
        int totalW = cell * cols;
        int totalH = cell * rows;
        int cx = w / 2;
        int x = cx - totalW / 2;
        int y = (int) (h * 0.35f) - totalH / 2;
        DuckMenuRender.drawBitmap(ctx, DuckMenuRender.DUCK, x, y, totalW, totalH, 0xFFFFFFFF);
    }
}
