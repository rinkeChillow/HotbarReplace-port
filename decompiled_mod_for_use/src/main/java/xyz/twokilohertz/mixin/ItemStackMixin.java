package xyz.twokilohertz.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.twokilohertz.HotbarReplace;

@Environment(value=EnvType.CLIENT)
@Mixin(value={ItemStack.class})
public class ItemStackMixin {
    private Item lastUsedItem = null;

    @Inject(at={@At(value="HEAD")}, method={"use"})
    private void ItemStack_use_HEAD(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> info) {
        this.lastUsedItem = player.getStackInHand(hand).getItem();
    }

    @Inject(at={@At(value="HEAD")}, method={"useOnBlock"})
    private void ItemStack_useOnBlock_HEAD(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
        PlayerEntity player = context.getPlayer();
        this.lastUsedItem = player != null ? player.getStackInHand(context.getHand()).getItem() : null;
    }

    @Inject(at={@At(value="HEAD")}, method={"useOnEntity"})
    private void ItemStack_useOnEntity_HEAD(PlayerEntity player, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        this.lastUsedItem = player.getStackInHand(hand).getItem();
    }

    @Inject(at={@At(value="TAIL")}, method={"use"})
    private void ItemStack_use_TAIL(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> info) {
        if (info.getReturnValue().getResult() != ActionResult.SUCCESS) {
            return;
        }
        if (player.getStackInHand(hand).getCount() != 0) {
            return;
        }
        HotbarReplace.tryReplaceSlot(player, hand, this.lastUsedItem);
    }

    @Inject(at={@At(value="TAIL")}, method={"useOnBlock"})
    private void ItemStack_useOnBlock_TAIL(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
        Hand hand = context.getHand();
        if (info.getReturnValue() != ActionResult.SUCCESS) {
            return;
        }
        PlayerEntity player = context.getPlayer();
        if (player == null) {
            return;
        }
        if (player.getStackInHand(hand).getCount() != 0) {
            return;
        }
        HotbarReplace.tryReplaceSlot(player, hand, this.lastUsedItem);
    }

    @Inject(at={@At(value="TAIL")}, method={"useOnEntity"})
    private void ItemStack_useOnEntity_TAIL(PlayerEntity player, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        if (info.getReturnValue() != ActionResult.SUCCESS) {
            return;
        }
        if (player.getStackInHand(hand).getCount() != 0) {
            return;
        }
        HotbarReplace.tryReplaceSlot(player, hand, this.lastUsedItem);
    }
}
