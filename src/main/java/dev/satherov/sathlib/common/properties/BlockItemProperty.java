package dev.satherov.sathlib.common.properties;

import dev.satherov.sathlib.client.lang.SLTranslatable;
import dev.satherov.sathlib.core.annotations.NothingNull;
import dev.satherov.sathlib.network.chat.SLComponent;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

@NothingNull
public class BlockItemProperty<T> {
   private final Identifier identifier;
   private final Class<T> type;
   private final SLTranslatable name;
   private final PropertyCycler<T> cycler;
   private final PropertyExtractor<T> itemExtractor;
   private final PropertyApplicator<T, PropertyItemHolder> itemApplicator;
   private final PropertyExtractor<T> blockExtractor;
   private final PropertyApplicator<T, PropertyBlockHolder> blockApplicator;
   private final PropertyDisplayer<T> valueDisplayer;
   private final PropertyDisplayer<T> tooltipDisplayer;

   public static <T> BlockItemProperty.BlockItemPropertyBuilder<T> builder(Identifier identifier, Class<T> type, SLTranslatable name) {
      return new BlockItemProperty.BlockItemPropertyBuilder<T>().identifier(identifier).type(type).name(name);
   }

   public T extractValueItem(ItemStack stack, BlockState state) {
      return this.extractValueItem(stack, state, null);
   }

   public T extractValueItem(ItemStack stack, BlockState state, @Nullable BlockEntity entity) {
      PropertyItemHolder item = new PropertyItemHolder(stack);
      PropertyBlockHolder block = new PropertyBlockHolder(state, entity);
      return this.itemExtractor.extract(block, item);
   }

   public T extractValueBlock(ItemStack stack, BlockState state) {
      return this.extractValueBlock(stack, state, null);
   }

   public T extractValueBlock(ItemStack stack, BlockState state, @Nullable BlockEntity entity) {
      PropertyItemHolder item = new PropertyItemHolder(stack);
      PropertyBlockHolder block = new PropertyBlockHolder(state, entity);
      return this.blockExtractor.extract(block, item);
   }

   public PropertyItemHolder applyToItem(ItemStack stack, BlockState state) {
      return this.applyToItem(stack, state, null);
   }

   public PropertyItemHolder applyToItem(ItemStack stack, BlockState state, @Nullable BlockEntity entity) {
      PropertyItemHolder item = new PropertyItemHolder(stack);
      PropertyBlockHolder block = new PropertyBlockHolder(state, entity);
      T original = this.itemExtractor.extract(block, item);
      T value = this.blockExtractor.extract(block, item);
      return Objects.equals(value, original) ? item : this.itemApplicator.apply(block, item, value);
   }

   public PropertyItemHolder applyValueItem(T value, ItemStack stack, BlockState state) {
      return this.applyValueItem(value, stack, state, null);
   }

   public PropertyItemHolder applyValueItem(T value, ItemStack stack, BlockState state, @Nullable BlockEntity entity) {
      PropertyItemHolder item = new PropertyItemHolder(stack);
      PropertyBlockHolder block = new PropertyBlockHolder(state, entity);
      return this.itemApplicator.apply(block, item, value);
   }

   public PropertyBlockHolder applyToBlock(ItemStack stack, BlockState state) {
      return this.applyToBlock(stack, state, null);
   }

   public PropertyBlockHolder applyToBlock(ItemStack stack, BlockState state, @Nullable BlockEntity entity) {
      PropertyItemHolder item = new PropertyItemHolder(stack);
      PropertyBlockHolder block = new PropertyBlockHolder(state, entity);
      T original = this.blockExtractor.extract(block, item);
      T value = this.itemExtractor.extract(block, item);
      return Objects.equals(value, original) ? block : this.blockApplicator.apply(block, item, value);
   }

   public PropertyBlockHolder applyValueBlock(T value, ItemStack stack, BlockState state) {
      return this.applyValueBlock(value, stack, state, null);
   }

   public PropertyBlockHolder applyValueBlock(T value, ItemStack stack, BlockState state, @Nullable BlockEntity entity) {
      PropertyItemHolder item = new PropertyItemHolder(stack);
      PropertyBlockHolder block = new PropertyBlockHolder(state, entity);
      return this.blockApplicator.apply(block, item, value);
   }

   public SLComponent displayAll(T value) {
      return SLComponent.empty()
         .append(this.name.translate(ChatFormatting.GRAY))
         .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
         .append(this.displayValue(value));
   }

   public SLComponent displayItemValue(ItemStack stack, BlockState state) {
      return this.displayValue(this.extractValueItem(stack, state));
   }

   public SLComponent displayItemValue(ItemStack stack, BlockState state, @Nullable BlockEntity entity) {
      return this.displayValue(this.extractValueItem(stack, state, entity));
   }

   public SLComponent displayBlockValue(ItemStack stack, BlockState state) {
      return this.displayValue(this.extractValueBlock(stack, state));
   }

   public SLComponent displayBlockValue(ItemStack stack, BlockState state, @Nullable BlockEntity entity) {
      return this.displayValue(this.extractValueBlock(stack, state, entity));
   }

