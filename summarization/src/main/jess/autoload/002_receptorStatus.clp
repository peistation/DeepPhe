;
; ER, PER. Her2 Phenotyping Rules:
; 
;  Author:  Rebecca Jacobson
;  Date:  May 2015
;

;
; ER - Pathology Report
;
;  If patient has encounter data "tumor" from Pathology with ER relation <ER, value>
;  => phenotype ER = value

(defrule receptor-er-pathology-report "Create phenotype from single value of ER from Pathology Report FD or addendum"
      (declare (salience 1000))
      (Goal (name ?goalName&:(eq ?goalName "extract-receptor-status")) (isActive 1))
      (Encounter (id ?encounterId) (kind ?kind&:(or (eq ?kind "Pathology Report") (eq ?kind "Progress Note"))))
      (Er (summarizableId ?encounterId)
          (code ?code)
          (preferredTerm ?preferredTerm)
          (value ?value)
          (unitOfMeasure ?unitOfMeasure))
      (Patient (id ?patientId))
      (not (Er (summarizableId ?patientId)))
=>
      (printout t "er-pathology-report fires..." crlf)
      (bind ?patientEr (add (new Er)))
      (modify ?patientEr (summarizableId ?patientId)
                         (code ?code)
                         (preferredTerm ?preferredTerm)
                         (value ?value)
                         (unitOfMeasure ?unitOfMeasure)
                         (isActive 1)))

;
; PR - Pathology Report
;
;  If patient has encounter data "tumor" from Pathology with PR relation <PR, value>
;  => phenotype PR = value

(defrule receptor-pr-pathology-report "Create phenotype from single value of PR from Pathology Report FD or addendum"
      (declare (salience 1000))
      (Goal (name ?goalName&:(eq ?goalName "extract-receptor-status")) (isActive 1))
      (Encounter (id ?encounterId) (kind ?kind&:(or (eq ?kind "Pathology Report") (eq ?kind "Progress Note"))))
      (Pr (summarizableId ?encounterId)
          (code ?code)
          (preferredTerm ?preferredTerm)
          (value ?value)
          (unitOfMeasure ?unitOfMeasure))
      (Patient (id ?patientId))
      (not (Pr (summarizableId ?patientId)))
=>
    (printout t "pr-pathology-report fires..." crlf)
      (bind ?patientPr (add (new Pr)))
      (modify ?patientPr (summarizableId ?patientId)
                         (code ?code)
                         (preferredTerm ?preferredTerm)
                         (value ?value)
                         (unitOfMeasure ?unitOfMeasure)
                         (isActive 1)))

;
; Her2 - Pathology Report
;
;  If patient has encounter data "tumor" from Pathology with Her2 relation <Her2, value>
;  => phenotype Her2 = value

(defrule receptor-her2-pathology-report "Create phenotype from single Pathology Report Her2 FD or addendum"
      (declare (salience 1000))
      (Goal (name ?goalName&:(eq ?goalName "extract-receptor-status")) (isActive 1))
      (Encounter (id ?encounterId) (kind ?kind&:(or (eq ?kind "Pathology Report") (eq ?kind "Progress Note"))))
      (Her2 (summarizableId ?encounterId)
          (code ?code)
          (preferredTerm ?preferredTerm)
          (value ?value)
          (unitOfMeasure ?unitOfMeasure))
      (Patient (id ?patientId))
      (not (Her2 (summarizableId ?patientId)))
=>
  (printout t "her2-pathology-report fires..." crlf)
      (bind ?patientHer2 (add (new Her2)))
      (modify ?patientHer2 (summarizableId ?patientId)
                         (code ?code)
                         (preferredTerm ?preferredTerm)
                         (value ?value)
                         (unitOfMeasure ?unitOfMeasure)
                         (isActive 1)))

