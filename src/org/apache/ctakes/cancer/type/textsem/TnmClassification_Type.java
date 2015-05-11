
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
import org.apache.ctakes.typesystem.type.textsem.SignSymptomMention_Type;

/** 
 * Updated by JCasGen Mon May 11 11:00:51 EDT 2015
 * @generated */
public class TnmClassification_Type extends SignSymptomMention_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (TnmClassification_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = TnmClassification_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new TnmClassification(addr, TnmClassification_Type.this);
  			   TnmClassification_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new TnmClassification(addr, TnmClassification_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = TnmClassification.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.apache.ctakes.cancer.type.textsem.TnmClassification");
 
  /** @generated */
  final Feature casFeat_size;
  /** @generated */
  final int     casFeatCode_size;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getSize(int addr) {
        if (featOkTst && casFeat_size == null)
      jcas.throwFeatMissing("size", "org.apache.ctakes.cancer.type.textsem.TnmClassification");
    return ll_cas.ll_getRefValue(addr, casFeatCode_size);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSize(int addr, int v) {
        if (featOkTst && casFeat_size == null)
      jcas.throwFeatMissing("size", "org.apache.ctakes.cancer.type.textsem.TnmClassification");
    ll_cas.ll_setRefValue(addr, casFeatCode_size, v);}
    
  
 
  /** @generated */
  final Feature casFeat_nodeSpread;
  /** @generated */
  final int     casFeatCode_nodeSpread;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getNodeSpread(int addr) {
        if (featOkTst && casFeat_nodeSpread == null)
      jcas.throwFeatMissing("nodeSpread", "org.apache.ctakes.cancer.type.textsem.TnmClassification");
    return ll_cas.ll_getRefValue(addr, casFeatCode_nodeSpread);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNodeSpread(int addr, int v) {
        if (featOkTst && casFeat_nodeSpread == null)
      jcas.throwFeatMissing("nodeSpread", "org.apache.ctakes.cancer.type.textsem.TnmClassification");
    ll_cas.ll_setRefValue(addr, casFeatCode_nodeSpread, v);}
    
  
 
  /** @generated */
  final Feature casFeat_metastasis;
  /** @generated */
  final int     casFeatCode_metastasis;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getMetastasis(int addr) {
        if (featOkTst && casFeat_metastasis == null)
      jcas.throwFeatMissing("metastasis", "org.apache.ctakes.cancer.type.textsem.TnmClassification");
    return ll_cas.ll_getRefValue(addr, casFeatCode_metastasis);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setMetastasis(int addr, int v) {
        if (featOkTst && casFeat_metastasis == null)
      jcas.throwFeatMissing("metastasis", "org.apache.ctakes.cancer.type.textsem.TnmClassification");
    ll_cas.ll_setRefValue(addr, casFeatCode_metastasis, v);}
    
  
 
  /** @generated */
  final Feature casFeat_options;
  /** @generated */
  final int     casFeatCode_options;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getOptions(int addr) {
        if (featOkTst && casFeat_options == null)
      jcas.throwFeatMissing("options", "org.apache.ctakes.cancer.type.textsem.TnmClassification");
    return ll_cas.ll_getRefValue(addr, casFeatCode_options);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setOptions(int addr, int v) {
        if (featOkTst && casFeat_options == null)
      jcas.throwFeatMissing("options", "org.apache.ctakes.cancer.type.textsem.TnmClassification");
    ll_cas.ll_setRefValue(addr, casFeatCode_options, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public int getOptions(int addr, int i) {
        if (featOkTst && casFeat_options == null)
      jcas.throwFeatMissing("options", "org.apache.ctakes.cancer.type.textsem.TnmClassification");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_options), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_options), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_options), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setOptions(int addr, int i, int v) {
        if (featOkTst && casFeat_options == null)
      jcas.throwFeatMissing("options", "org.apache.ctakes.cancer.type.textsem.TnmClassification");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_options), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_options), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_options), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public TnmClassification_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_size = jcas.getRequiredFeatureDE(casType, "size", "org.apache.ctakes.cancer.type.textsem.TnmStage", featOkTst);
    casFeatCode_size  = (null == casFeat_size) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_size).getCode();

 
    casFeat_nodeSpread = jcas.getRequiredFeatureDE(casType, "nodeSpread", "org.apache.ctakes.cancer.type.textsem.TnmStage", featOkTst);
    casFeatCode_nodeSpread  = (null == casFeat_nodeSpread) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_nodeSpread).getCode();

 
    casFeat_metastasis = jcas.getRequiredFeatureDE(casType, "metastasis", "org.apache.ctakes.cancer.type.textsem.TnmStage", featOkTst);
    casFeatCode_metastasis  = (null == casFeat_metastasis) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_metastasis).getCode();

 
    casFeat_options = jcas.getRequiredFeatureDE(casType, "options", "uima.cas.FSArray", featOkTst);
    casFeatCode_options  = (null == casFeat_options) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_options).getCode();

  }
}



    