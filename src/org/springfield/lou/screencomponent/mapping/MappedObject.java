package org.springfield.lou.screencomponent.mapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.lou.json.JSONField;
import org.springfield.lou.json.JSONObservable;

public abstract class MappedObject extends JSONObservable {
	
	private String id;
	private String nodeName;
	private String parentURI = null;
	private String path = null;
	private Integer order = null;
	
	public MappedObject(){
		id = null;
		parseSmithersName();
	}
	
	public MappedObject(Map<String, Object> properties){
		id = null;
		
		parseSmithersName();
		
		for(Method method : this.getClass().getMethods()){
			if(method.isAnnotationPresent(SmithersToObjectSetter.class)){
				Annotation annotation = method.getAnnotation(SmithersToObjectSetter.class);
				SmithersToObjectSetter mapping = (SmithersToObjectSetter) annotation;
				String fieldName = mapping.mapTo();
				
				if(properties.containsKey(fieldName)){
					try {
						method.invoke(this, properties.get(fieldName));
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else if(method.isAnnotationPresent(SmithersToObjectChildrenSetter.class)){
				
			}
		}
 	}
	
	public MappedObject(Map<String, Object> properties, FsNode parent){
		this(properties);
		this.parentURI = parent.getPath();
	}
	
	public MappedObject(Map<String, Object> properties, String parent){
		this(properties);
		this.parentURI = parent;
	}
	
	public MappedObject(FsNode node){
		parseSmithersName();
		
		for(Method method : this.getClass().getMethods()){
			if(method.isAnnotationPresent(SmithersToObjectSetter.class)){
				SmithersToObjectSetter mapping = method.getAnnotation(SmithersToObjectSetter.class);
				String fieldName = mapping.mapTo();
				
				try {
					if(fieldName.equals("@id")){
						method.invoke(this, node.getId());
					}else if(fieldName.equals("@referid")){
						method.invoke(this, node.getReferid());
					}else if(!fieldName.isEmpty()){
						method.invoke(this, node.getProperty(fieldName));
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(method.isAnnotationPresent(SmithersToObjectChildrenSetter.class)){
				SmithersToObjectChildrenSetter setter = method.getAnnotation(SmithersToObjectChildrenSetter.class);
				Class<? extends MappedObject> type = setter.type();
				try {
					Constructor<? extends MappedObject> constructor = type.getConstructor(FsNode.class);
					MappingSettings mappingSettings = type.getAnnotation(MappingSettings.class);
					String systemName = mappingSettings.systemName();
					List<FsNode> childrenNodes = Fs.getNodes(node.getPath() + "/" + systemName, 1);
					ArrayList<MappedObject> children = new ArrayList<MappedObject>();
					for(FsNode childNode : childrenNodes){
						MappedObject child = constructor.newInstance(childNode);
						children.add(child);
					}
					method.invoke(this, children);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		this.path = node.getPath();
	}
	
	public MappedObject(FsNode node, FsNode parent){
		this(node);
		this.parentURI = parent.getPath();
		this.path = node.getPath();
	}
	
	public MappedObject(FsNode node, String parent){
		this(node);
		this.parentURI = parent;
		this.path = node.getPath();
	}
	
	@SmithersToObjectSetter(mapTo = "@id")
	public void setId(String id){
		this.id = id;
	}
	
	@JSONField(field = "id")
	public String getId(){
		return this.id;
	}
	
	public void save() throws NoParentForMappedObjectException{
		if(parentURI != null && (Fs.getNode(parentURI) != null || Fs.getNodes(parentURI, 1).size() > 0)){
			List<FsNode> referNodes = new ArrayList<FsNode>();
			List<MappedObject> children = new ArrayList<MappedObject>();
			FsNode node = new FsNode();
			node.setName(this.nodeName);
			
			if(this.getId() == null){
				this.setId(UUID.randomUUID().toString());
			}
			
			node.setId(this.getId());
			
			for(Method method : this.getClass().getMethods()){
				Annotation[] annotations = method.getDeclaredAnnotations();
				for(Annotation annotation : annotations){
					if(annotation.annotationType().equals(ObjectToSmithersGetter.class)){
						ObjectToSmithersGetter getter = (ObjectToSmithersGetter) annotation;
						String smithersPropName = getter.mapTo();
						if(smithersPropName != null && !smithersPropName.isEmpty()){
							try {
								Object results = method.invoke(this);
								String resultsStr = "";
								if(results != null){
									try{
										resultsStr = (String) results;
									}catch(ClassCastException cce){
										resultsStr = results.toString();
									}
								}
								node.setProperty(smithersPropName, resultsStr);
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}else if(annotation.annotationType().equals(SmithersReference.class)){
						if(method.getReturnType().equals(FsNode.class)){
							try {
								FsNode referredNode = (FsNode) method.invoke(this);
								referNodes.add(referredNode);
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch(NullPointerException e){
								e.printStackTrace();
							}
						}
					}else if(annotation.annotationType().equals(ObjectChildrenToSmithersGetter.class)){
						try {
							Object results = method.invoke(this);
							ObjectChildrenToSmithersGetter getter = (ObjectChildrenToSmithersGetter) annotation;
							boolean ordered = getter.ordered();
							if(List.class.isAssignableFrom(results.getClass())){
								int i = 0;
								List<Object> castedResults = (List<Object>) results;
								for(Object result : castedResults){
									if(MappedObject.class.isAssignableFrom(result.getClass())){
										MappedObject resultObj = (MappedObject) result;
										if(ordered)
											resultObj.setOrder(i);
										children.add(resultObj);
									}
									i++;
								}
							}
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
			}
			
			
			this.path = this.parentURI + "/" + this.nodeName + "/" + this.getId();
			if(Fs.getNode(path) != null){
				Fs.deleteNode(path);
			}
			
			
			if(this.order() != null){
				node.setProperty("myeuscreen_child_order", this.order().toString());
			}
			
			Fs.insertNode(node, this.parentURI);
			
			for(Iterator<FsNode> referenceIterator = referNodes.iterator(); referenceIterator.hasNext();){
				FsNode referedNode = referenceIterator.next();
				FsNode newReferingNode = new FsNode();
				newReferingNode.setName(referedNode.getName());
				newReferingNode.setId(referedNode.getId());
				newReferingNode.setReferid(referedNode.getPath());
				Fs.insertNode(newReferingNode, path);
			}
			
			for(Iterator<MappedObject> childrenIterator = children.iterator(); childrenIterator.hasNext();){
				MappedObject child = childrenIterator.next();
				child.save(path);
			}
			
			this.setPath(path);
		}else{
			throw new NoParentForMappedObjectException("Please provide a correct parent for this object, parent given: " + this.parentURI);
		}
			
	}
		
	public void save(String parentNode) throws NoParentForMappedObjectException{	
		this.parentURI = parentNode;
		this.save();
	}
	
	public void save(FsNode parentNode) throws NoParentForMappedObjectException{
		this.parentURI = parentNode.getPath();
		this.save();
	}
	
	public void createReference(FsNode referencingParentNode) throws ReferencedNodeNotExistsException{
		if(this.path != null){
			FsNode node = new FsNode();
			node.setName(this.nodeName);
			node.setId(this.id);
			node.setReferid(this.path);
			Fs.insertNode(node, referencingParentNode.getPath());
		}else{
			throw new ReferencedNodeNotExistsException("The node your trying to create a reference for doesn't exist yet!");
		}
	}
	
	public void setOrder(Integer order){
		this.order = order;
	}
	
	public Integer order(){
		return this.order;
	}
	
	public String getNodeName(){
		return this.nodeName;
	}
	
	private void parseSmithersName(){
		SmithersName smithersName = this.getClass().getAnnotation(SmithersName.class);
		if(smithersName != null){
			nodeName = smithersName.name();
		}else{
			nodeName = this.getClass().getSimpleName().toLowerCase();
		}
	}
	
	public void setParent(FsNode parent){
		this.parentURI = parent.getPath();
	}
	
	public void setParent(String parent){
		this.parentURI = parent;
	}

	@JSONField(field="path")
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		if(path.contains("//")){
			path = path.replace("//", "/");
		}
		this.path = path;
	}

}
