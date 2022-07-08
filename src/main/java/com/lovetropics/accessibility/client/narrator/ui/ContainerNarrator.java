package com.lovetropics.accessibility.client.narrator.ui;

import com.lovetropics.accessibility.client.narrator.NarratorOutput;
import com.lovetropics.accessibility.client.narrator.description.ItemDescription;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class ContainerNarrator {
    private static final int NARRATE_INTERVAL_TICKS = SharedConstants.TICKS_PER_SECOND / 4;

    private final NarratorOutput output;

    private int ticks;

    private Target lastTarget;

    public ContainerNarrator(final NarratorOutput output) {
        this.output = output;
    }

    public void tick(final Minecraft minecraft) {
        if (ticks++ % NARRATE_INTERVAL_TICKS != 0) {
            return;
        }

        if (minecraft.screen instanceof AbstractContainerScreen<?> containerScreen) {
            tickContainer(containerScreen);
        } else {
            lastTarget = null;
        }
    }

    private void tickContainer(final AbstractContainerScreen<?> containerScreen) {
        final Target target = evaluateTarget(containerScreen);
        if (!Objects.equals(target, lastTarget)) {
            if (target != null) {
                narrateTarget(target);
            } else {
                output.acceptBlank();
            }

            lastTarget = target;
        }
    }

    @Nullable
    private Target evaluateTarget(final AbstractContainerScreen<?> containerScreen) {
        final Slot hoveredSlot = containerScreen.getSlotUnderMouse();
        if (hoveredSlot != null) {
            return new Target(hoveredSlot);
        }
        return null;
    }

    private void narrateTarget(final Target target) {
        final ItemDescription description = ItemDescription.describe(target.item());
        if (description != null) {
            output.accept(description.component());
        } else {
            output.acceptBlank();
        }
    }

    private record Target(Slot slot, ItemStack item) {
        public Target(final Slot slot) {
            this(slot, slot.getItem().copy());
        }

        @Override
        public boolean equals(final Object o) {
            if (o instanceof Target that) {
                return slot == that.slot && ItemStack.matches(item, that.item);
            }
            return false;
        }
    }
}
