package pl.pvpstats;

import net.minecraft.client.network.ClientPlayerEntity;

public final class DuelStats {
    private static final int COMBO_TIMEOUT_TICKS = 60; // 3 s bez trafienia = koniec combo

    public static boolean running;
    public static int hits, swings, hitsTaken, combo, bestCombo;
    public static long ticks, lastHitTick;
    private static int prevHurt;

    private DuelStats() {}

    public static void start() { reset(); running = true; }
    public static void stop() { running = false; }

    public static void reset() {
        running = false;
        hits = swings = hitsTaken = combo = bestCombo = 0;
        ticks = lastHitTick = 0;
        prevHurt = 0;
    }

    public static double accuracy() {
        return swings == 0 ? 0.0 : hits * 100.0 / swings;
    }

    public static double seconds() { return ticks / 20.0; }

    /** Wywoływane przy każdym kliknięciu LPM podczas duela. */
    public static void onSwing(boolean hitPlayer) {
        if (!running) return;
        swings++;
        if (hitPlayer) {
            hits++;
            if (ticks - lastHitTick > COMBO_TIMEOUT_TICKS) combo = 0;
            combo++;
            lastHitTick = ticks;
            if (combo > bestCombo) bestCombo = combo;
        }
    }

    public static void tick(ClientPlayerEntity player) {
        if (!running || player == null) return;
        ticks++;
        if (player.hurtTime > prevHurt) { // oberwałeś
            hitsTaken++;
            combo = 0;
        }
        prevHurt = player.hurtTime;
        if (combo > 0 && ticks - lastHitTick > COMBO_TIMEOUT_TICKS) combo = 0;
    }
}
