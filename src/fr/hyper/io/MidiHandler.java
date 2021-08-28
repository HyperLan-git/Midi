package fr.hyper.io;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import fr.hyper.midi.MidiData;

public class MidiHandler {
	private final Sequencer sequencer;

	private Sequence seq;

	private MidiData data;

	public MidiHandler() throws MidiUnavailableException {
		sequencer = MidiSystem.getSequencer();
	}

	public void read(File f) throws InvalidMidiDataException, IOException, MidiUnavailableException {
		seq = MidiSystem.getSequence(f);
		sequencer.setSequence(seq);
		sequencer.open();
	}

	public boolean isOpen() {
		return sequencer.isOpen();
	}

	public void close() {
		sequencer.close();
	}

	public void play() {
		sequencer.start();
	}

	public void stop() {
		sequencer.stop();
	}

	public long getPosition() {
		return sequencer.getMicrosecondPosition();
	}

	public float getBPM() {
		return sequencer.getTempoInBPM();
	}

	public double getTempoFactor() {
		return sequencer.getTempoFactor();
	}

	public float getMPQ() {
		return sequencer.getTempoInMPQ();
	}

	public long getLength() {
		return sequencer.getMicrosecondLength();
	}

	public long getResolution() {
		return sequencer.getSequence().getResolution();
	}

	public Track[] getTracks() {
		return seq.getTracks();
	}

	public boolean isRunning() {
		return sequencer.isRunning();
	}

	public void setTickPosition(long tickPosition) {
		sequencer.setTickPosition(tickPosition);
	}

	public long getTickLength() {
		return sequencer.getTickLength();
	}

	public long getTickPosition() {
		return sequencer.getTickPosition();
	}

	public MidiData getMidiData() {
		return data;
	}

	public void loadMidiData() {
		this.data = new MidiData(this);
	}
	
	public void setBPM(float bpm) {
		sequencer.setTempoInBPM(bpm);
	}

	public final File chooseMidi() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "Midi file (.mid)";
			}

			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".mid") || !f.isFile();
			}
		});
		if(chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			JOptionPane.showMessageDialog(null, "Choose a file !",
					"Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return chooser.getSelectedFile();
	}
}
