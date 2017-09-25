package law;

import java.util.ArrayList;

public class LawNode {

	private ArrayList<LawNode> children;
	private String type = "";
	private String number = "";
	private String title = "";
	private String details = "";

	public ArrayList<LawNode> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<LawNode> children) {
		this.children = children;
	}

	public void addChild(LawNode child) {
		if (children == null) {
			children = new ArrayList<>();
		}
		children.add(child);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = appendContent(this.type, type.trim());
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = appendContent(this.number, number.trim());
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = appendContent(this.title, title.trim());
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = appendContent(this.details, details.trim());
	}

	private String appendContent(String base, String extent) {
		if (base.length() > 0) {
			return base.concat("\n" + extent);
		} else {
			return extent;
		}
	}

}
