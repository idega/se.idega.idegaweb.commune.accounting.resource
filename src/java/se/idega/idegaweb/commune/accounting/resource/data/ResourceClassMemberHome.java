package se.idega.idegaweb.commune.accounting.resource.data;


public interface ResourceClassMemberHome extends com.idega.data.IDOHome
{
 public ResourceClassMember create() throws javax.ejb.CreateException;
 public ResourceClassMember findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAllByClassMemberId(java.lang.Integer p0)throws javax.ejb.FinderException;
 public java.util.Collection findAllByRscIdAndMemberId(java.lang.Integer p0,java.lang.Integer p1)throws javax.ejb.FinderException;
 public int countByRscIdAndMemberId(java.lang.Integer p0,java.lang.Integer p1)throws com.idega.data.IDOException;

}