package com.lovetropics.accessibility.client.narrator.world;

import com.lovetropics.accessibility.client.narrator.NarratorOutput;
import com.lovetropics.accessibility.client.narrator.description.BlockDescription;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
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
        } else if (hitResult instanceof EntityHitResult entityResult) {
            return new LookingAtEntity(entityResult.getEntity());
        }
        return null;
    }

    @Nullable
    private LookingAtBlock evaluateBlockTarget(final Minecraft minecraft, final ClientLevel level, final BlockHitResult blockResult) {
        final BlockPos pos = blockResult.getBlockPos();
        final BlockState state = level.getBlockState(pos);
        if (!state.isAir() && !isSubmergedIn(minecraft, state.getBlock())) {
            final BlockDescription details = BlockDescription.describe(minecraft, level, pos);
            return new LookingAtBlock(pos, state.getBlock(), details);
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

    private record LookingAtBlock(BlockPos pos, Block block, @Nullable BlockDescription details) implements Target {
        @Override
        public Component narration() {
            if (details != null) {
                return CommonComponents.joinForNarration(block.getName(), details.component());
            } else {
                return block.getName();
            }
        }
    }

    private record LookingAtEntity(Entity entity) implements Target {
        @Override
        public Component narration() {
            final Component typeName = entity.getType().getDescription();
            final Component customName = entity.getCustomName();
            if (customName != null) {
                return CommonComponents.joinForNarration(typeName, customName);
            } else {
                return typeName;
            }
        }

        @Override
        public boolean equals(final Object o) {
            return o instanceof LookingAtEntity that && that.entity.getUUID().equals(entity.getUUID());
        }

        @Override
        public int hashCode() {
            return entity.getUUID().hashCode();
        }
    }
}