;
; Receptor status triple positive
;
; If ER+, PR+, HER2+ => phenotype receptor status = Triple Positive
;
(defrule receptor-status-triple-positive "If ER+, PR+, HER2+ => phenotype receptor status = Triple Positive"
      (declare (salience 900))
      (Goal (name ?goalName&:(eq ?goalName "extract-receptor-status")) (isActive 1))
      (Patient (id ?patientId))
      (Er (summarizableId ?patientId)(value ?value&:(eq ?value "positive")))
      (Pr (summarizableId ?patientId)(value ?value))
      (Her2 (summarizableId ?patientId)(value ?value))
      (not (ReceptorStatus (summarizableId ?patientId) (value "TriplePositive")))
=>
      (printout t "receptor-status-triple-positive.fires adding new patient information..." crlf)
      (bind ?receptorStatus (add (new ReceptorStatus)))
      (modify ?receptorStatus (summarizableId ?patientId)(value "TriplePositive")(isActive 1)))

;
; Receptor status triple negative
;
; If ER-, PR-, HER2- => phenotype receptor status = Triple Negative
;
(defrule receptor-status-triple-negative "If ER-, PR-, HER2- => phenotype receptor status = Triple Negative"
      (declare (salience 900))
      (Goal (name ?goalName&:(eq ?goalName "extract-receptor-status")) (isActive 1))
      (Patient (id ?patientId))
      (Er (summarizableId ?patientId)(value ?value&:(eq ?value "negative")))
      (Pr (summarizableId ?patientId)(value ?value))
      (Her2 (summarizableId ?patientId)(value ?value))
      (not (ReceptorStatus (summarizableId ?patientId)(value "TripleNegative")))
=>
      (printout t "receptor-status-triple-positive.fires adding new patient information..." crlf)
      (bind ?receptorStatus (add (new ReceptorStatus)))
      (modify ?receptorStatus (summarizableId ?patientId)
                              (baseCode "C2348820")
                              (code "C2348820")
			      (preferredTerm "Triple-Negative Breast Carcinoma")
                              (value "TripleNegative")
                              (isActive 1)))

;
; Receptor status Her2 positive
;
; If HER2+ => phenotype receptor status = Her2 Positive
;
(defrule receptor-status-her2-positive "If HER2+ => phenotype receptor status = Her2 Positive"
      (declare (salience 800))
      (Goal (name ?goalName&:(eq ?goalName "extract-receptor-status")) (isActive 1))
      (Patient (id ?patientId))
      (Her2 (summarizableId ?patientId)(value ?value&:(eq ?value "positive")))
      (not (ReceptorStatus (summarizableId ?patientId) (value "Her2Positive")))
=>
      (printout t "receptor-status-her2-positive adding new patient information..." crlf)
      (bind ?receptorStatus (add (new ReceptorStatus)))
      (modify ?receptorStatus (summarizableId ?patientId)
                              (baseCode "C1960398")
                              (code "C1960398")
                              (preferredTerm "HER2 Positive Breast Carcinoma")
                              (value "Her2Positive")
                              (isActive 1)))

;
; Receptor status - Hormone receptor negative
;
; If ER-, PR- => phenotype receptor status = Hormone Receptor Negative
;
(defrule receptor-status-hormone-receptor-negative "If ER-, PR- => phenotype receptor status = Hormone Receptor Negative"
      (declare (salience 800))
      (Goal (name ?goalName&:(eq ?goalName "extract-receptor-status")) (isActive 1))
      (Patient (id ?patientId))
      (Er (summarizableId ?patientId)(value ?value&:(eq ?value "negative")))
      (Pr (summarizableId ?patientId)(value ?value))
      (not (ReceptorStatus (summarizableId ?patientId)(value "HormoneReceptorNegative")))
=>
      (printout t "receptor-status-hormone-receptor-negative fires adding new patient information..." crlf)
      (bind ?receptorStatus (add (new ReceptorStatus)))
      (modify ?receptorStatus (summarizableId ?patientId)
                              (code "C1234567") 
                              (preferredTerm "Hormone Receptor Negative*")
                              (value "HormoneReceptorNegative")(isActive 1)))

;
; Retract the goal for this module
;
(defrule retract-extract-receptor-status-goal "Retract the goal to extract receptor status"
     (declare (salience 500))
     ?g <- (Goal (name ?name&:(eq ?name "extract-receptor-status"))(isActive 1))
=>
     (printout t "Retract goal " ?name crlf)
     (retract ?g))