/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.explorer.ui.task;

import java.io.InputStream;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.identity.Picture;
import org.activiti.engine.identity.User;
import org.activiti.explorer.ExplorerApp;
import org.activiti.explorer.ViewManager;
import org.activiti.explorer.ui.ExplorerLayout;
import org.activiti.explorer.ui.Images;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * @author Joram Barrez
 */
public class UserDetailsComponent extends HorizontalLayout {
    
    private static final long serialVersionUID = 1L;
    
    protected IdentityService identityService;
    protected ViewManager viewManager;
    
    protected User user;
    protected String role;
    protected String buttonCaption;
    protected ClickListener clickListener;

    public UserDetailsComponent(String userId, String role, String buttonCaption, ClickListener clickListener) {
      this.role = role;
      this.buttonCaption = buttonCaption;
      this.clickListener = clickListener;
      
      identityService = ProcessEngines.getDefaultProcessEngine().getIdentityService();
      viewManager = ExplorerApp.get().getViewManager();
      
      if (userId != null) {
        user = identityService.createUserQuery().userId(userId).singleResult();
      }
      
      // init UI
      addUserPicture();
      addUserDetails();
      
    }

    protected void addUserPicture() {
      Resource pictureResource = Images.USER_48; // default icon
      if (user != null) {
        final Picture userPicture = identityService.getUserPicture(user.getId());
        if (userPicture != null) {
          pictureResource = new StreamResource(new StreamSource() {        
            public InputStream getStream() {
              return userPicture.getInputStream();
            }
          }, user.getId(), ExplorerApp.get());
          
        } 
      }
      Embedded picture = new Embedded(null, pictureResource);
      
      picture.setType(Embedded.TYPE_IMAGE);
      picture.addStyleName(ExplorerLayout.STYLE_TASK_EVENT_PICTURE);
      picture.setHeight("48px");
      picture.setWidth("48px");
      addComponent(picture);
      
      // Add profile popup listener
      if (user != null) {
        picture.addStyleName(ExplorerLayout.STYLE_CLICKABLE);
        picture.addListener(new com.vaadin.event.MouseEvents.ClickListener() {
          public void click(ClickEvent event) {
            viewManager.showProfilePopup(user.getId());
          }
        });
      }
    }
    
    protected void addUserDetails() {
      VerticalLayout detailsLayout = new VerticalLayout();
      addComponent(detailsLayout);
      
      // Name
      Label nameLabel = null;
      if (user != null) {
        nameLabel = new Label(user.getFirstName() + " " + user.getLastName());
        nameLabel.addStyleName(ExplorerLayout.STYLE_LABEL_BOLD);
      } else {
        nameLabel = new Label("&nbsp;", Label.CONTENT_XHTML);
      }
      detailsLayout.addComponent(nameLabel);
      
      // Layout for lower details
      HorizontalLayout actionsLayout = new HorizontalLayout();
      actionsLayout.setSpacing(true);
      detailsLayout.addComponent(actionsLayout);
      
      // Role
      Label roleLabel = new Label(role);
      actionsLayout.addComponent(roleLabel);
      
      // Action button
      Button button = new Button(buttonCaption);
      button.addStyleName(Reindeer.BUTTON_SMALL);
      button.addListener(clickListener);
      actionsLayout.addComponent(button);
    }
    
  }