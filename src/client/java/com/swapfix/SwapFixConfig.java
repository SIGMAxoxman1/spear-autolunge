package com.swapfix;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * إعدادات المود. بتتحفظ في config/swapfix.json وتتقرأ منه.
 * السلوتات هنا بالأرقام اللي شايفها في اللعبة (1-9) مش الـ index
 * الداخلي (0-8)، عشان تبقى مفهومة لو حد فتح الملف يدوي.
 */
public class SwapFixConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("swapfix.json");

    public int aspectSlot = 9;
    public int returnSlot = 8;

    private static SwapFixConfig instance;

    public static SwapFixConfig get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static SwapFixConfig load() {
        if (Files.exists(PATH)) {
            try (Reader reader = Files.newBufferedReader(PATH)) {
                SwapFixConfig loaded = GSON.fromJson(reader, SwapFixConfig.class);
                if (loaded != null) {
                    loaded.sanitize();
                    return loaded;
                }
            } catch (IOException e) {
                // لو فشلت القراءة، هنكمل بالإعدادات الافتراضية ونحفظها تاني
            }
        }
        SwapFixConfig defaults = new SwapFixConfig();
        defaults.save();
        return defaults;
    }

    public void sanitize() {
        aspectSlot = Math.max(1, Math.min(9, aspectSlot));
        returnSlot = Math.max(1, Math.min(9, returnSlot));
    }

    public void save() {
        sanitize();
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(PATH)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            // تجاهل - المود هيكمل شغال بالإعدادات اللي في الذاكرة
        }
    }
}
