import javax.sound.sampled.SourceDataLine;

public class Player implements Runnable{
	
	private Note myNote;
	private SourceDataLine myLine;
	private NoteLength myLength;
	private Thread playerThread;
	private Boolean isPlaying=false;
	
	Player(Note n){
		myNote = n;
		playerThread = new Thread(this,"Note "+myNote);
		playerThread.start();
	}
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("In thread "+myNote);
		while (isPlaying){
			final int ms = Math.min(myLength.timeMs(), Note.MEASURE_LENGTH_SEC * 1000);
			final int length = Note.SAMPLE_RATE * ms / 1000;
			myLine.write(myNote.sample(), 0, length);
			myLine.write(Note.REST.sample(), 0, 50);
			isPlaying = false;
		}
		isPlaying=false;
	}
	
	
	public void playNote(SourceDataLine line, NoteLength l) {
		myLine = line;		
		myLength =l;
		isPlaying = true;
		
		System.out.println("Play "+myNote+" for "+l);
		run();
		// Only plays the first note
//		playerThread.start();
//		while(isPlaying){
//			
//		}
//		try {
//			playerThread.join();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
					
	}

}
