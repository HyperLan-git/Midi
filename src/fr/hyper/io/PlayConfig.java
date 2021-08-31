package fr.hyper.io;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class PlayConfig extends JDialog implements ActionListener {
	private static final long serialVersionUID = 4513967062355295444L;

	private MidiHandler handler;

	private JLabel bpmLabel;

	private JTextField bpm;

	public PlayConfig() {
		this.setMinimumSize(new Dimension(100, 100));

		this.bpmLabel = new JLabel("BPM");
		this.bpm = new JTextField();

		this.setLayout(new BorderLayout());

		this.add(bpmLabel);
		this.add(bpm, BorderLayout.SOUTH);

		this.bpm.addActionListener(this);
	}

	public void setHandler(MidiHandler handler) {
		this.handler = handler;
	}

	public void write() {
		if(handler == null) return;
		this.bpm.setText(String.valueOf(handler.getBPM()));
	}

	public void read() {
		if(handler == null) return;
		String bpm = this.bpm.getText();
		try {
			float f = Float.valueOf(bpm);
				this.handler.setBPM(f);
		} catch(NumberFormatException e) {
			//Don't care
			write();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(bpm.equals(e.getSource())) {
			read();
		}
	}
}