   public SLComponent displayValue(T value) {
      return this.valueDisplayer.display(value);
   }

   public SLComponent displayItemTooltip(ItemStack stack, BlockState state) {
      return this.displayTooltip(this.extractValueItem(stack, state));
   }

   public SLComponent displayItemTooltip(ItemStack stack, BlockState state, @Nullable BlockEntity entity) {
      return this.displayTooltip(this.extractValueItem(stack, state, entity));
   }

   public SLComponent displayBlockTooltip(ItemStack stack, BlockState state) {
      return this.displayTooltip(this.extractValueBlock(stack, state));
   }

   public SLComponent displayBlockTooltip(ItemStack stack, BlockState state, @Nullable BlockEntity entity) {
      return this.displayTooltip(this.extractValueBlock(stack, state, entity));
   }

   public SLComponent displayTooltip(T value) {
      return this.tooltipDisplayer.display(value);
   }

   public PropertyItemHolder cycleItem(boolean forward, ItemStack stack, BlockState state) {
      return this.cycleItem(forward, stack, state, null);
   }

   public PropertyItemHolder cycleItem(boolean forward, ItemStack stack, BlockState state, @Nullable BlockEntity entity) {
      PropertyItemHolder item = new PropertyItemHolder(stack);
      PropertyBlockHolder block = new PropertyBlockHolder(state, entity);
      T original = this.itemExtractor.extract(block, item);
      T updated = this.cycler.cycle(forward, original);
      return Objects.equals(updated, original) ? item : this.applyValueItem(updated, stack, state, entity);
   }

   public PropertyBlockHolder cycleBlock(boolean forward, ItemStack stack, BlockState state) {
      return this.cycleBlock(forward, stack, state, null);
   }

   public PropertyBlockHolder cycleBlock(boolean forward, ItemStack stack, BlockState state, @Nullable BlockEntity entity) {
      PropertyItemHolder item = new PropertyItemHolder(stack);
      PropertyBlockHolder block = new PropertyBlockHolder(state, entity);
      T original = this.blockExtractor.extract(block, item);
      T updated = this.cycler.cycle(forward, original);
      return Objects.equals(updated, original) ? block : this.applyValueBlock(updated, stack, state, entity);
   }

   public boolean matchItems(ItemStack stack, ItemStack other, BlockState state) {
      return this.matchItems(stack, other, state, null);
   }

   public boolean matchItems(ItemStack stack, ItemStack other, BlockState state, @Nullable BlockEntity entity) {
      PropertyBlockHolder block = new PropertyBlockHolder(state, entity);
      PropertyItemHolder stackHolder = new PropertyItemHolder(stack);
      PropertyItemHolder otherHolder = new PropertyItemHolder(other);
      T stackValue = this.itemExtractor.extract(block, stackHolder);
      T otherValue = this.itemExtractor.extract(block, otherHolder);
      return Objects.equals(stackValue, otherValue);
   }

   public boolean matchBlocks(ItemStack stack, BlockState state, BlockState other) {
      return this.matchBlocks(stack, state, null, other, null);
   }

   public boolean matchBlocks(ItemStack stack, BlockState state, @Nullable BlockEntity entity, BlockState other, @Nullable BlockEntity otherEntity) {
      PropertyItemHolder item = new PropertyItemHolder(stack);
      PropertyBlockHolder stateHolder = new PropertyBlockHolder(state, entity);
      PropertyBlockHolder otherHolder = new PropertyBlockHolder(other, otherEntity);
      T stateValue = this.blockExtractor.extract(stateHolder, item);
      T otherValue = this.blockExtractor.extract(otherHolder, item);
      return Objects.equals(stateValue, otherValue);
   }

   public boolean match(ItemStack stack, BlockState state) {
      return this.match(stack, state, null);
   }

   public boolean match(ItemStack stack, BlockState state, @Nullable BlockEntity entity) {
      PropertyItemHolder item = new PropertyItemHolder(stack);
      PropertyBlockHolder block = new PropertyBlockHolder(state, entity);
      T stateValue = this.blockExtractor.extract(block, item);
      T itemValue = this.itemExtractor.extract(block, item);
      return Objects.equals(stateValue, itemValue);
   }

   private static <T> PropertyDisplayer<T> $default$valueDisplayer() {
      return PropertyDisplayer.defaultValueDisplayer();
   }

   private static <T> PropertyDisplayer<T> $default$tooltipDisplayer() {
      return PropertyDisplayer.empty();
   }

   BlockItemProperty(
      Identifier identifier,
      Class<T> type,
      SLTranslatable name,
      PropertyCycler<T> cycler,
      PropertyExtractor<T> itemExtractor,
      PropertyApplicator<T, PropertyItemHolder> itemApplicator,
      PropertyExtractor<T> blockExtractor,
      PropertyApplicator<T, PropertyBlockHolder> blockApplicator,
      PropertyDisplayer<T> valueDisplayer,
      PropertyDisplayer<T> tooltipDisplayer
   ) {
      this.identifier = identifier;
      this.type = type;
      this.name = name;
      this.cycler = cycler;
      this.itemExtractor = itemExtractor;
      this.itemApplicator = itemApplicator;
      this.blockExtractor = blockExtractor;
      this.blockApplicator = blockApplicator;
      this.valueDisplayer = valueDisplayer;
      this.tooltipDisplayer = tooltipDisplayer;
   }

