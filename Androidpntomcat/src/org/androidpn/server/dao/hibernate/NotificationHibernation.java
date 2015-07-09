package org.androidpn.server.dao.hibernate;

import java.util.List;

import org.androidpn.server.dao.NotificationDao;
import org.androidpn.server.model.Notification;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
/**
 * 推送消息dao实现类
 * @author Administrator
 *
 */
public class NotificationHibernation extends HibernateDaoSupport implements
		NotificationDao {
	
	
	public void save(Notification notification) {
		getHibernateTemplate().save(notification);
	}

	public void remove(Long id) {
		getHibernateTemplate().delete(id);
	}

	public void update(Notification notification) {
		getHibernateTemplate().saveOrUpdate(notification);
	}

	public Notification get(Long id) {
		return (Notification) getHibernateTemplate().get(Notification.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<Notification> getAll() {
		return getHibernateTemplate().find("from Notification where 1=1");
	}
	
	@SuppressWarnings("unchecked")
	public List<Notification> getByName(String name) throws Exception {
		List<Notification> notifications=getHibernateTemplate().find("from Notification where 1=1");
		if(notifications==null||notifications.size()==0){
			throw new Exception("未找到该用户名("+name+")对应的Notifications");
		}else{
			return notifications;
		}
	}

}
