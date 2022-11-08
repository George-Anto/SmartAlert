# SmartAlert

## Android App με Backend Module
### Link για Backend: `https://github.com/George-Anto/smart_alert_backend_public`

Η εφαρμογή έχει τον ρόλο της ειδοποίησης χρηστών που την έχουν εγκαταστήσει για επικίνδυνες - έκτακτες καταστάσεις (φωτιές πλημμύρες, σεισμούς) κοντά σε αυτούς. 
Ο χρήστης έχει την δυνατότητα και να ειδοποιήσει για ένα επικίνδυνο περιστατικό αλλά και να ειδοποιηθεί. <br/>

Η ενημέρωση γίνεται μέσω γραπτού μηνύματος σε ένα σύνολο από παραλήπτες. Σε κάθε ειδοποίηση περιλαμβάνεται η κατάσταση ανάγκης (με
την όποια πληροφορία συνοδεύεται), η γεωγραφική θέση του τρέχοντος event, ένα timestamp (ημερομηνία/ώρα), καθώς και οδηγίες από την πολιτική προστασία.

## Backend Module

Η εφαρμογή μας να διαθέτει ένα backend module, βασική λειτουργία του οποίου θα είναι η κατάταξη των υποβληθέντων περιστατικών με βάση το επίπεδο
συναγερμού, το οποίο υπολογίζεται ως εξής:<br/>
Για τα υποβληθέντα περιστατικά υπάρχουν τρία κριτήρια «βαρύτητας». Το 1ο
είναι το πλήθος των χρηστών που έχουν υποβάλλει το συγκεκριμένο περιστατικό κινδύνου (όσο περισσότερα άτομα, τόσο
πιο «έγκυρο» το περιστατικό), σε συγκεκριμένο εύρος χρόνου (δεν μπορεί να υποβάλουν μια πλημμύρα με χρονική απόσταση μιας ημέρας). Το 2ο
είναι η γεωγραφική απόσταση μεταξύ των υποβληθέντων περιστατικών (δεν μπορεί μια πυρκαγιά να έχει απόσταση 200 χιλιομέτρων). Το 3ο είναι πως ένας αν ένας 
χρήστης υποβάλει ξανά το ίδιο περιστατικό, αυτό δεν θα προσμετρηθεί ως ακόμα μια υποβολή της δημιουργηθείσας ομάδας. 

### Υπάλληλος πΠολιτικής πΠροστασίας - Δεύτερη Χρήση του Android App

Τέλος, ένας υπάλληλος πολιτικής προστασίας θα επιλέξει την ειδοποίηση των χρηστών για κάποιο (ή όλα) τα περιστατικά που το backend μας ομαδοποίησε και βαθμολόγησε
με βάση τα παραπάνω κριτήρια, τότε θα ειδοποιηθούν μόνο οι χρήστες οι οποίοι βρίσκονται σε συγκεκριμένη
απόσταση από το περιστατικό και οι οποίοι, Θα λάβουν μήνυμα κινδύνου με τις απαραίτητες πληροφορίες. <br/>
Οι παραπάνω λειτουργίες επιτελούνται από το android app μας και πάλι αφού ο υπάλληλος αυθεντικοποιηθεί ως τέτοιος από την εφαρμογή.
