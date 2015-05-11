
/* First created by JCasGen Mon May 11 11:00:52 EDT 2015 */
package org.apache.ctakes.typesystem.type.textsem;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** Contextual information of an entity. Equivalent to cTAKES: edu.mayo.bmi.uima.context.type.ContextAnnotation
 * Updated by JCasGen Mon May 11 11:00:52 EDT 2015
 * @generated */
public class ContextAnnotation_Type extends IdentifiedAnnotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ContextAnnotation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ContextAnnotation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ContextAnnotation(addr, ContextAnnotation_Type.this);
  			   ContextAnnotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ContextAnnotation(addr, ContextAnnotation_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ContextAnnotation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.apache.ctakes.typesystem.type.textsem.ContextAnnotation");
 
  /** @generated */
  final Feature casFeat_FocusText;
  /** @generated */
  final int     casFeatCode_FocusText;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getFocusText(int addr) {
        if (featOkTst && casFeat_FocusText == null)
      jcas.throwFeatMissing("FocusText", "org.apache.ctakes.typesystem.type.textsem.ContextAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_FocusText);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setFocusText(int addr, String v) {
        if (featOkTst && casFeat_FocusText == null)
      jcas.throwFeatMissing("FocusText", "org.apache.ctakes.typesystem.type.textsem.ContextAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_FocusText, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Scope;
  /** @generated */
  final int     casFeatCode_Scope;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getScope(int addr) {
        if (featOkTst && casFeat_Scope == null)
      jcas.throwFeatMissing("Scope", "org.apache.ctakes.typesystem.type.textsem.ContextAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Scope);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setScope(int addr, String v) {
        if (featOkTst && casFeat_Scope == null)
      jcas.throwFeatMissing("Scope", "org.apache.ctakes.typesystem.type.textsem.ContextAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_Scope, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public ContextAnnotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_FocusText = jcas.getRequiredFeatureDE(casType, "FocusText", "uima.cas.String", featOkTst);
    casFeatCode_FocusText  = (null == casFeat_FocusText) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_FocusText).getCode();

 
    casFeat_Scope = jcas.getRequiredFeatureDE(casType, "Scope", "uima.cas.String", featOkTst);
    casFeatCode_Scope  = (null == casFeat_Scope) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Scope).getCode();

  }
}



    