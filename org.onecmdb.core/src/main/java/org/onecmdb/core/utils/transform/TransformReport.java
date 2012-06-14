package org.onecmdb.core.utils.transform;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class TransformReport {
	public static String DEBUG_MSG = "DEBUG";
	public static String INFO_MSG = "INFO";
	public static String SECTION_MSG = "SECTION";
	public static String WARNING_MSG = "WARNING";
	public static String ERROR_MSG = "ERROR";

	
	private long start;
	
	List sections = new ArrayList();
	List currentSection;
	List prevSection;
	
	
	class Message {
		
		private String type;
		private String msg;
		
		public Message(String type, String msg) {
			this.type = type;
			this.msg = msg;
		}

		public String getType() {
			return type;
		}

		public String getMsg() {
			return msg;
		}
		
		public String toString() {
			return(getType() + ":" + getMsg());
		}
	}
	
	public TransformReport() {
	}
	
	public void startReport() {
		pushSection("Start " + new Date());
		this.start = System.currentTimeMillis();
	}
	
	public void stopReport() {
		long stop = System.currentTimeMillis();
		popSection("Stop " + new Date() + " Total Time:" + (stop-this.start));
	}
	
	public void pushSection(String startSectionNote) {
		List newSection = new ArrayList();
		if (currentSection != null) {
			currentSection.add(newSection);
			sections.add(currentSection);
		}
		prevSection = currentSection;
		currentSection = newSection;
		currentSection.add(startSectionNote);
	}
	
	public void popSection(String endSectionNote) {
		if (prevSection != null) {
			prevSection.add(new Message(SECTION_MSG, endSectionNote));
		}
		currentSection = prevSection;
	}
	
	
	public void addError(String msg, Object instance) {
		if (currentSection != null) {
			currentSection.add(new Message(ERROR_MSG, msg + ":" + instance.toString()));
		}
	}
	
	public void addWarn(String msg, Object data) {
		if (currentSection != null) {
			currentSection.add(new Message(WARNING_MSG, msg + ":" + data.toString()));
		}
	}

	public void addInfo(String msg, Object instance) {
		if (currentSection != null) {
			currentSection.add(new Message(INFO_MSG, msg + ":" + instance.toString()));
		}
	}
	
	public void addDebug(String msg) {
		if (currentSection != null) {
			currentSection.add(new Message(DEBUG_MSG, msg));
		}
	}
	
	public String toString() {
		return(getReport(0, sections));
	}
	
	public String getReport(int level, List section) {
		StringBuffer buffer = new StringBuffer();
		for (Object o : sections) {
			if (o instanceof Message) {
				buffer.append(getTab(level));
				buffer.append(o.toString());
				buffer.append("\n");
			}
			if (o instanceof List) {
				buffer.append(getReport(level+1, (List)o));
			}
		}
		return(buffer.toString());
	}

	private StringBuffer getTab(int level) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < level; i++) {
			buffer.append("\t");
		}
		return(buffer);
	}


}
