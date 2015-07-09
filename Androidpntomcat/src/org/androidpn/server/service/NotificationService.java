package org.androidpn.server.service;

import java.util.List;

import org.androidpn.server.model.Notification;
/**
 * 推送消息服务层
 * @author Administrator
 *
 */
public interface NotificationService {
	public void save(Notification notification);
	public void remove(Long id);
	public void update(Notification notification);
	public Notification get(Long id);
	public List<Notification> getAll();
	public List<Notification>  getByName(String name) throws Exception;
}
