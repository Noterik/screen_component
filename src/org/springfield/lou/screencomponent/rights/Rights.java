package org.springfield.lou.screencomponent.rights;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.json.IJSONObserver;
import org.springfield.lou.json.JSONField;
import org.springfield.lou.json.JSONObservable;

public class Rights extends JSONObservable{
	private String parentPath;
	private FsNode parentNode;
	private String path;
	private FsNode node;
	private Map<Role, ArrayList<IRoleActor>> unsavedRoles;
	private Map<Role, ArrayList<IRoleActor>> roles;
	
	public Rights(){
		roles = new HashMap<Role, ArrayList<IRoleActor>>();
		unsavedRoles = new HashMap<Role, ArrayList<IRoleActor>>();
		node = new FsNode();
		node.setName("rights");
		node.setId("1");
	}
	
	public Rights(FsNode parentNode, IRoleActor user) throws AlreadyHasRoleException{
		roles = new HashMap<Role, ArrayList<IRoleActor>>();
		unsavedRoles = new HashMap<Role, ArrayList<IRoleActor>>();
		node = new FsNode();
		this.parentPath = parentNode.getPath();
		this.parentPath = this.parentPath.replace("//", "/");
		this.parentNode = Fs.getNode(this.parentPath);
		node.setName("rights");
		node.setId("1");
		this.giveRole(user, Role.OWNER);
		path = parentPath + "/rights/1";
	}
	
	public Rights(String uri){
		roles = new HashMap<Role, ArrayList<IRoleActor>>();
		unsavedRoles = new HashMap<Role, ArrayList<IRoleActor>>();
		
		String[] pathSplits = uri.split("/");
		for(int i = 0; i < (pathSplits.length - 2); i++){
			parentPath += pathSplits[i];
			if(i < pathSplits.length - 3){
				parentPath += "/";
			}
		}
		
		parentPath = parentPath.replace("//", "/");
		this.parentNode = Fs.getNode(this.parentPath);
		path = uri;
	}
	
	public Rights(FsNode rightsNode) throws IncorrectRightsNodeFormatException{
		roles = new HashMap<Role, ArrayList<IRoleActor>>();
		unsavedRoles = new HashMap<Role, ArrayList<IRoleActor>>();
		this.parseNode(rightsNode);
		
		parentPath = "";
		
		String[] pathSplits = rightsNode.getPath().split("/");
		for(int i = 0; i < (pathSplits.length - 2); i++){
			parentPath += pathSplits[i];
			if(i < pathSplits.length - 3){
				parentPath += "/";
			}
		}
		
		parentPath = parentPath.replace("//", "/");
		this.parentNode = Fs.getNode(this.parentPath);
		
		this.node = rightsNode;
		this.path = node.getPath();
	}
	
	public void giveRole(IRoleActor user, Role role) throws AlreadyHasRoleException{
		
		ArrayList<String> entriesToRemove = new ArrayList<String>();
		
		if(unsavedRoles.get(role) != null){
			if(roles.get(role) != null){
				for(IRoleActor curActor : roles.get(role)){
					if(curActor.getNode().getPath().equals(user.getNode().getPath())){
						throw new AlreadyHasRoleException("Actor " + curActor.getNode().getPath() + " already has role " + role.getRoleId());
					}
				}
			}
		}else{
			unsavedRoles.put(role, new ArrayList<IRoleActor>());
		}
		
		for(Role r : roles.keySet()){
			List<IRoleActor> actors = roles.get(r);
			for(IRoleActor actor : actors){
				if(actor.getNode().getReferid().equals(user.getNode().getPath())){
					entriesToRemove.add(actor.getNode().getPath());
				}
			}
		}
		
		this.unsavedRoles.get(role).add(user);
		
		if(entriesToRemove.size() > 0){
			for(String path : entriesToRemove){
				Class<Fs> fsClass = Fs.class;
				for(Method m : fsClass.getDeclaredMethods()){
					System.out.println("myeuscreen method: " + m.getName());
				}
				Fs.deleteNode(path);
			}
		}
		
	}
	
	public void setParent(String parent){
		this.parentPath = parent;
	}
	
	public String toString(){
		return this.roles.toString();
	}
	
