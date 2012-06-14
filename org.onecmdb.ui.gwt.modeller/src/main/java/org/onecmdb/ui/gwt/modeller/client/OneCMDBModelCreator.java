package org.onecmdb.ui.gwt.modeller.client;


import org.onecmdb.ui.gwt.modeller.client.view.login.ModelDesignerLoginScreen;
import org.onecmdb.ui.gwt.modeller.client.view.navigation.ModelCreatorNavigation;
import org.onecmdb.ui.gwt.modeller.client.view.screen.AddAttributeScreen;
import org.onecmdb.ui.gwt.modeller.client.view.screen.AddTemplateScreen;
import org.onecmdb.ui.gwt.modeller.client.view.screen.EditAttributeScreen;
import org.onecmdb.ui.gwt.modeller.client.view.screen.EditTemplateScreen;
import org.onecmdb.ui.gwt.modeller.client.view.screen.ListAttributeScreen;
import org.onecmdb.ui.gwt.modeller.client.view.screen.TemplateViewScreen;
import org.onecmdb.ui.gwt.modeller.client.view.screen.transform.EditAttributeSelectorScreen;
import org.onecmdb.ui.gwt.modeller.client.view.screen.transform.EditTransformScreen;
import org.onecmdb.ui.gwt.modeller.client.view.screen.transform.ListTransformScreen;
import org.onecmdb.ui.gwt.modeller.client.view.screen.transform.NewTransformScreen;
import org.onecmdb.ui.gwt.modeller.client.view.screen.transform.TestTransformScreen;
import org.onecmdb.ui.gwt.modeller.client.view.screen.transform.ViewTransformScreen;
import org.onecmdb.ui.gwt.toolkit.client.OneCMDBApplication;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.BaseScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.ci.NewCIScreen;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OneCMDBModelCreator extends OneCMDBApplication {
	// Define screens.
	
	public static final int NEW_INSTANCE_SCREEN = 100;
	public static final int NEW_TEMPLATE_SCREEN = 101;
	public static final int ADD_ATTRIBUTE_SCREEN = 102;
	public static final int TEMPLATE_VIEW_SCREEN = 103;
	public static final int ATTRIBUTE_VIEW_SCREEN = 104;
	public static final int EDIT_TEMPLATE_SCREEN = 105;
	public static final int EDIT_ATTRIBUTE_SCREEN = 106;
	
	// Transform screens
	public static final int NEW_TRANSFORM_SCREEN = 201;
	public static final int LIST_TRANSFORM_SCREEN = 202;
	public static final int EDIT_TRANSFORM_SCREEN = 203;
	public static final int VIEW_TRANSFORM_SCREEN = 204;
	public static final int TEST_TRANSFORM_SCREEN = 205;
	public static final int EDIT_ATTRIBUTE_SELECTOR_SCREEN = 206;
	
	

	protected BaseScreen getOneCMDBScreenFirstTime(int index) {
		BaseScreen base = null;
		switch(index) {
			case LOGIN_SCREEN:
				base = new ModelDesignerLoginScreen();
				break;
			case NAVIGATION_SCREEN:
				base = new ModelCreatorNavigation();
				break;
			case TEMPLATE_VIEW_SCREEN:
				base = new TemplateViewScreen();
				break;
			case ATTRIBUTE_VIEW_SCREEN:
				base = new ListAttributeScreen();
				break;
			case ADD_ATTRIBUTE_SCREEN:
				base = new AddAttributeScreen();
				break;
			case NEW_INSTANCE_SCREEN:
				base = new NewCIScreen();
				break;
			case NEW_TEMPLATE_SCREEN:
				base = new AddTemplateScreen();
				break;
			case EDIT_TEMPLATE_SCREEN:
				base = new EditTemplateScreen();
				break;
			case EDIT_ATTRIBUTE_SCREEN:
				base = new EditAttributeScreen();
				break;
				
			// Transfrom
			case NEW_TRANSFORM_SCREEN:
				base = new NewTransformScreen();
				break;
			case LIST_TRANSFORM_SCREEN:
				base = new ListTransformScreen();
				break;
			case EDIT_TRANSFORM_SCREEN:
				base = new EditTransformScreen();
				break;
			case VIEW_TRANSFORM_SCREEN:
				base = new ViewTransformScreen();
				break;
			case TEST_TRANSFORM_SCREEN:
				base = new TestTransformScreen();
				break;
			case EDIT_ATTRIBUTE_SELECTOR_SCREEN:
				base = new EditAttributeSelectorScreen();
				break;
		}
		return(base);		
	}


	public static OneCMDBApplication get() {
		return(singleton);
	}
	
	
	

}
