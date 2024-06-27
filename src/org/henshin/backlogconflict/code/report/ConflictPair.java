package org.henshin.backlogconflict.code.report;

import org.json.JSONObject;

public class ConflictPair {
	private String conflictPair1;
	private String conflictPair2;
	private JSONObject jsonConflict1;
	private JSONObject jsonConflict2;
	private String conflictReason;
	private String pId;
	private String noun;
	private String actionUs1;
	private String actionUs2;
	private String actionRuleUs1;
	private String actionRuleUs2;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass())return false;
		
		ConflictPair that = (ConflictPair) o;
		
		if(!conflictPair1.equals(that.conflictPair1))return false;
		return conflictPair2.equals(that.conflictPair2);
	}
	@Override
	public int hashCode() {
		int result = conflictPair1.hashCode();
		result=31 * result+ conflictPair2.hashCode();
		return result;
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public JSONObject getJsonConflict1() {
		return jsonConflict1;
	}

	public void setJsonConflict1(JSONObject jsonConflict1) {
		this.jsonConflict1 = jsonConflict1;
	}

	public JSONObject getJsonConflict2() {
		return jsonConflict2;
	}

	public void setJsonConflict2(JSONObject jsonConflict2) {
		this.jsonConflict2 = jsonConflict2;
	}

	public String getConflictReason() {
		return conflictReason;
	}

	public void setConflictReason(String conflictReason) {
		this.conflictReason = conflictReason;
	}

	public ConflictPair() {

	}

	public String getConflictPair1() {
		return conflictPair1;
	}

	public void setConflictPair1(String conflictPair1) {
		this.conflictPair1 = conflictPair1;
	}

	public String getConflictPair2() {
		return conflictPair2;
	}

	public void setConflictPair2(String conflictPair2) {
		this.conflictPair2 = conflictPair2;
	}
	public String getNoun() {
		return noun;
	}
	public void setNoun(String noun) {
		this.noun = noun;
	}
	public String getActionRuleUs1() {
		return actionRuleUs1;
	}
	public void setActionRuleUs1(String actionRuleUs1) {
		this.actionRuleUs1 = actionRuleUs1;
	}
	public String getActionRuleUs2() {
		return actionRuleUs2;
	}
	public void setActionRuleUs2(String actionRuleUs2) {
		this.actionRuleUs2 = actionRuleUs2;
	}
	public String getActionUs1() {
		return actionUs1;
	}
	public void setActionUs1(String actionUs1) {
		this.actionUs1 = actionUs1;
	}
	public String getActionUs2() {
		return actionUs2;
	}
	public void setActionUs2(String actionUs2) {
		this.actionUs2 = actionUs2;
	}

}
