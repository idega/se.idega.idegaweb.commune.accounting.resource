package se.idega.idegaweb.commune.accounting.resource.data;


public interface Resource extends com.idega.data.IDOEntity
{
 public void addSchoolType(com.idega.block.school.data.SchoolType p0)throws com.idega.data.IDOAddRelationshipException;
 public void addSchoolTypes(int[] p0);
 public void addSchoolYear(com.idega.block.school.data.SchoolYear p0)throws com.idega.data.IDOAddRelationshipException;
 public void addSchoolYears(int[] p0);
 public java.util.Collection findRelatedSchoolTypes()throws com.idega.data.IDORelationshipException;
 public java.util.Collection findRelatedSchoolYears()throws com.idega.data.IDORelationshipException;
 public java.lang.String getResourceName();
 public void initializeAttributes();
 public void removeAllSchoolTypes()throws com.idega.data.IDORemoveRelationshipException;
 public void removeAllSchoolYears()throws com.idega.data.IDORemoveRelationshipException;
 public void setResourceName(java.lang.String p0);
}
