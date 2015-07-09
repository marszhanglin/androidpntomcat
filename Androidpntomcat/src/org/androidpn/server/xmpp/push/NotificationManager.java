/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.androidpn.server.xmpp.push;

import java.util.Random;

import org.androidpn.server.model.Notification;
import org.androidpn.server.model.User;
import org.androidpn.server.service.NotificationService;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.service.UserService;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.SessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;

/** 
 * This class is to manage sending the notifcations to the users.  
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class NotificationManager {

    private static final String NOTIFICATION_NAMESPACE = "androidpn:iq:notification";

    private final Log log = LogFactory.getLog(getClass());

    private SessionManager sessionManager;
    private NotificationService notificationService;
    private UserService userService;
    /**
     * Constructor.
     */
    public NotificationManager() {
    	notificationService=ServiceLocator.getNotificationService();
    	userService=ServiceLocator.getUserService();
        sessionManager = SessionManager.getInstance();
    }

    /**
     * Broadcasts a newly created notification message to all connected users.
     * 
     * @param apiKey the API key
     * @param title the title
     * @param message the message details
     * @param uri the uri
     */
    public void sendBroadcast(String apiKey, String title, String message,
            String uri) {
        log.debug("sendBroadcast()...");
      //创建数据包
        IQ notificationIQ = createNotificationIQ(apiKey, title, message, uri);
        //发送给所有在线用户
        for (ClientSession session : sessionManager.getSessions()) {
            if (session.getPresence().isAvailable()) {
                notificationIQ.setTo(session.getAddress());
                session.deliver(notificationIQ);
            }
        }
    }

    /**
     * Sends a newly created notification message to the specific user.
     *  发送给指定用户
     * @param apiKey the API key
     * @param title the title
     * @param message the message details
     * @param uri the uri
     */ 
    public void sendNotifcationToUser(String apiKey, String username,
            String title, String message, String uri) {
        log.debug("sendNotifcationToUser()...");
        //创建数据包
        IQ notificationIQ = createNotificationIQ(apiKey, title, message, uri);
        //通过用户名获取用户会话
        ClientSession session = sessionManager.getSession(username);
        if (session != null) { 
            if (session.getPresence().isAvailable()) {//用户在线可以发送数据  就填充数据并发送
                notificationIQ.setTo(session.getAddress());
                session.deliver(notificationIQ);
            }else{
            	saveNotification(username, apiKey, title, message, uri);
            }
        }else{//没有会话   可能不在线  也可能根本不存在该用户
        	//查看用户是否存在
        	try {
				User user=userService.getUserByUsername(username);
				if(null!=user){//用户存在
					saveNotification(username, apiKey, title, message, uri);
				}
			} catch (UserNotFoundException e) {
				e.printStackTrace();
			}
        }
    }

    /**
     * 将不能发送成功的推送消息保存至数据库 
     * <br>等待下次链接时发送
     * @param username
     * @param apiKey
     * @param title
     * @param message
     * @param uri
     */
    private void saveNotification(String username,String apiKey,String  title,String  message,String  uri){
    	Notification notification=new Notification(apiKey, username, title, message, uri);
    	notificationService.save(notification);
    }
    
    /**
     * Creates a new notification IQ and returns it.
     */
    private IQ createNotificationIQ(String apiKey, String title,
            String message, String uri) {
        Random random = new Random();
        String id = Integer.toHexString(random.nextInt());
        // String id = String.valueOf(System.currentTimeMillis());

        Element notification = DocumentHelper.createElement(QName.get(
                "notification", NOTIFICATION_NAMESPACE));
        notification.addElement("id").setText(id);
        notification.addElement("apiKey").setText(apiKey);
        notification.addElement("title").setText(title);
        notification.addElement("message").setText(message);
        notification.addElement("uri").setText(uri);

        IQ iq = new IQ();
        iq.setType(IQ.Type.set);
        iq.setChildElement(notification);

        return iq;
    }
}
