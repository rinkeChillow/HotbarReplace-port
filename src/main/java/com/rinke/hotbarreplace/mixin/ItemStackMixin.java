package com.rinke.hotbarreplace.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.rinke.hotbarreplace.HotbarReplaceClient;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public class ItemStackMixin {
	@Unique
	private Item hotbarreplace$lastUsedItem = null;

	@Inject(at = @At("HEAD"), method = "use")
	private void hotbarreplace$use_HEAD(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
		this.hotbarreplace$lastUsedItem = player.getItemInHand(hand).getItem();
	}

	@Inject(at = @At("HEAD"), method = "useOn")
	private void hotbarreplace$useOn_HEAD(UseOnContext context, CallbackInfoReturnable<InteractionResult> info) {
		Player player = context.getPlayer();
		this.hotbarreplace$lastUsedItem = player != null ? player.getItemInHand(context.getHand()).getItem() : null;
	}

	@Inject(at = @At("HEAD"), method = "interactLivingEntity")
	private void hotbarreplace$interactLivingEntity_HEAD(Player player, LivingEntity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
		this.hotbarreplace$lastUsedItem = player.getItemInHand(hand).getItem();
	}

	@Inject(at = @At("TAIL"), method = "use")
	private void hotbarreplace$use_TAIL(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
		if (info.getReturnValue() != InteractionResult.SUCCESS) {
			return;
		}
		if (player.getItemInHand(hand).getCount() != 0) {
			return;
		}
		HotbarReplaceClient.tryReplaceSlot(player, hand, this.hotbarreplace$lastUsedItem);
	}

	@Inject(at = @At("TAIL"), method = "useOn")
	private void hotbarreplace$useOn_TAIL(UseOnContext context, CallbackInfoReturnable<InteractionResult> info) {
		if (info.getReturnValue() != InteractionResult.SUCCESS) {
			return;
		}
		Player player = context.getPlayer();
		if (player == null) {
			return;
		}
		if (player.getItemInHand(context.getHand()).getCount() != 0) {
			return;
		}
		HotbarReplaceClient.tryReplaceSlot(player, context.getHand(), this.hotbarreplace$lastUsedItem);
	}

	@Inject(at = @At("TAIL"), method = "interactLivingEntity")
	private void hotbarreplace$interactLivingEntity_TAIL(Player player, LivingEntity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
		if (info.getReturnValue() != InteractionResult.SUCCESS) {
			return;
		}
		if (player.getItemInHand(hand).getCount() != 0) {
			return;
		}
		HotbarReplaceClient.tryReplaceSlot(player, hand, this.hotbarreplace$lastUsedItem);
	}
}
