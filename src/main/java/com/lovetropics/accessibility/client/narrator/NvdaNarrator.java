package com.lovetropics.accessibility.client.narrator;

import com.lovetropics.accessibility.LoveAccessibility;
import com.mojang.text2speech.Narrator;
import dev.gegy.nvda_controller_client.NvdaControllerClient;

import javax.annotation.Nullable;

public final class NvdaNarrator {
    private static Narrator instance;

    static {
        try {
            final NvdaControllerClient nvda = NvdaControllerClient.create();
            instance = new Narrator() {
                @Override
                public void say(final String msg, final boolean interrupt) {
                    if (interrupt) {
                        nvda.cancel();
                    }
                    nvda.speak(msg);
                }

                @Override
                public void clear() {
                    nvda.cancel();
                }

                @Override
                public boolean active() {
                    return nvda.isRunning();
                }

                @Override
                public void destroy() {
                }
            };
        } catch (final Exception e) {
            LoveAccessibility.LOGGER.error("Failed to load NVDA controller client", e);
        }
    }

    @Nullable
    public static Narrator get() {
        return instance;
    }
}
