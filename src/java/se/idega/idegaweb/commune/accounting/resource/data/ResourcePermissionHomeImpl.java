package se.idega.idegaweb.commune.accounting.resource.data;


public class ResourcePermissionHomeImpl extends com.idega.data.IDOFactory implements ResourcePermissionHome
{
 protected Class getEntityInterfaceClass(){
  return ResourcePermission.class;
 }


 public ResourcePermission create() throws javax.ejb.CreateException{
  return (ResourcePermission) super.createIDO();
 }


public java.util.Collection findAllRscPermByRscId(java.lang.Integer p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ResourcePermissionBMPBean)entity).ejbFindAllRscPermByRscId(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllRscPermByRscIdAndGrpId(java.lang.Integer p0,java.lang.Integer p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ResourcePermissionBMPBean)entity).ejbFindAllRscPermByRscIdAndGrpId(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public ResourcePermission findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ResourcePermission) super.findByPrimaryKeyIDO(pk);
 }


public int countRscPermByRscIdAndGrpId(java.lang.Integer p0,java.lang.Integer p1)throws com.idega.data.IDOException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	int theReturn = ((ResourcePermissionBMPBean)entity).ejbHomeCountRscPermByRscIdAndGrpId(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}


}