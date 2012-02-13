package org.tolweb.btol;

import org.tolweb.treegrow.main.StringUtils;

/**
 * @hibernate.class table="PCRProtocols"
 * @author Danny
 *
 */
public class PCRProtocol extends NotesObject implements Defunct {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5419653108952689089L;
	public static final int STANDARD = 0;
	public static final int STEP_UP = 1;
	public static final int TOUCH_DOWN = 2;
    private int protocolType;
    private float ddH20Prop;
    private float templateProp;
    private int forwardPrimerConc;
    private float forwardPrimerProp;
    private int reversePrimerConc;
    private float reversePrimerProp;
    private float tenXBufferProp;
    private int dntpConc;
    private float dntpProp;
    private float mgCl2Added;
    private String taqName;
    private float taqProp;
    private int iniDTime;
    private float iniDTemp;
    private int stages;
    private int c1Cycles;
    private int d1Time;
    private float d1Temp;
    private int a1Time;
    private float a1Temp;
    private int e1Time;
    private float e1Temp;
    private int c2Cycles;
    private int d2Time;
    private float d2Temp;
    private int a2Time;
    private float a2Temp;
    private int e2Time;
    private int e2Temp;
    private float finalExtentionTemp;
    private int finalExtentionTime;
    private boolean defunct;
    /**
     * @hibernate.property
     * @return Returns the a1Temp.
     */
    public float getA1Temp() {
        return a1Temp;
    }
    /**
     * @param temp The a1Temp to set.
     */
    public void setA1Temp(float temp) {
        a1Temp = temp;
    }
    /**
     * @hibernate.property
     * @return Returns the a1Time.
     */
    public int getA1Time() {
        return a1Time;
    }
    public String getA1TimeString() {
    	return StringUtils.getTimeStringFromSeconds(getA1Time());
    }
    /**
     * @param time The a1Time to set.
     */
    public void setA1Time(int time) {
        a1Time = time;
    }
    /**
     * @hibernate.property
     * @return Returns the a2Temp.
     */
    public float getA2Temp() {
        return a2Temp;
    }
    /**
     * @param temp The a2Temp to set.
     */
    public void setA2Temp(float temp) {
        a2Temp = temp;
    }
    /**
     * @hibernate.property
     * @return Returns the a2Time.
     */
    public int getA2Time() {
        return a2Time;
    }
    public String getA2TimeString() {
    	return StringUtils.getTimeStringFromSeconds(getA2Time());
    }
    /**
     * @param time The a2Time to set.
     */
    public void setA2Time(int time) {
        a2Time = time;
    }
    /**
     * @hibernate.property
     * @return Returns the c1Cycles.
     */
    public int getC1Cycles() {
        return c1Cycles;
    }
    /**
     * @param cycles The c1Cycles to set.
     */
    public void setC1Cycles(int cycles) {
        c1Cycles = cycles;
    }
    /**
     * @hibernate.property
     * @return Returns the c2Cycles.
     */
    public int getC2Cycles() {
        return c2Cycles;
    }
    /**
     * @param cycles The c2Cycles to set.
     */
    public void setC2Cycles(int cycles) {
        c2Cycles = cycles;
    }
    /**
     * @hibernate.property
     * @return Returns the d1Temp.
     */
    public float getD1Temp() {
        return d1Temp;
    }
    /**
     * @param temp The d1Temp to set.
     */
    public void setD1Temp(float temp) {
        d1Temp = temp;
    }
    /**
     * @hibernate.property
     * @return Returns the d1Time.
     */
    public int getD1Time() {
        return d1Time;
    }
    public String getD1TimeString() {
    	return StringUtils.getTimeStringFromSeconds(getD1Time());
    }
    /**
     * @param time The d1Time to set.
     */
    public void setD1Time(int time) {
        d1Time = time;
    }
    /**
     * @hibernate.property
     * @return Returns the d2Temp.
     */
    public float getD2Temp() {
        return d2Temp;
    }
    /**
     * @param temp The d2Temp to set.
     */
    public void setD2Temp(float temp) {
        d2Temp = temp;
    }
    /**
     * @hibernate.property
     * @return Returns the d2Time.
     */
    public int getD2Time() {
        return d2Time;
    }
    public String getD2TimeString() {
    	return StringUtils.getTimeStringFromSeconds(getD2Time());
    }
    /**
     * @param time The d2Time to set.
     */
    public void setD2Time(int time) {
        d2Time = time;
    }
    /**
     * @hibernate.property
     * @return Returns the ddH20Prop.
     */
    public float getDdH20Prop() {
        return ddH20Prop;
    }
    /**
     * @param ddH20Prop The ddH20Prop to set.
     */
    public void setDdH20Prop(float ddH20Prop) {
        this.ddH20Prop = ddH20Prop;
    }
    /**
     * @hibernate.property
     * @return Returns the dntpConc.
     */
    public int getDntpConc() {
        return dntpConc;
    }
    /**
     * @param conc The dntpConc to set.
     */
    public void setDntpConc(int conc) {
        dntpConc = conc;
    }
    /**
     * @hibernate.property
     * @return Returns the dntpProp.
     */
    public float getDntpProp() {
        return dntpProp;
    }
    /**
     * @param prop The dntpProp to set.
     */
    public void setDntpProp(float prop) {
        dntpProp = prop;
    }
    /**
     * @hibernate.property
     * @return Returns the e1Temp.
     */
    public float getE1Temp() {
        return e1Temp;
    }
    /**
     * @param temp The e1Temp to set.
     */
    public void setE1Temp(float temp) {
        e1Temp = temp;
    }
    /**
     * @hibernate.property
     * @return Returns the e1Time.
     */
    public int getE1Time() {
        return e1Time;
    }
    public String getE1TimeString() {
    	return StringUtils.getTimeStringFromSeconds(getE1Time());
    }
    /**
     * @param time The e1Time to set.
     */
    public void setE1Time(int time) {
        e1Time = time;
    }
    /**
     * @hibernate.property
     * @return Returns the e2Time.
     */
    public int getE2Time() {
        return e2Time;
    }
    public String getE2TimeString() {
    	return StringUtils.getTimeStringFromSeconds(getE2Time());
    }
    /**
     * @param time The e2Time to set.
     */
    public void setE2Time(int time) {
        e2Time = time;
    }
    /**
     * @hibernate.property
     * @return Returns the e2Temp.
     */
    public int getE2Temp() {
        return e2Temp;
    }
    /**
     * @param e2Temp The e2Temp to set.
     */
    public void setE2Temp(int finE) {
        this.e2Temp = finE;
    }
    /**
     * @hibernate.property
     * @return Returns the forwardPrimerConc.
     */
    public int getForwardPrimerConc() {
        return forwardPrimerConc;
    }
    /**
     * @param prConc The forwardPrimerConc to set.
     */
    public void setForwardPrimerConc(int prConc) {
        forwardPrimerConc = prConc;
    }
    /**
     * @hibernate.property
     * @return Returns the forwardPrimerProp.
     */
    public float getForwardPrimerProp() {
        return forwardPrimerProp;
    }
    /**
     * @param prProp The forwardPrimerProp to set.
     */
    public void setForwardPrimerProp(float prProp) {
        forwardPrimerProp = prProp;
    }
    /**
     * @hibernate.property
     * @return Returns the iniDTemp.
     */
    public float getIniDTemp() {
        return iniDTemp;
    }
    /**
     * 
     * @param iniDTemp The iniDTemp to set.
     */
    public void setIniDTemp(float iniDTemp) {
        this.iniDTemp = iniDTemp;
    }
    /**
     * @hibernate.property
     * @return Returns the iniDTime.
     */
    public int getIniDTime() {
        return iniDTime;
    }
    public String getIniDTimeString() {
    	return StringUtils.getTimeStringFromSeconds(getIniDTime());
    }

