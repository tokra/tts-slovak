package io.tokra.audio.tts.slovak;

import static io.tokra.audio.tts.SAMPA.SK.convertTextToSAMPA;
import static io.tokra.audio.tts.SAMPA.SK.mutation;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.tokra.audio.player.WavPlayer;
import io.tokra.audio.tts.Synthesis;

public final class SynthesisSlovak extends Synthesis {
	
	private static final Logger logger = LoggerFactory.getLogger(SynthesisSlovak.class);
	
	public static final String AUDIO_DB_FILE = "/audio/database_final.wav";
	public static final String AUDIO_INDEX_FILE = "/audio/index_final.txt";

	/**
	 * @author Tomas Kramaric
	 * @since 1.0
	 * @param args
	 */
	public static void main(String[] args) { //FIXME Implement CommandLineParser
		SynthesisSlovak synthesis = new SynthesisSlovak();
		InputStream is = synthesis.tts("Dobrý deň");

		/* Play */
		WavPlayer.play(is);
	}
	
	@Override
	public List<Short> convertTextToVoiceSamples(String text, short[] audiodbSamples, Map<String, Vector<Integer>> audiodbIndexes) {
		Vector<String> textAsSampa = convertTextToSAMPA(text.toLowerCase().toCharArray());
		Vector<String> textWithSampaMutation = mutation(textAsSampa);
		
		logger.info("\n\tOriginal text: {}\n\tSAMPA text: {}\n\tSAMPA with mutation: {}", text, textAsSampa, textWithSampaMutation);

		List<Short> synthSamples = getSamplesFromSampedText(textWithSampaMutation, audiodbSamples, audiodbIndexes);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Path getAudioIndexFile() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
