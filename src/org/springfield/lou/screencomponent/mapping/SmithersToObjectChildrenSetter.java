package org.springfield.lou.screencomponent.mapping;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SmithersToObjectChildrenSetter {
	String mapTo() default "";
	Class<? extends MappedObject> type();
	boolean ordered() default false;
}
