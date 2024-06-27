package org.henshin.backlogconflict.code.report;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;

public class ConflictItems {
	private List<Entity> entity;
	private List<Action> action;
	private List<Triggers> triggers;
	private List<Targets> targets;
	private List<Contains> contains;
	private String textUs1;
	private String textUs2;
	private String UsNr1;
	private String UsNr2;

	// this values are used to find out how many conflict pairs
	// are found in which sentence part(main vs benefit)
	private int mainConflictCount;

	public int getMainConflictCount() {
		return mainConflictCount;
	}

	public void setMainConflictCount(int mainConflictCount) {
		this.mainConflictCount = mainConflictCount;
	}

	public String getUsNr1() {
		return UsNr1;
	}

	public void setUsNr1(String usNr1) {
		UsNr1 = usNr1;
	}

	public String getUsNr2() {
		return UsNr2;
	}

	public void setUsNr2(String usNr2) {
		UsNr2 = usNr2;
	}

	public ConflictItems() {

		this.entity = new ArrayList<>();
		this.action = new ArrayList<>();
		this.triggers = new ArrayList<>();
		this.targets = new ArrayList<>();
		this.contains = new ArrayList<>();
	}

	public void addEntity(Entity entity) {
		this.entity.add(entity);

	}

	public void addAction(Action action) {
		this.action.add(action);
	}

	public void addTriggers(Triggers trigger) {
		triggers.add(trigger);
	}

	public void addTargets(Targets target) {
		targets.add(target);
	}

	public void addContains(Contains contain) {
		contains.add(contain);
	}

	public List<Entity> getEntity() {
		return entity;
	}

	public List<Action> getActions() {
		return action;
	}

	public List<Triggers> getTriggers() {
		return triggers;
	}

	public List<Targets> getTargets() {
		return targets;
	}

	public List<Contains> getContains() {
		return contains;
	}

	public String getTextUs1() {
		return textUs1;
	}

	public void setTextUs1(String textUs1) {
		this.textUs1 = textUs1;
	}

	public String getTextUs2() {
		return textUs2;
	}

	public void setTextUs2(String textUs2) {
		this.textUs2 = textUs2;
	}

}
