package fraymus.hyper;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class HyperFormerLayer2Test {

    @Test
    void predictsNextAndPrev() {
        HyperFormer brain = new HyperFormer(42);

        brain.learnSentence(new String[]{"Fraymus", "is", "a", "living", "system"});

        assertEquals("system", brain.predictNext(new String[]{"Fraymus", "is", "a", "living"}));
        assertEquals("living", brain.predictPrev(new String[]{"system"}));
    }

    @Test
    void persistsAndRestores() throws Exception {
        HyperFormer brain = new HyperFormer(42);
        brain.learnSentence(new String[]{"Fraymus", "is", "a", "living", "system"});

        Path tmp = Files.createTempFile("fraymus", ".gz");
        brain.save(tmp);

        HyperFormer restored = HyperFormer.load(tmp);

        assertEquals("system", restored.predictNext(new String[]{"Fraymus", "is", "a", "living"}));
        assertEquals("living", restored.predictPrev(new String[]{"system"}));
    }
}
