/*
 * Created on 2003-aug-22
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package se.idega.idegaweb.commune.accounting.resource.data;

import java.util.Collection;

import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOException;
import com.idega.data.IDOQuery;
import com.idega.user.data.Group;

/**
 * @author wmgobom
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ResourcePermissionBMPBean extends GenericEntity implements ResourcePermission {

  private static final String TABLE_NAME = "CACC_RESOURCE_PERMISSION";
  private static final String ASSIGN = "permit_assign_resource";
  private static final String VIEW = "permit_view_resource";
  private static final String RESOURCE = "cacc_resource_id";
  private static final String GROUP = "ic_group_id";
  
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
    this.addAttribute(ASSIGN, "Assign resource permissin", true, true, Boolean.class);
    this.addAttribute(VIEW, "View resource permissin", true, true, Boolean.class);
    this.addManyToOneRelationship("cacc_resource_id", Resource.class);
    this.addManyToOneRelationship("ic_group_id", Group.class);
  }

  public Collection ejbFindAllRscPermByRscIdAndGrpId(Integer rscId, Integer grpId) throws FinderException {
    // Should only return one row. Is used just as a precaution against duplicate rows.
    IDOQuery q = idoQueryGetSelect();
    q.appendWhereEquals(RESOURCE, rscId);
    q.appendAndEquals(GROUP, grpId);
    return super.idoFindPKsByQuery(q);    
  }

  public Collection ejbFindAllRscPermByRscId(Integer rscId) throws FinderException {
    IDOQuery q = idoQueryGetSelect();
    q.appendWhereEquals(RESOURCE, rscId);
    return super.idoFindPKsByQuery(q);    
  }

  
  public int ejbHomeCountRscPermByRscIdAndGrpId(Integer rscId, Integer grpId) throws IDOException {
    IDOQuery q = idoQueryGetSelectCount();
    q.appendWhereEquals(RESOURCE, rscId);
    q.appendAndEquals(GROUP, grpId);
    return  super.idoGetNumberOfRecords(q);
  }
  
  public void deleteThisRscPerm() throws RemoveException {
    this.remove();
  }
  
  public boolean getPermitAssignResource() {
    return this.getBooleanColumnValue(ASSIGN);
  }
  
  public void setPermitAssignResource(boolean doPermit) {
    this.setColumn(ASSIGN, doPermit);    
  }

  public boolean getPermitViewResource() {
    return this.getBooleanColumnValue(VIEW);
  }
  
  public void setPermitViewResource(boolean doPermit) {
    this.setColumn(VIEW, doPermit);    
  }
  
  public void setResourceFK(int rscId) {
    this.setColumn(RESOURCE, rscId);
  }
  
  public int getResourceFK() {
    return this.getIntColumnValue(RESOURCE);
  }

  public void setGroupFK(int grpId) {
    this.setColumn(GROUP, grpId);
  }
  
  public int getGroupFK() {
    return this.getIntColumnValue(GROUP);
  }
}
