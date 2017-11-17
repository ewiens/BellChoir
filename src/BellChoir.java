import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * This program plays a song on 'hand bells' as a bell choir
 * 
 * @author Ericaf
 *
 */

public class BellChoir {

	private static final int finalIndex = Note.values().length - 1;
	
	public static Player[] player = new Player[finalIndex];
	

	public static void main(String[] args) {
		List<BellNote> notes = null;

		for (int i = 0; i < args.length; i++) {

			switch (i) {
			case 0:
				notes = loadFile(args[i]);
				break;
			default:
				System.err.println("Too many arguments");
			}
		}
		if (!validateData(notes)) {
			System.err.println("Failed");
			System.exit(-1);
		}

		// Teach the AI program using the data
		final AudioFormat af = new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);
		BellChoir t = new BellChoir(af);
		
		createPlayers();
		
		try {
			t.playSong(notes);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}

	}

	private static void createPlayers() {
		// TODO Auto-generated method stub
		
		for(int i=0; i<finalIndex; i++){
			player[i] = new Player(Note.values()[i+1]);
		}
		
	}

	/**
	 * Validates the data and determines if the song can run or not
	 * 
	 * @param notes
	 * @return boolean
	 */
	private static boolean validateData(List<BellNote> notes) {
		// assume it works until it breaks
		boolean success = true;

		// if any of the below conditions fail, the song will not play
		for (BellNote n : notes) {
			if (n.note == null) {
				success = false;
			}
			if (n.length == null) {
				success = false;
			}
		}
		return success;
	}

	/**
	 * Loads in the file and parses the lines to get the Bell Notes
	 * 
	 * @param filename
	 * @return array list of BellNotes
	 */
	private static List<BellNote> loadFile(String filename) {
		final List<BellNote> notes = new ArrayList<>();
		final File file = new File(filename);
		if (file.exists()) {
			try (FileReader fileReader = new FileReader(file); BufferedReader br = new BufferedReader(fileReader)) {

				String line = null;
				while ((line = br.readLine()) != null) {
					BellNote myNote = parseLine(line);
					if (myNote != null) {
						notes.add(myNote);
					} else {
						System.err.println("Error: Invalid note or note length '" + line + "'");
					}
				}
			} catch (IOException ignored) {
			}

		} else {
			System.err.println("File '" + filename + "' not found");
		}
		return notes;
	}

	/**
	 * Parses the values read in from the file into BellNotes
	 * 
	 * @param line
	 * @return BellNote
	 */
	private static BellNote parseLine(String line) {
		String[] fields = line.split("\\s+");
		if (fields.length == 2) {
			Note myNote = null;
			try {
				myNote = Note.valueOf(fields[0]);
			} catch (Exception e) {
				System.err.println("Error: Invalid Note '" + fields[0] + "'");
			}

			NoteLength myNoteLength = parseNoteLength(fields[1]);
			return new BellNote(myNote, myNoteLength);
		}
		return null;
	}

	/**
	 * Returns if the note is a proper notelength or prints an errors
	 * 
	 * @param noteLength
	 * @return NoteLength
	 */
	private static NoteLength parseNoteLength(String noteLength) {
		if (noteLength == null) {
			return null;
		}

		// checks to make sure the string is an integer
		try {
			Integer.parseInt(noteLength);
		} catch (Exception e) {
			System.err.println("Error: Invalid Note Length '" + noteLength + "'");
			return null;
		}

		// Returns the note length based on the given integer
		switch (Integer.parseInt(noteLength)) {
		case 1:
			return NoteLength.WHOLE;
		case 2:
			return NoteLength.HALF;
		case 4:
			return NoteLength.QUARTER;
		case 8:
			return NoteLength.EIGTH;

		default:
			System.err.println("Error: Invalid note length '" + noteLength + "'");
			return null;

		}
	}

	private final AudioFormat af;

	BellChoir(AudioFormat af) {
		this.af = af;
	}

	void playSong(List<BellNote> song) throws LineUnavailableException {
		try (final SourceDataLine line = AudioSystem.getSourceDataLine(af)) {
			line.open();
			line.start();
			
			Mutex m = new Mutex();
			
			for (BellNote bn : song) {
				m.acquire();
				try{
//					player[bn.note.ordinal()-1].setLine(line);
//					player[bn.note.ordinal()-1].setLength(bn.length);
					player[bn.note.ordinal()-1].playNote(line, bn.length);
				}finally{
					m.release();
				}
				
//				playNote(line, bn);
			}
			line.drain();
		}
	}

//	private void playNote(SourceDataLine line, BellNote bn) {
//		final int ms = Math.min(bn.length.timeMs(), Note.MEASURE_LENGTH_SEC * 1000);
//		final int length = Note.SAMPLE_RATE * ms / 1000;
//		line.write(bn.note.sample(), 0, length);
//		line.write(Note.REST.sample(), 0, 50);
//	}
	

}

/**
 * BellNote has the Note and the length of the note
 * 
 * @author Ericaf Note, NoteLength, and BellNote were written by Nate Williams
 */
class BellNote {
	final Note note;
	final NoteLength length;

	BellNote(Note note, NoteLength length) {
		this.note = note;
		this.length = length;
	}

	Note getNote() {
		return this.note;
	}

	NoteLength getNoteLength() {
		return this.length;
	}
}

/**
 * NoteLength is how long the note is. Whole(1), Half(2), Quarter(4), or Eigth(8)
 * @author Ericaf
 *
 */
enum NoteLength {
	WHOLE(1.0f), 
	HALF(0.5f), 
	QUARTER(0.25f), 
	EIGTH(0.125f);

	private final int timeMs;

	private NoteLength(float length) {
		timeMs = (int) (length * Note.MEASURE_LENGTH_SEC * 1000);
	}

	public int timeMs() {
		return timeMs;
	}
}

/**
 * Notes that the program knows how to play A4-B5 with sharps but no flats
 * @author Ericaf
 *
 */
enum Note {
	// REST Must be the first 'Note'
	REST, A4, A4S, B4, C4, C4S, D4, D4S, E4, E4S, F4, F4S, G4, G4S, A5, B5;

	public static final int SAMPLE_RATE = 48 * 1024; // ~48KHz
	public static final int MEASURE_LENGTH_SEC = 1;

	// Circumference of a circle divided by # of samples
	private static final double step_alpha = (2.0d * Math.PI) / SAMPLE_RATE;

	private final double FREQUENCY_A_HZ = 440.0d;
	private final double MAX_VOLUME = 127.0d;

	private final byte[] sinSample = new byte[MEASURE_LENGTH_SEC * SAMPLE_RATE];

	private Note() {
		int n = this.ordinal();
		if (n > 0) {
			// Calculate the frequency!
			final double halfStepUpFromA = n - 1;
			final double exp = halfStepUpFromA / 12.0d;
			final double freq = FREQUENCY_A_HZ * Math.pow(2.0d, exp);

			// Create sinusoidal data sample for the desired frequency
			final double sinStep = freq * step_alpha;
			for (int i = 0; i < sinSample.length; i++) {
				sinSample[i] = (byte) (Math.sin(i * sinStep) * MAX_VOLUME);
			}
		}
	}

	public byte[] sample() {
		return sinSample;
	}
}
