package fr.hyper.io;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MidiWindow {
	private JFrame frame = new JFrame();
	private MidiHandler handler;

	private JLabel noFile = new JLabel("No file chosen !", SwingConstants.CENTER);

	private NotesPanel panel = null;
	private JPanel controlPanel = new JPanel();

	private JButton stop, play;

	private JMenuItem load, quit;

	private MidiActionListener actionListener;
	private ZoomListener zoomListener;

	public MidiWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		System.setProperty("sun.awt.noerasebackground", "true");

		actionListener = new MidiActionListener();
		zoomListener = new ZoomListener();
		frame.addMouseWheelListener(zoomListener);

		this.panel = new NotesPanel(this);
		panel.add(noFile);

		stop = new JButton("⏹");
		play = new JButton("▶");
		stop.addActionListener(actionListener);
		play.addActionListener(actionListener);
		stop.setFont(new Font("u2400", 0, 30));
		play.setFont(new Font("u2400", 0, 30));
		stop.setEnabled(false);
		controlPanel.add(stop);
		controlPanel.add(play);

		frame.setMinimumSize(new Dimension(500, 500));
		frame.setLayout(new BorderLayout());
		frame.add(panel, BorderLayout.CENTER);
		frame.add(controlPanel, BorderLayout.SOUTH);

		try {
			handler = new MidiHandler();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}

		initJMenuBar();
		frame.setVisible(true);
	}

	private void initJMenuBar() {
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		quit = new JMenuItem("Quit");

		load = new JMenuItem("Load file");
		file.setMnemonic('F');
		quit.setMnemonic('Q');
		frame.setJMenuBar(bar);

		load.addActionListener(actionListener);
		quit.addActionListener(actionListener);

		file.add(load);

		bar.add(file);
		bar.add(quit);

		frame.setJMenuBar(bar);
	}

	public MidiHandler getMidiHandler() {
		return handler;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void update() {
		this.panel.repaint();
	}

	private class MidiActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Object o = e.getSource();
			if(o.equals(quit)) {
				frame.dispose();
			} else if(o.equals(load)) {
				try {
					File f = handler.chooseMidi();
					if(f != null) {
						handler.read(f);
						handler.play();//Why tf do I have to do that to get song info
						if(play.isEnabled()) handler.stop();
						System.out.println(handler.getBPM());
						System.out.println(handler.getTempoFactor());
					}
				} catch (InvalidMidiDataException | IOException | MidiUnavailableException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(frame, "Could not load midi file !", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} else if(o.equals(play)) {
				handler.play();
				play.setEnabled(false);
				stop.setEnabled(true);
				stop.grabFocus();
			} else if(o.equals(stop)) {
				handler.stop();
				play.setEnabled(true);
				stop.setEnabled(false);
				play.grabFocus();
			}
		}
	}

	private class ZoomListener implements MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			System.out.println(e.getWheelRotation());
			if(e.isAltDown())
				panel.zoom(e.getPreciseWheelRotation());
		}
	}
}
