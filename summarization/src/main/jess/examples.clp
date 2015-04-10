(ppdeftemplate AnatomicalSite)
(ppdeftemplate AnatomicalSiteMention)
(ppdeftemplate ProcedureMention)

(defrule print-anatomical-site-mention "Prints out AnatomicalSiteMentions"
      ?asm <- (AnatomicalSiteMention)
=>
     (printout t "found AnatomicalSiteMention" crlf))

(undefrule print-anatomical-site-mention)

(defrule print-procedure-mention "Prints out ProcedureMention"
      ?asm <- (ProcedureMention (id ?id))
=>
     (printout t "found ProcedureMention id #"  ?id crlf))

(undefrule print-procedure-mention)

(defrule print-identified-annotations "Prints out IdentifiedAnnotations"
      ?annot <- (IdentifiedAnnotation (id ?id) 
                                      (polarity ?polarity)
                                      (typeID ?typeID)
                                      (discoveryTechnique ?discoveryTechnique)
                                      (confidence ?confidence)
                                      (uncertainty ?uncertainty)
                                      (conditional ?conditional)
                                      (generic ?generic)
                                      (subject ?subject)
                                      (historyOf ?historyOf)
)
=>
     (printout t "found IdentifiedAnnotation id #"  ?id crlf)
     (printout t "                      fact id #"  (fact-slot-value ?annot id) crlf)
     (printout t "                      polarity "  ?polarity crlf)
     (printout t "                      typeID "  ?typeID crlf)
     (printout t "          discoveryTechnique "  ?discoveryTechnique crlf)
     (printout t "                  confidence "  ?confidence crlf)
     (printout t "                  uncertainty "  ?uncertainty crlf)
     (printout t "                  conditional "  ?conditional crlf)
     (printout t "                      generic "  ?generic crlf)
     (printout t "                      subject "  ?subject crlf)
     (printout t "                    historyOf "  ?historyOf crlf)
     )

(ppdeftemplate AnatomicalSite)
(ppdeftemplate AnatomicalSiteMention)


(defrule print-anatomical-site-mention "Prints out AnatomicalSiteMentions"
      ?asm <- (AnatomicalSiteMention)
=>
     (printout t "found AnatomicalSiteMention" crlf))

(undefrule print-anatomical-site-mention)

(defrule print-procedure-mention "Prints out ProcedureMention"
      ?asm <- (ProcedureMention (id ?id))
=>
     (printout t "found ProcedureMention id #"  ?id crlf))

(defrule print-identified-annotations "Prints out IdentifiedAnnotations"
      ?annot <- (IdentifiedAnnotation (id ?id) 
                                     (ontologyConceptArr $?ontologyConcepts)
                                      (polarity ?polarity)
                                      (typeID ?typeID)
                                      (discoveryTechnique ?discoveryTechnique)
                                      (confidence ?confidence)
                                      (uncertainty ?uncertainty)
                                      (conditional ?conditional)
                                      (generic ?generic)
                                      (subject ?subject)
                                      (historyOf ?historyOf)
                                     (begin ?begin)
                                     (end ?end)
)
=>
     (printout t "found IdentifiedAnnotation id #"  ?id crlf)
     (printout t "                      fact id #"  (fact-slot-value ?annot id) crlf)
     (printout t "                      polarity "  ?polarity crlf)
     (printout t "                      typeID "  ?typeID crlf)
     (printout t "          discoveryTechnique "  ?discoveryTechnique crlf)
     (printout t "                  confidence "  ?confidence crlf)
     (printout t "                  uncertainty "  ?uncertainty crlf)
     (printout t "                  conditional "  ?conditional crlf)
     (printout t "                      generic "  ?generic crlf)
     (printout t "                      subject "  ?subject crlf)
     (printout t "                    historyOf "  ?historyOf crlf)
     )


(defrule print-anatomical-site-mention "Prints out AnatomicalSiteMention"
      ?annot <- (AnatomicalSiteMention  (id ?id) 
                                      (polarity ?polarity)
                                      (typeID ?typeID)
                                      (discoveryTechnique ?discoveryTechnique)
                                      (confidence ?confidence)
                                      (uncertainty ?uncertainty)
                                      (conditional ?conditional)
                                      (generic ?generic)
                                      (subject ?subject)
                                      (historyOf ?historyOf)
                                     (begin ?begin)
                                     (end ?end)
                                     (ontologyConceptArr $?ontologyConcepts)
)
=>
     (printout t "found AnatomicalSiteMention  id #"  ?id crlf)
     (printout t "                      fact id #"  (fact-slot-value ?annot id) crlf)
     (printout t "                      polarity "  ?polarity crlf)
     (printout t "                      typeID "  ?typeID crlf)
     (printout t "          discoveryTechnique "  ?discoveryTechnique crlf)
     (printout t "                  confidence "  ?confidence crlf)
     (printout t "                  uncertainty "  ?uncertainty crlf)
     (printout t "                  conditional "  ?conditional crlf)
     (printout t "                      generic "  ?generic crlf)
     (printout t "                      subject "  ?subject crlf)
     (printout t "                    historyOf "  ?historyOf crlf)
     (printout t "                          begin "  ?begin crlf)
     (printout t "                             end "  ?end crlf)
    (foreach ?ontologyConcept $?ontologyConcepts
                      (printout t  ?ontologyConcept crlf)))
                      
