package com.betterstyleindicator;

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.betterstyleindicator.BetterStyleIndicatorConfig.SoundOption;


import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

class ScreenFlashOverlay extends Overlay
{
	private final BetterStyleIndicatorPlugin plugin;
	private final BetterStyleIndicatorConfig config;
	private final Client client; // Inject the Client object

	private boolean flashActive = false; // Is the screen flashing right now?
	private Timer flashTimer; // Timer for toggling the flash state

	@Inject
	private ScreenFlashOverlay(BetterStyleIndicatorPlugin plugin, BetterStyleIndicatorConfig config, Client client)
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
