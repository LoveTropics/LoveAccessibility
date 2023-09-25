package com.lovetropics.accessibility.mixin;

import com.lovetropics.accessibility.client.narrator.NvdaNarrator;
import com.mojang.text2speech.Narrator;
import net.minecraft.client.GameNarrator;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameNarrator.class)
public class GameNarratorMixin {
    @Mutable
    @Shadow
    @Final
    private Narrator narrator;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(final CallbackInfo ci) {
        final Narrator nvdaNarrator = NvdaNarrator.get();
        if (nvdaNarrator != null) {
            narrator = nvdaNarrator;
        }
    }
}
