package io.tokra.audio.tts.slovak;

import static io.tokra.audio.tts.SAMPA.SK.convertTextToSAMPA;
import static io.tokra.audio.tts.SAMPA.SK.mutation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
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
	public static String CONTENT = "";

	/**
	 * @author Tomas Kramaric
	 * @since 1.0
	 * @param args command line arguments
	 */
	public static void main(String[] args) { // FIXME Implement using apache's CommandLineParser
		SynthesisSlovak synthesis = new SynthesisSlovak();
		if (args.length > 0) {
			// Print statements
			System.out.println("The command line" + " arguments are:");
			// Iterating the args array
			// using for each loop
			// using for each loop istv_l@N621LIS-2 MINGW64 ~/git/tts-slovak (FEATURE_2) -> merge to main
			//d77
			
			//cohfo
			//rreemoote
			for (String val : args) {
				// Printing command line arguments
				System.out.println(val);

				try {
					System.out.println("args[0]  " + args[0]);
					// content = Files.readString(Paths.get(args[0]));
					// String file = "d:\\JEBCONT\\last\\2022\\dd\\SSS.txt";
//					String file = "d:\\JEBCONT\\last\\2022\\dd\\SSS_8.txt";
//					String file = "d:\\JEBCONT\\last\\2022\\dd\\chyby.txt";
					String file = ".\\text_tts.txt";
					// Paths.get(new URI();
					Paths.get(file).getFileName();
					// Charset.availableCharsets().keySet().forEach(key -> System.out.println(key));
					// "ISO-8859-2"

					// CONTENT = Files.readString(Paths.get(file), Charset.forName("UTF-8"));
					// String content = new Scanner(new File(file)).toString();
					CONTENT = FileUtils.readFileToString(new File(file), "UTF-8");
					System.out.println("CONTENT: " + CONTENT);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			// Print statements
			System.out.println("No command line " + "arguments found.");
		}

			
		if (!synthesis.isInitialised()) {
			logger.warn("Initialisation failed !");
			return;
		}
		
		InputStream is = synthesis.tts(CONTENT);
//		InputStream initialStream = FileUtils.openInputStream
		// (new File("src/main/resources/sample.txt"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
		Date date = new Date();

		File targetFile = new File(".\\targetFile_" + sdf.format(date.getTime()) + ".wav");

//		try {
//			FileUtils.copyInputStreamToFile(is, targetFile);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
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

		logger.info("\n\tOriginal text: {}\n\tSAMPA text: {}\n\tSAMPA with mutation: {}", text, textAsSampa,
				textWithSampaMutation);

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
