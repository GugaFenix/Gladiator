package me.gugafenix.legionmc.glad.objects;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;

public class Confirmation {
	private static Map<String, Long> map = Maps.newHashMap();
	private UUID uuid;
	private long time;
	private boolean confirmed;
	
	public Confirmation(UUID uuid, long time, boolean confirmed) {
		this.uuid = uuid;
		this.confirmed = false;
		this.time = time;
	}
	
	public boolean isConfirmed() { return confirmed; }

	public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }

	public UUID getUuid() { return uuid; }
	
	public void setUuid(UUID uuid) { this.uuid = uuid; }
	
	public long getTime() { return time; }
	
	public void setTime(long time) { this.time = time; }

	public static Map<String, Long> getMap() { return map; }
	
}
