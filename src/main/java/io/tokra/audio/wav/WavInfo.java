package io.tokra.audio.wav;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 
 * @author Tomas Kramaric
 * @since Feb 24, 2015
 *
 */
public class WavInfo {
	
	public WavInfo(){
		
	}
	
	private short[] samplesLowerBits;
	private short[] samplesUpperBits;
	private short[] decodedSamples;
	private RIFF riff;
	private FMT fmt;
	
	public short[] getSamplesLowerBits() {
		return samplesLowerBits;
	}
 
	public void setSamplesLowerBits(short[] samplesLowerBits) {
		this.samplesLowerBits = samplesLowerBits;
	}

	public short[] getSamplesUpperBits() {
		return samplesUpperBits;
	}

	public void setSamplesUpperBits(short[] samplesUpperBits) {
		this.samplesUpperBits = samplesUpperBits;
	}
	
	public short[] getDecodedSamples() {
		return decodedSamples;
	}

	public void setDecodedSamples(short[] decodedSamples) {
		this.decodedSamples = decodedSamples;
	}

	public FMT getFmt() {
		return fmt;
	}

	public void setFmt(FMT fmt) {
		this.fmt = fmt;
	}
	
	public RIFF getRiff() {
		return riff;
	}

	public void setRiff(RIFF riff) {
		this.riff = riff;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(riff != null) {
			sb.append(riff.toString());
		}
		if(fmt != null) {
			sb.append(fmt.toString());
		}
		sb.append("\n\tLower bits                        : " + samplesLowerBits.length);
		sb.append("\n\tUpper bits                        : " + samplesUpperBits.length);
		sb.append("\n\tDecoded samples                   : " + decodedSamples.length);
		return sb.toString();
	}

	/**
	 * FMT info wrapper
	 * 
	 * @author Tomas Kramaric
	 * @since Feb 24, 2015
	 */
	public static class FMT {
		private int audioFormat;
		private int numChannels;
		private int sampleRate;
		private int byteRate;
		private int blockAlign;
		private int bitsPerSample;
		
		private FMT(){
			
		}
		
		public FMT(ByteBuffer buf) throws IOException{
			decodeFmtChunk(buf);
		}

		protected void decodeFmtChunk(ByteBuffer buf) throws IOException {
			this.audioFormat = WavProcessing.getNumber(buf, 2);
			this.numChannels = WavProcessing.getNumber(buf, 2);
			this.sampleRate = WavProcessing.getNumber(buf, 4);
			this.byteRate = WavProcessing.getNumber(buf, 4);
			this.blockAlign = WavProcessing.getNumber(buf, 2);
			this.bitsPerSample = WavProcessing.getNumber(buf, 2);
		}
		
		/**
		 * FMT chunk from ByteBuffer
		 * 
		 * @author tokra
		 * @since Feb 24, 2015
		 * @param buf {@link ByteBuffer}
		 * @return FMT {@link FMT}
		 * @throws IOException io exception
		 */
		public static FMT getFmtChunk(ByteBuffer buf) throws IOException {
			FMT fmt = new FMT();
			fmt.audioFormat = WavProcessing.getNumber(buf, 2);
			fmt.numChannels = WavProcessing.getNumber(buf, 2);
			fmt.sampleRate = WavProcessing.getNumber(buf, 4);
			fmt.byteRate = WavProcessing.getNumber(buf, 4);
			fmt.blockAlign = WavProcessing.getNumber(buf, 2);
			fmt.bitsPerSample = WavProcessing.getNumber(buf, 2);
			return fmt;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			switch(audioFormat){
			case 1: 
				sb.append("\n\tAudioFormat                       : 'PCM'");
				break;
			case 3: 
				sb.append("\n\tAudioFormat                       : 'IEEE float'");
				break;
			case 6: 
				sb.append("\n\tAudioFormat                       : '8-bit ITU-T G.711 A-law'");
				break;
			case 7: 
				sb.append("\n\tAudioFormat                       : '8-bit ITU-T G.711 µ-law'");
				break;
			case 65534: 
				sb.append("\n\tAudioFormat                       : 'Determined by SubFormat'");
				break;
			}
			sb.append("\n\tNumber of interleaved channels    : " + numChannels);
			sb.append("\n\tSampling rate (blocks per second) : " + sampleRate);
			sb.append("\n\tData rate                         : " + byteRate);
			sb.append("\n\tData block size (bytes)           : " + blockAlign);
			sb.append("\n\tBits per sample                   : " + bitsPerSample);
			return sb.toString();
		}
	}
	
	/**
	 * Riff chunk data wrapper
	 * 
	 * @author Tomas Kramaric
	 * @since Feb 24, 2015
	 *
	 */
	public static class RIFF{
		
		private String format;
		
		public RIFF(ByteBuffer buf) throws IOException{
			this.format = WavProcessing.getText(buf, 4);
		}

		public String getFormat() {
			return format;
		}

		public void setFormat(String format) {
			this.format = format;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("\n\tRIFF Format                       : " + format);
			return sb.toString();
		}
	}
}
