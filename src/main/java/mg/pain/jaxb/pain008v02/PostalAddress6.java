package mg.pain.jaxb.pain008v02;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PostalAddress6", propOrder = {
        "strtNm", "bldgNb", "pstCd", "twnNm", "ctry", "adrLine"
})
public class PostalAddress6 {

    @XmlElement(name = "StrtNm")
    protected String strtNm;

    @XmlElement(name = "BldgNb")
    protected String bldgNb;

    @XmlElement(name = "PstCd")
    protected String pstCd;

    @XmlElement(name = "TwnNm")
    protected String twnNm;

    @XmlElement(name = "Ctry")
    protected String ctry;

    @XmlElement(name = "AdrLine")
    protected List<String> adrLine;

    public String getStrtNm() { return strtNm; }
    public void setStrtNm(String value) { this.strtNm = value; }

    public String getBldgNb() { return bldgNb; }
    public void setBldgNb(String value) { this.bldgNb = value; }

    public String getPstCd() { return pstCd; }
    public void setPstCd(String value) { this.pstCd = value; }

    public String getTwnNm() { return twnNm; }
    public void setTwnNm(String value) { this.twnNm = value; }

    public String getCtry() { return ctry; }
    public void setCtry(String value) { this.ctry = value; }

    public List<String> getAdrLine() {
        if (adrLine == null) adrLine = new ArrayList<>();
        return adrLine;
    }
}
