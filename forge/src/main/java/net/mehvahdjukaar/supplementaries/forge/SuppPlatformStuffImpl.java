package net.mehvahdjukaar.supplementaries.forge;

import net.mehvahdjukaar.moonlight.api.util.FakePlayerManager;
import net.mehvahdjukaar.supplementaries.common.capabilities.CapabilityHandler;
import net.mehvahdjukaar.supplementaries.common.utils.SlotReference;
import net.mehvahdjukaar.supplementaries.configs.ClientConfigs;
import net.mehvahdjukaar.supplementaries.mixins.forge.MobBucketItemAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class SuppPlatformStuffImpl {

    public static EntityType<?> getFishType(MobBucketItem bucketItem) {
        return ((MobBucketItemAccessor) bucketItem).invokeGetFishType();
    }

    /**
     * Does not check if its instance of ICapabilityProvider
     * Be sure to provide it with one, or it will fail
     */
    @Nullable
    public static <T> T getForgeCap(Object object, Class<T> capClass) {
        var t = CapabilityHandler.getToken(capClass);
        if (t != null && object instanceof ICapabilityProvider cp) {
            return CapabilityHandler.get(cp, t);
        }
        return null;
    }

    @Nullable
    public static BlockState getUnoxidised(Level level, BlockPos pos, BlockState state) {
        Player fp = FakePlayerManager.getDefault(level);
        fp.setItemInHand(InteractionHand.MAIN_HAND, Items.IRON_AXE.getDefaultInstance());
        Block b = state.getBlock();
        var context = new UseOnContext(fp, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));

        var modified = state;
        modified = b.getToolModifiedState(modified, context, ToolActions.AXE_WAX_OFF, false);
        if (modified == null) modified = state;
        while (true) {
            var newMod = b.getToolModifiedState(modified, context, ToolActions.AXE_SCRAPE, false);

            if (newMod == null || newMod == modified) break;
            else modified = newMod;
        }
        if (modified == state) return null;
        return modified;
    }

    public static boolean isEndermanMask(@NotNull EnderMan enderMan, Player player, ItemStack itemstack) {
        try {
            return itemstack.isEnderMask(player, enderMan);
        } catch (Exception e) {
            return false;
        }
    }

    public static int getItemLifeSpawn(ItemEntity itemEntity) {
        return itemEntity.lifespan;
    }

    public static void onItemPickup(Player player, ItemEntity itemEntity, ItemStack copy) {
        ForgeEventFactory.firePlayerItemPickupEvent(player, itemEntity, copy);
    }

    public static CreativeModeTab.Builder searchBar(CreativeModeTab.Builder c) {
        return c.withSearchBar();
    }

    public static float getDownfall(Biome biome) {
        return biome.getModifiedClimateSettings().downfall();
    }

    public static void disableAMWarn() {
        ((ForgeConfigSpec.BooleanValue) ClientConfigs.General.NO_AMENDMENTS_WARN).set(true);
    }

    public static void disableOFWarn(boolean on) {
        ((ForgeConfigSpec.BooleanValue) ClientConfigs.General.NO_OPTIFINE_WARN).set(on);
    }

    public static boolean canStickTo(BlockState movedState, BlockState blockState) {
        return movedState.canStickTo(blockState);
    }

    public static SlotReference getFirstInInventory(LivingEntity entity, Predicate<ItemStack> predicate) {
        var cap = CapabilityHandler.get(entity, ForgeCapabilities.ITEM_HANDLER);
        if (cap != null) {
            for (int i = 0; i < cap.getSlots(); i++) {
                ItemStack quiver = cap.getStackInSlot(i);
                if (predicate.test(quiver)) {
                    int finalI = i;
                    return () -> cap.getStackInSlot(finalI);
                }
            }
        }
        return SlotReference.EMPTY;
    }

    public static FoodProperties getFoodProperties(ItemStack selected, LivingEntity entity) {
        return selected.getFoodProperties(entity);
    }

    public static SoundType getSoundType(BlockState blockState, BlockPos pos, Level level, Entity entity) {
        return blockState.getSoundType(level, pos, entity);
    }

}
