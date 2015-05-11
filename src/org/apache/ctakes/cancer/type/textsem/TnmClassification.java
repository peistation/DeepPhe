

/* First created by JCasGen Mon May 11 11:00:51 EDT 2015 */
package org.apache.ctakes.cancer.type.textsem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.ctakes.typesystem.type.textsem.SignSymptomMention;


/** 
 * Updated by JCasGen Mon May 11 11:00:51 EDT 2015
 * XML source: /home/tseytlin/Work/DeepPhe/ctakes-cancer/src/main/resources/org/apache/ctakes/cancer/types/TypeSystem.xml
 * @generated */
public class TnmClassification extends SignSymptomMention {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TnmClassification.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected TnmClassification() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public TnmClassification(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public TnmClassification(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public TnmClassification(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: size

  /** getter for size - gets 
   * @generated
   * @return value of the feature 
   */
  public TnmStage getSize() {
    if (TnmClassification_Type.featOkTst && ((TnmClassification_Type)jcasType).casFeat_size == null)
      jcasType.jcas.throwFeatMissing("size", "org.apache.ctakes.cancer.type.textsem.TnmClassification");
    return (TnmStage)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((TnmClassification_Type)jcasType).casFeatCode_size)));}
    
  /** setter for size - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSize(TnmStage v) {
    if (TnmClassification_Type.featOkTst && ((TnmClassification_Type)jcasType).casFeat_size == null)
      jcasType.jcas.throwFeatMissing("size", "org.apache.ctakes.cancer.type.textsem.TnmClassification");
    jcasType.ll_cas.ll_setRefValue(addr, ((TnmClassification_Type)jcasType).casFeatCode_size, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: nodeSpread

  /** getter for nodeSpread - gets 
   * @generated
   * @return value of the feature 
   */
  public TnmStage getNodeSpread() {
    if (TnmClassification_Type.featOkTst && ((TnmClassification_Type)jcasType).casFeat_nodeSpread == null)
      jcasType.jcas.throwFeatMissing("nodeSpread", "org.apache.ctakes.cancer.type.textsem.TnmClassification");
    return (TnmStage)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((TnmClassification_Type)jcasType).casFeatCode_nodeSpread)));}
    
  /** setter for nodeSpread - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNodeSpread(TnmStage v) {
    if (TnmClassification_Type.featOkTst && ((TnmClassification_Type)jcasType).casFeat_nodeSpread == null)
      jcasType.jcas.throwFeatMissing("nodeSpread", "org.apache.ctakes.cancer.type.textsem.TnmClassification");
    jcasType.ll_cas.ll_setRefValue(addr, ((TnmClassification_Type)jcasType).casFeatCode_nodeSpread, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: metastasis

  /** getter for metastasis - gets 
   * @generated
   * @return value of the feature 
   */
  public TnmStage getMetastasis() {
    if (TnmClassification_Type.featOkTst && ((TnmClassification_Type)jcasType).casFeat_metastasis == null)
      jcasType.jcas.throwFeatMissing("metastasis", "org.apache.ctakes.cancer.type.textsem.TnmClassification");
    return (TnmStage)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((TnmClassification_Type)jcasType).casFeatCode_metastasis)));}
    
  /** setter for metastasis - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setMetastasis(TnmStage v) {
    if (TnmClassification_Type.featOkTst && ((TnmClassification_Type)jcasType).casFeat_metastasis == null)
      jcasType.jcas.throwFeatMissing("metastasis", "org.apache.ctakes.cancer.type.textsem.TnmClassification");
    jcasType.ll_cas.ll_setRefValue(addr, ((TnmClassification_Type)jcasType).casFeatCode_metastasis, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: options

  /** getter for options - gets 
   * @generated
   * @return value of the feature 
   */
  public FSArray getOptions() {
    if (TnmClassification_Type.featOkTst && ((TnmClassification_Type)jcasType).casFeat_options == null)
      jcasType.jcas.throwFeatMissing("options", "org.apache.ctakes.cancer.type.textsem.TnmClassification");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((TnmClassification_Type)jcasType).casFeatCode_options)));}
    
  /** setter for options - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setOptions(FSArray v) {
    if (TnmClassification_Type.featOkTst && ((TnmClassification_Type)jcasType).casFeat_options == null)
      jcasType.jcas.throwFeatMissing("options", "org.apache.ctakes.cancer.type.textsem.TnmClassification");
    jcasType.ll_cas.ll_setRefValue(addr, ((TnmClassification_Type)jcasType).casFeatCode_options, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for options - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public TnmOption getOptions(int i) {
    if (TnmClassification_Type.featOkTst && ((TnmClassification_Type)jcasType).casFeat_options == null)
      jcasType.jcas.throwFeatMissing("options", "org.apache.ctakes.cancer.type.textsem.TnmClassification");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((TnmClassification_Type)jcasType).casFeatCode_options), i);
    return (TnmOption)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((TnmClassification_Type)jcasType).casFeatCode_options), i)));}

  /** indexed setter for options - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setOptions(int i, TnmOption v) { 
    if (TnmClassification_Type.featOkTst && ((TnmClassification_Type)jcasType).casFeat_options == null)
      jcasType.jcas.throwFeatMissing("options", "org.apache.ctakes.cancer.type.textsem.TnmClassification");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((TnmClassification_Type)jcasType).casFeatCode_options), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((TnmClassification_Type)jcasType).casFeatCode_options), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    