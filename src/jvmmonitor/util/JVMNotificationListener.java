package jvmmonitor.util;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnectionNotification;

public class JVMNotificationListener implements NotificationListener {
	private final String serviceUrl;

	public JVMNotificationListener(String serviceUrl) {
		super();
		this.serviceUrl = serviceUrl;
	}

	@Override
	public void handleNotification(Notification notification, Object handback) {
		final JMXConnectionNotification notify = (JMXConnectionNotification) notification;
		if (!handback.equals(serviceUrl)) {
			return;
		}
		if (notify.getType().equals(JMXConnectionNotification.CLOSED)) {
			JVMMonitorUtil.disconnect();
		} else if (notify.getType().equals(JMXConnectionNotification.FAILED)) {
			JVMMonitorUtil.disconnect();
		} else if (notify.getType().equals(JMXConnectionNotification.NOTIFS_LOST)) {
			JVMMonitorUtil.disconnect();
		}
	}
}
