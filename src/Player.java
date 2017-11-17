import javax.sound.sampled.SourceDataLine;

/**
 * Player class that controls the player threads Sets the notes and plays them
 * for the designated amount of time
 * 
 * @author Ericaf
 *
 */
public class Player implements Runnable {

	private Note myNote;
	private SourceDataLine myLine;
	private NoteLength myLength;
	private Thread playerThread;
	private Boolean isPlaying = false;

	Player(Note n) {
		myNote = n;
		playerThread = new Thread(this, "Note " + myNote);
		playerThread.start();
	}

	/**
	 * plays the note assigned to the thread
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (isPlaying) {
			final int ms = Math.min(myLength.timeMs(), Note.MEASURE_LENGTH_SEC * 1000);
			final int length = Note.SAMPLE_RATE * ms / 1000;
			myLine.write(myNote.sample(), 0, length);
			myLine.write(Note.REST.sample(), 0, 50);
			isPlaying = false;
		}
		isPlaying = false;
	}

	/**
	 * Sets the line and the note length for the player to play tells the player
	 * to play the note
	 * 
	 * @param SourceDataLine
	 *            line
	 * @param NoteLenth
	 *            l
	 */
	public void playNote(SourceDataLine line, NoteLength l) {
		myLine = line;
		myLength = l;
		isPlaying = true;

		run();

	}

	/**
	 * Stops the player thread
	 * 
	 * @throws InterruptedException
	 */
	public void playerStop() throws InterruptedException {
		playerThread.join();
	}
}
