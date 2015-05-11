

/* First created by JCasGen Mon May 11 11:00:51 EDT 2015 */
package org.apache.ctakes.cancer.type.textsem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.ctakes.typesystem.type.textsem.SignSymptomMention;


/** Some Hormone Receptor Status Annotation for ER, PR, HER2 (/neu)
 * Updated by JCasGen Mon May 11 11:00:51 EDT 2015
 * XML source: /home/tseytlin/Work/DeepPhe/ctakes-cancer/src/main/resources/org/apache/ctakes/cancer/types/TypeSystem.xml
 * @generated */
public class ReceptorStatus extends SignSymptomMention {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ReceptorStatus.class);
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
  protected ReceptorStatus() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ReceptorStatus(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ReceptorStatus(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public ReceptorStatus(JCas jcas, int begin, int end) {
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
  //* Feature: code

  /** getter for code - gets 
   * @generated
   * @return value of the feature 
   */
  public String getCode() {
    if (ReceptorStatus_Type.featOkTst && ((ReceptorStatus_Type)jcasType).casFeat_code == null)
      jcasType.jcas.throwFeatMissing("code", "org.apache.ctakes.cancer.type.textsem.ReceptorStatus");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ReceptorStatus_Type)jcasType).casFeatCode_code);}
    
  /** setter for code - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setCode(String v) {
    if (ReceptorStatus_Type.featOkTst && ((ReceptorStatus_Type)jcasType).casFeat_code == null)
      jcasType.jcas.throwFeatMissing("code", "org.apache.ctakes.cancer.type.textsem.ReceptorStatus");
    jcasType.ll_cas.ll_setStringValue(addr, ((ReceptorStatus_Type)jcasType).casFeatCode_code, v);}    
   
    
  //*--------------*
  //* Feature: description

  /** getter for description - gets 
   * @generated
   * @return value of the feature 
   */
  public String getDescription() {
    if (ReceptorStatus_Type.featOkTst && ((ReceptorStatus_Type)jcasType).casFeat_description == null)
      jcasType.jcas.throwFeatMissing("description", "org.apache.ctakes.cancer.type.textsem.ReceptorStatus");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ReceptorStatus_Type)jcasType).casFeatCode_description);}
    
  /** setter for description - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setDescription(String v) {
    if (ReceptorStatus_Type.featOkTst && ((ReceptorStatus_Type)jcasType).casFeat_description == null)
      jcasType.jcas.throwFeatMissing("description", "org.apache.ctakes.cancer.type.textsem.ReceptorStatus");
    jcasType.ll_cas.ll_setStringValue(addr, ((ReceptorStatus_Type)jcasType).casFeatCode_description, v);}    
   
    
  //*--------------*
  //* Feature: value

  /** getter for value - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getValue() {
    if (ReceptorStatus_Type.featOkTst && ((ReceptorStatus_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "org.apache.ctakes.cancer.type.textsem.ReceptorStatus");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((ReceptorStatus_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setValue(boolean v) {
    if (ReceptorStatus_Type.featOkTst && ((ReceptorStatus_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "org.apache.ctakes.cancer.type.textsem.ReceptorStatus");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((ReceptorStatus_Type)jcasType).casFeatCode_value, v);}    
  }

    