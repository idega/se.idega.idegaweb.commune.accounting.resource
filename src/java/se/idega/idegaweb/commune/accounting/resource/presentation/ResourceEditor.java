/*
 * Created on 2003-aug-19
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package se.idega.idegaweb.commune.accounting.resource.presentation;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import se.idega.idegaweb.commune.accounting.presentation.AccountingBlock;
import se.idega.idegaweb.commune.accounting.presentation.ApplicationForm;
import se.idega.idegaweb.commune.accounting.presentation.ButtonPanel;
import se.idega.idegaweb.commune.accounting.presentation.ListTable;
import se.idega.idegaweb.commune.accounting.resource.business.ResourceBusiness;
import se.idega.idegaweb.commune.accounting.resource.data.Resource;
import se.idega.idegaweb.commune.accounting.resource.data.ResourceBMPBean;
import se.idega.idegaweb.commune.accounting.resource.data.ResourcePermission;
import se.idega.idegaweb.commune.presentation.CommuneBlock;

import com.idega.block.school.business.SchoolBusiness;
import com.idega.block.school.data.SchoolCategory;
import com.idega.block.school.data.SchoolType;
import com.idega.block.school.data.SchoolYear;
import com.idega.business.IBOLookup;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.ExceptionWrapper;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.util.text.TextFormat;

/**
 * @author wmgobom
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ResourceEditor extends AccountingBlock {
	IWResourceBundle iwrb;
	IWBundle iwb;
	TextFormat tFormat;
  ResourceBusiness busyBean;
  int provider_group_id;
  int commune_admin_group_id = 0;   // Commune admin permissions is not saved to db. Has all rights always.
  boolean providerGroupIdExists = true;
  // boolean communeAdminGroupIdExists = true;
  CommuneBlock styleObj = new CommuneBlock();

  /********************** Bundle properties ********************/
  private static final String BUNDLE_NAME_COMMUNE = "se.idega.idegaweb.commune";
  private static final String PROP_COMMUNE_PROVIDER_GRP_ID = "provider_administrators_group_id";
  
    
  /********************** Localization keys ********************/
  private static final String KP = "resource_editor.";  // Key prefix
  private static final String KEY_TITLE1 = KP + "rsc_title1";
  private static final String KEY_MAIN_ACTIVITY = KP + "main_activity";
//  private static final String KEY_SCHOOL = KP + "school";
  private static final String KEY_FORM_HEADER_RESOURCE = KP + "form_header.resource";
  private static final String KEY_FORM_HEADER_EDIT = KP + "form_header.edit";
  private static final String KEY_FORM_HEADER_REMOVE = KP + "form_header.delete";
  private static final String KEY_FORM_LABEL_RESOURCE = KP + "form_label.resource";
  private static final String KEY_FORM_LABEL_ASSIGN = KP + "form_label.assign";
  private static final String KEY_FORM_LABEL_VIEW = KP + "form_label.view";
  private static final String KEY_FORM_LABEL_ACTIVITIES = KP + "form_label.activities";
