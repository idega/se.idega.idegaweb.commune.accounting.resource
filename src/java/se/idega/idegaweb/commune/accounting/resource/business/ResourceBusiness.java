package se.idega.idegaweb.commune.accounting.resource.business;


public interface ResourceBusiness extends com.idega.business.IBOService
{
 public int countResourcePlacementsByRscIDAndMemberID(java.lang.Integer p0,java.lang.Integer p1) throws java.rmi.RemoteException;
 public void createResource(java.lang.String p0,int[] p1,int[] p2)throws java.rmi.RemoteException,javax.ejb.CreateException, java.rmi.RemoteException;
 public void createResourcePermission(int p0,int p1,boolean p2,boolean p3)throws java.rmi.RemoteException,javax.ejb.CreateException, java.rmi.RemoteException;
 public void createResourcePlacement(int p0,int p1,java.lang.String p2,java.lang.String p3,boolean p4)throws java.rmi.RemoteException,se.idega.idegaweb.commune.accounting.resource.business.DateException,se.idega.idegaweb.commune.accounting.resource.business.ResourceException,se.idega.idegaweb.commune.accounting.resource.business.ClassMemberException, java.rmi.RemoteException;
 public se.idega.idegaweb.commune.accounting.resource.data.ResourceClassMember createResourcePlacement(int p0,int p1,java.lang.String p2)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public void createResourcePlacement(int p0,int p1,java.lang.String p2,java.lang.String p3)throws java.rmi.RemoteException,se.idega.idegaweb.commune.accounting.resource.business.DateException,se.idega.idegaweb.commune.accounting.resource.business.ResourceException,se.idega.idegaweb.commune.accounting.resource.business.ClassMemberException, java.rmi.RemoteException;
 public void deletePermissionsForResource(java.lang.Integer p0)throws java.rmi.RemoteException,javax.ejb.FinderException,javax.ejb.RemoveException, java.rmi.RemoteException;
 public void deleteResourceClassMember(java.lang.Integer p0)throws java.rmi.RemoteException,javax.ejb.FinderException,javax.ejb.RemoveException, java.rmi.RemoteException;
 public java.util.Collection findAllResources() throws java.rmi.RemoteException;
 public java.util.Collection findAllResourcesByCategory(java.lang.String p0) throws java.rmi.RemoteException;
 public java.util.Collection findAllSchoolCategories() throws java.rmi.RemoteException;
 public java.util.Collection findAllSchoolTypes() throws java.rmi.RemoteException;
 public java.util.Collection findAllSchoolYears() throws java.rmi.RemoteException;
 public void finishResourceClassMember(java.lang.Integer p0,java.lang.Integer p1,java.lang.String p2,java.lang.String p3,boolean p4)throws javax.ejb.FinderException,java.rmi.RemoteException,se.idega.idegaweb.commune.accounting.resource.business.DateException,se.idega.idegaweb.commune.accounting.resource.business.ClassMemberException, java.rmi.RemoteException;
 public void finishResourceClassMember(java.lang.Integer p0,java.lang.Integer p1,java.lang.String p2,java.lang.String p3)throws javax.ejb.FinderException,java.rmi.RemoteException,se.idega.idegaweb.commune.accounting.resource.business.DateException,se.idega.idegaweb.commune.accounting.resource.business.ClassMemberException, java.rmi.RemoteException;
 public java.util.Collection getAssignRightResourcesForGroup(java.lang.Integer p0) throws java.rmi.RemoteException;
 public java.util.Collection getAssignableResourcesByYearAndType(java.lang.String p0,java.lang.String p1) throws java.rmi.RemoteException;
 public java.util.Collection getAssignableResourcesForPlacement(java.lang.Integer p0,java.lang.Integer p1) throws java.rmi.RemoteException;
 public java.util.Map getRelatedSchoolTypes(se.idega.idegaweb.commune.accounting.resource.data.Resource p0) throws java.rmi.RemoteException;
 public java.util.Map getRelatedSchoolYears(se.idega.idegaweb.commune.accounting.resource.data.Resource p0) throws java.rmi.RemoteException;
 public se.idega.idegaweb.commune.accounting.resource.data.Resource getResourceByName(java.lang.String p0) throws java.rmi.RemoteException;
 public se.idega.idegaweb.commune.accounting.resource.data.Resource getResourceByPrimaryKey(java.lang.Integer p0) throws java.rmi.RemoteException;
 public java.util.Collection getResourcePlacementsByMbrIdOrderByRscName(java.lang.Integer p0) throws java.rmi.RemoteException;
 public java.util.Collection getResourcePlacementsByMemberId(java.lang.Integer p0) throws java.rmi.RemoteException;
 public java.lang.String getResourcesString(com.idega.block.school.data.SchoolClassMember p0) throws java.rmi.RemoteException;
 public se.idega.idegaweb.commune.accounting.resource.data.ResourcePermission getRscPermByRscAndGrpId(java.lang.Integer p0,java.lang.Integer p1) throws java.rmi.RemoteException;
 public com.idega.block.school.business.SchoolBusiness getSchoolBusiness(com.idega.presentation.IWContext p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.block.school.data.SchoolCategoryHome getSchoolCategoryHome() throws java.rmi.RemoteException;
 public com.idega.block.school.data.SchoolClassMember getSchoolClassMember(java.lang.Integer p0) throws java.rmi.RemoteException;
 public java.util.Collection getViewRightResourcesForGroup(java.lang.Integer p0) throws java.rmi.RemoteException;
 public boolean hasResources(int p0,java.lang.String p1) throws java.rmi.RemoteException;
 public void removeResource(java.lang.Integer p0) throws java.rmi.RemoteException;
 public void saveResource(boolean p0,java.lang.String p1,int[] p2,int[] p3,boolean p4,boolean p5,int p6,int p7)throws se.idega.idegaweb.commune.accounting.resource.business.ResourceException, java.rmi.RemoteException;
}
