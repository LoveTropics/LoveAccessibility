package com.lovetropics.accessibility.client.narrator.world;

import com.lovetropics.accessibility.client.narrator.NarratorOutput;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

import java.util.List;

public final class WorldNarrator {
    private final NarratorOutput narratorOutput;

    private final Queue lookingAtQueue = new SinglePausingQueue(SharedConstants.TICKS_PER_SECOND / 2);
    private final List<Queue> queues = List.of(lookingAtQueue);

    private final LookingAtNarrator lookingAtNarrator = new LookingAtNarrator(lookingAtQueue);

    public WorldNarrator(final NarratorOutput narratorOutput) {
        this.narratorOutput = narratorOutput;
    }

    public void tick(final Minecraft minecraft, final ClientLevel level) {
        tickNarrators(minecraft, level);

        for (final Queue queue : queues) {
            if (queue.tick(narratorOutput)) {
                break;
            }
        }
    }

    private void tickNarrators(final Minecraft minecraft, final ClientLevel level) {
        lookingAtNarrator.tick(minecraft, level);
    }

    private static final class SinglePausingQueue implements Queue {
        private final int minimumIntervalTicks;

        private String queuedMessage;
        private int pauseTicks;

        private SinglePausingQueue(final int minimumIntervalTicks) {
            this.minimumIntervalTicks = minimumIntervalTicks;
        }

        @Override
        public void accept(final String message) {
            queuedMessage = message;
        }

        @Override
        public boolean tick(final NarratorOutput output) {
            if (pauseTicks > 0) {
                pauseTicks--;
                return true;
            }

            final String message = queuedMessage;
            if (message != null) {
                output.accept(message);
                queuedMessage = null;
                pauseTicks = minimumIntervalTicks;

                return true;
            }

            return false;
        }
    }

    private interface Queue extends NarratorOutput {
        boolean tick(NarratorOutput output);
    }
}
