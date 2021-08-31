package fr.hyper.io;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.JComponent;

import fr.hyper.midi.MidiData;
import fr.hyper.midi.MidiNote;

public class NotesPanel2 extends JComponent {
	private static final long serialVersionUID = -428498749840766201L;

	public static final int MIDI_NOTES = 128;

	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"},
			EU_NOTE_NAMES = {"do", "do#", "ré", "ré#", "mi", "fa", "fa#", "sol", "sol#", "la", "la#", "si"};

	private MidiWindow parent;

	private double beatsShown = 10;
	private int xPos = 0;
	private int colWidth = 50;

	public NotesPanel2(MidiWindow parent) {
		this.parent = parent;
		this.setMinimumSize(new Dimension(colWidth*MIDI_NOTES+1, 360));
		this.setPreferredSize(new Dimension(colWidth*MIDI_NOTES+1, 360));
		this.setSize(new Dimension(colWidth*MIDI_NOTES+1, 360));
		this.setBounds(0, 0, colWidth*MIDI_NOTES+1, 360);
	}

	@Override
	public void paint(Graphics g) {
		MidiHandler midi = parent.getMidiHandler();
		MidiData data = midi.getMidiData();
		if(!midi.isOpen() || data == null) {
			super.paint(g);
			return;
		}
		int w = this.getWidth()-1, h = this.getHeight();
		List<List<MidiNote>> tracks = data.getTracks();

		Graphics2D g2 = (Graphics2D) g;
		g2.setBackground(Color.BLACK);
		g2.clearRect(0, 0, w, h);
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke());
		for(int i = 0; i < MIDI_NOTES; i++) {
			int x = i*colWidth;
			g2.drawLine(x-xPos, 0, x-xPos, h);
		}
		g2.drawLine(colWidth*MIDI_NOTES-xPos, 0, colWidth*MIDI_NOTES-xPos, h);
		long beatLength = (long) (60000000.0/midi.getBPM()),
				prevBeat = midi.getPosition()-(midi.getPosition()%(beatLength)),
				start = midi.getPosition()-150000,
				end = (long) (midi.getPosition()+beatLength*beatsShown);

		for(int i = 0; i <= beatsShown+1; i++) {
			long t = prevBeat+beatLength*i;
			int y = getHeightPos(t, start, end, h);
			g2.drawLine(0, y, w, y);
		}

		for(int i = 0; i < tracks.size(); i++) {
			Color c = Color.getHSBColor((float) (i)/tracks.size(), 1, 1);
			List<MidiNote> track = tracks.get(i);
			for(int j = 0; j < track.size(); j++) {
				MidiNote event = track.get(j);
				int x1 = (event.getNote()+event.getOctave()*12)*colWidth+1,
						x2 = (event.getNote()+event.getOctave()*12+1)*colWidth-1;
				g2.setColor(c);
				long t = event.getPosition();
				int y = getHeightPos(t, start, end, h);
				int y2 = getHeightPos(t+event.getLength(), start, end, h);
				boolean drawStart = t >= start;
				if(!drawStart) y = h;
				if(y2 < 0) y2 = 0;
				g2.fillRect(x1-xPos, y2, colWidth-1, y-y2);
				if(drawStart) {
					g2.setColor(Color.WHITE);
					g2.setStroke(new BasicStroke(5));
					g2.drawLine(x1-xPos, y, x2-xPos, y);
					g2.setStroke(new BasicStroke());
				}
			}
		}

		g2.setColor(Color.WHITE);
		for(int i = 0; i < MIDI_NOTES; i++) {
			int x = i*colWidth;
			String str = EU_NOTE_NAMES[i%NOTE_NAMES.length];
			g2.drawString(str,
					(int) (x-xPos+colWidth/2-g2.getFont().getStringBounds(str, g2.getFontRenderContext()).getWidth()/2), h-30);
		}
		g2.setColor(Color.GREEN);
		int y1 = getHeightPos(midi.getPosition(), start, end, h);
		g2.drawLine(0, y1, w, y1);
	}

	public static final int getHeightPos(long position, long screenStart, long screenEnd, int height) {
		return height - (int)Math.round(((double) (position-screenStart))/(screenEnd-screenStart)*height);
	}

	public void zoom(double amount) {
		this.beatsShown *= 1+amount/10.0;
	}
	
	public void zoomHorizontally(double amount) {
		int temp = colWidth;
		this.colWidth *= 1+amount/10.0;
		if(colWidth <= 10) colWidth = temp;
	}

	public void move(double amount) {
		xPos += amount*25;
	}

	@Override
	public int getWidth() {
		return super.getWidth();
	}
}
