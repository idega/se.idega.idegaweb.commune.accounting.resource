package se.idega.idegaweb.commune.accounting.resource.data;


public interface ResourceClassMemberHome extends com.idega.data.IDOHome
{
 public ResourceClassMember create() throws javax.ejb.CreateException;
 public ResourceClassMember findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAllByClassMemberId(java.lang.Integer p0)throws javax.ejb.FinderException;
 public java.util.Collection findAllByClsMbrIdOrderByRscName(java.lang.Integer p0)throws javax.ejb.FinderException;
 public java.util.Collection findAllByRscIdAndMemberId(java.lang.Integer p0,java.lang.Integer p1)throws javax.ejb.FinderException;
 public java.util.Collection findByRscIdsAndSeasonId(int[] p0,int p1)throws javax.ejb.FinderException,com.idega.data.IDOLookupException,com.idega.data.IDOCompositePrimaryKeyException;
 public int countByRscIdAndMemberId(java.lang.Integer p0,java.lang.Integer p1)throws com.idega.data.IDOException;
 public int countByRscIdsAndUserId(int[] p0,int p1)throws com.idega.data.IDOException,com.idega.data.IDOLookupException,com.idega.data.IDOCompositePrimaryKeyException;
 public int countBySchoolTypeSeasonAndCommune(int p0,int p1,int p2)throws com.idega.data.IDOException,com.idega.data.IDOLookupException,com.idega.data.IDOCompositePrimaryKeyException;
 public int getCountOfResources(int p0,java.lang.String p1)throws com.idega.data.IDOException;

}