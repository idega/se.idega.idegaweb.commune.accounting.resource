/*
 * Created on 2003-aug-19
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package se.idega.idegaweb.commune.accounting.resource.business;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import se.idega.idegaweb.commune.accounting.resource.data.Resource;
import se.idega.idegaweb.commune.accounting.resource.data.ResourceHome;
import se.idega.idegaweb.commune.accounting.resource.data.ResourcePermission;
import se.idega.idegaweb.commune.accounting.resource.data.ResourcePermissionHome;

import com.idega.block.school.data.SchoolType;
import com.idega.block.school.data.SchoolTypeHome;
import com.idega.block.school.data.SchoolYear;
import com.idega.block.school.data.SchoolYearHome;
import com.idega.business.IBOServiceBean;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORelationshipException;

/**
 * @author G�ran Borgman
 *
 * This Businessbean contains methods for Resourcehandling(resource in the School sense of the word).
 */
public class ResourceBusinessBean extends IBOServiceBean  implements ResourceBusiness {
  
  /**
   * Returns a Collection with all instances of the class SchoolType from the DB.
   */
  public Collection findAllSchoolTypes() {
    try {
      SchoolTypeHome shome = (SchoolTypeHome) IDOLookup.getHome(SchoolType.class);
      return shome.findAllSchoolTypes();
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return com.idega.util.ListUtil.getEmptyList();
    }
  }
  
  /**
   * Returns a Collection with all instances of the class SchoolYear from the DB.
   */
   public Collection findAllSchoolYears() {
      try {
        SchoolYearHome shome = (SchoolYearHome) IDOLookup.getHome(SchoolYear.class);
        return shome.findAllSchoolYears();
      }
      catch (Exception ex) {
        ex.printStackTrace();
        return com.idega.util.ListUtil.getEmptyList();
      }
  }
  
  /**
   * Returns a Collection with all instances of the class accounting.Resource from the DB.
   */  
  public Collection findAllResources() {
    try {
			ResourceHome rHome = (ResourceHome) IDOLookup.getHome(Resource.class);
      return rHome.findAllResources();
		} catch (Exception e) {
      e.printStackTrace();
      return com.idega.util.ListUtil.getEmptyList();
		}
  }
  
  /**
   * Returns one instance of the class Resource from the DB, with param pk as primary key. 
   * @param pk An Integer object with the value of the primary key
   */  
  public Resource getResourceByPrimaryKey(Integer pk) {
    try {
			ResourceHome rHome = (ResourceHome) IDOLookup.getHome(Resource.class);
      return rHome.findByPrimaryKey(pk);
		} catch (Exception e) {
			e.printStackTrace();
      return null;
		}
  }
  
  /**
   * Returns one instance of the class Resource from the DB, with the unique name of param name
   * @param name The name of the requested Resource
   */
  public Resource getResourceByName(String name) {
    Resource rsc = null;
    try {
			ResourceHome rHome = (ResourceHome) IDOLookup.getHome(Resource.class);
      rsc = rHome.findResourceByName(name);
		}  catch (FinderException e) {
      e.printStackTrace();
    } catch (IDOLookupException lue) {
      lue.printStackTrace();
    }
    return rsc;
  }
  
