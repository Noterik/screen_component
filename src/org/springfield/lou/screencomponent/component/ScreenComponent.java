package org.springfield.lou.screencomponent.component;

import org.springfield.lou.application.Html5Application;
import org.springfield.lou.json.JSONSerializable;
import org.springfield.lou.screen.Screen;

public abstract class ScreenComponent extends JSONSerializable implements IScreenComponent{
	private Screen screen;
	private String componentIdentifier;
	private String target;
	
	public ScreenComponent(Screen s){
		this.screen = s;
		this.parseComponentIdentifier();
		this.target = this.getClass().getSimpleName().toLowerCase();
	}
	
	public ScreenComponent(Screen s, String target){
		this(s);
		this.target = target;
	}
			
	public String getComponentTarget(){
		return this.target;
	}

	public void sendMessage(String message){
		screen.putMsg(this.componentIdentifier, "", message);
	}
	
	public void render(){
		Html5Application app = (Html5Application) screen.getApplication();
		app.loadContent(screen, this.getComponentTarget(), this.componentIdentifier);
	}
	
	public void destroy(){
		Html5Application app = (Html5Application) screen.getApplication();
		app.setContent(this.componentIdentifier, "");
	}
	
	private void parseComponentIdentifier(){
		Class<? extends ScreenComponent> inheritedClass = this.getClass();	
		if(inheritedClass.isAnnotationPresent(ComponentIdentifier.class)){
			ComponentIdentifier identifier = (ComponentIdentifier) inheritedClass.getAnnotation(ComponentIdentifier.class);
			this.componentIdentifier = identifier.id();
		}else{
			this.componentIdentifier = this.getClass().getSimpleName().toLowerCase();
		}
			
	}
	
	public Screen getScreen(){
		return this.screen;
	}
	
	public void hide(){
		screen.putMsg(this.componentIdentifier, "", "hide()");
	}
	
	public void show(){
		screen.putMsg(this.componentIdentifier, "", "show()");
	}
	
	public void sync(){
		this.sendMessage("update(" + this.toJSON() + ")");
	}
}
