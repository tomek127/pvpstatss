package pl.pvpstats;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

public class PvpStatsClient implements ClientModInitializer {
    private static KeyBinding openKey;

    @Override
    public void onInitializeClient() {
        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pvpstats.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.pvpstats"));

        // Zliczanie machnięć i trafień w graczy
        ClientPreAttackCallback.EVENT.register((client, player, clickCount) -> {
            if (!DuelStats.running || clickCount == 0) return false;
            HitResult hr = client.crosshairTarget;
            boolean hit = hr instanceof EntityHitResult ehr
                    && ehr.getEntity() instanceof PlayerEntity target
                    && target != player && !target.isSpectator();
            DuelStats.onSwing(hit);
            return false; // nie blokuj ataku
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            DuelStats.tick(client.player);
            while (openKey.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new StatsScreen());
            }
        });

        // Mały HUD podczas duela
        HudRenderCallback.EVENT.register((ctx, tickCounter) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (!DuelStats.running || mc.options.hudHidden || mc.currentScreen != null) return;
            int x = 6, y = 6;
            ctx.fill(x - 3, y - 3, x + 112, y + 58, 0x90000000);
            ctx.drawTextWithShadow(mc.textRenderer, "§l⚔ PvP Stats", x, y, 0xFFFFFF);
            ctx.drawTextWithShadow(mc.textRenderer, "§7Trafienia: §f" + DuelStats.hits + "§7/§f" + DuelStats.swings, x, y + 12, 0xFFFFFF);
            ctx.drawTextWithShadow(mc.textRenderer, "§7Celność: " + StatsScreen.color(DuelStats.accuracy())
                    + String.format("%.1f%%", DuelStats.accuracy()), x, y + 24, 0xFFFFFF);
            ctx.drawTextWithShadow(mc.textRenderer, "§7Combo: §e" + DuelStats.combo, x, y + 36, 0xFFFFFF);
            ctx.drawTextWithShadow(mc.textRenderer, "§7Best: §6" + DuelStats.bestCombo, x, y + 48, 0xFFFFFF);
        });
    }
}
