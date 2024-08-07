package org.backlogconflict.code.report;

import org.json.JSONObject;

public class ConflictPair {
	private String conflictPair1;
	private String conflictPair2;
	private String usId1;
	private String usId2;
	private JSONObject jsonConflict1;
	private JSONObject jsonConflict2;
	private String conflictReason;
	private String pId;
	private String entity;
	private String entityContainUs2;
	private String actionUs1;
	private String actionUs2;
	private String actionAnnotationUs1;
	private String actionAnnotationUs2;

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
	public String getNounMainUs1() {
		return entity;
	}
	public void setNounMainUs1(String nounUs1) {
		this.entity = nounUs1;
	}
	public String getActionAnnotationUs1() {
		return actionAnnotationUs1;
	}
	public void setActionAnnotationUs1(String actionAnnotationUs1) {
		this.actionAnnotationUs1 = actionAnnotationUs1;
	}
	public String getActionAnnotationUs2() {
		return actionAnnotationUs2;
	}
	public void setActionAnnotationUs2(String actionAnnotationUs2) {
		this.actionAnnotationUs2 = actionAnnotationUs2;
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

	public String getNounContainUs2() {
		return entityContainUs2;
	}
	public void setNounContainUs2(String nounContainUs2) {
		this.entityContainUs2 = nounContainUs2;
	}
	public String getUsId2() {
		return usId2;
	}
	public void setUsId2(String usId2) {
		this.usId2 = usId2;
	}
	public String getUsId1() {
		return usId1;
	}
	public void setUsId1(String usId1) {
		this.usId1 = usId1;
	}




}
