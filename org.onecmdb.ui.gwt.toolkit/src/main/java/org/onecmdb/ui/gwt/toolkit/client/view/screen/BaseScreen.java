/*
 * Copyright 2007 Aditya Kapur <addy AT gwtiger.org>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onecmdb.ui.gwt.toolkit.client.view.screen;

import java.util.Date;

import org.onecmdb.ui.gwt.toolkit.client.view.ci.CIIconDisplayNameWidget;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * All screens should inherit from BaseScreen This will help all the screens
 * have consistant look and feel.
 * <p>
 * This also has utility methods to show the Loading message and error messages
 * whenever required.
 * 
 * @author Aditya Kapur
 * 
 */

public abstract class BaseScreen extends Composite {

    protected DockPanel dockPanel = new DockPanel();

    protected static final DockPanel.DockLayoutConstant EAST = DockPanel.EAST;

    protected static final DockPanel.DockLayoutConstant WEST = DockPanel.WEST;

    protected static final DockPanel.DockLayoutConstant NORTH = DockPanel.NORTH;

    protected static final DockPanel.DockLayoutConstant SOUTH = DockPanel.SOUTH;

    protected static final DockPanel.DockLayoutConstant CENTER = DockPanel.CENTER;

    private HorizontalPanel lblTitle = new HorizontalPanel();

    private Label lblTitleText = new Label("");

    private Label lblError = new Label(" ");

    private static final String loadingLabel = "Loading...";

    private static Label lblLoading = new Label(loadingLabel);

    private String errorStyle = "mdv-form-error";

    private static final String loadingStyle = "mdv-form-loading-text";

    private static final String style_label = "mdv-layouts-Label";

    /**
     * Constructor
     * 
     */
    public BaseScreen() {
    	
    	lblTitleText.setWordWrap(false);
        lblTitle.add(lblTitleText);
        lblTitle.setSpacing(8);
    	dockPanel.setStyleName("mdv-form");
        lblTitle.setStyleName("mdv-form-title");
        lblTitle.setWidth("100%");
        dockPanel.add(lblTitle, DockPanel.NORTH);
        dockPanel.setCellWidth(lblTitle, "100%");
        lblError.setStyleName(errorStyle);
        lblLoading.setStyleName(loadingStyle);

        lblLoading.setVisible(false);
        HorizontalPanel hp = new HorizontalPanel();
        // hp.setStyleName("");
        hp.add(lblError);
        hp.add(lblLoading);
        dockPanel.add(hp, DockPanel.NORTH);

    }

    /**
     * Close/Hide the screen
     * 
     */
    public void close() {
        dockPanel.setVisible(false);
    }

    /**
     * This method sets the style for the error message
     * 
     * @param style
     *            style as used in the stylesheet
     */
    public void setErrorStyle(String style) {
        this.errorStyle = style;
        lblError.setStyleName(style);
    }

    /**
     * Sets the error string to the given string. If the string is not empty
     * then it is displayed
     * 
     * @param errorText
     *            the error message to be displayed.
     */

    public void setErrorText(String errorText) {
        lblError.setStyleName(errorStyle);
        lblError.setText(errorText);
    }

    /**
     * Sets the title for the screen
     * 
     * @param title
     *            The text that should be set as the title for the screen
     */

    public void setTitleText(String title) {
        lblTitleText.setText(title);
        
    }
    
    protected void setTitleWidget(Widget widget) {
    	lblTitle.clear();
    	lblTitle.add(lblTitleText);
    	lblTitle.add(widget);
    	lblTitle.setCellWidth(widget, "100%");
    	lblTitle.setCellHorizontalAlignment(widget, HorizontalPanel.ALIGN_LEFT);
    }
   
    
    /**
     * This method should be used to override the default style of the title
     * text
     * 
     * @param style
     *            name of the style
     */

    public void setTitleStyle(String style) {
        lblTitle.setStyleName(style);
    }

    /**
     * This method should be used to override the default loading message of
     * 
     * <pre>
     * Loading...
     * </pre>
     * 
     * @param loadingText
     *            the text that will be used for the Loading message
     */

    public void setLoadingText(String loadingText) {
        lblLoading.setText(loadingText);
    }

    /**
     * Method to show or hide the Error Message
     * 
     * @param visible
     *            if
     * 
     * <pre>
     * true
     * </pre>
     * 
     * then the error message is displayed, hidden when
     * 
     * <pre>
     * false
     * </pre>
     */

    public void showError(boolean visible) {
        lblError.setVisible(visible);
    }

    /**
     * Method to show or hide the Loading message
     * 
     * @param visible
     *            if
     * 
     * <pre>
     * true
     * </pre>
     * 
     * then the Loading message is displayed, hidden when
     * 
     * <pre>
     * false
     * </pre>
     */

    public void showLoading(boolean visible) {

        lblLoading.setVisible(visible);
        if (visible == false)
            lblLoading.setText(loadingLabel);

    }

    /**
     * This method should be defined in all the inherited screens.<br>
     * This is called each time the screen is loaded. This is to preload any
     * data if required
     * 
     * @see BaseEntryScreen#showScreen(int) showScreen
     */

    public void load() {
        clear();
    }

    public void clear() {

    }

    /**
     * This method should be defined in all the inherited screens.<br>
     * This is called each time the screen is loaded. This is to preload any
     * data if required
     * 
     * @param objectType
     *            This parameter is passed to the screen. Is used to identify
     *            which Object to load
     * @param objectId
     *            This is the parameter that identifies the ID of the object to
     *            be loaded
     * @see BaseEntryScreen#showScreen(int, String, Long) showScreen
     */

    public void load(String objectType, Long objectId) {
        clear();
    }

    /**
     * Helper function to take a string and convert to HTML and apply style
     * 
     * @param caption
     * @return HTML HTML string
     */
    protected HTML makeTitle(String caption) {
        HTML html = new HTML(caption);
        html.setStyleName(style_label);
        return html;
    }

    /**
     * Helper function to save Cookie
     * 
     * @param cookieName
     *            name of the cookie
     * @param value -
     *            value to be saved in the cookie
     * @param days -
     *            number of days this cookie should be kept alive
     */
    public void setCookie(String cookieName, String value, int days) {

        Date date = new Date();
        long dateLong = date.getTime();
        dateLong += (1000 * 60 * 60 * 24 * days);// convert days to ms
        date.setTime(dateLong); // Set the new date

        Cookies.setCookie(cookieName, value, date);
    }

    /**
     * Helper function to save Cookie. The cookie will be saved for 30 days by
     * default
     * 
     * @param cookieName
     *            name of the cookie
     * @param value -
     *            value to be saved in the cookie
     */
    public void setCookie(String cookieName, String value) {

        setCookie(cookieName, value, 30);
    }

    /**
     * Helper function to get the value from the cookie
     * 
     * @param cookieName
     *            Name of the cookie
     * @return value of the cookie
     */
    public String getCookie(String cookieName) {
        return Cookies.getCookie(cookieName);
    }

}
