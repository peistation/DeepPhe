
/* First created by JCasGen Mon May 11 11:00:51 EDT 2015 */
package org.apache.ctakes.cancer.type.textsem;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.ctakes.typesystem.type.textsem.MeasurementAnnotation_Type;

/** Size measurements for tumor, lesion, etc.  Can have multiple dimensions
 * Updated by JCasGen Mon May 11 11:00:51 EDT 2015
 * @generated */
public class CancerSize_Type extends MeasurementAnnotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (CancerSize_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = CancerSize_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new CancerSize(addr, CancerSize_Type.this);
  			   CancerSize_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new CancerSize(addr, CancerSize_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = CancerSize.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.apache.ctakes.cancer.type.textsem.CancerSize");
 
  /** @generated */
  final Feature casFeat_measurements;
  /** @generated */
  final int     casFeatCode_measurements;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getMeasurements(int addr) {
        if (featOkTst && casFeat_measurements == null)
      jcas.throwFeatMissing("measurements", "org.apache.ctakes.cancer.type.textsem.CancerSize");
    return ll_cas.ll_getRefValue(addr, casFeatCode_measurements);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setMeasurements(int addr, int v) {
        if (featOkTst && casFeat_measurements == null)
      jcas.throwFeatMissing("measurements", "org.apache.ctakes.cancer.type.textsem.CancerSize");
    ll_cas.ll_setRefValue(addr, casFeatCode_measurements, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public int getMeasurements(int addr, int i) {
        if (featOkTst && casFeat_measurements == null)
      jcas.throwFeatMissing("measurements", "org.apache.ctakes.cancer.type.textsem.CancerSize");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_measurements), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_measurements), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_measurements), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setMeasurements(int addr, int i, int v) {
        if (featOkTst && casFeat_measurements == null)
      jcas.throwFeatMissing("measurements", "org.apache.ctakes.cancer.type.textsem.CancerSize");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_measurements), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_measurements), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_measurements), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public CancerSize_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_measurements = jcas.getRequiredFeatureDE(casType, "measurements", "uima.cas.FSArray", featOkTst);
    casFeatCode_measurements  = (null == casFeat_measurements) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_measurements).getCode();

  }
}



    