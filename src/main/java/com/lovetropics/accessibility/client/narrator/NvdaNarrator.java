package com.lovetropics.accessibility.client.narrator;

import com.lovetropics.accessibility.LoveAccessibility;
import com.mojang.text2speech.Narrator;
import dev.gegy.nvda_controller_client.NvdaControllerClient;
import net.minecraft.client.GameNarrator;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

            if(nvda != null && !instance.active()) {
                // Added just as a little help window in case NVDA is installed but not active
                String programFilesPath = System.getenv("ProgramFiles(X86)");
                Path folderPath = Paths.get(programFilesPath, "NVDA\\nvda.exe");
                if(Files.exists(folderPath) && !TinyFileDialogs.tinyfd_messageBox("Minecraft", "NVDA detected but not active, do you want to try to default back to the native narrator?\n\nIf this problem persists, please report it at https://github.com/LoveTropics/LoveAccessibility", "yesno", "error", true)) {
                    throw new GameNarrator.NarratorInitException("NVDA Narrator library is not active");
                }
                instance = null;
            }

        } catch(GameNarrator.NarratorInitException e) {
            throw e;
        } catch(final Exception e) {
            LoveAccessibility.LOGGER.error("Failed to load NVDA controller client", e);
        }
    }

    @Nullable
    public static Narrator get() {
        return instance;
    }
}
