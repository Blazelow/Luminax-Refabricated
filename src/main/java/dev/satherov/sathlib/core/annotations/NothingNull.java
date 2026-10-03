package dev.satherov.sathlib.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@NonNull
@Nonnull
@TypeQualifierDefault({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR, ElementType.PACKAGE, ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.CLASS)
@NotNull
public @interface NothingNull {
}
