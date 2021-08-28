package fr.hyper.midi;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import fr.hyper.io.MidiHandler;

public class MidiData {
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;

	private List<List<MidiNote>> tracks;

	public MidiData(MidiHandler midi) {
		tracks = new ArrayList<>(midi.getTracks().length);
		double ticksPerSecond = midi.getResolution() * (midi.getBPM() / 60.0);
		long tickSize = (long) (1000000.0 / ticksPerSecond);
		for(int i = 0; i < midi.getTracks().length; i++) {
			List<MidiNote> notes = new ArrayList<MidiNote>();
			List<MidiEvent> currentNotes = new ArrayList<MidiEvent>();
			Track t = midi.getTracks()[i];
			for(int j = 0; j < t.size(); j++) {
				MidiEvent e = t.get(j);
				if(!(e.getMessage() instanceof ShortMessage)) continue;
				ShortMessage message = (ShortMessage) e.getMessage();
				long pos = tickSize*e.getTick();
				//TODO possible refactoring
				if(message.getCommand() == NOTE_ON) {
					for(int k = currentNotes.size()-1; k >= 0; k--) {
						MidiEvent event = currentNotes.get(k);
						MidiMessage msg = event.getMessage();
						if(event.getTick() != e.getTick()) {
							currentNotes.remove(k);
							notes.add(new MidiNote(event.getTick()*tickSize, pos-event.getTick()*tickSize,
									msg.getMessage()[1], msg.getMessage()[2]));
						}
					}
					if(e.getMessage().getMessage()[2] != 0)
						currentNotes.add(e);
				}
				if(message.getCommand() == NOTE_OFF) {
					for(int k = currentNotes.size()-1; k >= 0; k--) {
						MidiEvent event = currentNotes.get(k);
						MidiMessage msg = event.getMessage();
						if(msg.getMessage()[1] == message.getMessage()[1]) {
							currentNotes.remove(k);
							notes.add(new MidiNote(event.getTick()*tickSize, pos-event.getTick()*tickSize,
									msg.getMessage()[1], msg.getMessage()[2]));
						}
					}
				}
			}
			for(int k = currentNotes.size()-1; k >= 0; k--) {
				MidiEvent event = currentNotes.get(k);
				MidiMessage msg = event.getMessage();
				notes.add(new MidiNote(event.getTick()*tickSize, t.ticks()*tickSize-event.getTick()*tickSize,
						msg.getMessage()[1], msg.getMessage()[2]));
			}
			tracks.add(notes);
		}
	}

	public List<List<MidiNote>> getTracks() {
		return tracks;
	}
}
