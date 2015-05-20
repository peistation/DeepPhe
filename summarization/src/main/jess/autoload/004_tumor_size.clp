;; ============================================================
;;  
;; Tumor Size
;;
;; Set 1: Document Genre Rules for sourcing data
;;
;; ============================================================
(defrule tumor-size-from-first-enc "calculate tumor size from first reliable encounter"
     (declare (salience 1000))
     (Goal (name "extract-tumor-size")(isActive 1))
     (Encounter (id ?encounterOneId) (sequence ?encounterOneSequence))
     (TumorSize
          (summarizableId ?encounterOneId)
          (baseCode ?baseCode)
          (code ?code)
          (preferredTerm ?preferredTerm)
          (value ?value)
          (unitOfMeasure ?unitOfMeasure)
          (greatestDimension ?greatestDimension)
          (dimensionOne ?dimensionOne)
          (dimensionTwo ?dimensionTwo)
          (dimensionThree ?dimensionThree)
         )
      (Patient (id ?patientId))
      (not (TumorSize (summarizableId ?patientId)))
      (not (and 
             (Encounter (id ?encounterTwoId&:(neq ?encounterTwoId ?encounterOneId))
                        (sequence ?encounterTwoSequence&:(< ?encounterTwoSequence ?encounterOneSequence)))
             (TumorSize (summarizableId ?encounterTwoId))))
=>
     (bind ?tumorSize (add (new TumorSize)))
     (printout t "tumor-size-from-first-enc adding patient information..." crlf)
     (modify ?tumorSize    (summarizableId ?patientId)
                           (baseCode ?baseCode)
                           (code ?code)
                           (preferredTerm ?preferredTerm)
                           (value ?value)
                           (unitOfMeasure ?unitOfMeasure)
                           (greatestDimension ?greatestDimension)
                           (dimensionOne ?dimensionOne)
                           (dimensionTwo ?dimensionTwo)
                           (dimensionThree ?dimensionThree)))

;
; Retract the goal for this module
;
(defrule retract-tumor-size-goal "Retract the goal to extract tumor size"
     (declare (salience 500))
     ?g <- (Goal (name ?name&:(eq ?name "extract-tumor-size"))(isActive 1))
=>
     (printout t "Retract goal " ?name crlf)
     (retract ?g))


;; ============================================================
;;  
;; Tumor Size
;;
;; Set 1: Document Genre Rules for sourcing data
;;
;; ============================================================

;; ============================================================
;;  
;; Tumor Size preferred from PathologyReport or OperativeReport
;; for BreastCancer
;;
;; Record tumor size from Pathology document if available unless
;; the patient has received radiation or chemotherapy
;; (neoadjuvant therapy) prior to resection
;;
;; ============================================================

;; ============================================================
;;  
;; Use Operative Report as second option
;;
;; If there is no tumor size in the Pathology document and there
;; is a size reported in the Op Note, then record the size from 
;; the Op Note
;; 
;; ============================================================

;; ============================================================
;;  
;; Use Radiology Report as third option
;;
;; Set 1: Document Genre Rules for sourcing data
;;
;; ============================================================
