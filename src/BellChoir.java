import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BellChoir {

	public static void main(String[] args) {
        List<BellNote> notes = null;
        NoteLength first = NoteLength.WHOLE;

        for (int i = 0; i < args.length; i++) {
        	
            switch (i) {
            case 0:
                notes = loadFile(args[i]);
                break;
            default:
            	System.err.println("Too many arguments");
            }
        }
//        if (!validateData(notes, first)) {
//            System.err.println("Failed");
//            System.exit(-1);
//        }
        // Teach the AI program using the data
    }

	/**
	 * 
	 * @param filename
	 * @return array list of Notes
	 */
	private static List<BellNote> loadFile(String filename) {
		final List<BellNote> notes = new ArrayList<>();
		final File file = new File(filename);
		if (file.exists()){
			try (FileReader fileReader = new FileReader(file);
				 BufferedReader br = new BufferedReader(fileReader)) {

				String line = null;
				while ((line = br.readLine())!= null){
					BellNote myNote = parseLine(line);
					if (myNote != null) {
						System.out.print(myNote+" ");
						notes.add(myNote);
					}else {
						System.err.println("Error: Invalid note '" + line + "'");
					}
				}
			} catch (IOException ignored) {}
					
		} else {
			System.err.println("File '"+filename+"' ot found");
		}
		return notes;
	}
	
	private static BellNote parseLine(String line) {
        String[] fields = line.split("\\s+");
        if (fields.length == 2) {
           return new BellNote(parseNote(fields[0]), parseNoteLength(fields[1]));
        }
        return null;
    }

	private static Note parseNote(String string) {
		// TODO Auto-generated method stub
		
		
		
		return null;
	}

	private static NoteLength parseNoteLength(String noteLength) {
		// TODO Auto-generated method stub
		if (noteLength == null) {
			return null;
		}
		
		switch(Integer.parseInt(noteLength)){
		case 1:
			return NoteLength.WHOLE;
		case 2:
			return NoteLength.HALF;
		case 4:
			return NoteLength.QUARTER;
		case 8:
			return NoteLength.EIGTH;
		
		default:
			return null;
		
		}		
	}

	

}
/**
 * 
 * @author Ericaf
 * Note, NoteLength, and BellNote were written by Nate Williams
 */
/*
class BellNote {
    final Note note;
    final NoteLength length;

    BellNote(Note note, NoteLength length) {
        this.note = note;
        this.length = length;
    }
}

enum NoteLength {
    WHOLE(1.0f),
    HALF(0.5f),
    QUARTER(0.25f),
    EIGTH(0.125f);

    private final int timeMs;

    private NoteLength(float length) {
        timeMs = (int)(length * Note.MEASURE_LENGTH_SEC * 1000);
    }

    public int timeMs() {
        return timeMs;
    }
}

enum Note {
    // REST Must be the first 'Note'
    REST,
    A4,
    A4S,
    B4,
    C4,
    C4S,
    D4,
    D4S,
    E4,
    F4,
    F4S,
    G4,
    G4S,
    A5;

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
                sinSample[i] = (byte)(Math.sin(i * sinStep) * MAX_VOLUME);
            }
        }
    }

    public byte[] sample() {
        return sinSample;
    }
}
*/