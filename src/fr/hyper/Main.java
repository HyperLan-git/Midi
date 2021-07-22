package fr.hyper;

import javax.swing.JFrame;

import fr.hyper.io.MidiWindow;

public abstract class Main {
	public static void main(String[] args) {
		MidiWindow window = new MidiWindow();
		JFrame frame = window.getFrame();
		while(frame.isVisible())
			window.update();
		if(window.getMidiHandler().isOpen()) window.getMidiHandler().close();
		System.exit(0);
	}
}
