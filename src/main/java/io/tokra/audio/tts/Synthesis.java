package io.tokra.audio.tts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.tokra.audio.wav.WavInfo;
import io.tokra.audio.wav.WavProcessing;

public abstract class Synthesis {

	private static final Logger logger = LoggerFactory.getLogger(Synthesis.class);

	/** all info about audio database */
	private WavInfo audio;
	/** all indexes for audio database */
	private Map<String, Vector<Integer>> phonemes;
	
	private boolean isInitialised;
	
	private static final String THREAD_NAME_READ_AUDIO_DB = "Read Audio Database";
	private static final String THREAD_NAME_READ_PHONEMES = "Read Phonemes";
	
	public Synthesis(){
		StopWatch sw = new StopWatch();
		sw.start();
		logger.debug("Initialization... started !");
		init();
		sw.stop();
		logger.debug("Initialization... finished ! ... Runtime: '{}' ms", sw.getTime());
	}

	/**
	 * Initializer
	 * 
	 * @author Tomas Kramaric
	 */
	protected void init() {
		try {
			final File audiofile = getAudioDbFile() != null ? getAudioDbFile().toFile() : null;
			final File indexFile = getAudioIndexFile() != null ? getAudioIndexFile().toFile() : null;
			if (audiofile == null && indexFile == null) {
				throw new InterruptedException("Some of data file not found !");
			}
			loadData(audiofile, indexFile);
			isInitialised = true;		
			
		} catch (InterruptedException e) {
			logger.error("{} : {}", e.getClass().getName(), ExceptionUtils.getStackTrace(e));
			isInitialised = false;
		}
	}

	protected void loadData(final File... files) throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(2);
		Thread readWav = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					audio = WavProcessing.readWavFileSamples(files[0]);
					latch.countDown();
				} catch (IOException e) {
					logger.error("IOException", e);
				} catch (URISyntaxException e) {
					logger.error("URISyntaxException", e);
				}
			}
		}, THREAD_NAME_READ_AUDIO_DB);
		Thread readPhonemes = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					phonemes = readPhonemes(files[1]);
					latch.countDown();
				} catch (IOException e) {
					logger.error("IOException", e);
				}
			}
		}, THREAD_NAME_READ_PHONEMES);
		readWav.start();
		readPhonemes.start();
		latch.await(); /* lock until init is not done */
	}
	
	/**
	 * @author Tomas Kramaric
	 * @param text input text to be synthesized
	 * @return {@link InputStream} wav representation
	 */
	public InputStream tts(String text) {
		if (audio != null && phonemes != null) {
			List<Short> foldedVoiceSamples = convertTextToVoiceSamples(text);
			InputStream wasIS = WavProcessing.getWavInputStreamFromAudioSamples(foldedVoiceSamples);
			return wasIS;
		}
		return null;
	}

	/**
	 * Refactored from 'convertTextToVoiceSamples()'
	 * @param sampedText input text in form of sampa
	 * @param audiodbSamples database of audio samples
	 * @param audiodbIndexes database of audio samples indexes
	 * @return voice samples in form of {@link List} of short's
	 */
	protected List<Short> getSamplesFromSampedText(Vector<String> sampedText, short[] audiodbSamples, Map<String, Vector<Integer>> audiodbIndexes) {
		List<Short> synthSamples = new ArrayList<Short>();
		for (int i = 0; i < sampedText.size() - 1; i++) {
			String key1 = sampedText.elementAt(i);
			String key2 = sampedText.elementAt(i + 1);

			if (audiodbIndexes.containsKey(key1 + key2)) {
				Vector<Integer> pom3 = audiodbIndexes.get(key1 + key2); //hodnoty ktore prisluchaju dvojici sampa znakov v hashtable
				Vector<Integer> pom4 = audiodbIndexes.get(key1);

				int offset = pom4.elementAt(0).intValue() - 1;
				int start = pom3.elementAt(0).intValue() + offset - 1; // nacita prvu a druhu hodnotu Object(value)
				int end = pom3.elementAt(1).intValue() + offset - 1; // odrata jedna lebo sample zacinaju od 1 nie od 0
				if (start < 0) {
					start = 0;
				}
				if (end > audiodbSamples.length) {
					end = audiodbSamples.length - 1;
				}
				for (int j = start; j <= end; j++) {
					synthSamples.add(audiodbSamples[j]);
				}
			} else {
				logger.warn("Skipping: '{}' , '{}'", key1, key2);
			}
		}
		return synthSamples;
	}

	/**
	 * @param indexFile file of db indexes
	 * @return db indexes as map
	 * @throws IOException exception
	 */
	protected Map<String, Vector<Integer>> readPhonemes(File indexFile) throws IOException {
		StopWatch sw = new StopWatch();
		sw.start();
		Map<String, Vector<Integer>> audioMappings = new ConcurrentHashMap<String, Vector<Integer>>();
		logger.debug("Reading indexes for samples");

		List<String> lines = FileUtils.readLines(indexFile, "utf-8");

		for (String line : lines) {
			String[] mappingValues = line.split("\\t");

			int start = 0;
			int end = 0;
			int middle = 0;
			int offset = 0;
			Vector<Integer> mapping = new Vector<Integer>(); // TODO migrate to List

			switch (mappingValues.length) {
			case 4: { /* difon */
				start = Integer.valueOf(mappingValues[2]).intValue();
				end = Integer.valueOf(mappingValues[3]).intValue();
				mapping.addElement(new Integer(start));
				mapping.addElement(new Integer(end));
				audioMappings.put(mappingValues[0] + mappingValues[1], mapping);
				break;
			}
			case 6: {
				offset = Integer.valueOf(mappingValues[2]).intValue();
				start = Integer.valueOf(mappingValues[3]).intValue();
				end = Integer.valueOf(mappingValues[4]).intValue();
				middle = Integer.valueOf(mappingValues[5]).intValue();
				mapping.addElement(new Integer(offset));
				mapping.addElement(new Integer(start));
				mapping.addElement(new Integer(end));
				mapping.addElement(new Integer(middle));
				audioMappings.put(mappingValues[0], mapping);
				break;
			}
			default: {
				// skip anything else
			}
			}
		}
		sw.stop();
		logger.debug("Reading indexes for samples...Runtime: '{}' ms", sw.getTime());
		return audioMappings;
	}
	
	/*****************\
	|* Abstract      *|
	\*****************/
	
	/**
	 * @return audio db file path as string
	 */
	public abstract String getAudioDbFilePath();
	
	/**
	 * @return audio db indexes file path as string
	 */
	public abstract String getAudioIndexFilePath();
	
	/**
	 * @return audio db file {@link Path}
	 */
	public abstract Path getAudioDbFile();
	
	/**
	 * @return audio db indexes file {@link Path}
	 */
	public abstract Path getAudioIndexFile();
	
	/**
	 * @param text to be synthesized
	 * @return voice samples as {@link List} of short's
	 */
	public abstract List<Short> convertTextToVoiceSamples(String text);
	
	/*****************\
	|*Getters/Setters*|
	\*****************/
	
	/**
	 * @return isInitialised
	 */
	public boolean isInitialised() {
		return isInitialised;
	}
	
	/**
	 * @return decoded samples as short array
	 */
	public short[] getDecodedSamples() {
		return audio.getDecodedSamples();
	}
	
	public Map<String, Vector<Integer>> getPhonemes() {
		return phonemes;
	}
	
}