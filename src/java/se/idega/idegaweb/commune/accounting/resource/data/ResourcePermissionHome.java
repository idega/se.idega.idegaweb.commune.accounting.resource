package se.idega.idegaweb.commune.accounting.resource.data;


public interface ResourcePermissionHome extends com.idega.data.IDOHome
{
 public ResourcePermission create() throws javax.ejb.CreateException;
 public ResourcePermission findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAllRscPermByRscId(java.lang.Integer p0)throws javax.ejb.FinderException;
 public java.util.Collection findAllRscPermByRscIdAndGrpId(java.lang.Integer p0,java.lang.Integer p1)throws javax.ejb.FinderException;
 public int countRscPermByRscIdAndGrpId(java.lang.Integer p0,java.lang.Integer p1)throws com.idega.data.IDOException;

}