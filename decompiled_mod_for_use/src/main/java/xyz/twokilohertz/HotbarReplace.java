package xyz.twokilohertz;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public class HotbarReplace implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("hotbarreplace");
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void onInitializeClient() {
        LOGGER.info("HotbarReplace v0.1.4 initialised");
    }

    public static void tryReplaceSlot(PlayerEntity player, Hand hand, Item item) {
        if (player == null || player.isSpectator() || player.getAbilities().creativeMode) {
            return;
        }
        PlayerInventory inventory = player.getInventory();
        if (inventory == null || inventory.isEmpty() || player.currentScreenHandler == null) {
            return;
        }
        for (int i = 0; i < player.currentScreenHandler.slots.size(); ++i) {
            if (!player.currentScreenHandler.slots.get(i).getStack().isOf(item)) continue;
            if (client == null || client.interactionManager == null) {
                return;
            }
            int current_fps = client.getCurrentFps();
            // Fallback for division by zero if FPS is somehow reported as 0
            if (current_fps <= 0) {
                current_fps = 60;
            }
            int click_delay = Math.round(1.0f / (float)current_fps) * 1000;
            client.interactionManager.clickSlot(player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, player);
            int slot = hand == Hand.OFF_HAND ? 9 : inventory.selectedSlot;
            scheduler.schedule(() -> {
                if (client.interactionManager != null && player.currentScreenHandler != null) {
                    client.interactionManager.clickSlot(player.currentScreenHandler.syncId, slot + 36, 0, SlotActionType.PICKUP, player);
                }
            }, (long)click_delay, TimeUnit.MILLISECONDS);
            return;
        }
    }
}