//  private static final String KEY_FORM_LABEL_SCHOOLYEARS = KP + "form_label.schoolyears";
  private static final String KEY_BUTTON_NEW = KP + "button.new";
  private static final String KEY_BUTTON_SAVE = KP + "button.save";
  //private static final String KEY_BUTTON_DELETE = KP + "button.delete";
  private static final String KEY_BUTTON_EDIT = KP + "button.edit";
  private static final String KEY_BUTTON_CANCEL = KP + "button.cancel";
  private static final String KEY_DROPDOWN_COMMUNE = KP + "dropdown.commune";
  private static final String KEY_DROPDOWN_PROVIDER = KP + "dropdown.provider";
  private static final String KEY_DROPDOWN_CHOSE_SCH_CAT = KP + "dropdown.chose.sch_category";
  private static final String KEY_MSG_CONFIRM_DELETE_START = KP + "confirm.delete.resource.start";
  private static final String KEY_MSG_CONFIRM_DELETE_END = KP + "confirm.delete.resource.end";
    
  /********************** Parameters *********************/
  private static final String PP = "cacc_rsc_";    // Parameter prefix
  private static final String PARAM_RSC_SAVE = PP + "save_resource";
  private static final String PARAM_RSC_NEW = PP + "new_resource";
  private static final String PARAM_RSC_EDIT = PP + "edit_resource";
  private static final String PARAM_RSC_DELETE = PP + "delete_resource";
  private static final String PARAM_RSC_CANCEL = PP + "cancel";
  private static final String PARAM_RSC_NAME = PP + "name";
  private static final String PARAM_RSC_ASSIGN = PP + "assign_grp_id";
  private static final String PARAM_RSC_VIEW = PP + "view_grp_id";
  private static final String PARAM_RSC_SCHOOLTYPES = PP + "sch_type_ids";
  private static final String PARAM_RSC_SCHOOLYEARS = PP + "sch_year_ids";  
  private static final String PARAM_SCHOOL_CATEGORY = PP + "sch_category";
  public void control(IWContext iwc) throws Exception {
    initBeans(iwc);
    // Get group ids from bundle "se.idega.idegaweb.commune"
    if (!getGroupIds(iwc)) {
      // A groupid is missing. Error message is returned from getGroupIds()
    } else {
      // We have the group ids. Now do what the parameters say
      if (iwc.isParameterSet(PARAM_RSC_SAVE)) {
        saveResource(iwc);
        add(getRscList(iwc));
      } else if (iwc.isParameterSet(PARAM_RSC_DELETE)) {
        deleteResource(iwc);
        add(getRscList(iwc));
      } else if (iwc.isParameterSet(PARAM_RSC_NEW)) {
        add(getRscForm(iwc, null));
      } else if (iwc.isParameterSet(PARAM_RSC_EDIT)) {
        add(getRscForm(iwc, iwc.getParameter(PARAM_RSC_EDIT)));      
      } else {
        // Show - List Resources page
        add(getRscList(iwc));
        
      }
    }
  }
  
  public PresentationObject getRscList(IWContext iwc) throws RemoteException {
    ApplicationForm app = new ApplicationForm(this);
    
    // *** Title ***
    app.setLocalizedTitle(KEY_TITLE1, "Administration resurs");    
    // *** Search Panel ***
    Table searchTable = new Table();
    searchTable.add(getSmallHeader(localize(KEY_MAIN_ACTIVITY, "Huvudverksamhet: ")), 1, 1);
    searchTable.add(getSchoolCategoriesDropdown(iwc), 2, 1);
    app.setSearchPanel(searchTable);    
    //*** Main Panel ***
	ListTable LT = new ListTable(this, 3);
	LT.setLocalizedHeader(KEY_FORM_HEADER_RESOURCE, "Resource", 1);
	LT.setLocalizedHeader(KEY_FORM_HEADER_EDIT, "Edit", 2);
	LT.setColumnWidth(2, "70");
	LT.setLocalizedHeader(KEY_FORM_HEADER_REMOVE, "Delete", 3);
	LT.setColumnWidth(3, "70");
	if (iwc.isParameterSet(PARAM_SCHOOL_CATEGORY)) {
	        // Loop resources and create links to edit them
	    String catID = iwc.getParameter(PARAM_SCHOOL_CATEGORY); 
	    Collection rscList = busyBean.findAllResourcesByCategory(catID);
	    for (Iterator iter = rscList.iterator(); iter.hasNext();) {
			ResourceBMPBean elem = (ResourceBMPBean) iter.next();
	      	Link L = getSmallLink(elem.getResourceName());
	     	String primKey = ((Integer) elem.getPrimaryKey()).toString();
	     	L.setParameter(PARAM_RSC_EDIT, primKey);
	      	L.addParameter(PARAM_SCHOOL_CATEGORY, iwc.getParameter(PARAM_SCHOOL_CATEGORY));
	      	LT.add(L);
	      	// Get edit button
			Link editButt = new Link(getEditIcon(localize(KEY_BUTTON_EDIT, "Edit this provider")));
			editButt.addParameter(PARAM_RSC_EDIT, primKey);
			editButt.addParameter(PARAM_SCHOOL_CATEGORY, iwc.getParameter(PARAM_SCHOOL_CATEGORY));
			LT.add(editButt);

	      	// Get delete button      
	      	Image delImg = getDeleteIcon(localize("cacc.resource.remove","Delete resource"));
	      	SubmitButton delButt = new SubmitButton(delImg, PARAM_RSC_DELETE, primKey);
	      	String tmpRscName = elem.getResourceName();
	      	String confirmMsgStart = iwrb.getLocalizedString(KEY_MSG_CONFIRM_DELETE_START, "Vill du verkligen radera resursen -");
	      	String confirmMsgEnd = iwrb.getLocalizedString(KEY_MSG_CONFIRM_DELETE_END, "- från databasen?");
	      	String confirmMsg = confirmMsgStart + tmpRscName + confirmMsgEnd;
	       	delButt.setSubmitConfirm(confirmMsg); 
	      	LT.add(delButt);
	   }		
	}        
    app.setMainPanel(LT);    
    // *** Button Panel ***
    ButtonPanel bp = new ButtonPanel(this);
    bp.addLocalizedButton(PARAM_RSC_NEW, KEY_BUTTON_NEW, "Ny");
    app.setButtonPanel(bp);
   
    return app;
  }
  
  public PresentationObject getRscForm(IWContext iwc, String rscIdStr) throws java.rmi.RemoteException {
    ApplicationForm app = new ApplicationForm(this);
    
    // *** Title ***
    app.setLocalizedTitle(KEY_TITLE1, "Administration resurs");    
    // *** Main Panel ***    
    Table T = new Table();
    int row = 1;
    Resource theRsc = null;
    Integer theRscId = null;
    if (rscIdStr != null) {
      // This mean that an existing resource will be edited
      theRscId = new Integer(rscIdStr);
      theRsc = busyBean.getResourceByPrimaryKey(theRscId);     
    }
    if (iwc.isParameterSet(PARAM_SCHOOL_CATEGORY)) {
    	T.add(new HiddenInput(PARAM_SCHOOL_CATEGORY, 
    										iwc.getParameter(PARAM_SCHOOL_CATEGORY)), 1, 1); 
    }
        /*********** Get input labels ***********/
    T.add(getLocalizedLabel(KEY_FORM_LABEL_RESOURCE, "Resurs"), 1, row++);
    T.add(getLocalizedLabel(KEY_FORM_LABEL_ASSIGN, "Tilldelningsbehörighet"), 1, row++);
    T.add(getLocalizedLabel(KEY_FORM_LABEL_VIEW, "Visningsbehörighet"), 1, row++);
    T.add(getLocalizedLabel(KEY_FORM_LABEL_ACTIVITIES, "Verksamheter") , 1, row++);

        /*********** Get input objects ***********/    
    row = 1;
     // Resource Name
    TextInput rscNameInput = new TextInput(PARAM_RSC_NAME);
    if (theRsc != null) {
      // Existing Resource is being edited so unique name can not be changed 
      //rscNameInput.setReadOnly(true);
    }
    T.add(rscNameInput, 2, row++);
    // Assign Permission
    DropdownMenu assignDropdown = new DropdownMenu(PARAM_RSC_ASSIGN);
            // Get translations for dropdowns
    String communeString = (getLocalizedText(KEY_DROPDOWN_COMMUNE, "Centralt")).toString();
    String providerString = (getLocalizedText(KEY_DROPDOWN_PROVIDER, "Anordnare")).toString();
    assignDropdown.addMenuElement(commune_admin_group_id, communeString);
    assignDropdown.addMenuElement(provider_group_id, providerString);
    assignDropdown.setSelectedElement(commune_admin_group_id);
    T.add(assignDropdown, 2, row++);
    // View Permission
    DropdownMenu viewDropdown = new DropdownMenu(PARAM_RSC_VIEW);
    viewDropdown.addMenuElement(commune_admin_group_id, communeString);
    viewDropdown.addMenuElement(provider_group_id, providerString);
    viewDropdown.setSelectedElement(commune_admin_group_id);
    T.add(viewDropdown, 2, row++);    
        /********** Set input values ************/
    if (theRsc != null) {
      // Resource Name
      rscNameInput.setValue(theRsc.getResourceName());
      // Resource Permissions
      Integer providerGrpId = new Integer(provider_group_id);
      ResourcePermission rscPerm = busyBean.getRscPermByRscAndGrpId(theRscId, providerGrpId);
      if (rscPerm != null) {
        if (rscPerm.getPermitAssignResource()) {
          assignDropdown.setSelectedElement(provider_group_id);
        }
        if (rscPerm.getPermitViewResource()){
          viewDropdown.setSelectedElement(provider_group_id);
        }
      }      
    }   
        /************* Get input checkboxes ****************/
    // SchoolTypes checkbox table
    Table typeTable = new Table();
    Collection typeVec = busyBean.findAllSchoolTypes();
    //CheckBox typeOrgBox = new CheckBox(PARAM_RSC_SCHOOLTYPES);
    Map schoolTypeMap = null;
    if (rscIdStr != null) {
    	schoolTypeMap = getSchoolTypeMap(iwc, rscIdStr);
    } 
    Integer schTypePK;
    int typeRow = 1;
    Iterator loop = typeVec.iterator();
    while (loop.hasNext()) {
      SchoolType loopItem = (SchoolType) loop.next();
	  schTypePK = (Integer) loopItem.getPrimaryKey();
      String typeIdStr = schTypePK.toString();
      CheckBox cBox = new CheckBox(PARAM_RSC_SCHOOLTYPES+typeIdStr, typeIdStr);
      //cBox.setValue(schTypePK.intValue());
      
      // Set related school types to checked
      if (theRsc != null) {
        Map typeMap = busyBean.getRelatedSchoolTypes(theRsc);
        Set typeKeys = typeMap.keySet();
        if (typeKeys.contains(schTypePK)) {
          cBox.setChecked(true);
        }
      }
      typeTable.add(cBox, 1, typeRow);
      typeTable.add(getSmallText(loopItem.getSchoolTypeName()), 2, typeRow);
      
      // *** Add horisontal SCHOOL YEAR TABLE if applicable on school type ***
	  int typeIdInt = -1;	
      if (schTypePK != null) {
	      typeIdInt = schTypePK.intValue();
	  }
	  Map schoolYearMap = null;
	  if (schoolTypeMap != null) {
		schoolYearMap = (Map) schoolTypeMap.get(schTypePK.toString());	  	
	  }
      Table yearTable = getSchoolYearCheckBoxes(iwc, typeIdInt, schoolYearMap);
	  if (yearTable != null)  {
	  	typeRow++;
	  	typeTable.add(yearTable, 2, typeRow);
	  }
	  // ***	END SCHOOL YEAR TABLE *** 
      typeRow++;
    }
    T.mergeCells( 1, row, 2, row);
    T.add(typeTable, 1, row++); 
       
    // *** Schoolyear checkbox table (horisontal)
 /*   T.add(getLocalizedText(KEY_FORM_LABEL_SCHOOLYEARS, "Skolår"), 1, row++);
    Table yearTable = new Table();
    Collection yearVec = busyBean.findAllSchoolYears();
    Integer yearPrimKey;
    CheckBox yearOrgBox = new CheckBox(PARAM_RSC_SCHOOLYEARS);
    int yearColumn = 1;
    loop = yearVec.iterator();
    for (Iterator iter = yearVec.iterator(); iter.hasNext();) {
			SchoolYear elem = (SchoolYear) iter.next();
			yearPrimKey = (Integer) elem.getPrimaryKey();
      CheckBox yBox = (CheckBox) yearOrgBox.clone();
      yBox.setValue(yearPrimKey.intValue());
      // Set related school years to checked
      if (theRsc != null) {
        Map yearMap = busyBean.getRelatedSchoolYears(theRsc);
        Set yearKeys = yearMap.keySet();
        if (yearKeys.contains(yearPrimKey)) {
          yBox.setChecked(true);
        }
      }      
      yearTable.add(yBox, yearColumn, 1);
      yearTable.addText(elem.getName(), yearColumn, 2);
      yearColumn++;
		}
    T.mergeCells(1, row, 2, row);
    T.add(yearTable, 1, row++);    */
    app.setMainPanel(T);
    
    // *** Button panel ***
    ButtonPanel bp = new ButtonPanel(this);
    String tmpRscId = "-1";
    if (theRsc != null) {
      // Save edited resource
      tmpRscId = ((Integer) theRsc.getPrimaryKey()).toString();
      bp.addLocalizedButton(PARAM_RSC_SAVE, tmpRscId, KEY_BUTTON_SAVE, "Spara");
    } else {
      // Save new resource
      bp.addLocalizedButton(PARAM_RSC_SAVE, tmpRscId, KEY_BUTTON_SAVE, "Spara");            
    }
    bp.addLocalizedButton(PARAM_RSC_CANCEL, "cancel", KEY_BUTTON_CANCEL, "Avbryt");
    app.setButtonPanel(bp);   
  
    return app;
  }
  
  private void initBeans(IWContext iwc) throws java.rmi.RemoteException, javax.ejb.CreateException {
    busyBean = (ResourceBusiness) IBOLookup.getServiceInstance(iwc, ResourceBusiness.class);
  }
  
  private boolean getGroupIds(IWContext iwc) {
    // Get group ids from the commune bundle for the groups Anordnare and Centralt
    IWBundle communeBundle = IWBundle.getBundle(BUNDLE_NAME_COMMUNE, iwc.getApplication());

    // Get provider group id from commune bundle
    String anordnareIdStr = communeBundle.getProperty(PROP_COMMUNE_PROVIDER_GRP_ID);
    if (anordnareIdStr != null && !anordnareIdStr.equals("")) {
      provider_group_id = Integer.valueOf(anordnareIdStr).intValue();
    } else {
      providerGroupIdExists = false;
    }   

    // if groupid is missing print error message
    if (!(providerGroupIdExists)) {      
      Table errT = new Table();
      int row = 1;
      if (!providerGroupIdExists) {
        errT.add(new Text(localize("cacc.resource.no.provider.id", "There is no \"provider_administrators_group_id\"-property " + 
                 "with the ic_group_id for the providergroup in the bindle \"se.idega.idegaweb.commune\"")),
                 1, row++);
      }
      add(errT);
    }

    return (providerGroupIdExists);
  }
 
  /*
   * Returns all school types.
   */
  private Collection getSchoolTypes(IWContext iwc) {
	  Collection c = null;
	  try {
		  c = getSchoolBusiness(iwc).findAllSchoolTypes();
	  } catch (RemoteException e) {
		  add(new ExceptionWrapper(e));
	  }
	  return c;
  }
  
  /*
   * Returns all school years for the specified school type id.
   */
  private Collection getSchoolYears(IWContext iwc, int schoolTypeId) {
	  Collection c = null;
	  try {
		  c = getSchoolBusiness(iwc).findAllSchoolYearsBySchoolType(schoolTypeId);
	  } catch (RemoteException e) {
		  add(new ExceptionWrapper(e));
	  }
	  return c;
  }
  
  /*
   * Returns a table with checkboxes for school years for the specified school type id. 
   */
  Table getSchoolYearCheckBoxes(IWContext iwc, int schoolTypeId, Map schoolYearMap) {
	  Table table = new Table();
	  table.setCellpadding(getCellpadding());
	  table.setCellspacing(getCellspacing());
		
	  Collection c = getSchoolYears(iwc, schoolTypeId);
	  Iterator iter = c.iterator();
	  int col = 1;
	  boolean hasSchoolYears = false;
	  while(iter.hasNext()) {
		  hasSchoolYears = true;
		  SchoolYear sy = (SchoolYear) iter.next();
		  String syId = sy.getPrimaryKey().toString();
		  CheckBox syCheckBox = new CheckBox(PARAM_RSC_SCHOOLYEARS + schoolTypeId, syId);
		  if (schoolYearMap != null && schoolYearMap.get(syId) != null) {
			  syCheckBox.setChecked(true);
		  }
		  table.add(syCheckBox, col, 1);
		  table.add(getText(sy.getName()), col, 2);
		  table.setAlignment(col, 2, Table.HORIZONTAL_ALIGN_CENTER);
		  col++;								
	  }

	  if (hasSchoolYears) {
		  return table;
	  } else {
		  return null;
	  }
  }
	
	/**
	 * resturns school years and types related to the Resource in a Map with type as key and a 
	 * schoolYearMap as value
	 * @param iwc
	 * @param rscIdStr
	 * @return Map 
	 * @throws RemoteException
	 */
	public Map getSchoolTypeMap(IWContext iwc, String rscIdStr) throws RemoteException {		
		Map schoolTypeMap = new TreeMap();
		
		ResourceBusiness rscBiz = getResourceBusiness(iwc);
		Resource rsc = rscBiz.getResourceByPrimaryKey(new Integer(rscIdStr));
		Map sts = null;
		Map sys = null;	
		if (rsc != null) {
			sts = rscBiz.getRelatedSchoolTypes(rsc);
			sys = rscBiz.getRelatedSchoolYears(rsc);
		}
		if (sts != null) {
			Iterator iter = sts.values().iterator();
			while(iter.hasNext()) {
				SchoolType st = (SchoolType) iter.next();
				int stId = ((Integer) st.getPrimaryKey()).intValue();
				String stIdString = st.getPrimaryKey().toString();
				Map schoolYearMap = new TreeMap();
				if (sys != null) {
					Iterator iter2 = sys.values().iterator();
					while(iter2.hasNext()) {
						SchoolYear sy = (SchoolYear) iter2.next();
						if (sy.getSchoolTypeId() == stId) {
							String syId = sy.getPrimaryKey().toString();
							schoolYearMap.put(syId, syId);
						}
					}
				}
				schoolTypeMap.put(stIdString, schoolYearMap);
			}			
		}
		return schoolTypeMap;
	}
	
	private Map getSchoolTypeParamMap(IWContext iwc) {
		Collection schTypes = getSchoolTypes(iwc);
		Iterator iter = schTypes.iterator();
		Map schoolTypeMap = new TreeMap();
		while(iter.hasNext()) {
			SchoolType st = (SchoolType) iter.next();
			String stId = st.getPrimaryKey().toString();			
			if (iwc.isParameterSet(PARAM_RSC_SCHOOLTYPES + stId)) {		
				Map schoolYearMap = new TreeMap();
				String[] schoolYearIds = iwc.getParameterValues(PARAM_RSC_SCHOOLYEARS + stId);
				if (schoolYearIds != null) {
					for (int i = 0; i < schoolYearIds.length; i++) {
						schoolYearMap.put(schoolYearIds[i], schoolYearIds[i]);
					}
				}
				schoolTypeMap.put(stId, schoolYearMap);
			}
		}
		return schoolTypeMap;
	}
	
	/*
	 * Extracts the school type ids from the specified map
	 */
	private int[] getSchoolTypeIds(Map map) {
		int[] ids = new int[map.size()];
		Iterator iter = map.keySet().iterator();
		int i = 0;
		while (iter.hasNext()) {
			int id = getInt((String) iter.next());
			ids[i] = id;
			i++;
		}
		return ids;
	}

	/*
	 * Extracts the school year ids from the specified map
	 */
	private int[] getSchoolYearIds(Map map) {
		int yearCount = 0;
		Collection years = new java.util.ArrayList();
		Iterator iter = map.values().iterator();
		while (iter.hasNext()) {
			Map m = (Map) iter.next();
			Iterator iter2 = m.values().iterator();
			while(iter2.hasNext()) {
				years.add(iter2.next());
				yearCount++;
			}
		}
		int[] ids = new int[yearCount];
		iter = years.iterator();
		int i = 0;
		while (iter.hasNext()) {
			int id = getInt((String) iter.next());
			ids[i] = id;
			i++;
		}
		return ids;
	}

	/*
	 * Parses an integer, returns -1 if exception
	 */
	int getInt(String s) {
		int n = -1;
		try {
			n = Integer.parseInt(s);
		} catch (Exception e) {}
		return n;
	}	
	
    
  private void saveResource(IWContext iwc) throws RemoteException {   
    boolean isSavingExistingRsc = !("-1".equals(iwc.getParameter(PARAM_RSC_SAVE)));
    
    // Get Resource BMP bean fields
    String rscIdStr = iwc.getParameter(PARAM_RSC_SAVE);
    // PARAM_RSC_SAVE contains the current resource id or -1 if new rsc
    int rscId = Integer.parseInt(rscIdStr);
    String rscName = iwc.getParameter(PARAM_RSC_NAME);
    
    /*String[] schTypeArr = iwc.getParameterValues(PARAM_RSC_SCHOOLTYPES);
    String[] schYearArr = iwc.getParameterValues(PARAM_RSC_SCHOOLYEARS);   
    int[] typeInts = getIntArrFromStrArr(schTypeArr);
    int[] yearInts = getIntArrFromStrArr(schYearArr);
    */
    
    Map typeYearMap = getSchoolTypeParamMap(iwc);
    int[] typeInts = getSchoolTypeIds(typeYearMap);
    int[] yearInts = getSchoolYearIds(typeYearMap);
    
    // Get ResourcePermission BMP bean fields
    String assignGrpIdStr = iwc.getParameter(PARAM_RSC_ASSIGN);
    String viewGrpIdStr = iwc.getParameter(PARAM_RSC_VIEW);
    int assignGrpId = Integer.parseInt(assignGrpIdStr);
    int viewGrpId = Integer.parseInt(viewGrpIdStr);
    boolean permitAssign = (assignGrpId == provider_group_id);
    boolean permitView = (viewGrpId == provider_group_id);
    // Save the resource
    busyBean.saveResource(isSavingExistingRsc, rscName, typeInts, yearInts, 
                                          permitAssign, permitView, provider_group_id, rscId);
  }
  
  public void deleteResource(IWContext iwc) throws RemoteException {
    String rscIdStr = iwc.getParameter(PARAM_RSC_DELETE);
    Integer rscIdInteger = new Integer(rscIdStr);
    busyBean.removeResource(rscIdInteger);
  }
 
  public int[] getIntArrFromStrArr(String[] strInts) {
    int[] ints;
    if (strInts != null) {
      ints = new int[strInts.length];
      for (int i = 0; i < strInts.length; i++) {
  			ints[i] = Integer.parseInt(strInts[i]);			
  		}
    } else {
      ints = new int[0];
    }        
    return ints;
  }
  
  public void init(IWContext iwc) throws Exception {
    iwrb = getResourceBundle(iwc);
    tFormat = TextFormat.getInstance();
    control(iwc);    
  }
  
  private DropdownMenu getSchoolCategoriesDropdown(IWContext iwc) throws RemoteException {
	  // Get dropdown for school categories
	  DropdownMenu schoolCats = new DropdownMenu(PARAM_SCHOOL_CATEGORY);
	  schoolCats.setToSubmit(true);
	  schoolCats.addMenuElement("-1", localize(KEY_DROPDOWN_CHOSE_SCH_CAT, "-Chose operational field-"));
	Collection catsColl = getResourceBusiness(iwc).findAllSchoolCategories();
	for (Iterator iter = catsColl.iterator(); iter.hasNext();) {
		SchoolCategory cat = (SchoolCategory) iter.next();
		String pkStr = (String) cat.getPrimaryKey();
		String nameKey = cat.getLocalizedKey();
		String name = getLocalizedText(nameKey, nameKey).toString();
		schoolCats.addMenuElement(pkStr, name);		
	}
	if (iwc.isParameterSet(PARAM_SCHOOL_CATEGORY)) {
		schoolCats.setSelectedElement(iwc.getParameter(PARAM_SCHOOL_CATEGORY));
	}

	  return schoolCats;
  }

  
  /*
   * Returns a school business object
   */
  private SchoolBusiness getSchoolBusiness(IWContext iwc) {
	  SchoolBusiness sb = null;
	  try {
		  sb = (SchoolBusiness) IBOLookup.getServiceInstance(iwc, SchoolBusiness.class);
	  } catch (RemoteException e) {
		  add(new ExceptionWrapper(e));
	  }
	  return sb;
  }
  
/**
 * Returns a ResourceBusiness object
 */
 private ResourceBusiness getResourceBusiness(IWContext iwc) throws RemoteException {
 	return (ResourceBusiness) IBOLookup.getServiceInstance(iwc, ResourceBusiness.class);
 }

}
