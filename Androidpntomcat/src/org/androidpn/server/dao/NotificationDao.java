package org.androidpn.server.dao;

import java.util.List;

import org.androidpn.server.model.Notification;


public interface NotificationDao {
public void save(Notification notification);
public void remove(Long id);
public void update(Notification notification);
public Notification get(Long id);
public List<Notification> getAll();
public List<Notification>  getByName(String name) throws Exception;
}
