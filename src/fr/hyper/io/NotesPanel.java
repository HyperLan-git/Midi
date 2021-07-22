package fr.hyper.io;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.swing.JPanel;

public class NotesPanel extends JPanel {
	public static final int AAA = 60;
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

	private MidiWindow parent;

	private double beatsShown = 4;

	public NotesPanel(MidiWindow parent) {
		this.parent = parent;
	}

	@Override
	public void paint(Graphics g) {
		if(!parent.getMidiHandler().isOpen()) {
			super.paint(g);
			return;
		}
		int w = this.getWidth(), h = this.getHeight();

		Graphics2D g2 = (Graphics2D) g;
		MidiHandler midi = parent.getMidiHandler();
		g2.setBackground(Color.BLACK);
		g2.clearRect(0, 0, w, h);
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke());
		Track[] tracks = midi.getTracks();
		for(int i = 0; i < tracks.length; i++) {
			int x = (int) ((double)(i*w)/tracks.length);
			g2.drawLine(x, 0, x, h);
		}
		long beatLength = (long) (1000000.0/midi.getBPM()),
				prevBeat = midi.getPosition()-(midi.getPosition()%(beatLength*AAA)),
				start = midi.getPosition(),
				end = (long) (midi.getPosition()+beatLength*beatsShown*AAA);

		double ticksPerSecond = midi.getResolution() * (midi.getBPM() / 60.0);
		long tickSize = (long) (1000000.0 / ticksPerSecond);

		for(int i = 0; i <= beatsShown*AAA+1; i+=AAA) {
			long t = prevBeat+beatLength*i;
			t -= start;
			int y = (int) ((double) (t)/(end-start)*h);
			y = h-y;
			g2.drawLine(0, y, w, y);
		}

		for(int i = 0; i < tracks.length; i++) {
			g2.setStroke(new BasicStroke(5));
			Color c = Color.getHSBColor((float)(i)/tracks.length, 1, 1);
			Track track = tracks[i];
			g2.setColor(c);
			int x1 = (int) ((double)(i*w)/tracks.length),
					x2 = (int) ((double)((i+1)*w)/tracks.length);
			for(int j = 0; j < track.size(); j++) {
				MidiEvent event = track.get(j);
				if(!(event.getMessage() instanceof ShortMessage)) continue;
				if(((ShortMessage)(event.getMessage())).getCommand() != NOTE_ON) continue;
				long t = tickSize*event.getTick();
				if(t < start) continue;
				if(t > end) break;
				t -= start;
				int y = (int) ((double) (t)/(end-start)*h);
				y = h-y;
				g2.drawLine(x1, y, x2, y);
			}
		}
	}
}
