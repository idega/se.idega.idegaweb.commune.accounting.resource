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

import se.idega.idegaweb.commune.accounting.presentation.AccountingBlock;
import se.idega.idegaweb.commune.accounting.presentation.ApplicationForm;
import se.idega.idegaweb.commune.accounting.presentation.ButtonPanel;
import se.idega.idegaweb.commune.accounting.presentation.ListTable;
import se.idega.idegaweb.commune.accounting.resource.business.ResourceBusiness;
import se.idega.idegaweb.commune.accounting.resource.data.Resource;
import se.idega.idegaweb.commune.accounting.resource.data.ResourceBMPBean;
import se.idega.idegaweb.commune.accounting.resource.data.ResourcePermission;
import se.idega.idegaweb.commune.presentation.CommuneBlock;

import com.idega.block.school.data.SchoolType;
import com.idega.block.school.data.SchoolYear;
import com.idega.business.IBOLookup;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DropdownMenu;
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
  private static final String KEY_SCHOOL = KP + "school";
  private static final String KEY_FORM_HEADER_RESOURCE = KP + "form_header.resource";
  private static final String KEY_FORM_HEADER_REMOVE = KP + "form_header.delete";
  private static final String KEY_FORM_LABEL_RESOURCE = KP + "form_label.resource";
  private static final String KEY_FORM_LABEL_ASSIGN = KP + "form_label.assign";
  private static final String KEY_FORM_LABEL_VIEW = KP + "form_label.view";
  private static final String KEY_FORM_LABEL_ACTIVITIES = KP + "form_label.activities";
  private static final String KEY_FORM_LABEL_SCHOOLYEARS = KP + "form_label.schoolyears";
  private static final String KEY_BUTTON_NEW = KP + "button.new";
  private static final String KEY_BUTTON_SAVE = KP + "button.save";
  //private static final String KEY_BUTTON_DELETE = KP + "button.delete";
  private static final String KEY_BUTTON_CANCEL = KP + "button.cancel";
  private static final String KEY_DROPDOWN_COMMUNE = KP + "dropdown.commune";
  private static final String KEY_DROPDOWN_PROVIDER = KP + "dropdown.provider";
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
  private static final String PARAM_RSC_ACTIVITIES = PP + "sch_type_ids";
  private static final String PARAM_RSC_SCHOOLYEARS = "sch_year_ids";  
  
  public void control(IWContext iwc) throws Exception {
    initBeans(iwc);
    // Get group ids from bundle "se.idega.idegaweb.commune"
    if (!getGroupIds(iwc)) {
      // A groupid is missing. Error message is returned from getGroupIds()
    } else {
      // We have the group ids. Now do what the parameters say
      if (iwc.isParameterSet(PARAM_RSC_SAVE)) {
        saveResource(iwc);
        add(getRscList());
      } else if (iwc.isParameterSet(PARAM_RSC_DELETE)) {
        deleteResource(iwc);
        add(getRscList());
      } else if (iwc.isParameterSet(PARAM_RSC_NEW)) {
        add(getRscForm(null));
      } else if (iwc.isParameterSet(PARAM_RSC_EDIT)) {
        add(getRscForm(iwc.getParameter(PARAM_RSC_EDIT)));      
      } else {
        // Show - List Resources page
        add(getRscList());
        
      }
    }
  }
  
  public PresentationObject getRscList() throws RemoteException {
    ApplicationForm app = new ApplicationForm(this);
    
    // *** Title ***
    app.setLocalizedTitle(KEY_TITLE1, "Administration resurs");    
    // *** Search Panel ***
    Table searchTable = new Table();
    searchTable.add(getSmallHeader(localize(KEY_MAIN_ACTIVITY, "Huvudverksamhet: ")), 1, 1);
    searchTable.add(getSmallText(localize(KEY_SCHOOL, "Skola")), 2, 1);
    app.setSearchPanel(searchTable);    
    //*** Main Panel ***
    ListTable LT = new ListTable(this, 2);
    LT.setLocalizedHeader(KEY_FORM_HEADER_RESOURCE, "Resurs", 1);
    LT.setLocalizedHeader(KEY_FORM_HEADER_REMOVE, "Ta bort", 2);
        // Loop resources and create links to edit them
    Collection rscList = busyBean.findAllResources();
    for (Iterator iter = rscList.iterator(); iter.hasNext();) {
			ResourceBMPBean elem = (ResourceBMPBean) iter.next();
      Link L = getSmallLink(elem.getResourceName());
      String primKey = ((Integer) elem.getPrimaryKey()).toString();
      L.setParameter(PARAM_RSC_EDIT, primKey);
      LT.add(L);
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
    app.setMainPanel(LT);    
    // *** Button Panel ***
    ButtonPanel bp = new ButtonPanel(this);
    bp.addLocalizedButton(PARAM_RSC_NEW, KEY_BUTTON_NEW, "Ny");
    app.setButtonPanel(bp);
   
    return app;
  }
  
  public PresentationObject getRscForm(String rscIdStr) throws java.rmi.RemoteException {
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
    CheckBox typeOrgBox = new CheckBox(PARAM_RSC_ACTIVITIES);
    Integer primaryKey;
    int typeRow = 1;
    Iterator loop = typeVec.iterator();
    while (loop.hasNext()) {
      SchoolType loopItem = (SchoolType) loop.next();
      CheckBox cBox = (CheckBox) typeOrgBox.clone();
      primaryKey = (Integer) loopItem.getPrimaryKey();
      cBox.setValue(primaryKey.intValue());
      // Set related school types to checked
      if (theRsc != null) {
        Map typeMap = busyBean.getRelatedSchoolTypes(theRsc);
        Set typeKeys = typeMap.keySet();
        if (typeKeys.contains(primaryKey)) {
          cBox.setChecked(true);
        }
      }
      typeTable.add(cBox, 1, typeRow);
      typeTable.addText(loopItem.getSchoolTypeName(), 2, typeRow);    
      typeRow++;
    }
    T.mergeCells( 1, row, 2, row);
    T.add(typeTable, 1, row++);    
    // Schoolyear checkbox table (horisontal)
    T.add(getLocalizedText(KEY_FORM_LABEL_SCHOOLYEARS, "Skolår"), 1, row++);
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
    T.add(yearTable, 1, row++);    
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
    
  private void saveResource(IWContext iwc) throws RemoteException {   
    boolean isSavingExistingRsc = !("-1".equals(iwc.getParameter(PARAM_RSC_SAVE)));
    
    // Get Resource BMP bean fields
    String rscIdStr = iwc.getParameter(PARAM_RSC_SAVE);
    // PARAM_RSC_SAVE contains the current resource id or -1 if new rsc
    int rscId = Integer.parseInt(rscIdStr);
    String rscName = iwc.getParameter(PARAM_RSC_NAME);
    String[] schTypeArr = iwc.getParameterValues(PARAM_RSC_ACTIVITIES);
    String[] schYearArr = iwc.getParameterValues(PARAM_RSC_SCHOOLYEARS);
    int[] typeInts = getIntArrFromStrArr(schTypeArr);
    int[] yearInts = getIntArrFromStrArr(schYearArr);
    
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

}
