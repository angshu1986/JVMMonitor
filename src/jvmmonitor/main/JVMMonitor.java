package jvmmonitor.main;

import java.io.IOException;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import com.sun.tools.attach.VirtualMachineDescriptor;

import jvmmonitor.util.FileUtil;
import jvmmonitor.util.JVMMonitorUtil;

public class JVMMonitor {
	public static void main(String[] args) {
		List<VirtualMachineDescriptor> vmDescList = JVMMonitorUtil.getVMDescriptors();
		for (VirtualMachineDescriptor vmd : vmDescList) {
			/*
			 * if (!vmd.displayName().equals("WaitingThreadTest")) { continue; }
			 */
			StringBuilder sb = new StringBuilder();
			try {
				String vmId = vmd.id();
				String className = vmd.displayName();
				if (null == className || "".equals(className)) {
					continue;
				}
				String url = JVMMonitorUtil.getServiceUrlString(vmId);
				System.out.println("Stats for vm pid: " + vmId + ":: classname : " + className + ":: url : " + url);
				if (JVMMonitorUtil.connectAndEstablishBeans(url)) {
					ThreadMXBean tBean = JVMMonitorUtil.getThreadMXBean();
					RuntimeMXBean rBean = JVMMonitorUtil.getRuntimeMXBean();
					MemoryMXBean memoryBean = JVMMonitorUtil.getMemoryMXBean();

					MemoryUsage heap = memoryBean.getHeapMemoryUsage();
					MemoryUsage nonHeap = memoryBean.getNonHeapMemoryUsage();

					Long usedHeap = new Long(heap.getUsed() / 1024);
					Long maxHeap = new Long(heap.getMax() / 1024);

					Long usedNonHeap = new Long(nonHeap.getUsed() / 1024);
					Long maxNonHeap = new Long(nonHeap.getMax() / 1024);

					long[] tids = tBean.getAllThreadIds();
					sb.append("Specification: " + rBean.getSpecName() + " Vendor: " + rBean.getSpecVendor()
							+ " Version: " + rBean.getSpecVersion() + "VM Options :: Name: " + rBean.getVmName()
							+ " Vendor: " + rBean.getVmVendor() + " Version: " + rBean.getVmVersion());
					sb.append("\n");
					System.out.println("Specification: " + rBean.getSpecName() + " Vendor: " + rBean.getSpecVendor()
							+ " Version: " + rBean.getSpecVersion() + "VM Options :: Name: " + rBean.getVmName()
							+ " Vendor: " + rBean.getVmVendor() + " Version: " + rBean.getVmVersion());
					sb.append("PID: " + vmd.id() + " Process Name: " + vmd.displayName());
					sb.append("\n");
					System.out.println("PID: " + vmd.id() + " Process Name: " + vmd.displayName());
					sb.append("VM Start time: " + new Date(rBean.getStartTime()));
					sb.append("\n");
					sb.append("Snapshot taken at: " + new Date(System.currentTimeMillis()));
					sb.append("\n");
					System.out.println("VM Start time: " + new Date(rBean.getStartTime()));

					double usedHeapPert = (usedHeap.doubleValue() * 100.0) / maxHeap.doubleValue();
					double usedNonHeapPert = (usedHeap.doubleValue() * 100.0) / maxHeap.doubleValue();

					DecimalFormat df = new DecimalFormat("##.##");

					System.out.println("Total heap memory: " + maxHeap + " kilobytes");
					System.out.println("Heap memory usage: " + usedHeap + " kilobytes");
					sb.append("Total heap memory: " + maxHeap + " kilobytes");
					sb.append("\n");
					sb.append("Heap memory usage: " + usedHeap + " kilobytes");
					sb.append("\n");
					System.out.println("Heap memory usage percentage: " + df.format(usedHeapPert) + " %");
					sb.append("Heap memory usage percentage: " + df.format(usedHeapPert) + " %");
					sb.append("\n");
					System.out.println("Total non heap memory: " + maxNonHeap + " kilobytes");
					System.out.println("Non heap memory usage: " + usedNonHeap + " kilobytes");

					sb.append("Total non heap memory: " + maxNonHeap + " kilobytes");
					sb.append("\n");
					sb.append("Non heap memory usage: " + usedNonHeap + " kilobytes");
					sb.append("\n");

					System.out.println("Non heap memory usage percentage: " + df.format(usedNonHeapPert) + " %");

					sb.append("Non heap memory usage percentage: " + df.format(usedNonHeapPert) + " %");
					sb.append("\n");

					sb.append("Daemon threads : " + tBean.getDaemonThreadCount());
					sb.append("\n");
					System.out.println("Daemon threads : " + tBean.getDaemonThreadCount());
					System.out.println("********************Printing thread information********************");
					sb.append("********************Printing thread information********************");
					sb.append("\n");
					for (long id : tids) {
						ThreadInfo info = tBean.getThreadInfo(id);
						System.out.println(tBean.getThreadCpuTime(id) / 1000);
						System.out.println(tBean.getThreadUserTime(id) / 1000);
						sb.append("Thread id: " + id + " Thread name: " + info.getThreadName() + " State: "
								+ info.getThreadState().name() + " Wait count: " + info.getWaitedCount()
								+ " Waiting time: " + info.getWaitedTime() + " ms" + " Suspended: " + info.isSuspended()
								+ " CPU Use time: " + tBean.getThreadCpuTime(id) / 1000 + " ms");
						System.out.println("Thread id: " + id + " Thread name: " + info.getThreadName() + " State: "
								+ info.getThreadState().name() + " Wait count: " + info.getWaitedCount()
								+ " Waiting time: " + info.getWaitedTime() + " ms" + " Suspended: " + info.isSuspended()
								+ " CPU Use time: " + tBean.getThreadCpuTime(id) / 1000 + " ms");
						sb.append("\n");
						sb.append("\n");
						System.out.println("");

					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (JVMMonitorUtil.disconnect()) {
					System.out.println("Successfully disconnected from VM");
					sb.append("Successfully disconnected from VM");
					try {
						FileUtil.writeToFile(vmd.id(), sb.toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

}