(undefrule print-identified-annotations)

(defrule anatomicalSiteWithMeasurement "look for an anatomical site mention with a nearby measurement"
     (declare (salience 100))
      (DeepPheGoal (name "tnm-extraction"))
      ?site <- (AnatomicalSiteMention (id ?siteId)  (begin ?siteBegin)  (end ?siteEnd)  (coveredText ?siteText) )
       (UmlsConcept  (oui ?oui) (cui ?cui&:(eq ?cui "C0006141")))
       (DeepPheLink (srcUuid ?siteId)  (tgtUuid ?tgtUuid&:(eq (integer ?oui)  ?tgtUuid)))
=>
      (printout t "found anatomical site <" ?siteId "> " ?siteText " with cui of " ?cui crlf))

(defrule  quit-extracting "quit extracting once all rules are done"
     (declare (salience 10))
      ?g <- (DeepPheGoal (name "tnm-extraction"))
=>
     (retract ?g)
      (printout t "done extracting" crlf))

(deffunction extract-tnm () "assert the goal and runs the rules"
     (bind ?myGoal (assert (DeepPheGoal (name "tnm-extraction"))))
     (run))

(extract-tnm)


;; ============================================================
;;  
;;   If cancer is discovered in the pateint notes establish a TNM target template
;;
;; ============================================================

(defrule establish-tnm-target "add tnm target template for cancer"
     (declare (salience 100))
      (DeepPheGoal (name "extract-tnm"))
      (DiseaseDisorderMention (id ?diseaseId) )
      (UmlsConcept  (oui ?oui) (cui ?cui&:(eq ?cui "C1134719")))
      (DeepPheLink (srcUuid ?diseaseId)  
                                 (tgtUuid ?tgtUuid&:(eq (integer ?oui)  ?tgtUuid)))
=>
       (bind ?deepPheTnmTarget (new DeepPheTnmResult))
       (add ?deepPheTnmTarget))


;; ============================================================
;;  
;;   If populating aTNM target template, convert cTAKES Measurements to 
;;   DeepPheSize objects
;;
;; ============================================================

(defrule convert-measurements-to-size "measurements to size"
     (declare (salience 90))
      (DeepPheGoal (name "extract-tnm"))
      (DeepPheTnmResult (tumor "NA"))
      (MeasurementAnnotation (id ?measurementId) (coveredText ?measurementText))
      (DeepPheDocument (uuid ?docUid))
      (DeepPheLink (srcUuid ?measurementId)  
                                 (tgtUuid ?docUid))
=>
       (bind ?deepPheSizeObj  (new DeepPheSize))
       (call ?deepPheSizeObj setDocUid ?docUid)
       (call ?deepPheSizeObj assignMeasure ?measurementText)
       (bind ?deepPheSizeFact (add ?deepPheSizeObj))
       (set-slot-value ?deepPheSizeFact uuid (fact-id ?deepPheSizeFact))      
       (bind ?deepPheLinkObj (new DeepPheLink))
       (call ?deepPheLinkObj setName "Document")
       (call ?deepPheLinkObj setSrcUuid  (fact-slot-value ?deepPheSizeFact uuid))
       (call ?deepPheLinkObj setTgtUuid  ?docUid)
       (add ?deepPheLinkObj))

;; ============================================================
;;  
;;   If populating aTNM target template, the latest mention of tumor size can
;;   fill an empty tumor slot.   Use the biggest tumor related size from the latest
;;  note to fill the slot based on AJCC guidelines
;;
;; ============================================================

(deffunction assign-tumor (?tnm ?sizeOneMax) "assigns tumor based on size"
    (if (and  (> ?sizeOneMax 2)  (< ?sizeOneMax 3)) then
           (modify ?tnm (tumor "T1"))
        else   (if (and  (> ?sizeOneMax 3)  (< ?sizeOneMax 4)) then
           (modify ?tnm (tumor "T2"))
       else   (if (> ?sizeOneMax 4)  then
            (modify ?tnm (tumor "T3"))))))

(defrule assign-tumor-by-size  "tumor by size"
     (declare (salience 80))
      (DeepPheGoal (name "extract-tnm"))
      ?tnm <- (DeepPheTnmResult (tumor "NA"))
      (DeepPheSize  (docUuid ?sizeOneDocUuid)  (sizeInMaxDimension ?sizeOneMax) )                            
      (DeepPheDocument (uuid  ?sizeOneDocUuid)  (sequence ?sizeOneDocSeq))
     (not (and (DeepPheSize (docUuid ?sizeTwoDocUuid))
                      (DeepPheDocument (uuid ?sizeTwoDocUuid) (sequence ?sizeTwoDocSeq&:(> ?sizeTwoDocSeq ?sizeOneDocSeq)))))
     (not (DeepPheSize (docUuid ?sizeOneDocUuid)
                                       (sizeInMaxDimension ?sizeTwoMax&:(> ?sizeTwoMax ?sizeOneMax)))) 
=>
       (printout t "Found latest greatest tumor dimension mention.  Assigning to TNM tumor" crlf)
       (assign-tumor ?tnm ?sizeOneMax))


