package com.rinke.hotbarreplace;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;

@Environment(EnvType.CLIENT)
public class HotbarReplaceClient implements ClientModInitializer {
	private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

	// Hotbar slots in the player container menu start at index 36 (0-8 hotbar, 45 offhand)
	private static final int HOTBAR_SLOT_OFFSET = 36;
	private static final int OFFHAND_SLOT = 9;

	@Override
	public void onInitializeClient() {
		HotbarReplace.LOGGER.info("HotbarReplace initialised");
	}

	/**
	 * Searches the player's current container menu for another stack of {@code item}
	 * and swap-clicks it into the hand slot that just ran out.
	 */
	public static void tryReplaceSlot(Player player, InteractionHand hand, Item item) {
		if (player == null || item == null || player.isSpectator() || player.getAbilities().instabuild) {
			return;
		}
		Inventory inventory = player.getInventory();
		if (inventory == null || inventory.isEmpty() || player.containerMenu == null) {
			return;
		}
		Minecraft client = Minecraft.getInstance();
		if (client == null || client.gameMode == null) {
			return;
		}
		for (int i = 0; i < player.containerMenu.slots.size(); ++i) {
			if (player.containerMenu.slots.get(i).getItem().getItem() != item) {
				continue;
			}
			// Wait roughly one frame between the pick-up click and the put-down click
			int currentFps = client.getFps();
			if (currentFps <= 0) {
				currentFps = 60;
			}
			long clickDelayMs = Math.round(1000.0f / currentFps);
			client.gameMode.handleContainerInput(player.containerMenu.containerId, i, 0, ContainerInput.PICKUP, player);
			int slot = hand == InteractionHand.OFF_HAND ? OFFHAND_SLOT : inventory.getSelectedSlot();
			SCHEDULER.schedule(() -> client.execute(() -> {
				if (client.gameMode != null && player.containerMenu != null) {
					client.gameMode.handleContainerInput(player.containerMenu.containerId, slot + HOTBAR_SLOT_OFFSET, 0, ContainerInput.PICKUP, player);
				}
			}), clickDelayMs, TimeUnit.MILLISECONDS);
			return;
		}
	}
}
