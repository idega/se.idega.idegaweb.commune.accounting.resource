package se.idega.idegaweb.commune.accounting.resource.business;


public interface ResourceBusiness extends com.idega.business.IBOService
{
 public void deletePermissionsForResource(java.lang.Integer p0)throws java.rmi.RemoteException,javax.ejb.FinderException,javax.ejb.RemoveException, java.rmi.RemoteException;
 public java.util.Collection findAllResources() throws java.rmi.RemoteException;
 public java.util.Collection findAllSchoolTypes() throws java.rmi.RemoteException;
 public java.util.Collection findAllSchoolYears() throws java.rmi.RemoteException;
 public java.util.Map getRelatedSchoolTypes(se.idega.idegaweb.commune.accounting.resource.data.Resource p0) throws java.rmi.RemoteException;
 public java.util.Map getRelatedSchoolYears(se.idega.idegaweb.commune.accounting.resource.data.Resource p0) throws java.rmi.RemoteException;
 public se.idega.idegaweb.commune.accounting.resource.data.Resource getResourceByName(java.lang.String p0) throws java.rmi.RemoteException;
 public se.idega.idegaweb.commune.accounting.resource.data.Resource getResourceByPrimaryKey(java.lang.Integer p0) throws java.rmi.RemoteException;
 public se.idega.idegaweb.commune.accounting.resource.data.ResourcePermission getRscPermByRscAndGrpId(java.lang.Integer p0,java.lang.Integer p1) throws java.rmi.RemoteException;
 public void removeResource(java.lang.Integer p0) throws java.rmi.RemoteException;
 public void saveResource(java.lang.String p0,int[] p1,int[] p2) throws java.rmi.RemoteException;
 public void saveResourcePermission(int p0,int p1,boolean p2,boolean p3) throws java.rmi.RemoteException;
}
