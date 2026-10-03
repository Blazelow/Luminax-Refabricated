package dev.satherov.sathlib.compat;

public enum Mods implements CompatMod {
   JADE("jade"),
   JEI("jei"),
   EMI("emi");

   private final String modId;

   Mods(final String modId) {
      this.modId = modId;
   }

   @Override
   public String getModId() {
      return this.modId;
   }
}
