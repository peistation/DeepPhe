


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
       (not (DeepPheTnmResult))
=>
      (printout t "established TNM summary target..." crlf)
       (bind ?deepPheTnmTarget (new DeepPheTnmResult))
       (add ?deepPheTnmTarget))


;; ============================================================
;;  
;;   If populating aTNM target template, and there exists a TNM
;; document level mention for the patient and not in the past
;; then use this as the TNM
;;
;; ============================================================
(defrule populate-from-document-tnm-mention "use document tnm mention"
     (declare (salience 95))
     (DeepPheGoal (name "extract-tnm"))
     ?summaryTnm <- (DeepPheTnmResult (tumor "NA"))
	 (TnmMention (tumor ?tumor) 
	             (node ?node) 
	             (metastasis ?metastasis)
	             (historyOf ?historyOf&:(eq* ?historyOf 0)))
=>
     (printout t "found TNM! with historyOf " ?historyOf crlf)
     (modify ?summaryTnm (tumor ?tumor))
     (modify ?summaryTnm (node ?node))
     (modify ?summaryTnm (metastasis ?metastasis)))	


;; ============================================================
;;  
;;   If populating aTNM target template, convert cTAKES Measurements to 
;;   DeepPheSize objects
;;
;; ============================================================
(deffunction addDeepPheSize(?docUuid ?measurementText)
   "adds a DeepPheSize"
     (bind ?deepPheSizeObj  (new DeepPheSize))
     (call ?deepPheSizeObj setDocUuid ?docUuid)
     (call ?deepPheSizeObj assignMeasure ?measurementText)
     (bind ?deepPheSizeFact (add ?deepPheSizeObj))
     (modify ?deepPheSizeFact (uuid (call ?deepPheSizeFact getFactId))) 
     (bind ?deepPheLinkObj (new DeepPheLink))
     (call ?deepPheLinkObj setName "Document")
     (call ?deepPheLinkObj setSrcUuid  (fact-slot-value ?deepPheSizeFact uuid))
     (call ?deepPheLinkObj setTgtUuid  ?docUuid)
     (add ?deepPheLinkObj))

(defrule convert-measurements-to-size-test-1 "measurements to size"
      (declare (salience 93))
      (DeepPheGoal (name "extract-tnm"))
      (DeepPheTnmResult (tumor "NA"))
      (MeasurementAnnotation (id ?mId) (coveredText ?measurementText))
      (DeepPheDocument (uuid ?docUuid))
=>
   (printout t "Measurement  " ?mId " document " ?docUuid crlf))
;;(undefrule convert-measurements-to-size-test-1)

(defrule convert-measurements-to-size-test-2 "measurements to size"
      (declare (salience 92))
      (DeepPheGoal (name "extract-tnm"))
      (DeepPheLink (srcUuid ?sId)  
                                  (tgtUuid ?tId))
=>
   (printout t "Link  " ?sId " to " ?tId crlf))
(undefrule convert-measurements-to-size-test-2)

(defrule convert-measurements-to-size "measurements to size"
      (declare (salience 90))
      (DeepPheGoal (name "extract-tnm"))
      (DeepPheTnmResult (tumor "NA"))
      (MeasurementAnnotation (id ?mId) (coveredText ?measurementText))
      (DeepPheDocument (uuid ?docUuid))
      (DeepPheLink (srcUuid ?mId)  
                   (tgtUuid ?docUuid))
=>
   (printout t "adding deep phe size object..." crlf)
    (addDeepPheSize ?docUuid ?measurementText))

;; ============================================================
;;  
;;   If populating aTNM target template, the latest mention of tumor size can
;;   fill an empty tumor slot.   Use the biggest tumor related size from the latest
;;  note to fill the slot based on AJCC guidelines
;;
;; ============================================================

(deffunction assign-tumor (?tnm ?sizeOneMax) "assigns tumor based on size"
    (if (and  (> ?sizeOneMax 2)  (< ?sizeOneMax 3)) then
           (modify ?tnm (tumor "1"))
        else   (if (and  (> ?sizeOneMax 3)  (< ?sizeOneMax 4)) then
           (modify ?tnm (tumor "2"))
       else   (if (> ?sizeOneMax 4)  then
            (modify ?tnm (tumor "3"))))))

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


;; ============================================================
;;  
;;   If populating aTNM target template,  fill in empty Node
;;   and Metastasis optimistically
;;
;; ============================================================

(defrule assign-tumor-optimistically  "no node involvement"
     (declare (salience 75))
      (DeepPheGoal (name "extract-tnm"))
      ?tnm <- (DeepPheTnmResult (tumor "NA"))
=>
     (printout t "assigning tumor optimistically" crlf)
     (modify ?tnm (tumor "0")))

(defrule assign-node-optimistically  "no node involvement"
     (declare (salience 70))
      (DeepPheGoal (name "extract-tnm"))
      ?tnm <- (DeepPheTnmResult (node "NA"))
=>
     (printout t "assigning node optimistically" crlf)
     (modify ?tnm (node "0")))
     
(defrule assign-metastices-optimistically  "no metastices"
     (declare (salience 60))
      (DeepPheGoal (name "extract-tnm"))
      ?tnm <- (DeepPheTnmResult (metastasis "NA"))
=>
     (modify ?tnm (metastasis "0")))

;; ============================================================
;;  
;;  Aggregate the TNM
;;
;; ============================================================

(defrule aggregate-tnm  "use filled slots"
     (declare (salience 50))
      (DeepPheGoal (name "extract-tnm"))
      ?tnm <- (DeepPheTnmResult 
                   (tnmAggregate "NA")
                   (tumor ?t&:(neq ?t "NA"))
                   (node ?n&:(neq ?n "NA"))
                   (metastasis ?m&:(neq ?m "NA")))
=>
     (modify ?tnm (tnmAggregate (str-cat "T" ?t " N" ?n " M" ?m))))

;; ============================================================
;;  
;;  Display and clean up
;;
;; ============================================================

(defrule display-tnm "displays the overall tnm"
  (declare (salience 20))
     (DeepPheGoal (name "extract-tnm"))
      ?tnm <- (DeepPheTnmResult   (tnmAggregate ?tnmAggregate)
                                  (tumor ?tumor) 
                                  (node ?node)
                                  (metastasis ?metastasis))
      
=>
      (printout t "The tnm is "  ?tnmAggregate crlf
                   " t = " ?tumor crlf
                   " n = " ?node crlf
                   " m = " ?metastasis crlf
                   ))

(defrule  quit-extracting "quit extracting once all rules are done"
     (declare (salience 10))
      ?g <- (DeepPheGoal (name "extract-tnm"))
     ?tnm <- (DeepPheTnmResult)
=>
    (retract ?tnm)
     (retract ?g)
      (printout t "done extracting" crlf))

(deffunction extract-tnm () "assert the goal and runs the rules"
     (bind ?myGoal (assert (DeepPheGoal (name "extract-tnm"))))
     (run))

