(defrule display-identified-annotations "display identified annotations"
   (declare (salience 1000))
   (IdentifiedAnnotation
          (id ?id)
          (OBJECT ?obj))
=>
   (bind ?clsName (call (call ?obj getClass) getSimpleName))
   (printout t ?clsName " ==> " ?id crlf))

(defrule display-documents "display the deep phe documents"
    (declare (salience 900))
    (DeepPheDocument (uuid ?docUuid) (name ?documentName))
=>
    (printout t "DeepPheDocument " ?docUuid crlf))

(defrule display-links "display the deep phe links"
    (declare (salience 800))
    (DeepPheLink (srcUuid ?srcUuid) (tgtUuid ?tgtUuid))
=>
    (printout t "DeepPheLink " ?srcUuid " to " ?tgtUuid crlf))