package io.tokra.audio.tts.slovak;

import static io.tokra.audio.tts.SAMPA.SK.convertTextToSAMPA;
import static io.tokra.audio.tts.SAMPA.SK.mutation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.tokra.audio.player.WavPlayer;
import io.tokra.audio.tts.Synthesis;
import io.tokra.ioutils.ResourcesSupport;

public final class SynthesisSlovak extends Synthesis {
	
	private static final Logger logger = LoggerFactory.getLogger(SynthesisSlovak.class);
	
	public static final String AUDIO_DB_FILE = "audio/database_final.wav";
	public static final String AUDIO_INDEX_FILE = "audio/index_final.txt";

	/**
	 * @author Tomas Kramaric
	 * @since 1.0
	 * @param args
	 */
	public static void main(String[] args) { // FIXME Implement using apache's CommandLineParser
		SynthesisSlovak synthesis = new SynthesisSlovak();
		if (!synthesis.isInitialised()) {
			logger.warn("Initialisation failed !");
			return;
		}
		InputStream is = synthesis.tts("Dobrý deň");
		if (is == null) { 
			logger.warn("Cannot play WAV because IS is null !");
			return;
		}
		WavPlayer.play(is); /* Play */
	}
	
	@Override
	public List<Short> convertTextToVoiceSamples(String text) {
		Vector<String> textAsSampa = convertTextToSAMPA(text.toLowerCase().toCharArray());
		Vector<String> textWithSampaMutation = mutation(textAsSampa);
		
		logger.info("\n\tOriginal text: {}\n\tSAMPA text: {}\n\tSAMPA with mutation: {}", text, textAsSampa, textWithSampaMutation);

		List<Short> synthSamples = getSamplesFromSampedText(textWithSampaMutation, getDecodedSamples(), getPhonemes());
		return synthSamples;
	}

	@Override
	public String getAudioDbFilePath() {
		return AUDIO_DB_FILE;
	}

	@Override
	public String getAudioIndexFilePath() {
		return AUDIO_INDEX_FILE;
	}

	@Override
	public Path getAudioDbFile() {
		try {
			return ResourcesSupport.getResourceAsPath(AUDIO_DB_FILE);
		} catch (IOException e) {
			logger.error("{} : {}", e.getClass().getName(), ExceptionUtils.getStackTrace(e));
			return null;
		}
	}

	@Override
	public Path getAudioIndexFile() {
		try {
			return ResourcesSupport.getResourceAsPath(AUDIO_INDEX_FILE);
		} catch (IOException e) {
			logger.error("{} : {}", e.getClass().getName(), ExceptionUtils.getStackTrace(e));
			return null;
		}
	}
	
}