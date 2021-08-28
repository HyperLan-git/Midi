package fr.hyper.midi;

import java.util.Comparator;

public class MidiNote {
	/**
	 * Contains all the notes in order, they correspond to the bytes in note on events, middle C is 60=0x36
	 */
	public static final String[] NOTES = new String[] {
		"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
	};
	
	private long position, length;

	private short note, octave, strength;
	
	public MidiNote(long position, long length, int midiNote, short strength) {
		this(position, length, (short) (midiNote%12), (short) (midiNote/12), strength);
	}

	public MidiNote(long position, long length, short note, short octave, short strength) {
		this.position = position;
		this.length = length;
		this.note = note;
		this.octave = octave;
		this.strength = strength;
	}

	public long getPosition() {
		return this.position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

	public long getLength() {
		return this.length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public short getNote() {
		return this.note;
	}

	public void setNote(short note) {
		this.note = note;
	}

	public short getOctave() {
		return this.octave;
	}

	public void setOctave(short octave) {
		this.octave = octave;
	}

	public short getStrength() {
		return this.strength;
	}

	public void setStrength(short strength) {
		this.strength = strength;
	}
	
	public class NotesSorter implements Comparator<MidiNote> {
		@Override
		public int compare(MidiNote o1, MidiNote o2) {
			if(o1.position != o2.position) return (o1.position-o2.position) > 0?1:-1;
			if(o1.length != o2.length) return (o1.length-o2.length) > 0?1:-1;
			return 0;
		}
	}
}
