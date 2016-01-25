package io.tokra.audio.player;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.lang3.time.StopWatch;

/**
	 * @author ToKra
	 * @since Feb 24, 2015
	 *
	 */
	public class PlayWavRunnable implements Runnable {
		
		private InputStream is;
		private CountDownLatch latch;
		
		public PlayWavRunnable(InputStream is){
			this.is = is;
			WavPlayer.logger.debug("Runnable 'play WAV' instance created !");
		}
		
		public PlayWavRunnable(InputStream is, CountDownLatch latch){
			this.is = is;
			this.latch = latch;
			WavPlayer.logger.debug("Runnable 'play WAV' instance created !");
		}

		@Override
		public void run() {
			StopWatch sw = new StopWatch();
			sw.start();
			WavPlayer.logger.info("Play !");
			AudioInputStream audioInputStream = null;
			try {
				if(is instanceof AudioInputStream){
					audioInputStream = (AudioInputStream) is;
				} else {
					audioInputStream = AudioSystem.getAudioInputStream(is);
				}
				audioInputStream.close();
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
				return;
			} catch (IOException e) {
				e.printStackTrace();

				return;
			}

			SourceDataLine sourceDataLine = null;
			try {
				AudioFormat audioFormat = audioInputStream.getFormat();
				DataLine.Info info = new DataLine.Info(SourceDataLine.class,
						audioFormat);
				sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
				sourceDataLine.open(audioFormat);
			} catch (LineUnavailableException e) {
				e.printStackTrace();
				return;
			}

			sourceDataLine.start();
			byte[] data = new byte[524288];// 128Kb
			try {
				int bytesRead = 0;
				while (bytesRead != -1) {
					bytesRead = audioInputStream.read(data, 0, data.length);
					if (bytesRead >= 0)
						sourceDataLine.write(data, 0, bytesRead);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return;
			} finally {
				sourceDataLine.drain();
				sourceDataLine.close();
			}
			sw.stop();
			WavPlayer.logger.info("Stop ! ...Runtime: '{}' ms", sw.getTime());
			if (latch != null) {
				latch.countDown();
			}
		}
		
	}