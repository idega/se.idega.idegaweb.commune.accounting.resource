package se.idega.idegaweb.commune.accounting.resource.data;


public class ResourceClassMemberHomeImpl extends com.idega.data.IDOFactory implements ResourceClassMemberHome
{
 protected Class getEntityInterfaceClass(){
  return ResourceClassMember.class;
 }


 public ResourceClassMember create() throws javax.ejb.CreateException{
  return (ResourceClassMember) super.createIDO();
 }


public java.util.Collection findAllByClassMemberId(java.lang.Integer p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ResourceClassMemberBMPBean)entity).ejbFindAllByClassMemberId(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllByClsMbrIdOrderByRscName(java.lang.Integer p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ResourceClassMemberBMPBean)entity).ejbFindAllByClsMbrIdOrderByRscName(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllByRscIdAndMemberId(java.lang.Integer p0,java.lang.Integer p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ResourceClassMemberBMPBean)entity).ejbFindAllByRscIdAndMemberId(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

 public ResourceClassMember findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ResourceClassMember) super.findByPrimaryKeyIDO(pk);
 }


public int countByRscIdAndMemberId(java.lang.Integer p0,java.lang.Integer p1)throws com.idega.data.IDOException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	int theReturn = ((ResourceClassMemberBMPBean)entity).ejbHomeCountByRscIdAndMemberId(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public int getCountOfResources(int p0,java.lang.String p1)throws com.idega.data.IDOException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	int theReturn = ((ResourceClassMemberBMPBean)entity).ejbHomeGetCountOfResources(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}


}