package org.springfield.lou.screencomponent.component;

import org.springfield.lou.screen.Screen;

public interface IScreenComponent {
	public void sendMessage(String message);
	public String getComponentTarget();
	public void destroy();
	public Screen getScreen();
}
