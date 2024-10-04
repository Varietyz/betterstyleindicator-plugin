/*
 * Copyright (c) 2024, Smoke (Smoked today) <https://github.com/Varietyz>
 * Copyright (c) 2017, honeyhoney <https://github.com/honeyhoney>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.stylealert;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("attackIndicator")
public interface StyleAlertConfig extends Config
{
	@ConfigItem(
			keyName = "enableFlashing",
			name = "Enable Screen Flashing",
			description = "Enable or disable the fullscreen flashing feature for warned attack styles",
			position = 6
	)
	default boolean enableFlashing()
	{
		return true; // Default to enabled
	}

	@ConfigItem(
		keyName = "warnForDefensive",
		name = "Warn for defence",
		description = "Show warning when a Defence skill combat option is selected",
		position = 1
	)
	default boolean warnForDefence()
	{
		return false;
	}

	@ConfigItem(
		keyName = "warnForAttack",
		name = "Warn for attack",
		description = "Show warning when an Attack skill combat option is selected",
		position = 2
	)
	default boolean warnForAttack()
	{
		return false;
	}

	@ConfigItem(
		keyName = "warnForStrength",
		name = "Warn for strength",
		description = "Show warning when a Strength skill combat option is selected",
		position = 3
	)
	default boolean warnForStrength()
	{
		return false;
	}

	@ConfigItem(
		keyName = "warnForRanged",
		name = "Warn for ranged",
		description = "Show warning when a Ranged skill combat option is selected",
		position = 4
	)
	default boolean warnForRanged()
	{
		return false;
	}

	@ConfigItem(
		keyName = "warnForMagic",
		name = "Warn for magic",
		description = "Show warning when a Magic skill combat option is selected",
		position = 5
	)
	default boolean warnForMagic()
	{
		return false;
	}
	@ConfigItem(
			keyName = "flashColor",
			name = "Flash Color",
			description = "Color of the flash overlay",
			position = 7
	)
	default Color flashColor() {
		return Color.RED; // Default flash color
	}

	@ConfigItem(
			keyName = "flashTransparency",
			name = "Flash Opacity",
			description = "Set the transparency for the screen flash (Light, Medium, Strong)",
			position = 8
	)
	default FlashTransparency flashTransparency()
	{
		return FlashTransparency.LIGHT;
	}

	@ConfigItem(
			keyName = "flashInterval",
			name = "Flash Speed",
			description = "Set the interval for the screen flash (Rapid, Normal, Slow)",
			position = 9
	)
	default FlashInterval flashInterval()
	{
		return FlashInterval.NORMAL;
	}

	@ConfigItem(
			keyName = "selectedSound",
			name = "Notification Sound",
			description = "Sound on, Sound off.",
			position = 10
	)
	default SoundOption selectedSound()
	{
		return SoundOption.OFF;
	}

	@ConfigItem(
			keyName = "soundVolume",
			name = "Sound Volume",
			description = "Set the volume level for notification sounds.",
			position = 11
	)
	default SoundVolume soundVolume() {
		return SoundVolume.NORMAL; // Default to 100%
	}

	enum SoundVolume {
		NORMAL(0.6f),
		LOUD(0.7f),
		LOUDER(0.8f),
		LOUDEST(0.9f);

		private final float volume;

		SoundVolume(float volume) {
			this.volume = volume;
		}

		public float getVolume() {
			return volume;
		}
	}

	enum SoundOption
	{
		OFF("off"), // Add this line for the OFF option
		ON("net/runelite/client/notification.wav");

		private final String fileName;

		SoundOption(String fileName)
		{
			this.fileName = fileName;
		}

		public String getFileName()
		{
			return fileName;
		}
	}


	enum FlashInterval
	{
		RAPID(250),
		NORMAL(500),
		SLOW(750);

		private final int interval;

		FlashInterval(int interval)
		{
			this.interval = interval;
		}

		public int getInterval()
		{
			return interval;
		}
	}

	enum FlashTransparency
	{
		LIGHT(50),
		MEDIUM(90),
		STRONG(110);

		private final int transparency;

		FlashTransparency(int transparency)
		{
			this.transparency = transparency;
		}

		public int getTransparency()
		{
			return transparency;
		}
	}
}
