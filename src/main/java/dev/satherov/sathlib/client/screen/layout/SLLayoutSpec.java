package dev.satherov.sathlib.client.screen.layout;

public record SLLayoutSpec(
   SLLength width,
   SLLength height,
   SLInsets margin,
   SLInsets padding,
   SLAlignment horizontalAlignment,
   SLAlignment verticalAlignment,
   SLScalar offsetX,
   SLScalar offsetY
) {
   public static SLLayoutSpec defaultSpec() {
      return new SLLayoutSpec(
         SLLength.content(), SLLength.content(), SLInsets.zero(), SLInsets.zero(), SLAlignment.START, SLAlignment.START, SLScalar.zero(), SLScalar.zero()
      );
   }

   public SLLayoutSpec withWidth(SLLength width) {
      return new SLLayoutSpec(width, this.height, this.margin, this.padding, this.horizontalAlignment, this.verticalAlignment, this.offsetX, this.offsetY);
   }

   public SLLayoutSpec withHeight(SLLength height) {
      return new SLLayoutSpec(this.width, height, this.margin, this.padding, this.horizontalAlignment, this.verticalAlignment, this.offsetX, this.offsetY);
   }

   public SLLayoutSpec withMargin(SLInsets margin) {
      return new SLLayoutSpec(this.width, this.height, margin, this.padding, this.horizontalAlignment, this.verticalAlignment, this.offsetX, this.offsetY);
   }

   public SLLayoutSpec withPadding(SLInsets padding) {
      return new SLLayoutSpec(this.width, this.height, this.margin, padding, this.horizontalAlignment, this.verticalAlignment, this.offsetX, this.offsetY);
   }

   public SLLayoutSpec withAlignment(SLAlignment horizontalAlignment, SLAlignment verticalAlignment) {
      return new SLLayoutSpec(this.width, this.height, this.margin, this.padding, horizontalAlignment, verticalAlignment, this.offsetX, this.offsetY);
   }

   public SLLayoutSpec withOffset(SLScalar offsetX, SLScalar offsetY) {
      return new SLLayoutSpec(this.width, this.height, this.margin, this.padding, this.horizontalAlignment, this.verticalAlignment, offsetX, offsetY);
   }
}
