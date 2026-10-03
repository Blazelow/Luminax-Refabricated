package dev.satherov.sathlib.client.screen.layout;

public record SLBounds(int x, int y, int width, int height) {
   public static final SLBounds EMPTY = new SLBounds(0, 0, 0, 0);

   public int right() {
      return this.x + this.width;
   }

   public int bottom() {
      return this.y + this.height;
   }

   public boolean contains(double pointX, double pointY) {
      return pointX >= this.x && pointX < this.right() && pointY >= this.y && pointY < this.bottom();
   }

   public SLBounds inset(SLInsets insets) {
      int left = insets.left(this.width);
      int top = insets.top(this.height);
      int right = insets.right(this.width);
      int bottom = insets.bottom(this.height);
      int width = Math.max(0, this.width - left - right);
      int height = Math.max(0, this.height - top - bottom);
      return new SLBounds(this.x + left, this.y + top, width, height);
   }

   public SLBounds translate(int offsetX, int offsetY) {
      return new SLBounds(this.x + offsetX, this.y + offsetY, this.width, this.height);
   }
}
