package se.idega.idegaweb.commune.accounting.resource.data;


public interface ResourceClassMember extends com.idega.data.IDOEntity
{
 public java.sql.Date getEndDate();
 public int getMemberFK();
 public se.idega.idegaweb.commune.accounting.resource.data.Resource getResource();
 public int getResourceFK();
 public com.idega.block.school.data.SchoolClassMember getSchoolClassMember();
 public java.sql.Date getStartDate();
 public void initializeAttributes();
 public void setEndDate(java.util.Date p0);
 public void setMemberFK(int p0);
 public void setResourceFK(int p0);
 public void setStartDate(java.util.Date p0);
}
