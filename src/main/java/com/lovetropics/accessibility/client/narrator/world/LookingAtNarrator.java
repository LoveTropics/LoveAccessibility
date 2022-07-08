package com.lovetropics.accessibility.client.narrator.world;

import com.lovetropics.accessibility.client.narrator.NarratorOutput;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.Objects;

public final class LookingAtNarrator {
    private final NarratorOutput output;

    private Target currentTarget;

    public LookingAtNarrator(final NarratorOutput output) {
        this.output = output;
    }

    public void tick(final Minecraft minecraft, final ClientLevel level) {
        final Target newTarget = evaluateTarget(minecraft, level, minecraft.hitResult);
        if (!Objects.equals(currentTarget, newTarget)) {
            currentTarget = newTarget;
            if (newTarget != null) {
                output.accept(newTarget.narration());
            } else {
                output.acceptBlank();
            }
        }
    }

    @Nullable
    private Target evaluateTarget(final Minecraft minecraft, final ClientLevel level, final HitResult hitResult) {
        if (hitResult instanceof BlockHitResult blockResult) {
            return evaluateBlockTarget(minecraft, level, blockResult);
        }
        return null;
    }

    @Nullable
    private LookingAtBlock evaluateBlockTarget(final Minecraft minecraft, final ClientLevel level, final BlockHitResult blockResult) {
        final BlockPos pos = blockResult.getBlockPos();
        final BlockState state = level.getBlockState(pos);
        if (!state.isAir() && !isSubmergedIn(minecraft, state.getBlock())) {
            return new LookingAtBlock(pos, state.getBlock());
        }
        return null;
    }

    private boolean isSubmergedIn(final Minecraft minecraft, final Block block) {
        final Camera camera = minecraft.getEntityRenderDispatcher().camera;
        return camera != null && camera.getBlockAtCamera().is(block);
    }

    private interface Target {
        Component narration();
    }

    private record LookingAtBlock(BlockPos pos, Block block) implements Target {
        @Override
        public Component narration() {
            return block.getName();
        }
    }
}
