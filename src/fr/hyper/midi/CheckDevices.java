package fr.hyper.midi;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

public class CheckDevices {
	public static void main(String[] args) {
		MidiDevice device;
		// display each device's properties
		for (MidiDevice.Info info: MidiSystem.getMidiDeviceInfo()) {

			try {
				device = MidiSystem.getMidiDevice(info);

				System.out.println("\nDevice: ");
				System.out.println("Name: " + device.getDeviceInfo().getName());
				System.out.println("Vendor: " + device.getDeviceInfo().getVendor());
				System.out.println("Version: " + device.getDeviceInfo().getVersion());
				System.out.println("Description: " + device.getDeviceInfo().getDescription());
				System.out.println("Transmitters: " + device.getMaxTransmitters());
				System.out.println("Receivers: " + device.getMaxReceivers());

			} catch (MidiUnavailableException ex) {
				Logger.getLogger(CheckDevices.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
