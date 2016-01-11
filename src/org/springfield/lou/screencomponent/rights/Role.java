package org.springfield.lou.screencomponent.rights;

public enum Role{
	VIEWER ("viewer", "Can View"),
	COMMENTER ("commenter", "Can Comment"),
	EDITOR("editor", "Can Edit"),
	OWNER("owner", "Owner");
	
	private String id;
	private String readable;
	
	Role(String id, String readable){
		this.id = id;
		this.readable = readable;
	}
	
	public String getRoleId(){
		return this.id;
	}
	
	public boolean equals(Role role){
		return role.getRoleId().equals(this.getRoleId());
	}
	
	public String getReadable(){
		return this.readable;
	}
}
