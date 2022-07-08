package com.lovetropics.accessibility.client.narrator;

import com.lovetropics.accessibility.LoveAccessibility;
import com.lovetropics.accessibility.client.narrator.world.WorldNarrator;
import com.mojang.text2speech.Narrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import javax.annotation.Nullable;

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
        final Narrator narrator = getNarrator();
        final NarratorOutput output = message -> {
            if (narrator.active() && !message.isBlank()) {
                narrator.clear();
                narrator.say(message, true);
            }
        };

        final WorldNarrator world = new WorldNarrator(output);
        return new Narrators(world);
    }

    private static Narrator getNarrator() {
        return ObfuscationReflectionHelper.getPrivateValue(NarratorChatListener.class, NarratorChatListener.INSTANCE, "f_93313_");
    }

    private static NarratorStatus getNarratorStatus() {
        return Minecraft.getInstance().options.narratorStatus;
    }

    private record Narrators(WorldNarrator world) {
        public void tick(final Minecraft minecraft, final ClientLevel level) {
            world.tick(minecraft, level);
        }
    }
}
