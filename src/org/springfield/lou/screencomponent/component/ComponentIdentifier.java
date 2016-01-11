package org.springfield.lou.screencomponent.component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentIdentifier {
	String id() default "";
}
