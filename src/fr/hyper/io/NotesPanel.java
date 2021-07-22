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
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

	private MidiWindow parent;

	private double beatsShown = 10;

	public NotesPanel(MidiWindow parent) {
		this.parent = parent;
	}

	@SuppressWarnings("null")
	@Override
	public void paint(Graphics g) {
		if(!parent.getMidiHandler().isOpen()) {
			super.paint(g);
			return;
		}
		int w = this.getWidth()-1, h = this.getHeight();

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
		g2.drawLine(w, 0, w, h);
		long beatLength = (long) (60000000.0/midi.getBPM()),
				prevBeat = midi.getPosition()-(midi.getPosition()%(beatLength)),
				start = midi.getPosition()-150000,
				end = (long) (midi.getPosition()+beatLength*beatsShown);

		double ticksPerSecond = midi.getResolution() * (midi.getBPM() / 60.0);
		long tickSize = (long) (1000000.0 / ticksPerSecond);

		for(int i = 0; i <= beatsShown+1; i++) {
			long t = prevBeat+beatLength*i;
			t -= start;
			int y = (int) ((double) (t)/(end-start)*h);
			y = h-y;
			g2.drawLine(0, y, w, y);
		}

		for(int i = 0; i < tracks.length; i++) {
			Color c = Color.getHSBColor((float)(i)/tracks.length, 1, 1);
			Track track = tracks[i];
			int x1 = (int) ((double)(i*w)/tracks.length)+1,
					x2 = (int) ((double)((i+1)*w)/tracks.length)-1;
			int tW = x2-x1+1;
			boolean started = false;
			for(int j = 0; j < track.size(); j++) {
				MidiEvent event = track.get(j);
				if(!(event.getMessage() instanceof ShortMessage)) continue;
				int command = ((ShortMessage)(event.getMessage())).getCommand();
				g2.setColor(c);
				if(!started && command == NOTE_OFF) {
					long t = tickSize*event.getTick();
					if(t < start) continue;//Before the screen starts
					if(t > end) break;//We're done drawing
					int y = getHeightPos(t, start, end, h);
					g2.fillRect(x1, y, tW, h-y);
				}
				if(command != NOTE_ON) continue;
				long t = tickSize*event.getTick();
				if(t < start) continue;//Before the screen starts
				started = true;
				if(t > end) break;//We're done drawing
				int y = getHeightPos(t, start, end, h);
				int i2 = 0;
				MidiEvent e = event;
				ShortMessage message = null;
				do {
					e = track.get(j+(i2++));
					message = (e.getMessage() instanceof ShortMessage)?(ShortMessage)e.getMessage():null;
				} while((message != null
						|| message.getCommand() != NOTE_OFF
						|| message.getMessage()[0] != event.getMessage().getMessage()[0])
						&& (j+i2) < track.size());
				long t2 = tickSize*e.getTick();
				int y2 = getHeightPos(t2, start, end, h);
				g2.fillRect(x1, y2, tW, y-y2);
				g2.setColor(Color.WHITE);
				g2.setStroke(new BasicStroke(5));
				g2.drawLine(x1, y, x2, y);
				g2.setStroke(new BasicStroke());
			}
		}
	}

	public static final int getHeightPos(long position, long screenStart, long screenEnd, int height) {
		return height - (int)Math.round(((double) (position-screenStart))/(screenEnd-screenStart)*height);
	}

	public void zoom(double amount) {
		this.beatsShown *= 1+amount/10.0;
	}
}
