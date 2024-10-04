/*
 * Copyright (c) 2024, Smoke (Smoked today) <https://github.com/Varietyz>
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.inject.Inject;
import javax.sound.sampled.*;
import javax.swing.Timer;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import java.io.IOException;
import java.io.InputStream;

import com.stylealert.StyleAlertConfig.SoundOption;


import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

class ScreenFlashOverlay extends Overlay
{
	private final StyleAlertPlugin plugin;
	private final StyleAlertConfig config;
	private final Client client; // Inject the Client object

	private boolean flashActive = false; // Is the screen flashing right now?
	private Timer flashTimer; // Timer for toggling the flash state

	@Inject
	private ScreenFlashOverlay(StyleAlertPlugin plugin, StyleAlertConfig config, Client client)
	{
		this.plugin = plugin;
		this.config = config; // Initialize the config object
		this.client = client; // Initialize the Client object
		setPosition(OverlayPosition.DYNAMIC);
		addMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Attack style overlay");
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		boolean warnedSkillSelected = plugin.isWarnedSkillSelected();

		// Check if flashing is enabled in the config
		if (config.enableFlashing() && warnedSkillSelected)
		{
			startFlashing();
		}
		else
		{
			// Stop flashing if the warned skill is no longer selected or flashing is disabled
			stopFlashing();
		}

		// Only render the flashing effect if flashActive is true
		if (flashActive)
		{
			// Get the client's dimensions to cover the full window
			Dimension clientDimensions = client.getCanvas().getSize();

			// Get the color from the configuration
			Color flashColor = config.flashColor(); // Use configurable flash color

			// Get the transparency value from the configuration
			int transparency = config.flashTransparency().getTransparency();

			// Draw a semi-transparent overlay covering the entire client
			graphics.setColor(new Color(flashColor.getRed(), flashColor.getGreen(), flashColor.getBlue(), transparency));
			graphics.fillRect(0, 0, clientDimensions.width, clientDimensions.height);
		}

		return null;
	}

	private void playSound(String soundFilePath)
	{
		if (soundFilePath.equals(SoundOption.OFF.getFileName()))
		{
			return; // Don't play any sound if OFF is selected
		}

		try
		{
			InputStream soundStream = getClass().getClassLoader().getResourceAsStream(soundFilePath);

			if (soundStream == null)
			{
				System.err.println("Sound file not found: " + soundFilePath);
				return;
			}

			AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundStream);
			Clip clip = AudioSystem.getClip();
			clip.open(audioStream);

			// Adjust the volume based on user preference
			float volume = config.soundVolume().getVolume();
			FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			volumeControl.setValue(volumeControl.getMinimum() + (volume * (volumeControl.getMaximum() - volumeControl.getMinimum())));

			clip.start();
		}
		catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
		{
			e.printStackTrace();
		}
	}



	// Start flashing the screen indefinitely until the style is changed
	void startFlashing()

	{
		if (flashTimer != null && flashTimer.isRunning())
		{
			// Flashing is already running, so no need to start it again
			return;
		}

		flashActive = true; // Start in the active flash state

		// Get the flash interval from the configuration
		int flashInterval = config.flashInterval().getInterval();

		// Timer to toggle flash on and off every configured interval
		flashTimer = new Timer(flashInterval, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				flashActive = !flashActive; // Toggle flash state

				// Play the selected sound
				String soundFilePath = config.selectedSound().getFileName(); // Get the selected sound
				playSound(soundFilePath);

			}
		});

		flashTimer.start();
	}

	// Stop flashing when the attack style has been changed
	void stopFlashing()
	{
		if (flashTimer != null)
		{
			flashTimer.stop();
			flashActive = false; // Reset the flash state
		}
	}
}
