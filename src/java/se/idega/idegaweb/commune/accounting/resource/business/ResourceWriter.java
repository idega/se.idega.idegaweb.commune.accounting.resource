/*
 * $Id: ResourceWriter.java,v 1.16 2004/10/07 15:23:57 thomas Exp $
 *
 * Copyright (C) 2003 Agura IT. All Rights Reserved.
 *
 * This software is the proprietary information of Agura IT AB.
 * Use is subject to license terms.
 *
 */
package se.idega.idegaweb.commune.accounting.resource.business;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.FinderException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import se.idega.idegaweb.commune.accounting.extra.data.Resource;
import se.idega.idegaweb.commune.accounting.extra.data.ResourceClassMember;
import se.idega.idegaweb.commune.accounting.extra.data.ResourceClassMemberHome;
import se.idega.idegaweb.commune.accounting.extra.data.ResourceHome;
import se.idega.idegaweb.commune.accounting.presentation.AccountingBlock;
import se.idega.idegaweb.commune.business.CommuneUserBusiness;
import se.idega.idegaweb.commune.school.data.CurrentSchoolSeason;
import se.idega.idegaweb.commune.school.data.CurrentSchoolSeasonHome;
import se.idega.idegaweb.commune.school.data.SchoolChoice;
import se.idega.idegaweb.commune.school.data.SchoolChoiceHome;

import com.idega.block.school.business.SchoolBusiness;
import com.idega.block.school.data.School;
import com.idega.block.school.data.SchoolClass;
import com.idega.block.school.data.SchoolClassMember;
import com.idega.block.school.data.SchoolManagementType;
import com.idega.block.school.data.SchoolManagementTypeHome;
import com.idega.block.school.data.SchoolSeason;
import com.idega.block.school.data.SchoolType;
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
 * Last modified: $Date: 2004/10/07 15:23:57 $ by $Author: thomas $
 *
 * @author Anders Lindman
 * @version $Revision: 1.16 $
 */
public class ResourceWriter {

	private final static String EXPORT_FOLDER_NAME = "Export Files";

	private final static int RESOURCE_ID_NATIVE_LANGUAGE_1 = 30;
	private final static int RESOURCE_ID_NATIVE_LANGUAGE_2 = 31;
	
	public final static int TYPE_NATIVE_LANGUAGE_CHOICE_LIST = 1;
	public final static int TYPE_NATIVE_LANGUAGE_PLACEMENT_LIST = 2;
	public final static int TYPE_MANAGEMENT_TYPE_RESOURCE_LIST = 3;

	Table _table = null;
	String _filename = null;
	HSSFCellStyle _headerStyle = null;
	
	/**
	 * Constructs a resource writer object.
	 */	
	public ResourceWriter(String filename) {
		_filename = filename;
	}	
	
	/**
	 * Creates a resource export file.
	 * @param isSchoolChoice true if school choice file, false if placement file
	 */
	public ICFile createFile(IWContext iwc, boolean isSchoolChoice) {
		if (isSchoolChoice) {
			return createFile(iwc, TYPE_NATIVE_LANGUAGE_CHOICE_LIST);
		} else {
			return createFile(iwc, TYPE_NATIVE_LANGUAGE_PLACEMENT_LIST);
		}
	}
	
