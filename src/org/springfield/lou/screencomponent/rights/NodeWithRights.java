package org.springfield.lou.screencomponent.rights;

public interface NodeWithRights {
	public void giveRole(IRoleActor user, Role role) throws AlreadyHasRoleException;
	public Rights getRights();
}
