/*
 * Created on 2003-aug-14
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package se.idega.idegaweb.commune.accounting.resource.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.block.school.data.SchoolType;
import com.idega.block.school.data.SchoolYear;
import com.idega.data.GenericEntity;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOQuery;
import com.idega.data.IDORelationshipException;
import com.idega.data.IDORemoveRelationshipException;

/**
 * @author wmgobom
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ResourceBMPBean extends GenericEntity implements Resource {

  public static final String TABLE_NAME="CACC_RESOURCE";
  public static final String NAME = "resource_name";

	/* (non-Javadoc)
	 * @see com.idega.data.GenericEntity#getEntityName()
	 */
	public String getEntityName() {
		return TABLE_NAME;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.GenericEntity#initializeAttributes()
	 */
	public void initializeAttributes() {
		this.addAttribute(getIDColumnName());
    this.addAttribute(NAME, "Resourcename", true, true, String.class);
    this.setUnique(NAME, true);
    this.addManyToManyRelationShip(SchoolType.class);
    this.addManyToManyRelationShip(SchoolYear.class);
	}
  
  public Collection ejbFindAllResources() throws FinderException {
    IDOQuery query = idoQueryGetSelect();
    return super.idoFindPKsByQuery(query);        
  }
  
  public Integer ejbFindResourceByName(String name) throws FinderException {
    IDOQuery q = this.idoQueryGetSelect();
    q.appendWhereEqualsQuoted(NAME, name); 

    return (Integer) super.idoFindOnePKByQuery(q);    
  }
  
  public Collection ejbFindViewRightResourcesByGrpId(Integer grpId) throws FinderException {
    IDOQuery q = idoQuery();
    q.append("select r.* from cacc_resource r, cacc_resource_permission rp ");
    q.appendWhereEquals("r.cacc_resource_id", "rp.cacc_resource_id");
    q.appendAndEquals("rp.ic_group_id", grpId);
    q.appendAndEqualsQuoted("rp.permit_view_resource", "Y");   
    
    return idoFindPKsByQuery(q);
  }

  public Collection ejbFindAssignRightResourcesByGrpId(Integer grpId) throws FinderException {
    IDOQuery q = idoQuery();
    q.append("select r.* from cacc_resource r, cacc_resource_permission rp ");
    q.appendWhereEquals("r.cacc_resource_id", "rp.cacc_resource_id");
    q.appendAndEquals("rp.ic_group_id", grpId);
    q.appendAndEqualsQuoted("rp.permit_assign_resource", "Y");  
    
    return idoFindPKsByQuery(q);
  }
  
  public String getResourceName() {
    return this.getStringColumnValue(NAME);
  }
  
  public void setResourceName(String name) {
    this.setColumn(NAME, name);    
  }
  
  public void addSchoolTypes(int[] ids) {
    try {
      super.addTo(SchoolType.class, ids);
    }
    catch (java.sql.SQLException sql) {
      sql.printStackTrace();
    }
  }

  public void addSchoolYears(int[] ids) {
    try {
      super.addTo(SchoolYear.class, ids);
    }
    catch (java.sql.SQLException sql) {

    }
  }
  
  public void addSchoolType(SchoolType type) throws IDOAddRelationshipException {
    super.idoAddTo(type);
  }
  
  public void removeAllSchoolTypes() throws IDORemoveRelationshipException {
    super.idoRemoveFrom(SchoolType.class);
  }

  public void addSchoolYear(SchoolYear type) throws IDOAddRelationshipException {
    super.idoAddTo(type);
  }
  
  public void removeAllSchoolYears() throws IDORemoveRelationshipException {
    super.idoRemoveFrom(SchoolYear.class);
  }
  
  public Collection findRelatedSchoolTypes() throws IDORelationshipException {
    return super.idoGetRelatedEntities(SchoolType.class);
  }

  public Collection findRelatedSchoolYears() throws IDORelationshipException {
    return super.idoGetRelatedEntities(SchoolYear.class);
  }
  
}
