package se.idega.idegaweb.commune.accounting.resource.data;


public interface ResourcePermission extends com.idega.data.IDOEntity
{
 public void deleteThisRscPerm()throws javax.ejb.RemoveException;
 public void getGroupFK(com.idega.user.data.Group p0);
 public boolean getPermitAssignResource();
 public boolean getPermitViewResource();
 public void getResourceFK(se.idega.idegaweb.commune.accounting.resource.data.Resource p0);
 public void initializeAttributes();
 public void setGroupFK(int p0);
 public void setPermitAssignResource(boolean p0);
 public void setPermitViewResource(boolean p0);
 public void setResourceFK(int p0);
}
