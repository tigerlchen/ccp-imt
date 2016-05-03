package com.alibaba.imt.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 根据group分组，生成menu
 * @author hongwei.quhw
 *
 */
public class ImtGroup {
	private final String uuid;
	private ImtGroup previous;
	private List<ImtGroup> nexts;
	private final String name;
	private List<InterfaceInfo> interfaceInfos;

	public ImtGroup(String name) {
		this.name = name;
		uuid = UUID.randomUUID().toString();
	}

	public String getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public ImtGroup getPrevious() {
		return previous;
	}

	public void setPrevious(ImtGroup previous) {
		this.previous = previous;
	}

	public List<ImtGroup> getNexts() {
		return nexts;
	}

	public void addNext(ImtGroup next) {
		if (null == nexts) {
			nexts = new ArrayList<ImtGroup>();
		}
		nexts.add(next);
	}

	public List<InterfaceInfo> getInterfaceInfos() {
		return interfaceInfos;
	}

	public void addInterfaceInfo(InterfaceInfo interfaceInfo) {
		if (null == interfaceInfos) {
			interfaceInfos = new ArrayList<InterfaceInfo>();
		}
		interfaceInfos.add(interfaceInfo);
	}

	public ImtGroup getNextGroupByName(String name) {
		if (null == nexts) {
			return null;
		}

		for (ImtGroup imtGroup : nexts) {
			if (name.equals(imtGroup.getName())) {
				return imtGroup;
			}
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImtGroup other = (ImtGroup) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}
}
