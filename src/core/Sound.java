package core;

import javax.sound.midi.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Sound {
    private Sequencer sequencer;
    private Map<String, Sequence> loadedSequences;
    private String currentTrack;

    public Sound() {
        loadedSequences = new HashMap<>();
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadTrack(String name, String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            if (is != null) {
                Sequence sequence = MidiSystem.getSequence(is);
                loadedSequences.put(name, sequence);
                is.close();
            } else {
                System.out.println("Could not find audio file: " + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play(String name) {
        if (sequencer == null || !loadedSequences.containsKey(name)) return;
        if (name.equals(currentTrack) && sequencer.isRunning()) return;

        try {
            sequencer.stop();
            sequencer.setSequence(loadedSequences.get(name));
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            currentTrack = name;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (sequencer != null && sequencer.isRunning()) {
            sequencer.stop();
            currentTrack = null;
        }
    }

    public String getCurrentTrack() {
        return currentTrack;
    }
}
