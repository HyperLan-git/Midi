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
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MidiWindow {
	private JFrame frame = new JFrame();
	private MidiHandler handler;

	private JLabel noFile = new JLabel("No file chosen !", SwingConstants.CENTER);

	private NotesPanel2 notesPanel = null;
	private JScrollPane scrollPane = null;
	private JPanel controlPanel = new JPanel();

	private PlayConfig config;

	private JButton stop, play;

	private JSlider advancement;

	private JMenuItem load, options, quit;

	private MidiListener listener;
	private ZoomListener zoomListener;

	public MidiWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		System.setProperty("sun.awt.noerasebackground", "true");

		listener = new MidiListener();
		zoomListener = new ZoomListener();
		frame.addMouseWheelListener(zoomListener);

		scrollPane = new JScrollPane(notesPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		notesPanel = new NotesPanel2(this);
		scrollPane.add(notesPanel);
		notesPanel.add(noFile);

		config = new PlayConfig();

		stop = new JButton("⏹");
		play = new JButton("▶");
		advancement = new JSlider();
		advancement.setEnabled(false);
		advancement.setMinimumSize(new Dimension(300, 50));
		advancement.setPreferredSize(new Dimension(300, 50));
		stop.addActionListener(listener);
		play.addActionListener(listener);
		advancement.addChangeListener(listener);
		stop.setFont(new Font("u2400", 0, 30));
		play.setFont(new Font("u2400", 0, 30));
		stop.setEnabled(false);
		controlPanel.add(stop);
		controlPanel.add(play);
		controlPanel.add(advancement);

		frame.setMinimumSize(new Dimension(500, 500));
		frame.setLayout(new BorderLayout());
		frame.add(notesPanel, BorderLayout.CENTER);
		frame.add(controlPanel, BorderLayout.SOUTH);

		try {
			handler = new MidiHandler();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
			System.exit(1);
		}

		initJMenuBar();
		frame.setVisible(true);
	}

	private void initJMenuBar() {
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		options = new JMenuItem("Options");
		quit = new JMenuItem("Quit");

		load = new JMenuItem("Load file");
		file.setMnemonic('F');
		quit.setMnemonic('Q');
		options.setMnemonic('O');
		frame.setJMenuBar(bar);

		load.addActionListener(listener);
		options.addActionListener(listener);
		quit.addActionListener(listener);

		file.add(load);

		bar.add(file);
		bar.add(options);
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
		this.notesPanel.repaint();
		if(!advancement.isEnabled())
			advancement.getModel().setValue((int) handler.getTickPosition());
		if(handler.getTickPosition() == handler.getTickLength()) {
			advancement.setEnabled(true);
			play.setEnabled(true);
			stop.setEnabled(false);
			play.grabFocus();
		}
	}

	private class MidiListener implements ActionListener, ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			Object o = e.getSource();
			if(o.equals(advancement)) {
				if(advancement.isEnabled()) {
					handler.setTickPosition(advancement.getValue());
					config.read();
				}
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object o = e.getSource();
			if(o.equals(quit)) {
				frame.dispose();
			} else if(o.equals(options) && handler != null) {
				config.setVisible(true);
			} else if(o.equals(load)) {
				try {
					File f = handler.chooseMidi();
					if(f != null) {
						handler.read(f);
						handler.play();//Why tf do I have to do that to get song info
						advancement.setEnabled(true);
						if(play.isEnabled()) {
							handler.stop();
							advancement.setEnabled(false);
						}
						config.setHandler(handler);
						handler.loadMidiData();
						advancement.setMinimum(0);
						advancement.setMaximum((int) handler.getTickLength());
					}
				} catch (InvalidMidiDataException | IOException | MidiUnavailableException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(frame, "Could not load midi file !", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} else if(o.equals(play) && handler != null) {
				config.read();
				handler.play();
				advancement.setEnabled(false);
				play.setEnabled(false);
				stop.setEnabled(true);
				stop.grabFocus();
			} else if(o.equals(stop) && handler != null) {
				handler.stop();
				advancement.setEnabled(true);
				play.setEnabled(true);
				stop.setEnabled(false);
				play.grabFocus();
			}
		}
	}

	private class ZoomListener implements MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			if(e.isAltDown())
				notesPanel.zoom(e.getPreciseWheelRotation());
			else if(e.isControlDown())
				notesPanel.zoomHorizontally(e.getPreciseWheelRotation());
			else
				notesPanel.move(e.getPreciseWheelRotation());
		}
	}
}
