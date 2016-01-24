package io.tokra.audio.tts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
	 * @lastModified 12.5.2014
	 * @throws URISyntaxException
	 */
	protected void init() {
		try {
			final File file = new File(Synthesis.class.getResource(getAudioDbFilePath()).toURI());
			final CountDownLatch latch = new CountDownLatch(2);
			Thread readWav = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						audio = WavProcessing.readWavFileSamples(file);
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
						phonemes = readPhonemes(getAudioIndexFilePath());
						latch.countDown();
					} catch (IOException e) {
						logger.error("IOException", e);
					}
				}
			}, THREAD_NAME_READ_PHONEMES);
			readWav.start();
			readPhonemes.start();
			latch.await(); /* lock until init is not done */
			isInitialised = true;		
			
		} catch (URISyntaxException e) {
			logger.error("URISyntaxException", e);
			isInitialised = false;
		} catch (InterruptedException e) {
			logger.error("InterruptedException", e);
			isInitialised = false;
		}
	}
	
	/**
	 * @author Tomas Kramaric
	 * @lastModified ToKra, 12.5.2014, changed name
	 * @param text
	 * @return {@link InputStream} wav representation
	 */
	public InputStream tts(String text) {
		if(audio != null ){
			List<Short> foldedVoiceSamples = convertTextToVoiceSamples(text, audio.getDecodedSamples(), phonemes);
			ByteArrayOutputStream baos = createWavFromAudioSamples(foldedVoiceSamples);
			InputStream isSynthetized = new ByteArrayInputStream(baos.toByteArray());
			return isSynthetized;
		}
		return null;
	}

	/**
	 * @author Tomas Kramaric
	 * @lastModified 12.5.2014 | refactor
	 * @param foldedVoiceSamples
	 * @return {@link ByteArrayOutputStream} wav representation
	 */
	protected static ByteArrayOutputStream createWavFromAudioSamples(List<Short> foldedVoiceSamples) {
		int wavSize = foldedVoiceSamples.size() * 2 + 36;
		int dataSize = foldedVoiceSamples.size() * 2;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		try {
			logger.info("Creating WAV header");
			dos.write("RIFF".getBytes()); // chunk ID
			dos.writeInt(intLittleEndian(wavSize)); // velkost wavka -8
			dos.write("WAVE".getBytes()); // RIFF typ
			dos.write("fmt ".getBytes()); // subchunk ID
			dos.writeInt(intLittleEndian(16)); // velkost fmt chunku
			dos.writeShort(shortLittleEndian(1)); // audio format
			dos.writeShort(shortLittleEndian(1)); // pocet kanalov
			dos.writeInt(intLittleEndian(16000)); // sample rate
			dos.writeInt(intLittleEndian(32000)); // byte rate
			dos.writeShort(shortLittleEndian(2)); // wave blok zarovnanie
			dos.writeShort(shortLittleEndian(16)); // bit/sakunda
			dos.write("data".getBytes()); // subchunk2 ID
			dos.writeInt(intLittleEndian(dataSize)); // dlzka datovej casti

			logger.info("Creating WAV content");
			for(Short sample : foldedVoiceSamples){
				dos.writeShort(shortLittleEndian(sample.shortValue()));
			}
			dos.flush();

		} catch (IOException e) {
			logger.info("Could not create WAV content");
		}
		return baos;
	}

	/**
	 * Refactored from 'convertTextToVoiceSamples()'
	 * 
	 * @author Tomas Kramaric
	 * @lastModified Feb 25, 2015
	 * @param dbSamples
	 * @param sampedText
	 * @return synth samples
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
	 * @author Tomas Kramaric
	 * @lastmodified Feb 25, 2015 | refactor
	 * @param indexFile
	 * @return
	 * @throws IOException
	 */
	protected Map<String, Vector<Integer>> readPhonemes(String indexFile) throws IOException {
		StopWatch sw = new StopWatch();
		sw.start();
		Map<String, Vector<Integer>> audioMappings = new ConcurrentHashMap<String, Vector<Integer>>();
		try {
			File file = new File(getClass().getResource(indexFile).toURI());
			logger.debug("Reading indexes for samples");

			List<String> lines = FileUtils.readLines(file, "utf-8");

			for (String line : lines) {
				String[] mappingValues = line.split("\\t");

				int start = 0;
				int end = 0;
				int middle = 0;
				int offset = 0;
				Vector<Integer> mapping = new Vector<Integer>(); //TODO migrate to List
				
				switch(mappingValues.length){
				case 4  : { /* difon */
					start = Integer.valueOf(mappingValues[2]).intValue();
					end = Integer.valueOf(mappingValues[3]).intValue();
					mapping.addElement(new Integer(start));
					mapping.addElement(new Integer(end));
					audioMappings.put(mappingValues[0] + mappingValues[1], mapping);
					break;
				}
				case 6 : {
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
				default : {
					//skip anything else
				}
				}
			}
		
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		sw.stop();
		logger.debug("Reading indexes for samples...Runtime: '{}' ms", sw.getTime());
		return audioMappings;
	}

	protected static final int intLittleEndian(int v) {
		return (v >>> 24) | (v << 24) | ((v << 8) & 0x00FF0000) | ((v >> 8) & 0x0000FF00); 
	}

	protected static final int shortLittleEndian(int v) {
		return ((v >>> 8) & 0x00FF) | ((v << 8) & 0xFF00);
	}
	
	/*****************\
	|* Abstract      *|
	\*****************/
	
	public abstract String getAudioDbFilePath();
	public abstract String getAudioIndexFilePath();
	public abstract Path getAudioDbFile();
	public abstract Path getAudioIndexFile();
	public abstract List<Short> convertTextToVoiceSamples(String text, short[] audiodbSamples, Map<String, Vector<Integer>> audiodbIndexes);
	
	/*****************\
	|*Getters/Setters*|
	\*****************/
	
	public boolean isInitialised() {
		return isInitialised;
	}
	
}
