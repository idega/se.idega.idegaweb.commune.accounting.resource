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
import javax.ejb.RemoveException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOException;
import com.idega.data.IDOQuery;
import com.idega.block.school.data.SchoolClassMember;

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
  
  public String getEntityName() {
    return TABLE_NAME;
  }
  
  public void initializeAttributes() {
    this.addAttribute(getIDColumnName());
    this.addManyToOneRelationship(RESOURCE, Resource.class);
    this.addManyToOneRelationship(MEMBER, SchoolClassMember.class);
    this.addAttribute(STARTDATE, "Startdate for resourceperiod", true, true, Date.class);
    this.addAttribute(ENDDATE, "Enddate for resourceperiod", true, true, Date.class);    
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
  
  public Collection ejbFindAllByClassMemberId(Integer memberId) throws FinderException {
    IDOQuery q = idoQueryGetSelect();
    q.appendWhereEquals(MEMBER, memberId);
    return super.idoFindPKsByQuery(q);    
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

}
