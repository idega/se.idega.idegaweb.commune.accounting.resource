/*
 * Created on 2003-aug-19
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package se.idega.idegaweb.commune.accounting.resource.business;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import se.idega.idegaweb.commune.accounting.resource.data.Resource;
import se.idega.idegaweb.commune.accounting.resource.data.ResourceClassMember;
import se.idega.idegaweb.commune.accounting.resource.data.ResourceClassMemberHome;
import se.idega.idegaweb.commune.accounting.resource.data.ResourceHome;
import se.idega.idegaweb.commune.accounting.resource.data.ResourcePermission;
import se.idega.idegaweb.commune.accounting.resource.data.ResourcePermissionHome;

import com.idega.block.school.business.SchoolBusiness;
import com.idega.block.school.data.SchoolCategory;
import com.idega.block.school.data.SchoolCategoryHome;
import com.idega.block.school.data.SchoolClassMember;
import com.idega.block.school.data.SchoolClassMemberHome;
import com.idega.block.school.data.SchoolType;
import com.idega.block.school.data.SchoolTypeHome;
import com.idega.block.school.data.SchoolYear;
import com.idega.block.school.data.SchoolYearHome;
import com.idega.business.IBOLookup;
import com.idega.business.IBOServiceBean;
import com.idega.data.IDOException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORelationshipException;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.presentation.IWContext;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;
import com.idega.util.text.Name;


/**
 * @author Göran Borgman
 *
 * This Businessbean contains methods for Resourcehandling(resource in the School sense of the word).
 */
public class ResourceBusinessBean extends IBOServiceBean implements ResourceBusiness {
	
	private static final String KP = "ResourceBusiness.";
	private static final String KEY_ERR_MSG_COULD_NOT_SAVE_RSC = 
																						KP + "err_msg.could_not_save_resource";
  
