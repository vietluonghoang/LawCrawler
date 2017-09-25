package medical;

import java.util.ArrayList;

public class Disease {
	private String name;
	private String url;
	private ArrayList<String> relatedUrls;

	private String general = "";
	private String symptom = "";
	private String diagnose = "";
	private String cause = "";
	private String treatment = "";
	private String obviate = "";
	private String traditional = "";
	private String risk = "";
	private String complication = "";
	private String care = "";

	public String[] getInfo() {
		return new String[] { getName(), getGeneral(), getSymptom(), getDiagnose(), getCause(), getTreatment(),
				getObviate(), getTraditional(), getRisk(), getComplication(), getCare() };
	}

	public String getGeneral() {
		return general;
	}

	public void setGeneral(String general) {
		this.general += general;
//		this.general += "\n " + general;
	}

	public String getSymptom() {
		return symptom;
	}

	public void setSymptom(String symptom) {
//		this.symptom += "\n " + symptom;
		this.symptom += symptom;
	}

	public String getDiagnose() {
		return diagnose;
	}

	public void setDiagnose(String diagnose) {
		this.diagnose += diagnose;
//		this.diagnose += "\n " + diagnose;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause += cause;
//		this.cause += "\n " + cause;
	}

	public String getTreatment() {
		return treatment;
	}

	public void setTreatment(String treatment) {
		this.treatment += treatment;
//		this.treatment += "\n " + treatment;
	}

	public String getObviate() {
		return obviate;
	}

	public void setObviate(String obviate) {
		this.obviate += obviate;
//		this.obviate += "\n " + obviate;
	}

	public String getTraditional() {
		return traditional;
	}

	public void setTraditional(String traditional) {
		this.traditional += traditional;
//		this.traditional += "\n " + traditional;
	}

	public String getRisk() {
		return risk;
	}

	public void setRisk(String risk) {
		this.risk += risk;
//		this.risk += "\n " + risk;
	}

	public String getComplication() {
		return complication;
	}

	public void setComplication(String complication) {
		this.complication += complication;
//		this.complication += "\n " + complication;
	}

	public String getCare() {
		return care;
	}

	public void setCare(String care) {
		this.care += care;
//		this.care += "\n " + care;
	}

	public void setRelatedUrls(ArrayList<String> relatedUrls) {
		this.relatedUrls = relatedUrls;
	}

	public Disease(String name, String url) {
		this.name = name;
		this.url = url;
		relatedUrls = new ArrayList<String>();
	}

	public Disease() {
	}
	
	public ArrayList<String> getRelatedUrls() {
		return relatedUrls;
	}

	public void setRelatedUrls(String url) {
		if (url != null && url != "") {
			int size = relatedUrls.size();
			for (String existUrl : relatedUrls) {
				if (existUrl.equals(url)) {
					break;
				}
				size--;
			}
			if (size < 1) {
				relatedUrls.add(url);
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
