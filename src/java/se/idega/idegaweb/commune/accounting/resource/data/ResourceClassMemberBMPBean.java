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

import com.idega.block.school.data.School;
import com.idega.block.school.data.SchoolClass;
import com.idega.block.school.data.SchoolClassMember;
import com.idega.core.location.data.Address;
import com.idega.data.GenericEntity;
import com.idega.data.IDOCompositePrimaryKeyException;
import com.idega.data.IDOEntityDefinition;
import com.idega.data.IDOException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOQuery;
import com.idega.user.data.User;

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
  
  public int ejbHomeCountByRscIdsAndUserId(int[] rscIds, int userId) throws IDOException, IDOLookupException, IDOCompositePrimaryKeyException {
	IDOEntityDefinition cmDef = IDOLookup.getEntityDefinitionForClass(SchoolClassMember.class);
	String cmTableName = cmDef.getSQLTableName();
	String cmIdName = cmDef.getPrimaryKeyDefinition().getField().getSQLFieldName();
	IDOEntityDefinition scDef = IDOLookup.getEntityDefinitionForClass(SchoolClass.class);
	String scTableName = scDef.getSQLTableName();
	String scIdName = scDef.getPrimaryKeyDefinition().getField().getSQLFieldName();	
	Date today = new Date(System.currentTimeMillis());
	IDOQuery q = idoQuery();
	q.append("select count(distinct sc.sch_school_season_id) from " + getEntityName() + " rp, ")
	.append(cmTableName + " cm, ")
	.append(scTableName + " sc")
	.appendWhereEquals("rp." + MEMBER, "cm." + cmIdName)
	.appendAnd().append("cm.register_date").appendLessThanOrEqualsSign().appendWithinSingleQuotes(today) 
	.appendAndEquals("cm." + scIdName, "sc." + scIdName)
	.appendAnd().append("rp." + RESOURCE + " IN ").appendLeftParenthesis();
	for (int i = 0; i < rscIds.length; i++) {
		q.append(rscIds[i]);
		if (i < rscIds.length - 1) {
			q.append(",");
		}
	}
	q.appendRightParenthesis();
	q.appendAndEquals("cm.ic_user_id", userId);
	return super.idoGetNumberOfRecords(q);    
  }
  
  public int ejbHomeCountBySchoolTypeSeasonAndCommune(int schoolTypeId, int seasonId, int communeId) throws IDOException, IDOLookupException, IDOCompositePrimaryKeyException {
	IDOEntityDefinition cmDef = IDOLookup.getEntityDefinitionForClass(SchoolClassMember.class);
	String cmTableName = cmDef.getSQLTableName();
	String cmIdName = cmDef.getPrimaryKeyDefinition().getField().getSQLFieldName();

	IDOEntityDefinition scDef = IDOLookup.getEntityDefinitionForClass(SchoolClass.class);
	String scTableName = scDef.getSQLTableName();
	String scIdName = scDef.getPrimaryKeyDefinition().getField().getSQLFieldName();
	
	IDOEntityDefinition uDef = IDOLookup.getEntityDefinitionForClass(User.class);
	String uTableName = uDef.getSQLTableName();
	String uIdName = uDef.getPrimaryKeyDefinition().getField().getSQLFieldName();

	IDOEntityDefinition aDef = IDOLookup.getEntityDefinitionForClass(Address.class);
	String aTableName = aDef.getSQLTableName();
	String aIdName = aDef.getPrimaryKeyDefinition().getField().getSQLFieldName();
	
	String uaTableName = "ic_user_address";
	
	Date today = new Date(System.currentTimeMillis());
	IDOQuery q = idoQuery();
	q.append("select count(distinct u." + uIdName + ") from " + getEntityName() + " rp, ")
	.append(cmTableName + " cm, ")
	.append(scTableName + " sc, ")
	.append(uTableName + " u, ")
	.append(aTableName + " a, ")
	.append(uaTableName + " ua")
	.appendWhereEquals("rp." + MEMBER, "cm." + cmIdName)
	.appendAnd().append("cm.register_date").appendLessThanOrEqualsSign().appendWithinSingleQuotes(today) 
	.appendAnd().appendLeftParenthesis().append("cm.removed_date is null")
	.appendOr().append("cm.removed_date").appendGreaterThanSign().appendWithinSingleQuotes(today).appendRightParenthesis() 
	.appendAndEquals("cm." + scIdName, "sc." + scIdName)
	.appendAndEquals("sc.sch_school_season_id", seasonId)
	.appendAndEquals("cm.sch_school_type_id", schoolTypeId)
	.appendAndEquals("cm.ic_user_id", "u." + uIdName)
	.appendAndEquals("u." + uIdName, "ua.ic_user_id")
	.appendAndEquals("au.ic_address_id", "a." + aIdName)
	.appendAndEquals("a.ic_commune_id", communeId)
	.appendAnd().appendLeftParenthesis().append("rp." + ENDDATE + " is null")
	.appendOr().append("rp." + ENDDATE).appendGreaterThanSign().appendWithinSingleQuotes(today).appendRightParenthesis();
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

  public Collection ejbFindByRscIdsAndSeasonId(int[] rscIds, int seasonId) throws FinderException, IDOLookupException, IDOCompositePrimaryKeyException {
	IDOEntityDefinition cmDef = IDOLookup.getEntityDefinitionForClass(SchoolClassMember.class);
	String cmTableName = cmDef.getSQLTableName();
	String cmIdName = cmDef.getPrimaryKeyDefinition().getField().getSQLFieldName();
	IDOEntityDefinition scDef = IDOLookup.getEntityDefinitionForClass(SchoolClass.class);
	String scTableName = scDef.getSQLTableName();
	String scIdName = scDef.getPrimaryKeyDefinition().getField().getSQLFieldName();	
	IDOEntityDefinition sDef = IDOLookup.getEntityDefinitionForClass(School.class);
	String sTableName = sDef.getSQLTableName();
	String sIdName = sDef.getPrimaryKeyDefinition().getField().getSQLFieldName();
	Date today = new Date(System.currentTimeMillis());
	IDOQuery q = idoQuery();
	q.append("select rp.* from " + getEntityName() + " rp, ")
	.append(cmTableName + " cm, ")
	.append(scTableName + " sc, ")
	.append(sTableName + " s")
	.appendWhereEquals("rp." + MEMBER, "cm." + cmIdName)
	.appendAnd().append("cm.register_date").appendLessThanOrEqualsSign().appendWithinSingleQuotes(today) 
	.appendAnd().appendLeftParenthesis().append("cm.removed_date is null")
	.appendOr().append("cm.removed_date").appendGreaterThanSign().appendWithinSingleQuotes(today).appendRightParenthesis() 
	.appendAndEquals("cm." + scIdName, "sc." + scIdName)
	.appendAnd().append("rp." + RESOURCE + " IN ").appendLeftParenthesis();
	for (int i = 0; i < rscIds.length; i++) {
		q.append(rscIds[i]);
		if (i < rscIds.length - 1) {
			q.append(",");
		}
	}
	q.appendRightParenthesis();
	q.appendAndEquals("sc.school_id", "s." + sIdName)
	.appendAndEquals("sc.sch_school_season_id", seasonId)
	.appendAnd().appendLeftParenthesis().append("rp." + ENDDATE + " is null")
	.appendOr().append("rp." + ENDDATE).appendGreaterThanSign().appendWithinSingleQuotes(today).appendRightParenthesis()
	.appendOrderBy("s.school_name");
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