	/**
	 * Creates a resource export file according to the specified list type.
	 */
	public ICFile createFile(IWContext iwc, int listType) {
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
			MemoryFileBuffer buffer = null;
			switch (listType) {
				case TYPE_NATIVE_LANGUAGE_CHOICE_LIST:
					buffer = getNativeLanguageSchoolChoiceBuffer(iwc);
					break;
				case TYPE_NATIVE_LANGUAGE_PLACEMENT_LIST:
					buffer = getNativeLanguageBuffer(iwc);
					break;
				case TYPE_MANAGEMENT_TYPE_RESOURCE_LIST:
					buffer = getManagementTypeResourceBuffer(iwc);
					break;
			}
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

	/*
	 * Returns XLS buffer with native language list. 
	 */
	private MemoryFileBuffer getNativeLanguageBuffer(IWContext iwc) {
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
			
			HSSFRow row = sheet.createRow(cellRow++);			
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
			cell.setCellValue(iwrb.getLocalizedString("resource.school", "School"));
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
				
				ResourceClassMember resourceMember = (ResourceClassMember) iter.next();
				SchoolClassMember placement = resourceMember.getSchoolClassMember();
				SchoolYear schoolYear = placement.getSchoolYear();
				User student = placement.getStudent();
				if (student == null) {
					continue;
				}
				String studentName = student.getLastName() + " " + student.getFirstName();
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
	
				row = sheet.createRow(cellRow++);
				
				row.createCell((short)cellColumn++).setCellValue(PersonalIDFormatter.format(student.getPersonalID(), iwc.getCurrentLocale()));
				row.createCell((short)cellColumn++).setCellValue(studentName);
	
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

				if (nativeLanguage != null) {
					row.createCell((short)cellColumn++).setCellValue(nativeLanguage.getName());
				} else {
					cellColumn++;
				}

				int userId = ((Integer) student.getPrimaryKey()).intValue(); 
				int nrOfYears = rcmHome.countByRscIdsAndUserId(resourceIds, userId);
				row.createCell((short)cellColumn++).setCellValue(nrOfYears);
			}
			
			wb.write(mos);
			buffer.setMimeType("application/vnd.ms-excel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/*
	 * Returns XLS buffer with native language school choice list. 
	 */
	private MemoryFileBuffer getNativeLanguageSchoolChoiceBuffer(IWContext iwc) {
		MemoryFileBuffer buffer = null;
		try {
			IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(AccountingBlock.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc.getCurrentLocale());

			SchoolBusiness sb = getSchoolBusiness(iwc);
			SchoolSeason currentSeason = sb.getCurrentSchoolSeason();
			CurrentSchoolSeasonHome seasonHome = (CurrentSchoolSeasonHome) IDOLookup.getHome(CurrentSchoolSeason.class);
			CurrentSchoolSeason season = seasonHome.findCurrentSeason();
			int currentSchoolChoiceSeasonId = season.getCurrent().intValue();
			
			CommuneHome communeHome = (CommuneHome) IDOLookup.getHome(Commune.class);
			Commune homeCommune = communeHome.findDefaultCommune();
			int homeCommuneId = ((Integer) homeCommune.getPrimaryKey()).intValue();
			
			SchoolChoiceHome scHome = (SchoolChoiceHome) IDOLookup.getHome(SchoolChoice.class);
			Collection schoolChoices = scHome.findAllPlacedBySeason(currentSchoolChoiceSeasonId);
			
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
			sheet.setColumnWidth((short)cellColumn++, (short) (24 * 256));
			sheet.setColumnWidth((short)cellColumn++, (short) (14 * 256));
			sheet.setColumnWidth((short)cellColumn++, (short) (14 * 256));
			sheet.setColumnWidth((short)cellColumn++, (short) (24 * 256));
			sheet.setColumnWidth((short)cellColumn++, (short) (14 * 256));
			sheet.setColumnWidth((short)cellColumn++, (short) (14 * 256));
			sheet.setColumnWidth((short)cellColumn++, (short) (24 * 256));
			HSSFFont font = wb.createFont();
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			font.setFontHeightInPoints((short)12);
			HSSFCellStyle style = wb.createCellStyle();
			style.setFont(font);
	
			cellColumn = 0;
			int cellRow = 0;			
			
			HSSFRow row = sheet.createRow(cellRow++);			
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
			cell.setCellValue(iwrb.getLocalizedString("resource.school_current_season", "School current season"));
			cell.setCellStyle(style);
			cell = row.createCell((short)cellColumn++);
			cell.setCellValue(iwrb.getLocalizedString("resource.school_year", "School Year"));
			cell.setCellStyle(style);
			cell = row.createCell((short)cellColumn++);
			cell.setCellValue(iwrb.getLocalizedString("resource.school_class", "School Class"));
			cell.setCellStyle(style);
			cell = row.createCell((short)cellColumn++);
			cell.setCellValue(iwrb.getLocalizedString("resource.school_next_season", "School next season"));
			cell.setCellStyle(style);
			cell = row.createCell((short)cellColumn++);
			cell.setCellValue(iwrb.getLocalizedString("resource.school_year", "School Year"));
			cell.setCellStyle(style);
			cell = row.createCell((short)cellColumn++);
			cell.setCellValue(iwrb.getLocalizedString("resource.school_class", "School Class"));
			cell.setCellStyle(style);
			cell = row.createCell((short)cellColumn++);
			cell.setCellValue(iwrb.getLocalizedString("resource.native_language_choice", "Native Language choice"));
			cell.setCellStyle(style);
	
			Iterator iter = schoolChoices.iterator();
			while (iter.hasNext()) {
				cellColumn = 0;
				
				SchoolChoice schoolChoice = (SchoolChoice) iter.next();
				User student = schoolChoice.getChild();
				if (student == null) {
					continue;
				}
				ICLanguage nativeLanguage = student.getNativeLanguage();
				if (nativeLanguage == null) {
					// List only school choices with native language
					continue;
				}
				int studentId = ((Integer) student.getPrimaryKey()).intValue();

				String studentName = student.getLastName() + " " + student.getFirstName();
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
				
				String currentSchoolYearName = "";
				String currentSchoolClassName = "";
				String currentSchoolName = "";
				try {
					SchoolClassMember currentPlacement = getSchoolBusiness(iwc).findByStudentAndSeason(student, currentSeason);
					SchoolYear currentSchoolYear = currentPlacement.getSchoolYear();
					currentSchoolYearName = currentSchoolYear.getName();
					SchoolClass currentSchoolClass = currentPlacement.getSchoolClass();
					currentSchoolClassName = currentSchoolClass.getName();
					School currentSchool = currentSchoolClass.getSchool();
					currentSchoolName = currentSchool.getName();
				} catch (Exception e) {
					// No current placement, list blank cells
				}

				String nextSchoolYearName = "";
				String nextSchoolClassName = "";
				String nextSchoolName = "";
				try {
					SchoolClassMember nextPlacement = getSchoolBusiness(iwc).findByStudentAndSeason(studentId, currentSchoolChoiceSeasonId);
					SchoolYear nextSchoolYear = nextPlacement.getSchoolYear();
					nextSchoolYearName = nextSchoolYear.getName();
					SchoolClass nextSchoolClass = nextPlacement.getSchoolClass();
					nextSchoolClassName = nextSchoolClass.getName();
					School nextSchool = nextSchoolClass.getSchool();
					nextSchoolName = nextSchool.getName();
				} catch (Exception e) {
					// No next placement, list blank cells					
				}

				Phone phone = getCommuneUserBusiness(iwc).getChildHomePhone(student);
				User custodian = getCommuneUserBusiness(iwc).getCustodianForChild(student);
	
				row = sheet.createRow(cellRow++);
				
				row.createCell((short)cellColumn++).setCellValue(PersonalIDFormatter.format(student.getPersonalID(), iwc.getCurrentLocale()));
				row.createCell((short)cellColumn++).setCellValue(studentName);
	
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

				row.createCell((short)cellColumn++).setCellValue(currentSchoolName);
				
				row.createCell((short)cellColumn++).setCellValue(currentSchoolYearName);

				row.createCell((short)cellColumn++).setCellValue(currentSchoolClassName);

				row.createCell((short)cellColumn++).setCellValue(nextSchoolName);
				
				row.createCell((short)cellColumn++).setCellValue(nextSchoolYearName);

				row.createCell((short)cellColumn++).setCellValue(nextSchoolClassName);

				row.createCell((short)cellColumn++).setCellValue(nativeLanguage.getName());
			}
			
			wb.write(mos);
			buffer.setMimeType("application/vnd.ms-excel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/*
	 * Returns XLS buffer with management type resource list. 
	 */
	private MemoryFileBuffer getManagementTypeResourceBuffer(IWContext iwc) {
		MemoryFileBuffer buffer = null;
		try {
			IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(AccountingBlock.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc.getCurrentLocale());

			SchoolBusiness sb = getSchoolBusiness(iwc);
			String[] categories = new String[2];
			categories[0] = sb.getElementarySchoolSchoolCategory();
			categories[1] = sb.getHighSchoolSchoolCategory();
			
			SchoolManagementTypeHome smtHome = (SchoolManagementTypeHome) IDOLookup.getHome(SchoolManagementType.class);
			Collection managementTypes = smtHome.findManagementTypesByCategories(categories);
			
			SchoolSeason season = sb.getCurrentSchoolSeason();
			int seasonId = ((Integer) season.getPrimaryKey()).intValue();
			
			CommuneHome communeHome = (CommuneHome) IDOLookup.getHome(Commune.class);
			Commune homeCommune = communeHome.findDefaultCommune();
			int homeCommuneId = ((Integer) homeCommune.getPrimaryKey()).intValue();

			buffer = new MemoryFileBuffer();
			MemoryOutputStream mos = new MemoryOutputStream(buffer);
	
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet(_filename);

			sheet.setColumnWidth((short) 0, (short) (30 * 256));
			sheet.setColumnWidth((short) 1, (short) (20 * 256));
			sheet.setColumnWidth((short) 2, (short) (10 * 256));
			
			HSSFFont font = wb.createFont();
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			font.setFontHeightInPoints((short)12);
			_headerStyle = wb.createCellStyle();
			_headerStyle.setFont(font);

			Collection schoolTypes = new ArrayList();
			schoolTypes.addAll(sb.findAllSchoolTypesInCategory(categories[0], false));
			schoolTypes.addAll(sb.findAllSchoolTypesInCategory(categories[1], false));

			Iterator iter = managementTypes.iterator();
			int cellRow = 0;
			while (iter.hasNext()) {
				SchoolManagementType managementType = (SchoolManagementType) iter.next();
				cellRow = listResources(sheet, iwrb, managementType, schoolTypes, seasonId, homeCommuneId, cellRow); 
			}
			
			wb.write(mos);
			buffer.setMimeType("application/vnd.ms-excel");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer;			
	}
	
	private int listResources(
				HSSFSheet sheet,
				IWResourceBundle iwrb,
				SchoolManagementType managementType,
				Collection schoolTypes,
				int seasonId,
				int homeCommuneId,
				int cellRow) throws Exception {
		
		
		HSSFRow row = sheet.createRow(cellRow++);			
		HSSFCell cell = row.createCell((short) 0);
		cell.setCellValue(iwrb.getLocalizedString("resource.management_type", "Management type") + ": " +
				iwrb.getLocalizedString(managementType.getLocalizedKey(), managementType.getLocalizedKey()).toUpperCase());
		cell.setCellStyle(_headerStyle);
		if (cellRow == 1) {
			cell = row.createCell((short) 1);
			cell.setCellValue(iwrb.getLocalizedString("resource.commune_students", "Commune students"));
			cell.setCellStyle(_headerStyle);
			cell = row.createCell((short) 2);
			cell.setCellValue(iwrb.getLocalizedString("resource.outside_commune_students", "Students outside commune"));
			cell.setCellStyle(_headerStyle);
		}
		
		ResourceHome rHome = (ResourceHome) IDOLookup.getHome(Resource.class);
		ResourceClassMemberHome rcmHome = (ResourceClassMemberHome) IDOLookup.getHome(ResourceClassMember.class);
		String managementTypeId = (String) managementType.getPrimaryKey();
		Iterator iter = schoolTypes.iterator();
		while (iter.hasNext()) {
			SchoolType st = (SchoolType) iter.next();
			int schoolTypeId = ((Integer) st.getPrimaryKey()).intValue();
			row = sheet.createRow(cellRow++);
			cell = row.createCell((short) 0);
			cell.setCellValue(iwrb.getLocalizedString("resource.school_type", "Operation") + ": " + st.getName());
			cell.setCellStyle(_headerStyle);
			
			Collection resources = rHome.findBySchoolType(schoolTypeId);
			Iterator resourcesIter = resources.iterator();
			while (resourcesIter.hasNext()) {
				Resource resource = (Resource) resourcesIter.next();
				int resourceId = ((Integer) resource.getPrimaryKey()).intValue();
				row = sheet.createRow((short) cellRow++);
				cell = row.createCell((short) 0);
				cell.setCellValue(resource.getResourceName());
				
				int communeStudentCount = rcmHome.countByRscSchoolTypeSeasonManagementTypeAndCommune(resourceId, schoolTypeId, seasonId, managementTypeId, homeCommuneId, false);
				cell = row.createCell((short) 1);
				cell.setCellValue("" + communeStudentCount);
				
				int outsideCommuneStudentCount = rcmHome.countByRscSchoolTypeSeasonManagementTypeAndCommune(resourceId, schoolTypeId, seasonId, managementTypeId, homeCommuneId, true);
				cell = row.createCell((short) 2);
				cell.setCellValue("" + outsideCommuneStudentCount);				
			}
		}
		return cellRow;
	}

	protected CommuneUserBusiness getCommuneUserBusiness(IWApplicationContext iwc) throws RemoteException {
		return (CommuneUserBusiness) IBOLookup.getServiceInstance(iwc, CommuneUserBusiness.class);	
	}

	protected SchoolBusiness getSchoolBusiness(IWApplicationContext iwc) throws RemoteException {
		return (SchoolBusiness) IBOLookup.getServiceInstance(iwc, SchoolBusiness.class);	
	}
}