    /**
     * @param iniDTime The iniDTime to set.
     */
    public void setIniDTime(int iniDTime) {
        this.iniDTime = iniDTime;
    }
    /**
     * @hibernate.property
     * @return Returns the mgCl2Added.
     */
    public float getMgCl2Added() {
        return mgCl2Added;
    }
    /**
     * @param mgCl2Added The mgCl2Added to set.
     */
    public void setMgCl2Added(float mgCl2Added) {
        this.mgCl2Added = mgCl2Added;
    }
    /**
     * @hibernate.property
     * @return Returns the reversePrimerConc.
     */
    public int getReversePrimerConc() {
        return reversePrimerConc;
    }
    /**
     * @param prConc The reversePrimerConc to set.
     */
    public void setReversePrimerConc(int prConc) {
        reversePrimerConc = prConc;
    }
    /**
     * @hibernate.property
     * @return Returns the reversePrimerProp.
     */
    public float getReversePrimerProp() {
        return reversePrimerProp;
    }
    /**
     * @param prProp The reversePrimerProp to set.
     */
    public void setReversePrimerProp(float prProp) {
        reversePrimerProp = prProp;
    }
    /**
     * @hibernate.property
     * @return Returns the stages.
     */
    public int getStages() {
        return stages;
    }
    /**
     * @param stages The stages to set.
     */
    public void setStages(int stages) {
        this.stages = stages;
    }
    /**
     * @hibernate.property
     * @return Returns the taqName.
     */
    public String getTaqName() {
        return taqName;
    }
    /**
     * @param taqName The taqName to set.
     */
    public void setTaqName(String tagName) {
        this.taqName = tagName;
    }
    /**
     * @hibernate.property
     * @return Returns the taqProp.
     */
    public float getTaqProp() {
        return taqProp;
    }
    /**
     * @param taqProp The taqProp to set.
     */
    public void setTaqProp(float tagVolume) {
        this.taqProp = tagVolume;
    }
    /**
     * @hibernate.property
     * @return Returns the templateProp.
     */
    public float getTemplateProp() {
        return templateProp;
    }
    /**
     * @param templateProp The templateProp to set.
     */
    public void setTemplateProp(float templateProp) {
        this.templateProp = templateProp;
    }
    /**
     * @hibernate.property
     * @return Returns the tenXBufferProp.
     */
    public float getTenXBufferProp() {
        return tenXBufferProp;
    }
    /**
     * @param tenXBufferProp The tenXBufferProp to set.
     */
    public void setTenXBufferProp(float tenXBufferProp) {
        this.tenXBufferProp = tenXBufferProp;
    }
    /**
     * @hibernate.property
     * @return Returns the protocolType
     */    
	public int getProtocolType() {
		return protocolType;
	}
	public void setProtocolType(int protocolType) {
		this.protocolType = protocolType;
	}
	public String getProtocolTypeString() {
		switch (getProtocolType()) {
			case STEP_UP: return "Step-up";
			case TOUCH_DOWN: return "Touch-down";
			default: return "Standard";
		}
	}
    /**
     * @hibernate.property
     * @return Returns the finalExtentionTemp
     */	
	public float getFinalExtentionTemp() {
		return finalExtentionTemp;
	}
	public void setFinalExtentionTemp(float finalExtentionTemp) {
		this.finalExtentionTemp = finalExtentionTemp;
	}
    /**
     * @hibernate.property
     * @return Returns the finalExtentionTime
     */	
	public int getFinalExtentionTime() {
		return finalExtentionTime;
	}
	public void setFinalExtentionTime(int finalExtentionTime) {
		this.finalExtentionTime = finalExtentionTime;
	}
	public String getFinalExtentionTimeString() {
		return StringUtils.getTimeStringFromSeconds(getFinalExtentionTime());
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public boolean getDefunct() {
		return defunct;
	}
	public void setDefunct(boolean defunct) {
		this.defunct = defunct;
	}	
}
