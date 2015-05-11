
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

/** Value set
 * Updated by JCasGen Mon May 11 11:00:51 EDT 2015
 * @generated */
public class TnmOption_Type extends CodedDescribedType_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (TnmOption_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = TnmOption_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new TnmOption(addr, TnmOption_Type.this);
  			   TnmOption_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new TnmOption(addr, TnmOption_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = TnmOption.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.apache.ctakes.cancer.type.textsem.TnmOption");
 
  /** @generated */
  final Feature casFeat_value;
  /** @generated */
  final int     casFeatCode_value;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getValue(int addr) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "org.apache.ctakes.cancer.type.textsem.TnmOption");
    return ll_cas.ll_getIntValue(addr, casFeatCode_value);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setValue(int addr, int v) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "org.apache.ctakes.cancer.type.textsem.TnmOption");
    ll_cas.ll_setIntValue(addr, casFeatCode_value, v);}
    
  
 
  /** @generated */
  final Feature casFeat_certainty;
  /** @generated */
  final int     casFeatCode_certainty;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getCertainty(int addr) {
        if (featOkTst && casFeat_certainty == null)
      jcas.throwFeatMissing("certainty", "org.apache.ctakes.cancer.type.textsem.TnmOption");
    return ll_cas.ll_getIntValue(addr, casFeatCode_certainty);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCertainty(int addr, int v) {
        if (featOkTst && casFeat_certainty == null)
      jcas.throwFeatMissing("certainty", "org.apache.ctakes.cancer.type.textsem.TnmOption");
    ll_cas.ll_setIntValue(addr, casFeatCode_certainty, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public TnmOption_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_value = jcas.getRequiredFeatureDE(casType, "value", "uima.cas.Integer", featOkTst);
    casFeatCode_value  = (null == casFeat_value) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_value).getCode();

 
    casFeat_certainty = jcas.getRequiredFeatureDE(casType, "certainty", "uima.cas.Integer", featOkTst);
    casFeatCode_certainty  = (null == casFeat_certainty) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_certainty).getCode();

  }
}



    