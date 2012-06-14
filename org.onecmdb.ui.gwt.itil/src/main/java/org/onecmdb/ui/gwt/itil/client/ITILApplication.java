package org.onecmdb.ui.gwt.itil.client;



import org.onecmdb.ui.gwt.itil.client.application.ITILApplicationLoginScreen;
import org.onecmdb.ui.gwt.itil.client.application.asset.screen.ListHardwareByTypeScreen;
import org.onecmdb.ui.gwt.itil.client.application.asset.screen.ListHardwareScreen;
import org.onecmdb.ui.gwt.itil.client.application.incident.screen.ConfirmNewIncidentScreen;
import org.onecmdb.ui.gwt.itil.client.application.incident.screen.EditIncidentScreen;
import org.onecmdb.ui.gwt.itil.client.application.incident.screen.GroupListIncidentScreen;
import org.onecmdb.ui.gwt.itil.client.application.incident.screen.ListIncidentScreen;
import org.onecmdb.ui.gwt.itil.client.application.incident.screen.NewIncidentScreen;
import org.onecmdb.ui.gwt.itil.client.application.problem.screen.ConfirmNewProblemtScreen;
import org.onecmdb.ui.gwt.itil.client.application.problem.screen.EditProblemScreen;
import org.onecmdb.ui.gwt.itil.client.application.problem.screen.GroupListProblemScreen;
import org.onecmdb.ui.gwt.itil.client.application.problem.screen.ListProblemScreen;
import org.onecmdb.ui.gwt.itil.client.application.problem.screen.NewProblemScreen;
import org.onecmdb.ui.gwt.itil.client.application.problem.screen.ViewProblemScreen;
import org.onecmdb.ui.gwt.itil.client.main.screen.navigation.NavigationScreen;
import org.onecmdb.ui.gwt.toolkit.client.OneCMDBApplication;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.BaseScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.ci.EditCIScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.ci.ListCIScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.ci.NewCIScreen;
import org.onecmdb.ui.gwt.toolkit.client.view.screen.ci.ViewCIScreen;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ITILApplication extends OneCMDBApplication { 
	
	
	
	// Incident Screens
	public static final int NEW_INCDIENT_SCREEN = 100;
	public static final int LIST_INCDIENT_SCREEN = 101;
	public static final int EDIT_INCDIENT_SCREEN = 102;
	public static final int GROUP_LIST_INCDIENT_SCREEN = 103;
	public static final int CONFIRM_NEW_INCDIENT_SCREEN = 104;
	
	// Problem Screens.
	public static final int NEW_PROBLEM_SCREEN = 200;
	public static final int LIST_PROBLEM_SCREEN = 201;
	public static final int EDIT_PROBLEM_SCREEN = 202;
	public static final int GROUP_LIST_PROBLEM_SCREEN = 203;
	public static final int VIEW_PROBLEM_SCREEN = 204;
	public static final int CONFIRM_NEW_PROBLEM_SCREEN = 205;

	public static final int LIST_HARDWARE_SCREEN = 300;
	public static final int LIST_GROUP_HARDWARE_SCREEN = 301;
	
	

	protected static ITILApplication singleton;

   
	 
	public static ITILApplication get() {
		return(singleton);
	}
	
	

	    /**
	         * This is the entry point method.
	         */
	public void onModuleLoad() {
		super.onModuleLoad();
		singleton = this;
	}

	
	protected BaseScreen getOneCMDBScreenFirstTime(int index) {
		BaseScreen base = null;
		switch (index) {
	
		case LOGIN_SCREEN:
			base = new ITILApplicationLoginScreen();
			break;
			
		// Incident navigation screen..
		case NAVIGATION_SCREEN:
			base = new NavigationScreen();
			break;
			
			// Incident screens	
		case NEW_INCDIENT_SCREEN:
			base = new NewIncidentScreen();
			break;
		case LIST_INCDIENT_SCREEN:  
			base=new ListIncidentScreen();
			break;
		case GROUP_LIST_INCDIENT_SCREEN:  
			base=new GroupListIncidentScreen();
			break;
		case EDIT_INCDIENT_SCREEN:  
			base=new EditIncidentScreen();
			break;
		case CONFIRM_NEW_INCDIENT_SCREEN:
			base=new ConfirmNewIncidentScreen();
			break;
			// Problem screens	
		case NEW_PROBLEM_SCREEN:
			base = new NewProblemScreen();
			break;
		case LIST_PROBLEM_SCREEN:  
			base=new ListProblemScreen();
			break;
		case EDIT_PROBLEM_SCREEN:  
			base=new EditProblemScreen();
			break;
		case VIEW_PROBLEM_SCREEN:  
			base=new ViewProblemScreen();
			break;
	
		case GROUP_LIST_PROBLEM_SCREEN:
			base = new GroupListProblemScreen();
			break;

		case LIST_HARDWARE_SCREEN:
			base = new ListHardwareScreen();
			break;

		case LIST_GROUP_HARDWARE_SCREEN:
			base = new ListHardwareByTypeScreen();
			break;
		case CONFIRM_NEW_PROBLEM_SCREEN:
			base= new ConfirmNewProblemtScreen();
			break;
		default:
			System.out.println("Screen #" + index + " not found");
		break;

		}
		return base;
	}

	 
}
