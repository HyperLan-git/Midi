package fr.hyper.io;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ChannelsPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1667714472071677456L;

	private MidiWindow window;

	private JButton[] buttons;

	public ChannelsPanel(MidiWindow window) {
		this.window = window;
		GridLayout layout = new GridLayout(0, 1);
		this.setLayout(layout);
		this.setMinimumSize(new Dimension(50, 200));
		this.setPreferredSize(new Dimension(50, 200));
		init();
	}

	public void init() {
		if(buttons != null) for(JButton b : buttons) {
			b.removeActionListener(this);
			this.remove(b);
		}
		if(window.getMidiHandler() == null) return;
		this.buttons = new JButton[window.getMidiHandler().getTracks().length];
		for(int i = 0; i < buttons.length; i++) {
			JButton button = new JButton();
			Color c = Color.getHSBColor((float) (i)/buttons.length, 1, 1);
			button.setBackground(c);
			button.setForeground(c);
			button.setOpaque(true);
			button.addActionListener(this);
			button.setPreferredSize(new Dimension(40, 40));
			buttons[i] = button;
			this.add(button);
		}
		window.getFrame().revalidate();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		NotesPanel2 midiPanel = window.getMidiPanel();
		for(int i = 0; i < buttons.length; i++)
			if(e.getSource().equals(buttons[i])) midiPanel.setDraw(i, !midiPanel.draws(i));
	}
}
