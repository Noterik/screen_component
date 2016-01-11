package org.springfield.lou.screencomponent.mapping;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ObjectChildrenToSmithersGetter {
	boolean ordered() default false;
}
