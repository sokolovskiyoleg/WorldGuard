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

import java.util.Collections;
import java.util.IllegalFormatException;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Localization {

    private static final String MISSING_KEY_PATTERN = "!%s!";
    private static final Localization EMPTY = new Localization(Collections.emptyMap());

    private final Map<String, String> messages;

    private Localization(Map<String, String> messages) {
        this.messages = Collections.unmodifiableMap(new LinkedHashMap<>(messages));
    }

    public static Localization empty() {
        return EMPTY;
    }

    public static Localization of(Map<String, String> messages) {
        return new Localization(messages);
    }

    public String get(String key) {
        return messages.getOrDefault(key, MISSING_KEY_PATTERN.formatted(key));
    }

    public String format(String key, Object... arguments) {
        String template = get(key);
        if (arguments.length == 0) {
            return template;
        }

        try {
            return String.format(template, arguments);
        } catch (IllegalFormatException ignored) {
            return template;
        }
    }

    public Map<String, String> asMap() {
        return messages;
    }
}