  /**
   * Returns a Collection with all instances of the Class SchoolType from the DB.
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
   * Returns a Collection with all instances of the Class SchoolYear from the DB.
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
   * Returns a Collection with all instances of the Class accounting.Resource from the DB.
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
   * Returns one instance of the Class Resource from the DB, with param pk as primary key. 
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
   * Returns one instance of the Class Resource from the DB, with the unique name of param name
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
   * Returns a Collection of Resources that providers are allowed to assign to a child
   */
  public Collection getAssignRightResourcesForGroup(Integer grpId) {
    Collection rscColl = null; 
    try {
      ResourceHome rHome = (ResourceHome) IDOLookup.getHome(Resource.class);
      rscColl = rHome.findAssignRightResourcesByGrpId(grpId);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return rscColl;
  }

  /**
   * Returns a Collection of Resources that providers are allowed to view
   */
  public Collection getViewRightResourcesForGroup(Integer grpId) {
    Collection rscColl = null;
    try {
      ResourceHome rHome = (ResourceHome) IDOLookup.getHome(Resource.class);
      rscColl = rHome.findViewRightResourcesByGrpId(grpId);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return rscColl;
  }
  
  public boolean hasResources(int schoolClassMemberID, String resourceIDs) {
  	try {
	  	ResourceClassMemberHome mHome = (ResourceClassMemberHome) getIDOHome(ResourceClassMember.class);
	  	if (mHome.getCountOfResources(schoolClassMemberID, resourceIDs) > 0) {
	  		return true;
	  	}
  	}
  	catch (IDOException ie) {
  		log(ie);
  	}
  	catch (RemoteException re) {
  		log(re);
  	}
  	return false;
  }
  
  /**
   * Returns a Collection of Resources related to a placements school year and type, and with 
   * rights to assign the Resource for the given group id. If group id == -7 all resources are shown.
   * This is used when administered by central administration with rights to see all resources
   * 
   * @param grpID The Group id 
   * @param clsMemberID The SchoolClassMember(placement) id
   */
  public Collection getAssignableResourcesForPlacement(Integer grpID,  Integer clsMemberID) {  	
    Collection possibleRscs = null;
    Collection  validRscs = new Vector();
    SchoolClassMember mbr; 
    //SchoolClass schClass;
    int clsYearID = -1;
    int clsTypeID = -1;
    
    if (grpID.intValue() == -7)
    	// grpID is -7, means used by central admin
    	possibleRscs = findAllResources();
    else   
    	possibleRscs = getAssignRightResourcesForGroup(grpID);

    try {
      mbr = getSchoolClassMember(clsMemberID);

      //schClass = mbr.getSchoolClass();
      if (mbr != null) {
      	clsYearID = mbr.getSchoolYearId();
      	clsTypeID = mbr.getSchoolTypeId();      	
      }
      
      // Loop resources and check if the year and type match the SchoolClassMember(placement)      
      for (Iterator iter = possibleRscs.iterator(); iter.hasNext();) {
        boolean hasYear = false;
        boolean hasType = false;
        Resource theRsc = (Resource) iter.next();
        
        // if the group/class has no schoolyear, don't match year, just set hasYear to true
        if (clsYearID != -1) {
          Collection rscYears = theRsc.findRelatedSchoolYears();
          // Check if the resource has the placement's/schoolClass'es school year
          for (Iterator iterator = rscYears.iterator(); iterator.hasNext();) {
  					SchoolYear theYear = (SchoolYear) iterator.next();
  					Integer PK = (Integer) theYear.getPrimaryKey();
            if (clsYearID == PK.intValue()) {
              hasYear = true;
              break;
            }
  				}
        } else {
          hasYear = true;
        }
        
        Collection rscTypes = theRsc.findRelatedSchoolTypes();
        // Check if the resource has the placement's/schoolClass'es school year
        for (Iterator iterator = rscTypes.iterator(); iterator.hasNext();) {
          SchoolType theType = (SchoolType) iterator.next();
          Integer PK = (Integer) theType.getPrimaryKey();
          if (clsTypeID == PK.intValue()) {
            hasType = true;
            break;
          }					
				}
              
        // if the Resource has the year and type. Add to validRscs          
        if (hasYear  && hasType) {
          validRscs.add(theRsc);
        }
      }
		} catch (Exception e) {
			e.printStackTrace();
		}
    return validRscs;
  }

  /**
   * Returns a Collection of Resources related to the given school year and type.
   * @param yearID The school year id
   * @param typeID The school type id
   */
  public Collection getAssignableResourcesByYearAndType(String yearIdStr, String typeIdStr) {
    Collection possibleRscs = findAllResources();
    Collection  validRscs = new Vector();

    try {
      // Loop resources and check if the year and type match      
      for (Iterator iter = possibleRscs.iterator(); iter.hasNext();) {
        boolean hasYear = false;
        boolean hasType = false;
        Resource theRsc = (Resource) iter.next();
        
        // if the group/class has no schoolyear, don't match year, just set hasYear to true
        if (yearIdStr != null) {
          int yearID = Integer.parseInt(yearIdStr);
          Collection rscYears = theRsc.findRelatedSchoolYears();
          // Check if the resource has the placement's/schoolClass'es school year
          for (Iterator iterator = rscYears.iterator(); iterator.hasNext();) {
            SchoolYear theYear = (SchoolYear) iterator.next();
            Integer PK = (Integer) theYear.getPrimaryKey();
            if (yearID == PK.intValue()) {
              hasYear = true;
              break;
            }
          }
        }
        
        if (typeIdStr != null) {
          int typeID = Integer.parseInt(typeIdStr);
          Collection rscTypes = theRsc.findRelatedSchoolTypes();
          // Check if the resource has the placement's/schoolClass'es school year
          for (Iterator iterator = rscTypes.iterator(); iterator.hasNext();) {
            SchoolType theType = (SchoolType) iterator.next();
            Integer PK = (Integer) theType.getPrimaryKey();
            if (typeID == PK.intValue()) {
              hasType = true;
              break;
            }         
          }
        }
              
        // if the Resource has the year and type. Add to validRscs          
        if (hasYear  && hasType) {
          validRscs.add(theRsc);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return validRscs;
  }

  
  public SchoolClassMember getSchoolClassMember(Integer memberID) {
    SchoolClassMember mbr = null;
    try {
      SchoolClassMemberHome mHome = (SchoolClassMemberHome) IDOLookup.getHome(SchoolClassMember.class); 
      mbr = mHome.findByPrimaryKey(memberID);      
    } catch (Exception e) {
      e.printStackTrace();
    }
    return mbr;    
  }
  
  public SchoolBusiness getSchoolBusiness(IWContext iwc) throws RemoteException {
    return (SchoolBusiness) IBOLookup.getServiceInstance(iwc, SchoolBusiness.class);
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
  
  public Collection getResourcePlacementsByMemberId(Integer memberId) {
    Collection mColl = null;
    try {
      ResourceClassMemberHome mHome = (ResourceClassMemberHome) getIDOHome(ResourceClassMember.class);
      mColl = mHome.findAllByClassMemberId(memberId);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return mColl;
  }
  
  public Collection getResourcePlacementsByMbrIdOrderByRscName(Integer memberId) {
	Collection mColl = null;
	try {
	  ResourceClassMemberHome mHome = (ResourceClassMemberHome) getIDOHome(ResourceClassMember.class);
	  mColl = mHome.findAllByClsMbrIdOrderByRscName(memberId);
	} catch (Exception e) {
	  e.printStackTrace();
	}
	return mColl;
  }  
  
  public int countResourcePlacementsByRscIDAndMemberID(Integer rID, Integer mID) {
    int pSum = -1;
    try {
      ResourceClassMemberHome mHome = (ResourceClassMemberHome) getIDOHome(ResourceClassMember.class);
      pSum = mHome.countByRscIdAndMemberId(rID, mID);
    } catch (Exception e){
      e.printStackTrace();
    }
    return pSum;
  }
  
  public void saveResource(boolean isSavingExisting, String rscName, int[] typeInts, int[] yearInts, 
                                          boolean permitAssign, boolean permitView, int grpId, int rscId) 
                                          throws ResourceException {
    UserTransaction trans = getSessionContext().getUserTransaction();
    try {
       trans.begin();          // Start transaction                                        
       // Handle Resource
      Resource oldRsc = null;
      if (isSavingExisting) {
        // Remove old from relationships from db. 
        oldRsc = getResourceByPrimaryKey(new Integer(rscId));
        try {
          oldRsc.removeAllSchoolTypes();
          oldRsc.removeAllSchoolYears();
        } catch (IDORemoveRelationshipException e) {
          e.printStackTrace();
        }
        // Update resource fields
        oldRsc.setResourceName(rscName);
        oldRsc.addSchoolTypes(typeInts);
        oldRsc.addSchoolYears(yearInts);
        oldRsc.store(); 
      } else {
        // new rsc
        createResource(rscName, typeInts, yearInts);    
      }
      // Handle ResourcePermission
      Integer rscIdInteger = (Integer) getResourceByName(rscName).getPrimaryKey();
      deletePermissionsForResource(rscIdInteger);
      if (permitAssign || permitView) {         
        createResourcePermission(rscIdInteger.intValue(), grpId, permitAssign, permitView);
      }
      trans.commit();       // Commit transaction
    } catch (Exception e) {
      try {
        trans.rollback();     // Rollback transaction
      } catch (IllegalStateException e1) {
        e1.printStackTrace();
      } catch (SecurityException e1) {
        e1.printStackTrace();
      } catch (SystemException e1) {
        e1.printStackTrace();
      }
	  throw new ResourceException(KEY_ERR_MSG_COULD_NOT_SAVE_RSC,
													"Could not save resource. Check that resource name is unique.");
    }                         
  }

  /**
   * Saves a Resource to DB
   * @param name the name of the Resource
   * @param typeInts intArray of related SchoolTypes
   * @param yearInts intArray of related SchoolYears
   */
  public void createResource(String name, int[] typeInts, int[] yearInts) throws RemoteException, CreateException {
      ResourceHome rscHome = (ResourceHome) getIDOHome(Resource.class);
      Resource bmp = rscHome.create();
      bmp.setResourceName(name);
      bmp.store();
      bmp.addSchoolTypes(typeInts);
      bmp.addSchoolYears(yearInts);
  }
  
  /**
   * Saves a ResourcePermission to DB
   * @param rscId The requested ResourcePermissions related Resourceid. 
   * @param grpId The requested ResourcePermissions related Groupid.
   * @param canAssign If this group has permission to assign related Resource to a childs Placement
   * @param canView If this group has permission to view related Resource
   */
  public void createResourcePermission(int rscId, int grpId, boolean permitAssign, boolean permitView) 
                                                                                                          throws RemoteException, CreateException {
      ResourcePermissionHome rscPermHome = (ResourcePermissionHome) getIDOHome(ResourcePermission.class);
      ResourcePermission permBmp = rscPermHome.create();
      permBmp.setPermitAssignResource(permitAssign);
      permBmp.setPermitViewResource(permitView);
      permBmp.setResourceFK(rscId);
      permBmp.setGroupFK(grpId);
      permBmp.store();      
  }
  
  /**
   * Creates and stores a new ResourcePlacement to DB. No indata checks.
   * @param rscId The ResourceID. 
   * @param grpId The SchoolPlacements SchoolClassMemberID.
   * @param startDate Startdate of this ResourcePlacement
   * @param endDate Enddate of this ResourcePlacement
   */
  public ResourceClassMember createResourcePlacement(int rscId, int memberId, String startDateStr)  throws RemoteException {
  	return createResourcePlacement(rscId, memberId, startDateStr, -1);	
  }
  
  /**
   * Creates and stores a new ResourcePlacement to DB. No indata checks.
   * @param rscId The ResourceID. 
   * @param grpId The SchoolPlacements SchoolClassMemberID.
   * @param startDate Startdate of this ResourcePlacement
   * @param endDate Enddate of this ResourcePlacement
   * @param registratorID UserID of current user
   */
	public ResourceClassMember createResourcePlacement(int rscId, int memberId, String startDateStr, int registratorID)  throws RemoteException {
		ResourceClassMemberHome rscClMbrHome = (ResourceClassMemberHome) getIDOHome(ResourceClassMember.class);
		Date startDate = null;
		ResourceClassMember rscMemberBmp = null;
		if (!startDateStr.equals("")) {
			IWTimestamp start= new IWTimestamp(startDateStr);
			startDate = start.getDate();
		}
		
		// Get created date - rightNow
		java.util.Date rightNowDate = new java.util.Date();
		java.sql.Date rightNow = new java.sql.Date(rightNowDate.getTime());
		
		try {         
			rscMemberBmp = rscClMbrHome.create();
			rscMemberBmp.setResourceFK(rscId);
			rscMemberBmp.setMemberFK(memberId);
			rscMemberBmp.setStartDate(startDate);
			if (registratorID != -1)
				rscMemberBmp.setRegistratorId(registratorID);
			rscMemberBmp.setCreatedDate(rightNow);
			rscMemberBmp.store();
		}
		catch (javax.ejb.CreateException ce) {
			throw new java.rmi.RemoteException(ce.getMessage());
		}
		return rscMemberBmp;            
	}   

	
	/**
	 * Saves a ResourcePlacement to DB. Has indata checks and throws indata exceptions. Used primarily
	 * by SchoolAdminOverview.
	 * @param rscId The ResourceID. 
	 * @param grpId The SchoolPlacements SchoolClassMemberID.
	 * @param startDate Startdate of this ResourcePlacement
	 * @param endDate Enddate of this ResourcePlacement
	 */
	public void createResourcePlacement(int rscId, int memberId, String startDateStr, String endDateStr, int registratorID)  throws RemoteException, DateException, ResourceException, ClassMemberException {
		createResourcePlacement(rscId, memberId, startDateStr, endDateStr, registratorID, true);
	}
  /**
   * Saves a ResourcePlacement to DB. Has boolean to set if we want to check past time of start date. 
   * Has indata checks and throws indata exceptions. Used primarily
   * by SchoolAdminOverview.
   * @param rscId The ResourceID. 
   * @param grpId The SchoolPlacements SchoolClassMemberID.
   * @param startDate Startdate of this ResourcePlacement
   * @param endDate Enddate of this ResourcePlacement
   * @param isCentralAdmin check validity of start date if user is a provider
   */
  public void createResourcePlacement(int rscId, int schClsMbrID, String startDateStr, String endDateStr, int registratorID, boolean isCentralAdmin)  throws RemoteException, DateException, ResourceException, ClassMemberException {
      ResourceClassMemberHome rscClMbrHome = (ResourceClassMemberHome) getIDOHome(ResourceClassMember.class);
      try {
        IWTimestamp today = IWTimestamp.RightNow();
        today.setAsDate();    
        Date startDate = null;
        Date endDate = null;
        IWTimestamp start = null;
        IWTimestamp end = null;
        
        // Check valid resource and placement
        if (rscId == -1) {
          throw new ResourceException("cacc.rsc_placement.no_resource_chosen","You must chose a resource");
        } else if (schClsMbrID == -1){
          throw new ClassMemberException("cacc.rsc_placement.no_member_placement_id", "No group placement found");
        } else {
          int existing = countResourcePlacementsByRscIDAndMemberID(new Integer(rscId), new Integer(schClsMbrID));
          if (existing > 0) {
            throw new ResourceException("cacc.rsc_placement.this_resource_already_placed", "This placement already have this resource");
          }
        }
        
        // Get dates from the school placement
        IWTimestamp removed = null;
        IWTimestamp registered = null;
        SchoolClassMember schClsMbr = getSchoolClassMember(new Integer(schClsMbrID));
        if (schClsMbr != null) {
        	Timestamp remTS = schClsMbr.getRemovedDate();
        	if (remTS != null)
        		removed = new IWTimestamp(remTS.getTime());
        	if (removed != null)
        		removed.setAsDate();
      	
        	Timestamp regTS = schClsMbr.getRegisterDate();
        	if (regTS != null)
        		registered = new IWTimestamp(regTS);
        	if (registered != null)
        		registered.setAsDate();
        }
        
        // Check dates
        if (!startDateStr.equals("")) {
        	start= new IWTimestamp(startDateStr);
        	start.setAsDate();
        	if (!isCentralAdmin && start.isEarlierThan(today)) {
        		throw new DateException("cacc.rsc_placement.to_early_start_date", "Startdate can't be earlier than today");
        	} else {
        		startDate = start.getDate();
        	}
        } else {
        	throw new DateException("cacc.rsc_placement.must_enter_start_date", "You must chose a startdate");                
        }
        
        if (!endDateStr.equals("")) {
        	end = new IWTimestamp(endDateStr);
        	end.setAsDate();
        }

        // Check resource start date
        if (end != null && end.isEarlierThan(start)){
           throw new DateException("cacc.rsc_placement.enddate_earlier_than_startdate", "Enddate can't be earlier than startdate");            
        } else if (registered != null && start.isEarlierThan(registered)){
           throw new DateException("cacc.rsc_placement.startdate_earlier_than_registereddate", "Resource start date can't be earlier than the placements startdate");            
        // Check and set resource end date
        } else if (end != null) { 
        	if(removed != null && removed.isEarlierThan(end)) {
          		throw new DateException("cacc.rsc_placement.enddate_later_than_removeddate", "Resource enddate can't be later than school placements enddate");
          	}
          	endDate = end.getDate();
         } 
        
        // Get created date - rightNow
        java.util.Date rightNowDate = new java.util.Date();
        java.sql.Date rightNow = new java.sql.Date(rightNowDate.getTime());
        
        // Store       
        ResourceClassMember rscMemberBmp = rscClMbrHome.create();
        rscMemberBmp.setResourceFK(rscId);
        rscMemberBmp.setMemberFK(schClsMbrID);
        rscMemberBmp.setStartDate(startDate);
        if (endDate != null)
        	rscMemberBmp.setEndDate(endDate);
        if (registratorID != -1)
        	rscMemberBmp.setRegistratorId(registratorID);
        rscMemberBmp.setCreatedDate(rightNow);
        
        rscMemberBmp.store();
      }
      catch (javax.ejb.CreateException ce) {
        throw new java.rmi.RemoteException(ce.getMessage());
      }            
  } 

  /**
   * Sets end date of a resource placement
   * 
   * @param grpId The SchoolPlacements SchoolClassMemberID.
   * @param rscId The ResourceID. 
   * @param startDate Startdate of this ResourcePlacement
   * @param endDate Enddate of this ResourcePlacement
   */
  public void finishResourceClassMember(Integer schClsMbrID, Integer rscClsMbrId, String startDateStr, String endDateStr) throws FinderException, RemoteException, DateException, ClassMemberException {
    finishResourceClassMember(schClsMbrID, rscClsMbrId, startDateStr, endDateStr, false);
  }

  /**
   * Sets end date of a resource placement
   * 
   * @param grpId The SchoolPlacements SchoolClassMemberID.
   * @param rscId The ResourceID. 
   * @param startDate Startdate of this ResourcePlacement
   * @param endDate Enddate of this ResourcePlacement
   * @param isCentralAdmin user central admin or not
   */
  public void finishResourceClassMember(Integer schClsMbrID, Integer rscClsMbrId, String startDateStr, String endDateStr, boolean isCentralAdmin) throws FinderException, RemoteException, DateException, ClassMemberException {
    ResourceClassMemberHome mHome = (ResourceClassMemberHome) getIDOHome(ResourceClassMember.class);
    ResourceClassMember  mbr = mHome.findByPrimaryKey(rscClsMbrId);
    IWTimestamp today = IWTimestamp.RightNow();
    today.setAsDate();
    IWTimestamp start = new IWTimestamp(startDateStr);
    Date endDate;
    
    if (rscClsMbrId == null)
      throw new ClassMemberException("cacc.rsc_placement.no_member_placement_id", "No group placement found");

    // Check dates
    if (!endDateStr.equals("")) {
      IWTimestamp end = new IWTimestamp(endDateStr);
      end.setAsDate();

      IWTimestamp removed = null;
      IWTimestamp registered = null;
      SchoolClassMember schClsMbr = getSchoolClassMember(schClsMbrID);
      if (schClsMbr != null) {
      	Timestamp remTS = schClsMbr.getRemovedDate();
      	if (remTS != null)
      		removed = new IWTimestamp(remTS.getTime());
      	if (removed != null)
      		removed.setAsDate();

      	Timestamp regTS = schClsMbr.getRegisterDate();
      	if (regTS != null)
      		registered = new IWTimestamp(regTS.getTime());
      	if (registered != null)
      		registered.setAsDate();
      	
      }
           
      if (!isCentralAdmin && end.isEarlierThan(today)) {
        throw new DateException("cacc.rsc_placement.to_early_end_date", "Enddate can't be earlier than today");
      } else if (end.isEarlierThan(start)){
        throw new DateException("cacc.rsc_placement.enddate_earlier_than_startdate", "Enddate can't be earlier than startdate");            
      } else if (registered != null && end.isEarlierThan(registered)) {
      	throw new DateException("cacc.rsc_placement.enddate_earlier_than_registereddate", "Resource enddate can't be earlier than school placements  start date");
      } else if(removed != null && removed.isEarlierThan(end)) {
      	throw new DateException("cacc.rsc_placement.enddate_later_than_removeddate", "Resource enddate can't be later than school placements enddate");
      } else {
        endDate = end.getDate();
        mbr.setEndDate(endDate);
        mbr.store();
      }
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
   * Delete one ResourceClassMember (Resource placement) from a student
   * @param memberId The requested ResourcePermissions related Resourceid.
   */
  public void deleteResourceClassMember(Integer memberId) throws RemoteException, FinderException, RemoveException {
    ResourceClassMemberHome mHome = (ResourceClassMemberHome) getIDOHome(ResourceClassMember.class);
    ResourceClassMember rscMember = (ResourceClassMember) mHome.findByPrimaryKeyIDO(memberId);
    rscMember.remove();
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
  
  public Collection findAllSchoolCategories() {
  	Collection cats = new Vector();
  	try {
		cats = getSchoolCategoryHome().findAllCategories();
	} catch (FinderException e) {
		e.printStackTrace();
	}
	return cats;
  }
  
  public Collection findAllResourcesByCategory(String schCategoryID) {
	ResourceHome rscHome = null;
	Collection rscColl = new Vector();
  	try {
		rscHome = (ResourceHome) IDOLookup.getHome(Resource.class);
		rscColl = rscHome.findBySchCategory(schCategoryID);
	} catch (IDOLookupException e) {
		e.printStackTrace();
	} catch (FinderException fe) {

	}
	return rscColl;
  }
  
  /**
   * Get a String with the Resource names related to param placement
   * @param placement
   * @return
   */	
  public String getResourcesString(SchoolClassMember placement) {
	  Collection coll = getResourcePlacementsByMbrIdOrderByRscName((Integer) placement.getPrimaryKey());
	  StringBuffer buf = new StringBuffer("");
	  int i = 1;
	  for (Iterator iter = coll.iterator(); iter.hasNext();) {
			ResourceClassMember rscPl = (ResourceClassMember) iter.next();
			buf.append(rscPl.getResource().getResourceName());
			if (i < coll.size())
				buf.append(", ");			
			i++;					
	  }
				
	  return buf.toString();
  }
  
  /**
   * Get a String with the Resource names related to param placement
   * @param placement
   * @return
   */	
  public String getResourcesStringXtraInfo(SchoolClassMember placement) {
  	Collection coll = getResourcePlacementsByMbrIdOrderByRscName((Integer) placement.getPrimaryKey());
  	StringBuffer buf = new StringBuffer("");
  	int i = 1;
  	for (Iterator iter = coll.iterator(); iter.hasNext();) {
  		ResourceClassMember rscPl = (ResourceClassMember) iter.next();
  		int registratorID = rscPl.getRegistratorId();
  		User registrator = null;
  		String userName = null;
  		try {
			registrator = getUserBusiness().getUser(registratorID);
			if (registrator != null) {
				Name name = new Name(registrator.getFirstName(), registrator.getMiddleName(), registrator.getLastName());
				userName = name.getName(getIWMainApplication().getSettings().getDefaultLocale());
			}
		} catch (Exception e) {}	

  		// Add createdDate and registrator
		buf.append(rscPl.getResource().getResourceName());
  		java.sql.Date createdDate = rscPl.getCreatedDate();
  		if (createdDate != null) {
  			buf.append("(" + createdDate.toString());
  			if (userName != null)
  				buf.append("/" + userName);
  			buf.append(")");
  		}
  		if (i < coll.size())
  			buf.append(", ");			
  		i++;					
  	}
  	
  	return buf.toString();
  }

  
  public SchoolCategoryHome getSchoolCategoryHome() {
  	SchoolCategoryHome home = null;
  	try {
		home = (SchoolCategoryHome) IDOLookup.getHome(SchoolCategory .class);
	} catch (IDOLookupException e) {
		e.printStackTrace();
	}
  	return home;
  }
  
  public UserBusiness getUserBusiness() throws RemoteException{
  	return (UserBusiness) getServiceInstance(UserBusiness.class);
  }

}
