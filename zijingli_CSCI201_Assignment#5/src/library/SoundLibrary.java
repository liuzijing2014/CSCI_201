package library;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundLibrary {
	
	public static Clip draw;
	public static Clip start;
	public static Clip win;
	public static Clip lose;
	
	public static void playDraw()
	{
		try {
			draw = AudioSystem.getClip();
			AudioInputStream soundInput = AudioSystem.getAudioInputStream(new File("sound/draw_card.wav"));
			draw.open(soundInput);
			draw.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void playStart()
	{
		try {
			start = AudioSystem.getClip();
			AudioInputStream soundInput;
			soundInput = AudioSystem.getAudioInputStream(new File("sound/your_turn.wav"));
			start.open(soundInput);
			start.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void playWin()
	{
		try {
			win = AudioSystem.getClip();
			AudioInputStream soundInput;
			soundInput = AudioSystem.getAudioInputStream(new File("sound/victory.wav"));
			win.open(soundInput);
			win.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void playLose()
	{
		try {
			lose = AudioSystem.getClip();
			AudioInputStream soundInput;
			soundInput = AudioSystem.getAudioInputStream(new File("sound/lose.wav"));
			lose.open(soundInput);
			lose.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
