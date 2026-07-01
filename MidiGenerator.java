import javax.sound.midi.*;
import java.io.File;

public class MidiGenerator {

    public static void main(String[] args) {
        try {
            File outDir = new File("src/res/sound");
            if (!outDir.exists()) outDir.mkdirs();

            System.out.println("Creating title...");
            createTitleMusic(new File(outDir, "title.mid"));

            System.out.println("Creating village...");
            createVillageMusic(new File(outDir, "village.mid"));

            System.out.println("Creating cave...");
            createCaveMusic(new File(outDir, "cave.mid"));

            System.out.println("Creating savannah...");
            createSavannahMusic(new File(outDir, "savannah.mid"));

            System.out.println("Creating forest...");
            createForestMusic(new File(outDir, "forest.mid"));

            System.out.println("Creating ancient ruins...");
            createAncientRuinsMusic(new File(outDir, "ancient_ruins.mid"));

            System.out.println("Done!");
            
            System.out.println("MIDI files generated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createTitleMusic(File out) throws Exception {
        Sequence s = new Sequence(Sequence.PPQ, 24);
        Track t = s.createTrack();

        // Channel 0: Acoustic Guitar (Nylon)
        t.add(new MidiEvent(new ShortMessage(ShortMessage.PROGRAM_CHANGE, 0, 24, 0), 0));
        // Channel 1: Flute
        t.add(new MidiEvent(new ShortMessage(ShortMessage.PROGRAM_CHANGE, 1, 73, 0), 0));

        // Arpeggios (8 measures, 8 eighth notes each = 64 notes)
        int[][] arpeggios = {
            {45, 52, 57, 60, 64, 60, 57, 52}, // Am
            {41, 48, 53, 57, 60, 57, 53, 48}, // F
            {36, 43, 48, 52, 55, 52, 48, 43}, // C
            {43, 50, 55, 59, 62, 59, 55, 50}, // G
            {45, 52, 57, 60, 64, 60, 57, 52}, // Am
            {41, 48, 53, 57, 60, 57, 53, 48}, // F
            {36, 43, 48, 52, 55, 52, 48, 43}, // C
            {40, 47, 50, 56, 59, 56, 50, 47}  // E7
        };

        // Write arpeggios
        int arpTick = 0;
        for (int measure = 0; measure < arpeggios.length; measure++) {
            for (int noteIndex = 0; noteIndex < arpeggios[measure].length; noteIndex++) {
                int note = arpeggios[measure][noteIndex];
                t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, note, 65), arpTick));
                t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, note, 0), arpTick + 10));
                arpTick += 12; // Eighth note duration
            }
        }

        // Flute melody notes (-1 = rest)
        int[] melodyNotes = {
            76, 81, 83, 84, // M1: Am
            84, 83, 81, 79, // M2: F
            79, 84, 86, 88, // M3: C
            86,             // M4: G (held)
            88, 86, 84, 83, // M5: Am
            81, 83, 84, 81, // M6: F
            79, 81, 79, 76, // M7: C
            81              // M8: E7 -> Am resolution
        };

        int[] melodyDurations = {
            24, 24, 24, 24, // M1
            48, 12, 12, 24, // M2
            24, 24, 24, 24, // M3
            96,             // M4
            48, 12, 12, 24, // M5
            48, 12, 12, 24, // M6
            48, 12, 12, 24, // M7
            96              // M8
        };

        // Write melody
        int melTick = 0;
        for (int i = 0; i < melodyNotes.length; i++) {
            int note = melodyNotes[i];
            int duration = melodyDurations[i];
            if (note != -1) {
                t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 1, note, 80), melTick));
                t.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 1, note, 0), melTick + duration - 2));
            }
            melTick += duration;
        }

        MidiSystem.write(s, 1, out);
    }

    private static void createVillageMusic(File out) throws Exception {
        Sequence s = new Sequence(Sequence.PPQ, 24);
        Track t = s.createTrack();

        // Strings on channel 0
        t.add(new MidiEvent(new ShortMessage(
                ShortMessage.PROGRAM_CHANGE, 0, 48, 0), 0)); // String Ensemble

        // Violin melody on channel 1
        t.add(new MidiEvent(new ShortMessage(
                ShortMessage.PROGRAM_CHANGE, 1, 40, 0), 0)); // Violin

        // Mozart's Eine kleine Nachtmusik melody notes (G major, -1 = rest)
        int[] melodyNotes = {
            67, -1, 62, -1,
            67, 62, 67, 71, 74, -1,
            72, -1, 69, -1,
            72, 69, 72, 66, 69, -1,
            67, -1, 67, 71, 74, 79,
            -1, 79, 78, 76, 74, 72, 69,
            66, -1, 66, 69, 72, 78,
            -1, 78, 79, -1
        };

        // Note durations in ticks (24 ticks = quarter note, 12 ticks = eighth note)
        int[] melodyDurations = {
            24, 24, 24, 24,
            12, 12, 12, 12, 24, 24,
            24, 24, 24, 24,
            12, 12, 12, 12, 24, 24,
            24, 12, 12, 12, 12, 24,
            12, 12, 12, 12, 12, 12, 24,
            24, 12, 12, 12, 12, 24,
            12, 12, 24, 48
        };

        // Accompanying bass notes (G major / D7 progression)
        int[] bassNotes = {
            43, 43,
            43, 43,
            38, 38,
            38, 38,
            43, 43,
            43, 38,
            38, 38,
            43, 43
        };

        int[] bassDurations = {
            48, 48,
            48, 48,
            48, 48,
            48, 48,
            48, 48,
            48, 48,
            48, 48,
            48, 48
        };

        // Write melody track
        int melodyTick = 0;
        for (int i = 0; i < melodyNotes.length; i++) {
            int note = melodyNotes[i];
            int duration = melodyDurations[i];
            if (note != -1) {
                t.add(new MidiEvent(
                        new ShortMessage(ShortMessage.NOTE_ON, 1, note, 92),
                        melodyTick));
                t.add(new MidiEvent(
                        new ShortMessage(ShortMessage.NOTE_OFF, 1, note, 0),
                        melodyTick + duration - 2));
            }
            melodyTick += duration;
        }

        // Write bass track
        int bassTick = 0;
        for (int i = 0; i < bassNotes.length; i++) {
            int note = bassNotes[i];
            int duration = bassDurations[i];
            if (note != -1) {
                t.add(new MidiEvent(
                        new ShortMessage(ShortMessage.NOTE_ON, 0, note, 58),
                        bassTick));
                t.add(new MidiEvent(
                        new ShortMessage(ShortMessage.NOTE_OFF, 0, note, 0),
                        bassTick + duration - 2));
            }
            bassTick += duration;
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

private static void createAncientRuinsMusic(File out) throws Exception {
    Sequence s = new Sequence(Sequence.PPQ, 24);
    Track t = s.createTrack();

    // Choir Pad
    t.add(new MidiEvent(
            new ShortMessage(ShortMessage.PROGRAM_CHANGE, 0, 91, 0), 0));

    // Tubular Bells
    t.add(new MidiEvent(
            new ShortMessage(ShortMessage.PROGRAM_CHANGE, 1, 14, 0), 0));

    // Choir melody
    int[] melody = {
            62, 65, 69,
            70, 69, 65,
            62, 57,
            60, 62, 65,
            69, 65, 62
    };

    int tick = 0;

    for (int note : melody) {
        t.add(new MidiEvent(
                new ShortMessage(ShortMessage.NOTE_ON, 0, note, 70), tick));

        t.add(new MidiEvent(
                new ShortMessage(ShortMessage.NOTE_OFF, 0, note, 0),
                tick + 70));

        tick += 72;
    }

    // Bell accents
    int[] bells = {74, 77, 74, 81};

    tick = 0;

    for (int note : bells) {
        t.add(new MidiEvent(
                new ShortMessage(ShortMessage.NOTE_ON, 1, note, 80), tick));

        t.add(new MidiEvent(
                new ShortMessage(ShortMessage.NOTE_OFF, 1, note, 0),
                tick + 20));

        tick += 288;
    }

    MidiSystem.write(s, 1, out);
 }
}
