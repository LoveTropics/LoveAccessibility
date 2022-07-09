package com.lovetropics.accessibility.client.narrator;

import com.mojang.text2speech.Narrator;
import net.minecraft.network.chat.Component;

public interface NarratorOutput {
    static NarratorOutput of(final Narrator narrator) {
        return message -> {
            if (narrator.active() && !message.isBlank()) {
                narrator.clear();
                narrator.say(message, true);
            }
        };
    }

    void accept(String message);

    default void accept(final Component message) {
        accept(message.getString());
    }

    default void acceptBlank() {
        accept("");
    }
}
