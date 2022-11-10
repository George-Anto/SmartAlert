# Ελληνικά

# SmartAlert

## Android App με Backend Module
### Link για [Backend](https://github.com/George-Anto/smart_alert_backend_public)

Η εφαρμογή έχει τον ρόλο της ειδοποίησης χρηστών που την έχουν εγκαταστήσει για επικίνδυνες - έκτακτες καταστάσεις (φωτιές πλημμύρες, σεισμούς) κοντά σε αυτούς. 
Ο χρήστης έχει την δυνατότητα και να ειδοποιήσει για ένα επικίνδυνο περιστατικό αλλά και να ειδοποιηθεί. <br/>

Η ενημέρωση γίνεται μέσω γραπτού μηνύματος σε ένα σύνολο από παραλήπτες. Σε κάθε ειδοποίηση περιλαμβάνεται η κατάσταση ανάγκης (με
την όποια πληροφορία συνοδεύεται), η γεωγραφική θέση του τρέχοντος event, ένα timestamp (ημερομηνία / ώρα), καθώς και οδηγίες από την πολιτική προστασία.

## Backend Module

Η εφαρμογή μας διαθέτει ένα backend module, βασική λειτουργία του οποίου είναι η κατάταξη των υποβληθέντων περιστατικών με βάση το επίπεδο
συναγερμού, το οποίο υπολογίζεται ως εξής:<br/>
Για τα υποβληθέντα περιστατικά υπάρχουν τρία κριτήρια «βαρύτητας». Το 1ο
είναι το πλήθος των χρηστών που έχουν υποβάλλει το συγκεκριμένο περιστατικό κινδύνου (όσο περισσότερα άτομα, τόσο
πιο «έγκυρο» το περιστατικό), σε συγκεκριμένο εύρος χρόνου (π.χ. δεν μπορεί να υποβάλουν μια πλημμύρα με χρονική απόσταση μιας ημέρας). Το 2ο
είναι η γεωγραφική απόσταση μεταξύ των υποβληθέντων περιστατικών (δεν μπορεί μια πυρκαγιά να έχει απόσταση 200 χιλιομέτρων). Το 3ο είναι πως αν ένας 
χρήστης υποβάλει ξανά το ίδιο περιστατικό, αυτό δεν θα προσμετρηθεί ως ακόμα μια υποβολή της δημιουργηθείσας ομάδας. 

## Υπάλληλος Πολιτικής Προστασίας - Δεύτερη Χρήση του Android App

Τέλος, ένας υπάλληλος πολιτικής προστασίας θα επιλέξει την ειδοποίηση των χρηστών για κάποιο (ή όλα) τα περιστατικά που το backend μας ομαδοποίησε και βαθμολόγησε
με βάση τα παραπάνω κριτήρια, τότε θα ειδοποιηθούν μόνο οι χρήστες οι οποίοι βρίσκονται σε συγκεκριμένη
απόσταση από το περιστατικό και Θα λάβουν μήνυμα κινδύνου με τις απαραίτητες πληροφορίες. <br/>
Οι παραπάνω λειτουργίες επιτελούνται από το android app μας και πάλι, αφού ο υπάλληλος αυθεντικοποιηθεί ως τέτοιος από την εφαρμογή.

# English

# SmartAlert

## Android App with Backend Module
### Link for [Backend](https://github.com/George-Anto/smart_alert_backend_public)

The application has the role of notifying users who have installed it about dangerous - emergency situations (fires, floods, earthquakes) near them.
The user has the ability to notify about a dangerous incident and also to be notified. <br/>

The notification is performed via text message to a set of recipients. Each notification includes the state of emergency (with
any accompanying information), the geographic location of the current event, a timestamp (date / time), as well as instructions from civil protection.

## Backend Module

Our application also has a backend module, the main function of which is the classification of the submitted incidents based on the alarm
level, which is calculated as follows:<br/>
There are three "severity" criterias for submitted incidents. The 1st
is the number of users who have submitted the specific risk incident (the more people, the
more "valid" the incident), in a specific time range (eg. they can't submit a flood with a time gap of one day). The 2nd
is the geographical distance between the submitted incidents (a fire cannot be 200 km apart). The 3rd is that if a user submits the same incident again, 
it will not count as another submission of the created group.

## Civil Protection Officer - Second Use of the Android App

Finally, a civil protection officer will choose to notify users of any (or all) incidents that our backend has grouped and rated
based on the above criterias, then only users who are in a specific distance from the incident will be notified 
and they will receive a distress message with the necessary information. <br/>
The above functions are performed by our android app too, after the employee is authenticated as such by the application.
