package se.idega.idegaweb.commune.accounting.resource.data;


public interface ResourceClassMember extends com.idega.data.IDOEntity
{
 public void deleteThisRSCM()throws javax.ejb.RemoveException;
 public java.sql.Date getEndDate();
 public int getMemberFK();
 public int getResourceFK();
 public java.sql.Date getStartDate();
 public void initializeAttributes();
 public void setEndDate(java.util.Date p0);
 public void setMemberFK(int p0);
 public void setResourceFK(int p0);
 public void setStartDate(java.util.Date p0);
}
