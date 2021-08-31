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
	public static final int PITCH_WHEEL = 0xE0;

	private List<List<MidiNote>> tracks;

	public MidiData(MidiHandler midi) {
		tracks = new ArrayList<>(midi.getTracks().length);
		int[] noteChange = new int[midi.getTracks().length];
		double ticksPerSecond = midi.getResolution() * (midi.getBPM() / 60.0);
		long tickSize = (long) (1000000.0 / ticksPerSecond);
		for(int i = 0; i < midi.getTracks().length; i++) {
			List<MidiNote> notes = new ArrayList<MidiNote>();
			//FIXME make a object that contains event and pitch shift
			List<MidiEvent> currentNotes = new ArrayList<MidiEvent>();
			Track t = midi.getTracks()[i];
			for(int j = 0; j < t.size(); j++) {
				MidiEvent e = t.get(j);
				if(!(e.getMessage() instanceof ShortMessage)) continue;
				ShortMessage message = (ShortMessage) e.getMessage();
				long pos = tickSize*e.getTick();
				//TODO possible refactoring
				switch(message.getCommand()) {
				case NOTE_ON:
					for(int k = currentNotes.size()-1; k >= 0; k--) {
						MidiEvent event = currentNotes.get(k);
						MidiMessage msg = event.getMessage();
						if(event.getTick() != e.getTick()) {
							currentNotes.remove(k);
							notes.add(new MidiNote(event.getTick()*tickSize, pos-event.getTick()*tickSize,
									msg.getMessage()[1], msg.getMessage()[2]));
						}
					}
					if(e.getMessage().getMessage()[2] != 0) //dum neguses donknow 'bout note off events wtf that's cringe innit
						currentNotes.add(e);
					break;
				case NOTE_OFF:
					for(int k = currentNotes.size()-1; k >= 0; k--) {
						MidiEvent event = currentNotes.get(k);
						MidiMessage msg = event.getMessage();
						if(msg.getMessage()[1] == message.getMessage()[1]) {
							currentNotes.remove(k);
							notes.add(new MidiNote(event.getTick()*tickSize, pos-event.getTick()*tickSize,
									msg.getMessage()[1] + noteChange[message.getChannel()], msg.getMessage()[2]));
						}
					}
					break;
				case PITCH_WHEEL:
					//WTF midi specs?????? 2 demitones up or down you can shift by
					//but it's unpredictable whether the synth will react accordingly?
					//Huh? You get 1 SS ping for that
					int value = message.getData1()+message.getData2()*256;
					int demiTones = value/4096-4;
					int channel = message.getChannel();
					noteChange[channel] = demiTones;
					break;
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
