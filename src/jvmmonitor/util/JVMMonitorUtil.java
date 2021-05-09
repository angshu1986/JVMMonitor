package jvmmonitor.util;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.List;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import jvmmonitor.constants.JVMConstants;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class JVMMonitorUtil implements JVMConstants {

	private static JVMNotificationListener listener;
	private static MBeanServerConnection mBeanServerConnection;
	private static JMXConnector connector;

	public static List<VirtualMachineDescriptor> getVMDescriptors() {
		return VirtualMachine.list();
	}

	public static String getServiceUrlString(String pid) throws Exception {
		VirtualMachine vm = null;
		String connectorAddress = null;
		try {
			vm = VirtualMachine.attach(pid);
			connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
			if (connectorAddress == null) {
				String agent = vm.getSystemProperties().getProperty(JAVA_HOME) + File.separator + LIB + File.separator
						+ MANAGEMENT_AGENT_JAR;
				vm.loadAgent(agent);
				connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
				if (connectorAddress == null) {
					throw new IllegalStateException("Got null connection address");
				}
			}
		} finally {
			vm.detach();
		}
		return connectorAddress;
	}

	public static boolean connectAndEstablishBeans(String url) {
		try {
			listener = new JVMNotificationListener(url);
			JMXServiceURL serviceUrl = new JMXServiceURL(url);
			connector = JMXConnectorFactory.newJMXConnector(serviceUrl, null);
			connector.addConnectionNotificationListener(listener, null, serviceUrl);
			connector.connect();
			mBeanServerConnection = connector.getMBeanServerConnection();
			return true;
		} catch (Exception e) {
			disconnect();
			throw new IllegalStateException("Couldn't connect to JVM with URL: " + url, e);
		}
	}

	public static boolean disconnect() {
		boolean result = false;
		try {
			if (connector != null && listener != null) {
				connector.removeConnectionNotificationListener(listener);
				connector.close();
			}
			result = true;
		} catch (ListenerNotFoundException e) {
			System.out.println("Ignored" + e);
			result = true;
		} catch (Exception e) {
			System.out.println("Closing the connection failed for JVM" + e);
		} finally {
			mBeanServerConnection = null;
			connector = null;
		}
		return result;
	}

	public static RuntimeMXBean getRuntimeMXBean() throws IOException {
		if (mBeanServerConnection != null) {
			return ManagementFactory.newPlatformMXBeanProxy(mBeanServerConnection,
					ManagementFactory.RUNTIME_MXBEAN_NAME, RuntimeMXBean.class);
		}
		throw new IllegalArgumentException("mBeanServerConnection cannot be null");
	}

	public static synchronized OperatingSystemMXBean getOperatingSystemMXBean() throws IOException {
		if (mBeanServerConnection != null) {
			return ManagementFactory.newPlatformMXBeanProxy(mBeanServerConnection,
					ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
		}
		throw new IllegalArgumentException("mBeanServerConnection cannot be null");
	}

	public static ThreadMXBean getThreadMXBean() throws IOException {
		if (mBeanServerConnection != null) {
			return ManagementFactory.newPlatformMXBeanProxy(mBeanServerConnection, ManagementFactory.THREAD_MXBEAN_NAME,
					ThreadMXBean.class);
		}
		throw new IllegalArgumentException("mBeanServerConnection cannot be null");
	}

	public static MemoryMXBean getMemoryMXBean() throws IOException {
		if (mBeanServerConnection != null) {
			return ManagementFactory.newPlatformMXBeanProxy(mBeanServerConnection, ManagementFactory.MEMORY_MXBEAN_NAME,
					MemoryMXBean.class);
		}
		throw new IllegalArgumentException("mBeanServerConnection cannot be null");
	}

	public static MemoryManagerMXBean getMemoryManagerMXBean() throws IOException {
		if (mBeanServerConnection != null) {
			return ManagementFactory.newPlatformMXBeanProxy(mBeanServerConnection,
					ManagementFactory.MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE, MemoryManagerMXBean.class);
		}
		throw new IllegalArgumentException("mBeanServerConnection cannot be null");
	}

	public static MemoryPoolMXBean getMemoryPoolMXBean() throws IOException {
		if (mBeanServerConnection != null) {
			return ManagementFactory.newPlatformMXBeanProxy(mBeanServerConnection,
					ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE, MemoryPoolMXBean.class);
		}
		throw new IllegalArgumentException("mBeanServerConnection cannot be null");
	}
}
