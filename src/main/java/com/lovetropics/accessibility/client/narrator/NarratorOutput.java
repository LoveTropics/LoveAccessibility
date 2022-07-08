package com.lovetropics.accessibility.client.narrator;

import net.minecraft.network.chat.Component;

public interface NarratorOutput {
    void accept(String message);

    default void accept(final Component message) {
        accept(message.getString());
    }

    default void acceptBlank() {
        accept("");
    }
}
