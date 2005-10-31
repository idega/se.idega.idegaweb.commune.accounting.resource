/*
 * $Id: NativeLanguageList.java,v 1.4 2005/10/31 16:42:30 sigtryggur Exp $
 *
 * Copyright (C) 2004 Agura IT. All Rights Reserved.
 *
 * This software is the proprietary information of Agura IT AB.
 * Use is subject to license terms.
 *
 */
package se.idega.idegaweb.commune.accounting.resource.presentation;

import se.idega.idegaweb.commune.accounting.presentation.AccountingBlock;
import se.idega.idegaweb.commune.accounting.resource.business.ResourceWriter;

import com.idega.core.file.data.ICFile;
import com.idega.presentation.ExceptionWrapper;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.util.Timer;

/** 
 * This idegaWeb block generates Excel file listing placements with native languge resources.
 * <p>
 * Last modified: $Date: 2005/10/31 16:42:30 $ by $Author: sigtryggur $
 *
 * @author Anders Lindman
 * @version $Revision: 1.4 $
 */
public class NativeLanguageList extends AccountingBlock {

	private final static int ACTION_DEFAULT = 0;
	private final static int ACTION_CREATE_NATIVE_LANGUAGE_SCHOOL_CHOICE_LIST = 1;
	private final static int ACTION_CREATE_NATIVE_LANGUAGE_PLACEMENT_LIST = 2;
	
	private final static String PP = "rsc_ntl_"; // Parameter prefix 

	private final static String PARAMETER_CREATE_NATIVE_LANGUAGE_SCHOOL_CHOICE_LIST = PP + "cnscl";
	private final static String PARAMETER_CREATE_NATIVE_LANGUAGE_PLACEMENT_LIST = PP + "cnll";
	
	private final static String KP = "resource."; // Key prefix

	private final static String KEY_CREATE_NATIVE_LANGUAGE_SCHOOL_CHOICE_LIST = KP + "create_native_language_school_choice_list";
	private final static String KEY_CREATE_NATIVE_LANGUAGE_PLACEMENT_LIST = KP + "create_native_language_placement_list";
	private final static String KEY_NATIVE_LANGUAGE_SCHOOL_CHOICE_LIST = KP + "native_language_school_choice_list";
	private final static String KEY_NATIVE_LANGUAGE_PLACEMENT_LIST = KP + "native_language_placement_list";
	private final static String KEY_BACK = KP + "back";

	/**
	 * @see se.idega.idegaweb.commune.accounting.presentation.AccountingBlock#init()
	 */
	public void init(IWContext iwc) {
		try {
			int action = parseAction(iwc);
			switch (action) {
				case ACTION_DEFAULT:
					handleDefaultAction();
					break;
				case ACTION_CREATE_NATIVE_LANGUAGE_SCHOOL_CHOICE_LIST:
					handleCreateNativeLanguagePlacementList(iwc, true);
					break;
				case ACTION_CREATE_NATIVE_LANGUAGE_PLACEMENT_LIST:
					handleCreateNativeLanguagePlacementList(iwc, false);
					break;
			}
		}
		catch (Exception e) {
			add(new ExceptionWrapper(e));
		}
	}

	/*
	 * Returns the action constant for the action to perform based 
	 * on the POST parameters in the specified context.
	 */
	private int parseAction(IWContext iwc) {
		int action = ACTION_DEFAULT;
		
		if (iwc.isParameterSet(PARAMETER_CREATE_NATIVE_LANGUAGE_SCHOOL_CHOICE_LIST)) {
			action = ACTION_CREATE_NATIVE_LANGUAGE_SCHOOL_CHOICE_LIST;
		} else if (iwc.isParameterSet(PARAMETER_CREATE_NATIVE_LANGUAGE_PLACEMENT_LIST)) {
			action = ACTION_CREATE_NATIVE_LANGUAGE_PLACEMENT_LIST;
		}
		
		return action;
	}

	/*
	 * Handles the default action for this block.
	 */	
	private void handleDefaultAction() {
		Table table = new Table();
		table.setCellpadding(getCellpadding());
		table.setCellspacing(getCellspacing());
		
		SubmitButton createSchoolChoiceListButton = new SubmitButton(PARAMETER_CREATE_NATIVE_LANGUAGE_SCHOOL_CHOICE_LIST, 
				localize(KEY_CREATE_NATIVE_LANGUAGE_SCHOOL_CHOICE_LIST, "Create native language school choice list"));
		createSchoolChoiceListButton = (SubmitButton) getButton(createSchoolChoiceListButton);
		table.add(createSchoolChoiceListButton, 1, 1);
		
		SubmitButton createListButton = new SubmitButton(PARAMETER_CREATE_NATIVE_LANGUAGE_PLACEMENT_LIST, 
				localize(KEY_CREATE_NATIVE_LANGUAGE_PLACEMENT_LIST, "Create native language placement list"));
		createListButton = (SubmitButton) getButton(createListButton);
		table.add(createListButton, 2, 1);

		add(table);		
	}
	
	/*
	 * Handles creation of native language placement list.
	 */	
	private void handleCreateNativeLanguagePlacementList(IWContext iwc, boolean isSchoolChoice) {
		String fileName = "native_language_placements.xls";
		if (isSchoolChoice) {
			fileName = "native_language_school_choice.xls";
		}
				
		Table table = new Table();
		table.setCellpadding(getCellpadding());
		table.setCellspacing(getCellspacing());

		try {
		    Timer t = new Timer();
			t.start();
			ResourceWriter xlsWriter = new ResourceWriter(fileName);
			ICFile file = xlsWriter.createFile(iwc, isSchoolChoice);
			Link iconLink = new Link(getBundle().getImage("shared/xls.gif"));
			iconLink.setFile(file);
			iconLink.setTarget("new");
			table.add(iconLink, 1, 1);
			String title = localize(KEY_NATIVE_LANGUAGE_PLACEMENT_LIST, "Native language placement list");
			if (isSchoolChoice) {
				title = localize(KEY_NATIVE_LANGUAGE_SCHOOL_CHOICE_LIST, "Native language school choice list");
			}
			Link link = new Link(title);
			link.setFile(file);
			link.setTarget("new");
			table.add(link, 2, 1);
			Form form = new Form();
			SubmitButton back = new SubmitButton("", localize(KEY_BACK, KEY_BACK));
			back = (SubmitButton) getButton(back);
			form.add(back);
			table.add(form, 1, 4);
			table.mergeCells(1, 4, 2, 4);
			add(table);
			t.stop();
			System.out.println("Total execution time of modersmallista was = "+t.getTimeString());

		} catch (Exception e) {
			log(e);
		}		
	}
}
