package org.androidpn.server.service.impl;

import java.util.List;

import org.androidpn.server.dao.NotificationDao;
import org.androidpn.server.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 消息推送服务层接口实现类
 * @author Administrator
 *
 */
public class NotificationService implements
		org.androidpn.server.service.NotificationService {
	
	/** 这个参数必须加set否则无法注入对象 */
	@Autowired  //或者加这个就不用set了
	private NotificationDao notificationDao; 
	
	 

	public NotificationDao getNotificationDao() {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao) {
        this.notificationDao = notificationDao;
    }

    public void save(Notification notification) {
		notificationDao.save(notification);
	}

	public void remove(Long id) {
		notificationDao.remove(id);
	}

	public void update(Notification notification) {
		notificationDao.update(notification);
	}

	public Notification get(Long id) {
		return notificationDao.get(id);
	}

	public List<Notification> getAll() {
		return notificationDao.getAll();
	}

	public List<Notification> getByName(String name) throws Exception {
		return notificationDao.getByName(name);
	}

}
