package com.rinke.hotbarreplace;

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
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

@Environment(EnvType.CLIENT)
public class HotbarReplaceClient implements ClientModInitializer {
	private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

	// Hotbar slots in the player screen handler start at index 36 (0-8 hotbar, 45 offhand)
	private static final int HOTBAR_SLOT_OFFSET = 36;
	private static final int OFFHAND_SLOT = 9;

	@Override
	public void onInitializeClient() {
		HotbarReplace.LOGGER.info("HotbarReplace initialised");
	}

	/**
	 * Searches the player's current screen handler for another stack of {@code item}
	 * and swap-clicks it into the hand slot that just ran out.
	 */
	public static void tryReplaceSlot(PlayerEntity player, Hand hand, Item item) {
		if (player == null || item == null || player.isSpectator() || player.getAbilities().creativeMode) {
			return;
		}
		PlayerInventory inventory = player.getInventory();
		if (inventory == null || inventory.isEmpty() || player.currentScreenHandler == null) {
			return;
		}
		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null || client.interactionManager == null) {
			return;
		}
		for (int i = 0; i < player.currentScreenHandler.slots.size(); ++i) {
			if (!player.currentScreenHandler.slots.get(i).getStack().isOf(item)) {
				continue;
			}
			// Wait roughly one frame between the pick-up click and the put-down click
			int currentFps = client.getCurrentFps();
			if (currentFps <= 0) {
				currentFps = 60;
			}
			long clickDelayMs = Math.round(1000.0f / currentFps);
			client.interactionManager.clickSlot(player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, player);
			int slot = hand == Hand.OFF_HAND ? OFFHAND_SLOT : inventory.getSelectedSlot();
			SCHEDULER.schedule(() -> client.execute(() -> {
				if (client.interactionManager != null && player.currentScreenHandler != null) {
					client.interactionManager.clickSlot(player.currentScreenHandler.syncId, slot + HOTBAR_SLOT_OFFSET, 0, SlotActionType.PICKUP, player);
				}
			}), clickDelayMs, TimeUnit.MILLISECONDS);
			return;
		}
	}
}
