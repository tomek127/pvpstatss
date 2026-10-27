package pl.pvpstats;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class StatsScreen extends Screen {
    private static final int W = 240, H = 190;
    private ButtonWidget toggle;

    public StatsScreen() { super(Text.literal("PvP Stats")); }

    @Override
    protected void init() {
        int x = (width - W) / 2, y = (height - H) / 2;
        toggle = addDrawableChild(ButtonWidget.builder(label(), b -> {
            if (DuelStats.running) DuelStats.stop(); else DuelStats.start();
            b.setMessage(label());
        }).dimensions(x + 12, y + H - 52, 105, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("§eReset"), b -> {
            DuelStats.reset();
            toggle.setMessage(label());
        }).dimensions(x + W - 117, y + H - 52, 105, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Zamknij"), b -> close())
                .dimensions(x + 12, y + H - 28, W - 24, 20).build());
    }

    private Text label() {
        return Text.literal(DuelStats.running ? "§c■ Stop" : "§a▶ Start");
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta); // tło + przyciski
        int x = (width - W) / 2, y = (height - H) / 2;
        ctx.fill(x, y, x + W, y + H, 0xE0101018);
        ctx.fill(x, y, x + W, y + 22, 0xFF2B2B55);
        ctx.fill(x, y + 22, x + W, y + 23, 0xFF6C6CFF);
        ctx.drawCenteredTextWithShadow(textRenderer, "§l⚔ PvP Stats", width / 2, y + 7, 0xFFFFFF);

        String status = DuelStats.running ? "§a● Duel trwa" : "§7● Zatrzymane";
        ctx.drawCenteredTextWithShadow(textRenderer, status, width / 2, y + 30, 0xFFFFFF);

        int ly = y + 48, lx = x + 18, rx = x + W - 18;
        row(ctx, lx, rx, ly,       "Trafienia",      "§f" + DuelStats.hits);
        row(ctx, lx, rx, ly + 14,  "Machnięcia",     "§f" + DuelStats.swings);
        row(ctx, lx, rx, ly + 28,  "Celność",        color(DuelStats.accuracy()) + String.format("%.1f%%", DuelStats.accuracy()));
        row(ctx, lx, rx, ly + 42,  "Combo",          "§e" + DuelStats.combo);
        row(ctx, lx, rx, ly + 56,  "Najlepsze combo","§6§l" + DuelStats.bestCombo);
        row(ctx, lx, rx, ly + 70,  "Otrzymane",      "§c" + DuelStats.hitsTaken);
        row(ctx, lx, rx, ly + 84,  "Czas",           "§b" + String.format("%.1fs", DuelStats.seconds()));
    }

    private void row(DrawContext ctx, int lx, int rx, int y, String name, String value) {
        ctx.drawTextWithShadow(textRenderer, "§7" + name, lx, y, 0xFFFFFF);
        ctx.drawTextWithShadow(textRenderer, value, rx - textRenderer.getWidth(value.replaceAll("§.", "")), y, 0xFFFFFF);
    }

    static String color(double acc) {
        return acc >= 70 ? "§a" : acc >= 40 ? "§e" : "§c";
    }

    @Override
    public boolean shouldPause() { return false; }
}
