package com.lovetropics.accessibility.client.narrator.description;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;

import javax.annotation.Nullable;

public record BlockDescription(Component component) {
    public static BlockDescription describe(final Level level, final BlockPos pos) {
        final BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof SignBlockEntity sign) {
            return describeSign(sign);
        } else if (entity instanceof CampfireBlockEntity campfire) {
            return describeInventory(campfire.getItems());
        }

        return null;
    }

    private static BlockDescription describeSign(final SignBlockEntity sign) {
        Component message = sign.getMessage(0, false);
        for (int i = 1; i < SignBlockEntity.LINES; i++) {
            message = CommonComponents.joinForNarration(message, sign.getMessage(i, false));
        }
        return new BlockDescription(message);
    }

    @Nullable
    private static BlockDescription describeInventory(final NonNullList<ItemStack> inventory) {
        Component component = null;

        for (final ItemStack item : inventory) {
            final ItemDescription description = ItemDescription.describe(item);
            if (description != null) {
                if (component == null) {
                    component = description.component();
                } else {
                    component = CommonComponents.joinForNarration(component, description.component());
                }
            }
        }

        if (component != null) {
            return new BlockDescription(component);
        }

        return null;
    }
}
