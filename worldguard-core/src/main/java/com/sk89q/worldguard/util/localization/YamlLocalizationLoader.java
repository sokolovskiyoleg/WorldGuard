/*
 * WorldGuard, a suite of tools for Minecraft
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldGuard team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.worldguard.util.localization;

import com.sk89q.util.yaml.YAMLFormat;
import com.sk89q.util.yaml.YAMLProcessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YamlLocalizationLoader {

    private static final Logger LOGGER = Logger.getLogger(YamlLocalizationLoader.class.getCanonicalName());

    public Localization load(Path configDirectory, String language) {
        Map<String, String> merged = new LinkedHashMap<>();

        File messagesDirectory = configDirectory.resolve("messages").toFile();
        loadInto(messagesDirectory, "en", merged, false);
        if (!"en".equalsIgnoreCase(language)) {
            loadInto(messagesDirectory, language, merged, true);
        }

        return Localization.of(merged);
    }

    private void loadInto(File messagesDirectory, String language, Map<String, String> target, boolean optional) {
        File file = new File(messagesDirectory, language + ".yml");
        if (!file.exists()) {
            if (optional) {
                LOGGER.warning("Localization file not found: " + file.getAbsolutePath());
            } else {
                LOGGER.warning("Base localization file not found: " + file.getAbsolutePath());
            }
            return;
        }

        YAMLProcessor processor = new YAMLProcessor(file, true, YAMLFormat.EXTENDED);
        try {
            processor.load();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load localization file: " + file.getAbsolutePath(), e);
            return;
        }

        flatten(null, processor.getMap(), target);
    }

    @SuppressWarnings("unchecked")
    private void flatten(String prefix, Map<String, Object> source, Map<String, String> target) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = prefix == null ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map<?, ?> nested) {
                flatten(key, (Map<String, Object>) nested, target);
            } else if (value != null) {
                target.put(key, String.valueOf(value));
            }
        }
    }
}
