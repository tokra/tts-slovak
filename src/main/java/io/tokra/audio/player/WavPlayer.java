package io.tokra.audio.player;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ToKra
 * @since 2012
 *
 */
public class WavPlayer {
	
	public static Logger logger = LoggerFactory.getLogger(WavPlayer.class);
	
	public static void play(File file, CountDownLatch latch) throws UnsupportedAudioFileException, IOException{
		InputStream fis = new FileInputStream(file); //read audio data from source
		InputStream bis = new BufferedInputStream(fis); //add buffer for mark/reset support
		AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
		play(ais, latch);
	}
	
	public static void play(final InputStream inputStream, CountDownLatch latch) {
		logger.debug("About to play WAV !");
		Thread playWavThread = new Thread(new PlayWavRunnable(inputStream, latch));
		playWavThread.setName("WavPlayer");
		playWavThread.start();
	}

	public static void play(final InputStream inputStream) {
		logger.debug("About to play WAV !");
		Thread playWavThread = new Thread(new PlayWavRunnable(inputStream));
		playWavThread.setName("WavPlayer");
		playWavThread.start();
	}

	public static void play(final String wavFile) throws FileNotFoundException {
		play(new FileInputStream(wavFile));
	}
	
}