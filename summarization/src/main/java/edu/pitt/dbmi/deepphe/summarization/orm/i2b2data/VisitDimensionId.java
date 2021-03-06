package edu.pitt.dbmi.deepphe.summarization.orm.i2b2data;

// Generated Apr 15, 2015 4:30:25 PM by Hibernate Tools 4.3.1

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * VisitDimensionId generated by hbm2java
 */
@Embeddable
public class VisitDimensionId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private BigDecimal encounterNum;
	private BigDecimal patientNum;

	public VisitDimensionId() {
	}

	public VisitDimensionId(BigDecimal encounterNum, BigDecimal patientNum) {
		this.encounterNum = encounterNum;
		this.patientNum = patientNum;
	}

	@Column(name = "ENCOUNTER_NUM", nullable = false, precision = 38, scale = 0)
	public BigDecimal getEncounterNum() {
		return this.encounterNum;
	}

	public void setEncounterNum(BigDecimal encounterNum) {
		this.encounterNum = encounterNum;
	}

	@Column(name = "PATIENT_NUM", nullable = false, precision = 38, scale = 0)
	public BigDecimal getPatientNum() {
		return this.patientNum;
	}

	public void setPatientNum(BigDecimal patientNum) {
		this.patientNum = patientNum;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof VisitDimensionId))
			return false;
		VisitDimensionId castOther = (VisitDimensionId) other;

		return ((this.getEncounterNum() == castOther.getEncounterNum()) || (this
				.getEncounterNum() != null
				&& castOther.getEncounterNum() != null && this
				.getEncounterNum().equals(castOther.getEncounterNum())))
				&& ((this.getPatientNum() == castOther.getPatientNum()) || (this
						.getPatientNum() != null
						&& castOther.getPatientNum() != null && this
						.getPatientNum().equals(castOther.getPatientNum())));
	}

	public int hashCode() {
		int result = 17;

		result = 37
				* result
				+ (getEncounterNum() == null ? 0 : this.getEncounterNum()
						.hashCode());
		result = 37
				* result
				+ (getPatientNum() == null ? 0 : this.getPatientNum()
						.hashCode());
		return result;
	}

}
