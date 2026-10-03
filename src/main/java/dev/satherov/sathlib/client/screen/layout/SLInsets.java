package dev.satherov.sathlib.client.screen.layout;

public record SLInsets(SLScalar left, SLScalar top, SLScalar right, SLScalar bottom) {
   public static SLInsets all(int pixels) {
      SLScalar scalar = SLScalar.pixels(pixels);
      return new SLInsets(scalar, scalar, scalar, scalar);
   }

   public static SLInsets all(SLScalar scalar) {
      return new SLInsets(scalar, scalar, scalar, scalar);
   }

   public static SLInsets symmetric(int horizontal, int vertical) {
      return new SLInsets(SLScalar.pixels(horizontal), SLScalar.pixels(vertical), SLScalar.pixels(horizontal), SLScalar.pixels(vertical));
   }

   public static SLInsets of(int left, int top, int right, int bottom) {
      return new SLInsets(SLScalar.pixels(left), SLScalar.pixels(top), SLScalar.pixels(right), SLScalar.pixels(bottom));
   }

   public static SLInsets zero() {
      return all(0);
   }

   public int left(int widthReference) {
      return this.left.resolve(widthReference);
   }

   public int right(int widthReference) {
      return this.right.resolve(widthReference);
   }

   public int top(int heightReference) {
      return this.top.resolve(heightReference);
   }

   public int bottom(int heightReference) {
      return this.bottom.resolve(heightReference);
   }

   public int horizontal(int widthReference) {
      return this.left(widthReference) + this.right(widthReference);
   }

   public int vertical(int heightReference) {
      return this.top(heightReference) + this.bottom(heightReference);
   }
}
