package edu.pitt.dbmi.deepphe.summarization.orm.i2b2meta;

// Generated Apr 29, 2015 4:35:02 PM by Hibernate Tools 4.3.1

import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * TableAccess generated by hbm2java
 */
@Entity
@Table(name = "TABLE_ACCESS", schema = "I2B2METADATA")
public class TableAccess implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String CTableCd;
	private String CTableName;
	private Character CProtectedAccess;
	private BigDecimal CHlevel;
	private String CFullname;
	private String CName;
	private char CSynonymCd;
	private String CVisualattributes;
	private BigDecimal CTotalnum;
	private String CBasecode;
	private Clob CMetadataxml;
	private String CFacttablecolumn;
	private String CDimtablename;
	private String CColumnname;
	private String CColumndatatype;
	private String COperator;
	private String CDimcode;
	private Clob CComment;
	private String CTooltip;
	private Date CEntryDate;
	private Date CChangeDate;
	private Character CStatusCd;
	private String valuetypeCd;

	public TableAccess() {
	}

	public TableAccess(String CTableCd, String CTableName, BigDecimal CHlevel,
			String CFullname, String CName, char CSynonymCd,
			String CVisualattributes, String CFacttablecolumn,
			String CDimtablename, String CColumnname, String CColumndatatype,
			String COperator, String CDimcode) {
		this.CTableCd = CTableCd;
		this.CTableName = CTableName;
		this.CHlevel = CHlevel;
		this.CFullname = CFullname;
		this.CName = CName;
		this.CSynonymCd = CSynonymCd;
		this.CVisualattributes = CVisualattributes;
		this.CFacttablecolumn = CFacttablecolumn;
		this.CDimtablename = CDimtablename;
		this.CColumnname = CColumnname;
		this.CColumndatatype = CColumndatatype;
		this.COperator = COperator;
		this.CDimcode = CDimcode;
	}

	public TableAccess(String CTableCd, String CTableName,
			Character CProtectedAccess, BigDecimal CHlevel, String CFullname,
			String CName, char CSynonymCd, String CVisualattributes,
			BigDecimal CTotalnum, String CBasecode, Clob CMetadataxml,
			String CFacttablecolumn, String CDimtablename, String CColumnname,
			String CColumndatatype, String COperator, String CDimcode,
			Clob CComment, String CTooltip, Date CEntryDate, Date CChangeDate,
			Character CStatusCd, String valuetypeCd) {
		this.CTableCd = CTableCd;
		this.CTableName = CTableName;
		this.CProtectedAccess = CProtectedAccess;
		this.CHlevel = CHlevel;
		this.CFullname = CFullname;
		this.CName = CName;
		this.CSynonymCd = CSynonymCd;
		this.CVisualattributes = CVisualattributes;
		this.CTotalnum = CTotalnum;
		this.CBasecode = CBasecode;
		this.CMetadataxml = CMetadataxml;
		this.CFacttablecolumn = CFacttablecolumn;
		this.CDimtablename = CDimtablename;
		this.CColumnname = CColumnname;
		this.CColumndatatype = CColumndatatype;
		this.COperator = COperator;
		this.CDimcode = CDimcode;
		this.CComment = CComment;
		this.CTooltip = CTooltip;
		this.CEntryDate = CEntryDate;
		this.CChangeDate = CChangeDate;
		this.CStatusCd = CStatusCd;
		this.valuetypeCd = valuetypeCd;
	}

	@Id
	@Column(name = "C_TABLE_CD", unique = true, nullable = false, length = 50)
	public String getCTableCd() {
		return this.CTableCd;
	}

	public void setCTableCd(String CTableCd) {
		this.CTableCd = CTableCd;
	}

	@Column(name = "C_TABLE_NAME", nullable = false, length = 50)
	public String getCTableName() {
		return this.CTableName;
	}

	public void setCTableName(String CTableName) {
		this.CTableName = CTableName;
	}

	@Column(name = "C_PROTECTED_ACCESS", length = 1)
	public Character getCProtectedAccess() {
		return this.CProtectedAccess;
	}

	public void setCProtectedAccess(Character CProtectedAccess) {
		this.CProtectedAccess = CProtectedAccess;
	}

	@Column(name = "C_HLEVEL", nullable = false, precision = 22, scale = 0)
	public BigDecimal getCHlevel() {
		return this.CHlevel;
	}

	public void setCHlevel(BigDecimal CHlevel) {
		this.CHlevel = CHlevel;
	}

	@Column(name = "C_FULLNAME", nullable = false, length = 700)
	public String getCFullname() {
		return this.CFullname;
	}

	public void setCFullname(String CFullname) {
		this.CFullname = CFullname;
	}

	@Column(name = "C_NAME", nullable = false, length = 2000)
	public String getCName() {
		return this.CName;
	}

	public void setCName(String CName) {
		this.CName = CName;
	}

	@Column(name = "C_SYNONYM_CD", nullable = false, length = 1)
	public char getCSynonymCd() {
		return this.CSynonymCd;
	}

	public void setCSynonymCd(char CSynonymCd) {
		this.CSynonymCd = CSynonymCd;
	}

	@Column(name = "C_VISUALATTRIBUTES", nullable = false, length = 3)
	public String getCVisualattributes() {
		return this.CVisualattributes;
	}

	public void setCVisualattributes(String CVisualattributes) {
		this.CVisualattributes = CVisualattributes;
	}

	@Column(name = "C_TOTALNUM", precision = 22, scale = 0)
	public BigDecimal getCTotalnum() {
		return this.CTotalnum;
	}

	public void setCTotalnum(BigDecimal CTotalnum) {
		this.CTotalnum = CTotalnum;
	}

	@Column(name = "C_BASECODE", length = 50)
	public String getCBasecode() {
		return this.CBasecode;
	}

	public void setCBasecode(String CBasecode) {
		this.CBasecode = CBasecode;
	}

	@Column(name = "C_METADATAXML")
	public Clob getCMetadataxml() {
		return this.CMetadataxml;
	}

	public void setCMetadataxml(Clob CMetadataxml) {
		this.CMetadataxml = CMetadataxml;
	}

	@Column(name = "C_FACTTABLECOLUMN", nullable = false, length = 50)
	public String getCFacttablecolumn() {
		return this.CFacttablecolumn;
	}

	public void setCFacttablecolumn(String CFacttablecolumn) {
		this.CFacttablecolumn = CFacttablecolumn;
	}

	@Column(name = "C_DIMTABLENAME", nullable = false, length = 50)
	public String getCDimtablename() {
		return this.CDimtablename;
	}

	public void setCDimtablename(String CDimtablename) {
		this.CDimtablename = CDimtablename;
	}

	@Column(name = "C_COLUMNNAME", nullable = false, length = 50)
	public String getCColumnname() {
		return this.CColumnname;
	}

	public void setCColumnname(String CColumnname) {
		this.CColumnname = CColumnname;
	}

	@Column(name = "C_COLUMNDATATYPE", nullable = false, length = 50)
	public String getCColumndatatype() {
		return this.CColumndatatype;
	}

	public void setCColumndatatype(String CColumndatatype) {
		this.CColumndatatype = CColumndatatype;
	}

	@Column(name = "C_OPERATOR", nullable = false, length = 10)
	public String getCOperator() {
		return this.COperator;
	}

	public void setCOperator(String COperator) {
		this.COperator = COperator;
	}

	@Column(name = "C_DIMCODE", nullable = false, length = 700)
	public String getCDimcode() {
		return this.CDimcode;
	}

	public void setCDimcode(String CDimcode) {
		this.CDimcode = CDimcode;
	}

	@Column(name = "C_COMMENT")
	public Clob getCComment() {
		return this.CComment;
	}

	public void setCComment(Clob CComment) {
		this.CComment = CComment;
	}

	@Column(name = "C_TOOLTIP", length = 900)
	public String getCTooltip() {
		return this.CTooltip;
	}

	public void setCTooltip(String CTooltip) {
		this.CTooltip = CTooltip;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "C_ENTRY_DATE", length = 7)
	public Date getCEntryDate() {
		return this.CEntryDate;
	}

	public void setCEntryDate(Date CEntryDate) {
		this.CEntryDate = CEntryDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "C_CHANGE_DATE", length = 7)
	public Date getCChangeDate() {
		return this.CChangeDate;
	}

	public void setCChangeDate(Date CChangeDate) {
		this.CChangeDate = CChangeDate;
	}

	@Column(name = "C_STATUS_CD", length = 1)
	public Character getCStatusCd() {
		return this.CStatusCd;
	}

	public void setCStatusCd(Character CStatusCd) {
		this.CStatusCd = CStatusCd;
	}

	@Column(name = "VALUETYPE_CD", length = 50)
	public String getValuetypeCd() {
		return this.valuetypeCd;
	}

	public void setValuetypeCd(String valuetypeCd) {
		this.valuetypeCd = valuetypeCd;
	}

}