	public void save(){
		String path = node.getPath();
		String creationDate = new Date().toString();
		if(path != null){
			creationDate = node.getProperty("creationDate");
		}
		
		node.setProperty("creationDate", creationDate);
		node.setProperty("lastUpdate", new Date().toString());
		
		Fs.insertNode(node, parentPath);
		
		for(Role role : unsavedRoles.keySet()){
			ArrayList<IRoleActor> actorsForRole = unsavedRoles.get(role);
			if(actorsForRole.size() > 0){
				FsNode roleNode = new FsNode();
				roleNode.setName("role");
				roleNode.setId(role.getRoleId());
				
				this.path = parentPath + "/rights/1";
				Fs.insertNode(roleNode, this.path);
				
				for(IRoleActor actor : actorsForRole){
					FsNode actorNode = new FsNode();
					actorNode.setName(actor.getNode().getName());
					actorNode.setId(UUID.randomUUID().toString());
					actorNode.setReferid(actor.getNode().getPath());
					
					String rolePath = this.path + "/role/" + role.getRoleId();
					
					Fs.insertNode(actorNode, rolePath);
					
					/* WHAT IS THIS FOR AGAIN?
					FsNode parentRefer = new FsNode();
					parentRefer.setId(this.parentNode.getId());
					parentRefer.setName(this.parentNode.getName());
					parentRefer.setReferid(this.parentNode.getPath());
					String pubsPath = actor.getNode().getPath() + "/publications/1";
					if(Fs.getNode(pubsPath) == null){
						FsNode pubsNode = new FsNode();
						pubsNode.setName("publications");
						pubsNode.setId("1");
						Fs.insertNode(pubsNode, actor.getNode().getPath());
					}
					Fs.insertNode(parentRefer, pubsPath);
					*/
				}
			}
		}
		unsavedRoles.clear();
		this.refresh();
		this.update();
	}
	
	private void refresh(){
		roles.clear();
		try {
			FsNode node = Fs.getNode(this.path);
			if(node != null){
				this.parseNode(node);
			}
		} catch (IncorrectRightsNodeFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void parseNode(FsNode rightsNode) throws IncorrectRightsNodeFormatException{
		if(!rightsNode.getName().equals("rights")){
			throw new IncorrectRightsNodeFormatException("The node name is not <rights>, please check if you're passing the correct node, node passed: " + rightsNode.asXML());
		}else{
			List<FsNode> rolesList = Fs.getNodes(rightsNode.getPath() + "/role", 1);
			for(FsNode node : rolesList){
				String roleId = node.getId();
				//FSList actorFsList = FSListManager.get(node.getPath(), false);
				List<FsNode> actorsList = Fs.getNodes(node.getPath(), 2);
				
				Role role = null;
				
				for(Role currentRole : Role.values()){
					if(currentRole.getRoleId().equals(roleId)){
						role = currentRole;
						break;
					}
				}
				
				for(FsNode actorNode : actorsList){
					actorNode = Fs.getNode(actorNode.getPath());
					IRoleActor actor = new RoleActor(actorNode);
					if(this.roles.get(role) == null){
						this.roles.put(role, new ArrayList<IRoleActor>());
					}
					this.roles.get(role).add(actor);
				}
			}
		}
	}
	
	
	@Override
	public void addObserver(IJSONObserver observer) {
		// TODO Auto-generated method stub
		super.addObserver(observer);
	}
	
	@JSONField(field = "roles")
	public JSONObject getRolesJSON(){
		JSONObject roles = new JSONObject();
		for(Role role : this.roles.keySet()){
			JSONArray actors = new JSONArray();
			for(IRoleActor actor : this.roles.get(role)){
				actors.add(actor);
			}
			roles.put(role.name(), actors);
		}
		
		return roles;
	}

	public Map<Role, ArrayList<IRoleActor>> getRoles(){
		return this.roles;
	}

	public void removeRightsForActor(IRoleActor actor) {
		// TODO Auto-generated method stub
		for(ArrayList<IRoleActor> actors : roles.values()){
			for(IRoleActor cActor : actors){
				if(cActor.getNode().getReferid().equals(actor.getNode().getPath())){
					Fs.deleteNode(cActor.getNode().getPath());
				}
			}
		}
		
		this.refresh();
		this.update();
	}
}
