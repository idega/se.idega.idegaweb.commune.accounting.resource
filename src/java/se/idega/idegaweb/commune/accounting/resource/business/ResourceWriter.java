/*
 * $Id: ResourceWriter.java,v 1.1 2004/03/16 13:53:57 anders Exp $
 *
 * Copyright (C) 2003 Agura IT. All Rights Reserved.
 *
 * This software is the proprietary information of Agura IT AB.
 * Use is subject to license terms.
 *
 */
package se.idega.idegaweb.commune.accounting.resource.business;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.FinderException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import se.idega.idegaweb.commune.accounting.presentation.AccountingBlock;
import se.idega.idegaweb.commune.accounting.resource.data.ResourceClassMember;
import se.idega.idegaweb.commune.accounting.resource.data.ResourceClassMemberHome;
import se.idega.idegaweb.commune.business.CommuneUserBusiness;

import com.idega.block.school.business.SchoolBusiness;
import com.idega.block.school.data.School;
import com.idega.block.school.data.SchoolClass;
import com.idega.block.school.data.SchoolClassMember;
import com.idega.block.school.data.SchoolSeason;
import com.idega.block.school.data.SchoolYear;
import com.idega.business.IBOLookup;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileBMPBean;
import com.idega.core.file.data.ICFileHome;
import com.idega.core.localisation.data.ICLanguage;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.Commune;
import com.idega.core.location.data.CommuneHome;
import com.idega.core.location.data.PostalCode;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.io.MemoryFileBuffer;
import com.idega.io.MemoryInputStream;
import com.idega.io.MemoryOutputStream;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.user.data.User;
import com.idega.util.PersonalIDFormatter;

/** 
 * Exports files with information connected to resources.
 * <p>
 * Last modified: $Date: 2004/03/16 13:53:57 $ by $Author: anders $
 *
 * @author Anders Lindman
 * @version $Revision: 1.1 $
 */
public class ResourceWriter {

	private final static String EXPORT_FOLDER_NAME = "School Export Files";

	private final int RESOURCE_ID_NATIVE_LANGUAGE_1 = 30;
	private final int RESOURCE_ID_NATIVE_LANGUAGE_2 = 31;

	Table _table = null;
	String _filename = null;
	
	/**
	 * Constructs a resource writer object.
	 */	
	public ResourceWriter(String filename) {
		_filename = filename;
	}	
	
