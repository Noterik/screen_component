package org.springfield.lou.screencomponent.rights;

import org.springfield.fs.FsNode;

public class RoleActor implements IRoleActor {
	private FsNode node;
	
	public RoleActor(FsNode node){
		this.node = node;
	}
	
	@Override
	public FsNode getNode() {
		// TODO Auto-generated method stub
		return node;
	}

}
