import javax.sound.midi.*;
import java.io.File;

public class MidiGenerator {

    public static void main(String[] args) {
        try {
            File outDir = new File("src/res/sound");
            if (!outDir.exists()) outDir.mkdirs();

            createVillageMusic(new File(outDir, "village.mid"));
            createCaveMusic(new File(outDir, "cave.mid"));
            createSavannahMusic(new File(outDir, "savannah.mid"));
            createForestMusic(new File(outDir, "forest.mid"));
            
            System.out.println("MIDI files generated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createVillageMusic(File out) throws Exception {
        Sequence s = new Sequence(Sequence.PPQ, 24);
        Track t = s.createTrack();
        // Instrument 25 (Acoustic Guitar)
        t.add(new MidiEvent(new ShortMessage(ShortMessage.PROGRAM_CHANGE, 0, 24, 0), 0));
        // Flute
        t.add(new MidiEvent(new ShortMessage(ShortMessage.PROGRAM_CHANGE, 1, 73, 0), 0));

        int[] melody = {60, 62, 64, 67, 69, 67, 64, 62, 60, 55, 60};
        int tick = 0;
        for (int note : melody) {
            t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 1, note, 80), tick));
            t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 1, note, 0), tick + 20));
            
            // Accompaniment
            t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, note-12, 60), tick));
            t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, note-12, 0), tick + 44));
            
            tick += 24;
        }
        MidiSystem.write(s, 1, out);
    }

    private static void createCaveMusic(File out) throws Exception {
        Sequence s = new Sequence(Sequence.PPQ, 24);
        Track t = s.createTrack();
        // Pad 4 (Choir)
        t.add(new MidiEvent(new ShortMessage(ShortMessage.PROGRAM_CHANGE, 0, 91, 0), 0));
        
        int[] notes = {48, 45, 41, 48, 52, 48};
        int tick = 0;
        for (int note : notes) {
            t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, note, 70), tick));
            t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, note, 0), tick + 90));
            tick += 96;
        }
        MidiSystem.write(s, 1, out);
    }

    private static void createSavannahMusic(File out) throws Exception {
        Sequence s = new Sequence(Sequence.PPQ, 24);
        Track t = s.createTrack();
        // Harmonica
        t.add(new MidiEvent(new ShortMessage(ShortMessage.PROGRAM_CHANGE, 0, 22, 0), 0));
        // Acoustic Guitar
        t.add(new MidiEvent(new ShortMessage(ShortMessage.PROGRAM_CHANGE, 1, 24, 0), 0));
        
        int[] melody = {64, 67, 71, 71, 67, 64, 64, 62, 64};
        int tick = 0;
        for (int i = 0; i < melody.length; i++) {
            int note = melody[i];
            int duration = (i == 3 || i == 5) ? 40 : 15;
            t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, note, 90), tick));
            t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, note, 0), tick + duration));
            
            if (i % 2 == 0) {
                t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 1, note-12, 70), tick));
                t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 1, note-12, 0), tick + 15));
            }
            
            tick += (i == 3 || i == 5) ? 48 : 24;
        }
        MidiSystem.write(s, 1, out);
    }

    private static void createForestMusic(File out) throws Exception {
        Sequence s = new Sequence(Sequence.PPQ, 24);
        Track t = s.createTrack();
        // Marimba
        t.add(new MidiEvent(new ShortMessage(ShortMessage.PROGRAM_CHANGE, 0, 12, 0), 0));
        // Flute
        t.add(new MidiEvent(new ShortMessage(ShortMessage.PROGRAM_CHANGE, 1, 73, 0), 0));

        int tick = 0;
        for (int i = 0; i < 16; i++) {
            int note = 60 + (i % 4) * 3 + (i % 3) * 4;
            t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, note, 70), tick));
            t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, note, 0), tick + 10));
            
            if (i % 4 == 0) {
                t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 1, note + 12, 60), tick));
                t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 1, note + 12, 0), tick + 40));
            }
            tick += 12;
        }
        MidiSystem.write(s, 1, out);
    }
}
