/*
 * $Id: ResourceList.java,v 1.1 2004/03/23 10:40:12 anders Exp $
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

/** 
 * This idegaWeb block generates Excel files listing placements and resources.
 * <p>
 * Last modified: $Date: 2004/03/23 10:40:12 $ by $Author: anders $
 *
 * @author Anders Lindman
 * @version $Revision: 1.1 $
 */
public class ResourceList extends AccountingBlock {

	private final static int ACTION_DEFAULT = 0;
	private final static int ACTION_CREATE_RESOURCE_PLACEMENT_LIST = 1;
	
	private final static String PP = "rscl_"; // Parameter prefix 

	private final static String PARAMETER_CREATE_RESOURCE_PLACEMENT_LIST = PP + "crpl";
	
	private final static String KP = "resource."; // Key prefix

	private final static String KEY_CREATE_RESOURCE_PLACEMENT_LIST = KP + "create_resource_placement_list";
	private final static String KEY_RESOURCE_PLACEMENT_LIST = KP + "resource_placement_list";
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
				case ACTION_CREATE_RESOURCE_PLACEMENT_LIST:
					handleCreateResourcePlacementList(iwc);
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
		
		if (iwc.isParameterSet(PARAMETER_CREATE_RESOURCE_PLACEMENT_LIST)) {
			action = ACTION_CREATE_RESOURCE_PLACEMENT_LIST;
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
		
		SubmitButton createResourcePlacementListButton = new SubmitButton(PARAMETER_CREATE_RESOURCE_PLACEMENT_LIST, 
				localize(KEY_CREATE_RESOURCE_PLACEMENT_LIST, "Create resource placement list"));
		createResourcePlacementListButton = (SubmitButton) getButton(createResourcePlacementListButton);
		table.add(createResourcePlacementListButton, 1, 1);
		
		add(table);		
	}
	
	/*
	 * Handles creation of resource placement list.
	 */	
	private void handleCreateResourcePlacementList(IWContext iwc) {
		String fileName = "resource_placements.xls";
				
		Table table = new Table();
		table.setCellpadding(getCellpadding());
		table.setCellspacing(getCellspacing());

		try {
			ResourceWriter xlsWriter = new ResourceWriter(fileName);
			ICFile file = xlsWriter.createFile(iwc, ResourceWriter.TYPE_MANAGEMENT_TYPE_RESOURCE_LIST);
			Link iconLink = new Link(getBundle().getImage("shared/xls.gif"));
			iconLink.setFile(file);
			table.add(iconLink, 1, 1);
			String title = localize(KEY_RESOURCE_PLACEMENT_LIST, "Resource placement list");
			Link link = new Link(title);
			link.setFile(file);
			table.add(link, 2, 1);
			Form form = new Form();
			SubmitButton back = new SubmitButton("", localize(KEY_BACK, KEY_BACK));
			back = (SubmitButton) getButton(back);
			form.add(back);
			table.add(form, 1, 4);
			table.mergeCells(1, 4, 2, 4);
			add(table);

		} catch (Exception e) {
			log(e);
		}		
	}
}
