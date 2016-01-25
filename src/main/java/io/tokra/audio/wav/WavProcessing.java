package io.tokra.audio.wav;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.tokra.audio.wav.WavInfo.FMT;
import io.tokra.audio.wav.WavInfo.RIFF;

/**
 * @author ToKra
 */
public class WavProcessing {
	
	private static final Logger logger = LoggerFactory.getLogger(WavProcessing.class);
	
	public static void saveInputStreamToWav(InputStream samples, String location) {
		try {
			/* File f = new File("C:/outFile.wav"); */
			File f = new File(location);
			f.createNewFile();
			OutputStream out = new FileOutputStream(f);
			byte buf[] = new byte[10000];
			int len;
			while ((len = samples.read(buf)) > 0){
				out.write(buf, 0, len);
			}
			out.close();
			logger.debug("\n\tWav File : '{}' ...was created", location);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @author Tomas Kramaric
	 * @since Feb 24, 2015
	 * @param wavFile wav file
	 * @return {@link WavInfo} wav info
	 * @throws IOException io exception
	 * @throws URISyntaxException uri syntax exception
	 */
	public static WavInfo readWavFileSamples(File wavFile) throws IOException, URISyntaxException {
		StopWatch sw = new StopWatch();
		sw.start();
		long length = getFileLenght(wavFile);
		
		WavInfo wav = null;
		RIFF riff = null;
		FMT fmt = null;
	
		ByteBuffer buf = ByteBuffer.allocate((int)length);
		FileInputStream fis = new FileInputStream(wavFile);
		FileChannel fileChannel = fis.getChannel();
		while (fileChannel.read(buf) > 0) {
			buf.flip();
			while (buf.hasRemaining()) {
				String headerChunkId = getText(buf, 4);
				int headerChunkSize = getNumber(buf, 4);
				logger.trace("Header_ChunkId: '{}', Header_ChunkSize: '{}'", headerChunkId, headerChunkSize);

				switch (headerChunkId) {
				case "RIFF":
					riff = new RIFF(buf);
//					logger.debug("{}", riff);
					break;
				case "fmt ":
					fmt = WavInfo.FMT.getFmtChunk(buf);
//					logger.debug("{}", fmt);
					break;
				case "data":
					wav = processDataChunk(buf, headerChunkSize);
					wav.setRiff(riff);
					wav.setFmt(fmt);
					logger.debug("{}", wav);
					break;
				default:
					getNumber(buf, headerChunkSize);
					break;
				}
			}
			buf.clear();
		}
		fis.close();
		sw.stop();
		logger.info("ReadWavFileSamples... Runtime: '{}' ms", sw.getTime());
		return wav;
	}

	/**
	 * @author Tomas Kramaric
	 * @since Feb 24, 2015
	 * @param buf {@link ByteBuffer}
	 * @param headerChunkSize header chunk size
	 * @return {@link WavInfo}
	 * @throws IOException io exception
	 */
	protected static WavInfo processDataChunk(ByteBuffer buf, int headerChunkSize) throws IOException {
		StopWatch sw = new StopWatch();
		sw.start();
		short[] lowerBits = new short[headerChunkSize];
		short[] upperBits = new short[headerChunkSize];
		readBits(buf, lowerBits, upperBits);
		short[] samples = decodePCM16bit(lowerBits, upperBits);
		sw.stop();
		logger.info("Processing Data Chunk... Runtime: '{}' ms", sw.getTime());
		{ /* store results */
			WavInfo wav = new WavInfo();
			wav.setSamplesLowerBits(lowerBits);
			wav.setSamplesUpperBits(upperBits);
			wav.setDecodedSamples(samples);
			return wav;
		}
	}
	
	/**
	 * 
	 * @author ToKra
	 * @since Feb 24, 2015 
	 * @param file file
	 * @return file length
	 * @throws IOException io exception
	 */ //FIXME use Files instead
	public static long getFileLenght(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		try {
			int length = is.available();
			logger.debug("File path: '{}', Length: '{}'", file.getAbsolutePath(), length);
			return length;
		} finally {
			is.close();
		}
	}
	
	/**
	 * 
	 * @author ToKra
	 * @since Feb 23, 2015 
	 * @param byteBuffer {@link ByteBuffer}
	 * @param arrayLength array length
	 * @return data as integer
	 * @throws IOException io expception
	 */
	public static int getNumber(ByteBuffer byteBuffer, int arrayLength) throws IOException{
		int number = 0;
		for (int i = 0; i < arrayLength; i++) {
			byte read = byteBuffer.get();
			int readUnsigned = getUnsignedByte(read);
			number += (int) (Math.pow(2, i * 8) * readUnsigned);
		}
		return number;
	}
	
	/**
	 * 
	 * @author ToKra
	 * @since Feb 23, 2015 
	 * @param byteBuffer {@link ByteBuffer}
	 * @param arrayLenght array lentght integer
	 * @return data as string
	 * @throws IOException io exception
	 */
	public static String getText(ByteBuffer byteBuffer, int arrayLenght) throws IOException{
		char[] array = new char[arrayLenght];
		for (int i = 0; i < arrayLenght; i++){
			char c = (char) byteBuffer.get();
			array[i] = c;
		}
		return new String(array);
	}
	
	/**
	 * 
	 * @author ToKra
	 * @since Feb 23, 2015 
	 * @param param byte number
	 * @return unsigned byte as short
	 */
	public static short getUnsignedByte(byte param) {
	    return (short) (param & 0xFF);
	}
	
	/**
	 * 
	 * @author ToKra
	 * @since Feb 23, 2015 
	 * @param byteBuffer {@link ByteBuffer}
	 * @param lowerBits as short array
	 * @param upperBits as short array
	 * @throws IOException io exception
	 */
	public static void readBits(ByteBuffer byteBuffer, short[] lowerBits, short[] upperBits) throws IOException {
		StopWatch sw = new StopWatch();
		sw.start();
		for (int i = 0; i < lowerBits.length; i++) {
			if(byteBuffer.hasRemaining()){
				byte readLower = byteBuffer.get();
				byte readUpper = byteBuffer.get();
				short readLowerUnsigned = getUnsignedByte(readLower);
				short readUpperUnsigned = getUnsignedByte(readUpper);
				lowerBits[i] = readLowerUnsigned;
				upperBits[i] = readUpperUnsigned;
			} else {
				break;
			}
		}
		logger.trace("Read LowerBits: '{}', Read UpperBits: '{}'", lowerBits.length, upperBits.length);
		sw.stop();
		logger.debug("Reading data bits... Runtime: '{}' ms", sw.getTime());
	}
	
	/**
	 * Decodes PCM data to samples
	 * 
	 * @author ToKra
	 * @since Feb 23, 2015 
	 * @param lower bits as short array
	 * @param upper bits as short array
	 * @return short array of decoded pcm16
	 */
	public static short[] decodePCM16bit(short[] lower, short[] upper){
		StopWatch sw = new StopWatch();
		sw.start();
		short[] samples = new short[lower.length];
		for(int i = 0; i < lower.length; i++){
			short sampleLittle = (short) ((lower[i] & 0xFF) | (upper[i] << 8)); //http://www.jsresources.org/faq_audio.html#reconstruct_samples
			samples[i] = sampleLittle;
//			logger.trace("Sample: '{}', Value: '{}'", i, sampleLittle);
		}
		sw.stop();
		logger.debug("Decode PCM... Runtime: '{}' ms", sw.getTime());
		return samples;
	}
	
	/**
	 * @author Tomas Kramaric
	 * @param foldedVoiceSamples in {@link List} of short's
	 * @return {@link ByteArrayOutputStream} wav representation
	 */
	public static ByteArrayOutputStream createWavFromAudioSamples(List<Short> foldedVoiceSamples) {
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
	
	public static InputStream getWavInputStreamFromAudioSamples(List<Short> foldedVoiceSamples) {
		ByteArrayOutputStream baos = WavProcessing.createWavFromAudioSamples(foldedVoiceSamples);
		if (baos != null) {
			return new ByteArrayInputStream(baos.toByteArray());
		}
		return null;
	}
	
	protected static final int intLittleEndian(int v) {
		return (v >>> 24) | (v << 24) | ((v << 8) & 0x00FF0000) | ((v >> 8) & 0x0000FF00); 
	}

	protected static final int shortLittleEndian(int v) {
		return ((v >>> 8) & 0x00FF) | ((v << 8) & 0xFF00);
	}

}
