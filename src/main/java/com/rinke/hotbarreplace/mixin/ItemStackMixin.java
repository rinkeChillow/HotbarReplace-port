package com.rinke.hotbarreplace.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
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
	private void hotbarreplace$use_HEAD(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
		this.hotbarreplace$lastUsedItem = player.getStackInHand(hand).getItem();
	}

	@Inject(at = @At("HEAD"), method = "useOnBlock")
	private void hotbarreplace$useOnBlock_HEAD(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
		PlayerEntity player = context.getPlayer();
		this.hotbarreplace$lastUsedItem = player != null ? player.getStackInHand(context.getHand()).getItem() : null;
	}

	@Inject(at = @At("HEAD"), method = "useOnEntity")
	private void hotbarreplace$useOnEntity_HEAD(PlayerEntity player, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> info) {
		this.hotbarreplace$lastUsedItem = player.getStackInHand(hand).getItem();
	}

	@Inject(at = @At("TAIL"), method = "use")
	private void hotbarreplace$use_TAIL(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
		if (info.getReturnValue() != ActionResult.SUCCESS) {
			return;
		}
		if (player.getStackInHand(hand).getCount() != 0) {
			return;
		}
		HotbarReplaceClient.tryReplaceSlot(player, hand, this.hotbarreplace$lastUsedItem);
	}

	@Inject(at = @At("TAIL"), method = "useOnBlock")
	private void hotbarreplace$useOnBlock_TAIL(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
		if (info.getReturnValue() != ActionResult.SUCCESS) {
			return;
		}
		PlayerEntity player = context.getPlayer();
		if (player == null) {
			return;
		}
		if (player.getStackInHand(context.getHand()).getCount() != 0) {
			return;
		}
		HotbarReplaceClient.tryReplaceSlot(player, context.getHand(), this.hotbarreplace$lastUsedItem);
	}

	@Inject(at = @At("TAIL"), method = "useOnEntity")
	private void hotbarreplace$useOnEntity_TAIL(PlayerEntity player, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> info) {
		if (info.getReturnValue() != ActionResult.SUCCESS) {
			return;
		}
		if (player.getStackInHand(hand).getCount() != 0) {
			return;
		}
		HotbarReplaceClient.tryReplaceSlot(player, hand, this.hotbarreplace$lastUsedItem);
	}
}
