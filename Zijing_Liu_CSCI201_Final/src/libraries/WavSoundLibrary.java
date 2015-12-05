package libraries;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.*;

//A statically used class for loading sound .wav resources
//Ensures that .wav files aren't loaded into memory more than necessary
public class WavSoundLibrary {
	private static Map<String, File> soundMap;
	
	static{
		soundMap = new HashMap<String,File>();
	}
	
	private WavSoundLibrary() {} //disable constructor
	
	//Plays the sound if available already, otherwise the sound is loaded and played
	public static void playSound(String sound) {
		File toPlay = soundMap.get(sound);
		if(toPlay == null) {
			toPlay = new File(sound);
				soundMap.put(sound, toPlay);
			}
		new Thread(new SoundPlayer(toPlay)).start();
	}
	
	//Forces the .wav to be reloaded from file
	public static void playSoundReload(String sound) {
		File toPlay;
		toPlay = new File(sound);
		soundMap.put(sound, toPlay);
		new Thread(new SoundPlayer(toPlay)).start();
	}
	
	//Clears out all the .wavs from the library
	public static void clear() {
		soundMap.clear();
	}
}

//Used to play the sounds from the WavSoundLibrary
class SoundPlayer implements Runnable {
	File toPlay;
	
	SoundPlayer(File inFile) {
		toPlay = inFile;
	}
	
	@Override
	public void run() {
		try {
			AudioInputStream stream = AudioSystem.getAudioInputStream(toPlay);
			AudioFormat format = stream.getFormat();
			SourceDataLine.Info info = new DataLine.Info(SourceDataLine.class,format,(int) (stream.getFrameLength() * format.getFrameSize()));
			SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
			
			line.open(stream.getFormat());
			line.start();
			int num_read = 0;
			byte[] buf = new byte[line.getBufferSize()];
			while ((num_read = stream.read(buf, 0, buf.length)) >= 0)
			{
				int offset = 0;
				
				while (offset < num_read)
				{
					offset += line.write(buf, offset, num_read - offset);
				}
			}
			line.drain();
			line.stop();
		} catch(IOException | UnsupportedAudioFileException | LineUnavailableException ioe) {
			System.out.println("Audio file is invalid!");
		}
	}
}
