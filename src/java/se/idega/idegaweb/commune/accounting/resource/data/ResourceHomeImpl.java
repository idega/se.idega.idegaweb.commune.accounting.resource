package se.idega.idegaweb.commune.accounting.resource.data;


public class ResourceHomeImpl extends com.idega.data.IDOFactory implements ResourceHome
{
 protected Class getEntityInterfaceClass(){
  return Resource.class;
 }


 public Resource create() throws javax.ejb.CreateException{
  return (Resource) super.createIDO();
 }


public java.util.Collection findAllResources()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ResourceBMPBean)entity).ejbFindAllResources();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public Resource findResourceByName(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((ResourceBMPBean)entity).ejbFindResourceByName(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 public Resource findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (Resource) super.findByPrimaryKeyIDO(pk);
 }



}