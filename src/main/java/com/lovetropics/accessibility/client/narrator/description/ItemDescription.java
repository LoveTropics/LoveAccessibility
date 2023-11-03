package com.lovetropics.accessibility.client.narrator.description;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public record ItemDescription(Component component) {
    @Nullable
    public static ItemDescription describe(@Nullable final Player player, final ItemStack item) {
        if (!item.isEmpty()) {
            return new ItemDescription(describeComponent(player, item));
        }
        return null;
    }

    private static Component describeComponent(final Player player, final ItemStack item) {
        final MutableComponent prefix = Component.literal(item.getCount() + " ");
        final Component typeName = item.getItem().getName(item);
        final List<Component> lines = new ArrayList<>();
        if (item.hasCustomHoverName()) {
            lines.add(item.getHoverName());
        }
        lines.add(typeName);
        addTooltip(player, item, lines);
        return prefix.append(CommonComponents.joinForNarration(lines.toArray(Component[]::new)));
    }

    private static void addTooltip(final Player player, final ItemStack item, final List<Component> lines) {
        final int removeIndex = lines.size();
        lines.addAll(item.getTooltipLines(player, TooltipFlag.NORMAL));
        lines.remove(removeIndex);
    }
}
