package com.lovetropics.accessibility.client.narrator;

import com.lovetropics.accessibility.LoveAccessibility;
import com.lovetropics.accessibility.client.narrator.ui.ContainerNarrator;
import com.lovetropics.accessibility.client.narrator.world.WorldNarrator;
import com.lovetropics.accessibility.mixin.GameNarratorAccessor;
import com.mojang.text2speech.Narrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = LoveAccessibility.ID, value = Dist.CLIENT)
public final class NarratorManager {
    private static Narrators narrators;

    @Nullable
    public static Narrators getNarrators() {
        return narrators;
    }

    @SubscribeEvent
    public static void onClientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            return;
        }

        final Minecraft minecraft = Minecraft.getInstance();
        final ClientLevel level = minecraft.level;

        final boolean enabled = level != null && getNarratorStatus() == NarratorStatus.ALL;
        if (enabled) {
            tickEnabled(minecraft, level);
        } else if (narrators != null) {
            narrators = null;
        }
    }

    private static void tickEnabled(final Minecraft minecraft, final ClientLevel level) {
        if (narrators == null) {
            narrators = createNarrators();
        }

        narrators.tick(minecraft, level);
    }

    private static Narrators createNarrators() {
        final NarratorOutput output = getNarratorOutput();

        final WorldNarrator world = new WorldNarrator(output);
        final ContainerNarrator container = new ContainerNarrator(output);
        return new Narrators(world, container);
    }

    private static NarratorOutput getNarratorOutput() {
        final Narrator narrator = Objects.requireNonNullElseGet(NvdaNarrator.get(), NarratorManager::getVanillaNarrator);
        return NarratorOutput.of(narrator);
    }

    private static Narrator getVanillaNarrator() {
        return ((GameNarratorAccessor) (Minecraft.getInstance().getNarrator())).getNarrator();
    }

    private static NarratorStatus getNarratorStatus() {
        return Minecraft.getInstance().options.narrator().get();
    }

    private record Narrators(WorldNarrator world, ContainerNarrator container) {
        public void tick(final Minecraft minecraft, final ClientLevel level) {
            world.tick(minecraft, level);
            container.tick(minecraft);
        }
    }
}
