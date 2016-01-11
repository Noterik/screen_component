package org.springfield.lou.screencomponent.mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)

public @interface MappingSettings {
	public String systemName();
}
