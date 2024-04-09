package lt.biip.basemap.layers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WaterwayTest {

    @Test
    void ensureHumanReadableName() {
        assertAll(
                () -> assertTrue(Waterway.hasHumanReadableName("Nemunas")),
                () -> assertTrue(Waterway.hasHumanReadableName("Kuršių marios")),
                () -> assertTrue(Waterway.hasHumanReadableName("Baltijos-8-jūra")),
                () -> assertFalse(Waterway.hasHumanReadableName("S-8")),
                () -> assertFalse(Waterway.hasHumanReadableName("S-2")),
                () -> assertFalse(Waterway.hasHumanReadableName("A-12")),
                () -> assertFalse(Waterway.hasHumanReadableName("")),
                () -> assertFalse(Waterway.hasHumanReadableName("   ")),
                () -> assertFalse(Waterway.hasHumanReadableName(null))
        );
    }
}
