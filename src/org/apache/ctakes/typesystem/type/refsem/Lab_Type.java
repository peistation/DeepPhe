
/* First created by JCasGen Mon May 11 11:00:52 EDT 2015 */
package org.apache.ctakes.typesystem.type.refsem;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** This is an Event from the UMLS semantic group of Laboratory Procedures.  Based on generic Clinical Element Models (CEMs)
 * Updated by JCasGen Mon May 11 11:00:52 EDT 2015
 * @generated */
public class Lab_Type extends Event_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Lab_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Lab_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Lab(addr, Lab_Type.this);
  			   Lab_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Lab(addr, Lab_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Lab.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.apache.ctakes.typesystem.type.refsem.Lab");
 
  /** @generated */
  final Feature casFeat_abnormalInterpretation;
  /** @generated */
  final int     casFeatCode_abnormalInterpretation;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getAbnormalInterpretation(int addr) {
        if (featOkTst && casFeat_abnormalInterpretation == null)
      jcas.throwFeatMissing("abnormalInterpretation", "org.apache.ctakes.typesystem.type.refsem.Lab");
    return ll_cas.ll_getRefValue(addr, casFeatCode_abnormalInterpretation);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAbnormalInterpretation(int addr, int v) {
        if (featOkTst && casFeat_abnormalInterpretation == null)
      jcas.throwFeatMissing("abnormalInterpretation", "org.apache.ctakes.typesystem.type.refsem.Lab");
    ll_cas.ll_setRefValue(addr, casFeatCode_abnormalInterpretation, v);}
    
  
 
  /** @generated */
  final Feature casFeat_deltaFlag;
  /** @generated */
  final int     casFeatCode_deltaFlag;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getDeltaFlag(int addr) {
        if (featOkTst && casFeat_deltaFlag == null)
      jcas.throwFeatMissing("deltaFlag", "org.apache.ctakes.typesystem.type.refsem.Lab");
    return ll_cas.ll_getRefValue(addr, casFeatCode_deltaFlag);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDeltaFlag(int addr, int v) {
        if (featOkTst && casFeat_deltaFlag == null)
      jcas.throwFeatMissing("deltaFlag", "org.apache.ctakes.typesystem.type.refsem.Lab");
    ll_cas.ll_setRefValue(addr, casFeatCode_deltaFlag, v);}
    
  
 
  /** @generated */
  final Feature casFeat_labValue;
  /** @generated */
  final int     casFeatCode_labValue;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getLabValue(int addr) {
        if (featOkTst && casFeat_labValue == null)
      jcas.throwFeatMissing("labValue", "org.apache.ctakes.typesystem.type.refsem.Lab");
    return ll_cas.ll_getRefValue(addr, casFeatCode_labValue);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setLabValue(int addr, int v) {
        if (featOkTst && casFeat_labValue == null)
      jcas.throwFeatMissing("labValue", "org.apache.ctakes.typesystem.type.refsem.Lab");
    ll_cas.ll_setRefValue(addr, casFeatCode_labValue, v);}
    
  
 
  /** @generated */
  final Feature casFeat_ordinalInterpretation;
  /** @generated */
  final int     casFeatCode_ordinalInterpretation;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getOrdinalInterpretation(int addr) {
        if (featOkTst && casFeat_ordinalInterpretation == null)
      jcas.throwFeatMissing("ordinalInterpretation", "org.apache.ctakes.typesystem.type.refsem.Lab");
    return ll_cas.ll_getRefValue(addr, casFeatCode_ordinalInterpretation);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setOrdinalInterpretation(int addr, int v) {
        if (featOkTst && casFeat_ordinalInterpretation == null)
      jcas.throwFeatMissing("ordinalInterpretation", "org.apache.ctakes.typesystem.type.refsem.Lab");
    ll_cas.ll_setRefValue(addr, casFeatCode_ordinalInterpretation, v);}
    
  
 
  /** @generated */
  final Feature casFeat_referenceRangeNarrative;
  /** @generated */
  final int     casFeatCode_referenceRangeNarrative;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getReferenceRangeNarrative(int addr) {
        if (featOkTst && casFeat_referenceRangeNarrative == null)
      jcas.throwFeatMissing("referenceRangeNarrative", "org.apache.ctakes.typesystem.type.refsem.Lab");
    return ll_cas.ll_getRefValue(addr, casFeatCode_referenceRangeNarrative);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setReferenceRangeNarrative(int addr, int v) {
        if (featOkTst && casFeat_referenceRangeNarrative == null)
      jcas.throwFeatMissing("referenceRangeNarrative", "org.apache.ctakes.typesystem.type.refsem.Lab");
    ll_cas.ll_setRefValue(addr, casFeatCode_referenceRangeNarrative, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Lab_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_abnormalInterpretation = jcas.getRequiredFeatureDE(casType, "abnormalInterpretation", "org.apache.ctakes.typesystem.type.relation.DegreeOf", featOkTst);
    casFeatCode_abnormalInterpretation  = (null == casFeat_abnormalInterpretation) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_abnormalInterpretation).getCode();

 
    casFeat_deltaFlag = jcas.getRequiredFeatureDE(casType, "deltaFlag", "org.apache.ctakes.typesystem.type.refsem.LabDeltaFlag", featOkTst);
    casFeatCode_deltaFlag  = (null == casFeat_deltaFlag) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_deltaFlag).getCode();

 
    casFeat_labValue = jcas.getRequiredFeatureDE(casType, "labValue", "org.apache.ctakes.typesystem.type.refsem.LabValue", featOkTst);
    casFeatCode_labValue  = (null == casFeat_labValue) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_labValue).getCode();

 
    casFeat_ordinalInterpretation = jcas.getRequiredFeatureDE(casType, "ordinalInterpretation", "org.apache.ctakes.typesystem.type.relation.DegreeOf", featOkTst);
    casFeatCode_ordinalInterpretation  = (null == casFeat_ordinalInterpretation) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ordinalInterpretation).getCode();

 
    casFeat_referenceRangeNarrative = jcas.getRequiredFeatureDE(casType, "referenceRangeNarrative", "org.apache.ctakes.typesystem.type.refsem.LabReferenceRange", featOkTst);
    casFeatCode_referenceRangeNarrative  = (null == casFeat_referenceRangeNarrative) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_referenceRangeNarrative).getCode();

  }
}



    