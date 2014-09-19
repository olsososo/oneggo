package com.oneggo.snacks.datatype;

public enum Action {
	addview("addview"), addlike("addlike"), removelike("removelike");
	
	private String action;
	
	Action(String action) {
		this.action = action;
    }
	
    public String getString() {
        return action;
    }	
}