	/**
	 * Creates a resource export file.
	 */
	public ICFile createFile(IWContext iwc) {
		ICFile reportFolder = null;
		ICFileHome fileHome = null;

		try {
			fileHome = (ICFileHome) com.idega.data.IDOLookup.getHome(ICFile.class);
			reportFolder = fileHome.findByFileName(EXPORT_FOLDER_NAME);
		} catch (FinderException e) {
			try {
				ICFile root = fileHome.findByFileName(ICFileBMPBean.IC_ROOT_FOLDER_NAME);
				reportFolder = fileHome.create();
				reportFolder.setName(EXPORT_FOLDER_NAME);
				reportFolder.setMimeType("application/vnd.iw-folder");
				reportFolder.store();
				root.addChild(reportFolder);
			} catch (Exception e2) {
				System.out.println(e2);
				return null;
			}
		} catch (IDOLookupException e) {
			System.out.println(e);
			return null;
		}

		ICFile exportFile = null;
				
		try {
			MemoryFileBuffer buffer = getNativeLanguageBuffer(iwc);
			MemoryInputStream mis = new MemoryInputStream(buffer);
			try {
				exportFile = fileHome.findByFileName(_filename);
				if (exportFile != null) {
					exportFile.remove();
				}
			} catch (FinderException e) {}

			exportFile = fileHome.create();
			exportFile.setFileValue(mis);
			exportFile.setMimeType("application/vnd.ms-excel");
			exportFile.setName(_filename);
			exportFile.setFileSize(buffer.length());
			exportFile.store();

			reportFolder.addChild(exportFile);
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return exportFile;
	}

	public MemoryFileBuffer getNativeLanguageBuffer(IWContext iwc) {
		MemoryFileBuffer buffer = null;
		try {
			IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(AccountingBlock.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc.getCurrentLocale());

			SchoolBusiness sb = getSchoolBusiness(iwc);
			SchoolSeason season = sb.getCurrentSchoolSeason();
			int seasonId = ((Integer) season.getPrimaryKey()).intValue();
			
			CommuneHome communeHome = (CommuneHome) IDOLookup.getHome(Commune.class);
			Commune homeCommune = communeHome.findDefaultCommune();
			int homeCommuneId = ((Integer) homeCommune.getPrimaryKey()).intValue();
			
			ResourceClassMemberHome rcmHome = (ResourceClassMemberHome) IDOLookup.getHome(ResourceClassMember.class);
			int[] resourceIds = new int[2];
			resourceIds[0] = RESOURCE_ID_NATIVE_LANGUAGE_1;
			resourceIds[1] = RESOURCE_ID_NATIVE_LANGUAGE_2;
			Collection resourceMembers = rcmHome.findByRscIdsAndSeasonId(resourceIds, seasonId);
			
			buffer = new MemoryFileBuffer();
			MemoryOutputStream mos = new MemoryOutputStream(buffer);
	
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet(_filename);
			int cellColumn = 0;

			sheet.setColumnWidth((short)cellColumn++, (short) (16 * 256));
			sheet.setColumnWidth((short)cellColumn++, (short) (30 * 256));
			sheet.setColumnWidth((short)cellColumn++, (short) (24 * 256));
			sheet.setColumnWidth((short)cellColumn++, (short) (24 * 256));
			sheet.setColumnWidth((short)cellColumn++, (short) (16 * 256));
			sheet.setColumnWidth((short)cellColumn++, (short) (28 * 256));
			sheet.setColumnWidth((short)cellColumn++, (short) (28 * 256));
			sheet.setColumnWidth((short)cellColumn++, (short) (14 * 256));
			sheet.setColumnWidth((short)cellColumn++, (short) (14 * 256));
			sheet.setColumnWidth((short)cellColumn++, (short) (24 * 256));
			sheet.setColumnWidth((short)cellColumn++, (short) (14 * 256));
			HSSFFont font = wb.createFont();
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			font.setFontHeightInPoints((short)12);
			HSSFCellStyle style = wb.createCellStyle();
			style.setFont(font);
	
			cellColumn = 0;
			int cellRow = 0;			
			
			HSSFRow row = sheet.createRow((short)cellRow++);			
			HSSFCell cell = row.createCell((short)cellColumn++);
			cell.setCellValue(iwrb.getLocalizedString("resource.personal_id", "Personal ID"));
			cell.setCellStyle(style);
			cell = row.createCell((short)cellColumn++);
			cell.setCellValue(iwrb.getLocalizedString("resource.name", "Name"));
			cell.setCellStyle(style);
			cell = row.createCell((short)cellColumn++);
			cell.setCellValue(iwrb.getLocalizedString("resource.street_address", "Street Address"));
			cell.setCellStyle(style);
			cell = row.createCell((short)cellColumn++);
			cell.setCellValue(iwrb.getLocalizedString("resource.postal_address", "Postal Address"));
			cell.setCellStyle(style);
			cell = row.createCell((short)cellColumn++);
			cell.setCellValue(iwrb.getLocalizedString("resource.phone", "Phone"));
			cell.setCellStyle(style);
			cell = row.createCell((short)cellColumn++);
			cell.setCellValue(iwrb.getLocalizedString("resource.custodian_email", "Custodian's e-mail"));
			cell.setCellStyle(style);
			cell = row.createCell((short)cellColumn++);
			cell.setCellValue(iwrb.getLocalizedString("resource.school", "Skola"));
			cell.setCellStyle(style);
			cell = row.createCell((short)cellColumn++);
			cell.setCellValue(iwrb.getLocalizedString("resource.school_year", "School Year"));
			cell.setCellStyle(style);
			cell = row.createCell((short)cellColumn++);
			cell.setCellValue(iwrb.getLocalizedString("resource.school_class", "School Class"));
			cell.setCellStyle(style);
			cell = row.createCell((short)cellColumn++);
			cell.setCellValue(iwrb.getLocalizedString("resource.native_language", "Native Language"));
			cell.setCellStyle(style);
			cell = row.createCell((short)cellColumn++);
			cell.setCellValue(iwrb.getLocalizedString("resource.nrof_native_language_years", "Number of native language years"));
			cell.setCellStyle(style);
	
			Iterator iter = resourceMembers.iterator();
			while (iter.hasNext()) {
				cellColumn = 0;
				row = sheet.createRow((short)cellRow++);
				
				ResourceClassMember resourceMember = (ResourceClassMember) iter.next();
				SchoolClassMember placement = resourceMember.getSchoolClassMember();
				SchoolYear schoolYear = placement.getSchoolYear();
				User student = placement.getStudent();
				SchoolClass schoolClass = placement.getSchoolClass();
				School school = schoolClass.getSchool();
				Address address = getCommuneUserBusiness(iwc).getUsersMainAddress(student);
				PostalCode postalCode = null;
				int communeId = -1;
				if (address != null) {
					postalCode = address.getPostalCode();
					communeId = address.getCommuneID();
				}
				if (communeId != homeCommuneId) {
					continue;
				}
				Phone phone = getCommuneUserBusiness(iwc).getChildHomePhone(student);
				User custodian = getCommuneUserBusiness(iwc).getCustodianForChild(student);
				ICLanguage nativeLanguage = student.getNativeLanguage();
	
				row.createCell((short)cellColumn++).setCellValue(PersonalIDFormatter.format(student.getPersonalID(), iwc.getCurrentLocale()));
				row.createCell((short)cellColumn++).setCellValue(student.getNameLastFirst(true));
	
				if (address != null) {
					row.createCell((short)cellColumn++).setCellValue(address.getStreetAddress());
					if (postalCode != null) {
						row.createCell((short)cellColumn++).setCellValue(postalCode.getPostalAddress());
					} else {
						cellColumn++;
					}
				} else {
					cellColumn += 2;
				}

				if (phone != null) {
					row.createCell((short)cellColumn++).setCellValue(phone.getNumber());
				} else {
					cellColumn++;
				}
				
				if (custodian != null) {
					Email email = getCommuneUserBusiness(iwc).getEmail(custodian);
					if (email != null) {
						row.createCell((short)cellColumn++).setCellValue(email.getEmailAddress());						
					} else {
						cellColumn++;
					}
				} else {
					cellColumn++;
				}

				row.createCell((short)cellColumn++).setCellValue(school.getSchoolName());
				
				row.createCell((short)cellColumn++).setCellValue(schoolYear.getName());

				row.createCell((short)cellColumn++).setCellValue(schoolClass.getName());

				row.createCell((short)cellColumn++).setCellValue(nativeLanguage.getName());

				int userId = ((Integer) student.getPrimaryKey()).intValue(); 
				int nrOfYears = rcmHome.countByRscIdsAndUserId(resourceIds, userId);
				row.createCell((short)cellColumn++).setCellValue(nrOfYears);
			}
			
			wb.write(mos);
			java.io.FileOutputStream out = new java.io.FileOutputStream("/Users/al/test.xls");
			wb.write(out);
			out.close();
			buffer.setMimeType("application/vnd.ms-excel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer;
	}

	protected CommuneUserBusiness getCommuneUserBusiness(IWApplicationContext iwc) throws RemoteException {
		return (CommuneUserBusiness) IBOLookup.getServiceInstance(iwc, CommuneUserBusiness.class);	
	}

	protected SchoolBusiness getSchoolBusiness(IWApplicationContext iwc) throws RemoteException {
		return (SchoolBusiness) IBOLookup.getServiceInstance(iwc, SchoolBusiness.class);	
	}
}