   public Identifier getIdentifier() {
      return this.identifier;
   }

   public Class<T> getType() {
      return this.type;
   }

   public SLTranslatable getName() {
      return this.name;
   }

   public static final class BlockItemPropertyBuilder<T> {
      private Identifier identifier;
      private Class<T> type;
      private SLTranslatable name;
      private PropertyCycler<T> cycler;
      private PropertyExtractor<T> itemExtractor;
      private PropertyApplicator<T, PropertyItemHolder> itemApplicator;
      private PropertyExtractor<T> blockExtractor;
      private PropertyApplicator<T, PropertyBlockHolder> blockApplicator;
      private boolean valueDisplayer$set;
      private PropertyDisplayer<T> valueDisplayer$value;
      private boolean tooltipDisplayer$set;
      private PropertyDisplayer<T> tooltipDisplayer$value;

      public BlockItemProperty.BlockItemPropertyBuilder<T> item(PropertyExtractor<T> extractor, PropertyApplicator<T, PropertyItemHolder> applicator) {
         return this.itemExtractor(extractor).itemApplicator(applicator);
      }

      public BlockItemProperty.BlockItemPropertyBuilder<T> block(PropertyExtractor<T> extractor, PropertyApplicator<T, PropertyBlockHolder> applicator) {
         return this.blockExtractor(extractor).blockApplicator(applicator);
      }

      BlockItemPropertyBuilder() {
      }

      public BlockItemProperty.BlockItemPropertyBuilder<T> identifier(Identifier identifier) {
         this.identifier = identifier;
         return this;
      }

      public BlockItemProperty.BlockItemPropertyBuilder<T> type(Class<T> type) {
         this.type = type;
         return this;
      }

      public BlockItemProperty.BlockItemPropertyBuilder<T> name(SLTranslatable name) {
         this.name = name;
         return this;
      }

      public BlockItemProperty.BlockItemPropertyBuilder<T> cycler(PropertyCycler<T> cycler) {
         this.cycler = cycler;
         return this;
      }

      public BlockItemProperty.BlockItemPropertyBuilder<T> itemExtractor(PropertyExtractor<T> itemExtractor) {
         this.itemExtractor = itemExtractor;
         return this;
      }

      public BlockItemProperty.BlockItemPropertyBuilder<T> itemApplicator(PropertyApplicator<T, PropertyItemHolder> itemApplicator) {
         this.itemApplicator = itemApplicator;
         return this;
      }

      public BlockItemProperty.BlockItemPropertyBuilder<T> blockExtractor(PropertyExtractor<T> blockExtractor) {
         this.blockExtractor = blockExtractor;
         return this;
      }

      public BlockItemProperty.BlockItemPropertyBuilder<T> blockApplicator(PropertyApplicator<T, PropertyBlockHolder> blockApplicator) {
         this.blockApplicator = blockApplicator;
         return this;
      }

      public BlockItemProperty.BlockItemPropertyBuilder<T> valueDisplayer(PropertyDisplayer<T> valueDisplayer) {
         this.valueDisplayer$value = valueDisplayer;
         this.valueDisplayer$set = true;
         return this;
      }

      public BlockItemProperty.BlockItemPropertyBuilder<T> tooltipDisplayer(PropertyDisplayer<T> tooltipDisplayer) {
         this.tooltipDisplayer$value = tooltipDisplayer;
         this.tooltipDisplayer$set = true;
         return this;
      }

      public BlockItemProperty<T> build() {
         PropertyDisplayer<T> valueDisplayer$value = this.valueDisplayer$value;
         if (!this.valueDisplayer$set) {
            valueDisplayer$value = BlockItemProperty.$default$valueDisplayer();
         }

         PropertyDisplayer<T> tooltipDisplayer$value = this.tooltipDisplayer$value;
         if (!this.tooltipDisplayer$set) {
            tooltipDisplayer$value = BlockItemProperty.$default$tooltipDisplayer();
         }

         return new BlockItemProperty<>(
            this.identifier,
            this.type,
            this.name,
            this.cycler,
            this.itemExtractor,
            this.itemApplicator,
            this.blockExtractor,
            this.blockApplicator,
            valueDisplayer$value,
            tooltipDisplayer$value
         );
      }

      @Override
      public String toString() {
         return "BlockItemProperty.BlockItemPropertyBuilder(identifier="
            + this.identifier
            + ", type="
            + this.type
            + ", name="
            + this.name
            + ", cycler="
            + this.cycler
            + ", itemExtractor="
            + this.itemExtractor
            + ", itemApplicator="
            + this.itemApplicator
            + ", blockExtractor="
            + this.blockExtractor
            + ", blockApplicator="
            + this.blockApplicator
            + ", valueDisplayer$value="
            + this.valueDisplayer$value
            + ", tooltipDisplayer$value="
            + this.tooltipDisplayer$value
            + ")";
      }
   }
}