  /**
   * Saves a Resource to DB
   * @param name the name of the Resource
   * @param typeInts intArray of related SchoolTypes
   * @param yearInts intArray of related SchoolYears
   */
  public void saveResource(String name, int[] typeInts, int[] yearInts) {
    UserTransaction trans = getSessionContext().getUserTransaction();
    try {
			ResourceHome rscHome = (ResourceHome) getIDOHome(Resource.class);
      Resource bmp = rscHome.create();
      trans.begin();          // Start transaction
      bmp.setResourceName(name);
      bmp.store();
      bmp.addSchoolTypes(typeInts);
      bmp.addSchoolYears(yearInts);
      trans.commit();       // Commit transaction
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
      try {
				trans.rollback();     // Rollback transaction
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (SystemException e1) {
				e1.printStackTrace();
			}
    } 
  }
  
  /**
   * Gets a ResourcePermission by ResourceId and GroupId from db.
   * @param rscId The requested ResourcePermissions related Resourceid. 
   * @param grpId The requested ResourcePermissions related Groupid.
   */
  public ResourcePermission getRscPermByRscAndGrpId(Integer rscId, Integer grpId) {
    ResourcePermission rscPerm = null;
    try {
      ResourcePermissionHome rpHome = 
                (ResourcePermissionHome) this.getIDOHome(ResourcePermission.class);
      Collection rpColl = rpHome.findAllRscPermByRscIdAndGrpId(rscId, grpId);
      int row = 0;
      for (Iterator iter = rpColl.iterator(); iter.hasNext();) {
				ResourcePermission tmpRscPerm = (ResourcePermission) iter.next();
        row++;
        if(row == 1) {
          rscPerm = tmpRscPerm;
        } else {
          // delete duplicate entity/row from db
          tmpRscPerm.remove();
        }				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    return rscPerm;
  }
  
  /**
   * Saves a ResourcPermission to DB
   * @param rscId The requested ResourcePermissions related Resourceid. 
   * @param grpId The requested ResourcePermissions related Groupid.
   * @param canAssign If this group has permission to assign related Resource to a childs Placement
   * @param canView If this group has permission to view related Resource
   */
  public void saveResourcePermission(int rscId, int grpId, boolean canAssign, boolean canView) {
    try {
      ResourcePermissionHome rscPermHome = 
        (ResourcePermissionHome) getIDOHome(ResourcePermission.class);
      ResourcePermission permBmp = rscPermHome.create();
      permBmp.setPermitAssignResource(canAssign);
      permBmp.setPermitViewResource(canView);
      permBmp.setResourceFK(rscId);
      permBmp.setGroupFK(grpId);
      permBmp.store();
    } catch (CreateException ce) {
        ce.printStackTrace();
        System.out.println(ce.getMessage());
    } catch (RemoteException re) {
        re.printStackTrace();
        System.out.println(re.getMessage());
    }        
  }
  
  /**
   * Delete all ResourcPermissions related to Resource with id rscId
   * @param rscId The requested ResourcePermissions related Resourceid.
   */
  public void deletePermissionsForResource(Integer rscId) throws RemoteException, FinderException, RemoveException {
    Collection rscColl = null;
		ResourcePermissionHome rpHome = (ResourcePermissionHome) getIDOHome(ResourcePermission.class);
    rscColl = rpHome.findAllRscPermByRscId(rscId);
    for (Iterator iter = rscColl.iterator(); iter.hasNext();) {
			ResourcePermission element = (ResourcePermission) iter.next();
			element.remove();
		}
  }
  
  /**
   * Gets all SchoolType instances from db related to the Resource rsc
   * @param rsc The Resource
   */
  public Map getRelatedSchoolTypes(Resource rsc) {
    HashMap typeMap = null; 
    try {
      Collection rscColl = rsc.findRelatedSchoolTypes();
      typeMap = new HashMap(rscColl.size());
      for (Iterator iter = rscColl.iterator(); iter.hasNext();) {
				SchoolType aType = (SchoolType) iter.next();
        typeMap.put(aType.getPrimaryKey(), aType);				
			}        
			return typeMap;
		} catch (IDORelationshipException e) {
      e.printStackTrace();
		}
    return typeMap;   
  }

  /**
   * Gets all SchoolYear instances from db related to the Resource rsc
   * @param rsc The Resource
   */
  public Map getRelatedSchoolYears(Resource rsc) {
    HashMap yearMap = null; 
    try {
      Collection rscColl = rsc.findRelatedSchoolYears();
      yearMap = new HashMap(rscColl.size());
      for (Iterator iter = rscColl.iterator(); iter.hasNext();) {
        SchoolYear aYear = (SchoolYear) iter.next();
        yearMap.put(aYear.getPrimaryKey(), aYear);        
      }        
      return yearMap;
    } catch (IDORelationshipException e) {
      e.printStackTrace();
    }
    return yearMap;   
  }
  
  /**
   * Removes a Resource and all related rows from db 
   * @param rscId
   */
  public void removeResource(Integer rscId){
    Resource theRsc = getResourceByPrimaryKey(rscId);
    // Get transaction
    UserTransaction trans = getSessionContext().getUserTransaction();
    try{
      trans.begin();        // Start the transaction
      theRsc.removeAllSchoolTypes();
      theRsc.removeAllSchoolYears();
      deletePermissionsForResource(rscId);
      theRsc.remove();   // Delete the resource 
      trans.commit();      // Commit the transaction
    } 
    catch(Exception e){
      e.printStackTrace();
      System.out.println(e.getMessage());
      try {
				trans.rollback();
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (SystemException e1) {
				e1.printStackTrace();
			}
    }    
  }

}
