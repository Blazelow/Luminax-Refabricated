package dev.satherov.sathlib;

import net.minecraft.resources.Identifier;

/**
 * Namespace helper for the bundled SathLib translation keys ({@code assets/sathlib/lang}).
 */
public final class SathLib {
    
    public static final String MOD_ID = "sathlib";
    
    private SathLib() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    public static Identifier id(final String name) {
        return Identifier.fromNamespaceAndPath(SathLib.MOD_ID, name);
    }
}
