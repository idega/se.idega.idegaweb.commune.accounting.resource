/*
 * Created on 2003-sep-08
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package se.idega.idegaweb.commune.accounting.resource.data;

import java.sql.Date;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.block.school.data.SchoolClassMember;
import com.idega.data.GenericEntity;
import com.idega.data.IDOException;
import com.idega.data.IDOQuery;

/**
 * @author wmgobom
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ResourceClassMemberBMPBean extends GenericEntity implements ResourceClassMember {
  
  private final static String TABLE_NAME = "CACC_RESOURCE_CLASS_MEMBER";
  private static final String RESOURCE = "cacc_resource_id";
  private static final String MEMBER = "sch_class_member_id";
  private static final String STARTDATE = "startdate";
  private static final String ENDDATE = "enddate";
  private static final String REGISTRATOR = "registrator";
  private static final String CREATEDDATE = "created_date";
  
  public String getEntityName() {
    return TABLE_NAME;
  }
  
  public void initializeAttributes() {
    this.addAttribute(getIDColumnName());
    this.addManyToOneRelationship(RESOURCE, Resource.class);
    this.addManyToOneRelationship(MEMBER, SchoolClassMember.class);
    this.addAttribute(STARTDATE, "Startdate for resourceperiod", true, true, Date.class);
    this.addAttribute(ENDDATE, "Enddate for resourceperiod", true, true, Date.class);    
    this.addAttribute(REGISTRATOR, "registrator", true, true, Integer.class, MANY_TO_ONE, com.idega.core.user.data.User.class);
    this.addAttribute(CREATEDDATE, "Date for resource placement creation", true, true, Date.class);    
  }
  
  public Collection ejbFindAllByRscIdAndMemberId(Integer rscId, Integer mbrId) throws FinderException {
    IDOQuery q = idoQueryGetSelect();
    q.appendWhereEquals(RESOURCE, rscId);
    q.appendAndEquals(MEMBER, mbrId);
    return super.idoFindPKsByQuery(q);    
  }
  
  public int ejbHomeCountByRscIdAndMemberId(Integer rscId, Integer mbrId) throws IDOException {
    IDOQuery q = idoQuery();
    q.append("select count(*) from " + getEntityName());
    q.appendWhereEquals(RESOURCE, rscId);
    q.appendAndEquals(MEMBER, mbrId);
    return super.idoGetNumberOfRecords(q);    
  }
  
  /**
   * Finds all ResourceClassMembers for a SchoolClassMember
   * @param memberId
   * @return
   * @throws FinderException
   */
  public Collection ejbFindAllByClassMemberId(Integer schClassMemberId) throws FinderException {
    IDOQuery q = idoQueryGetSelect();
    q.appendWhereEquals(MEMBER, schClassMemberId);
    return super.idoFindPKsByQuery(q);    
  }

  public Collection ejbFindAllByClsMbrIdOrderByRscName(Integer memberId) throws FinderException {
	IDOQuery q = idoQuery();
	q.append("select rp.* from " + getEntityName() + " rp, ")
	.append(ResourceBMPBean.TABLE_NAME + " r")
	.appendWhereEquals("rp." + MEMBER, memberId)
	.appendAndEquals("rp." + RESOURCE, "r.cacc_resource_id")
	.appendOrderBy("r." + ResourceBMPBean.NAME);
	return super.idoFindPKsByQuery(q);    
  }
  
  public int ejbHomeGetCountOfResources(int schoolClassMemberID, String resourceIDs) throws IDOException {
  	IDOQuery q = this.idoQueryGetSelectCount();
  	q.appendWhereEquals(MEMBER, schoolClassMemberID);
  	q.appendAnd().append(RESOURCE).appendIn(resourceIDs);
  	return idoGetNumberOfRecords(q);
  }
  
  public Resource getResource() {
	  return (Resource) getColumnValue(RESOURCE);
  }

  public SchoolClassMember getSchoolClassMember() {
	  return (SchoolClassMember) getColumnValue(MEMBER);
  } 
    
  public void setResourceFK(int rscId) {
    this.setColumn(RESOURCE, rscId);
  }
  
  public int getResourceFK() {
    return this.getIntColumnValue(RESOURCE);
  }
  
  /**
   *  Sets foreign key to SchoolClassMember the childs SchoolPlacement
   */
  public void setMemberFK(int memId) {
    this.setColumn(MEMBER, memId);
  }

  /**
   *  Gets foreign key to SchoolClassMember the childs SchoolPlacement
   */  
  public int getMemberFK() {
    return this.getIntColumnValue(MEMBER);
  }
  
  public void setStartDate(java.util.Date start) {
    this.setColumn(STARTDATE, start);
  }
  
  public Date getStartDate() {
    return this.getDateColumnValue(STARTDATE);
  }
  
  public void setEndDate(java.util.Date end) {
    setColumn(ENDDATE, end);
  }
  
  public Date getEndDate() {
    return getDateColumnValue(ENDDATE);
  }
  public void setRegistratorId(int id) {
  	setColumn(REGISTRATOR, id);
  }
  public int getRegistratorId() {
  	return getIntColumnValue(REGISTRATOR);
  }
  
  public void setCreatedDate(java.util.Date end) {
  	setColumn(CREATEDDATE, end);
  }
  
  public Date getCreatedDate() {
  	return getDateColumnValue(CREATEDDATE);
  }
  
}
