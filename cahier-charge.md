 Il s'agit de réaliser la partie serveur d'une application
client-serveur permettant de faire des réunions virtuelles
multimédia sur Internet. L'objectif de cette application est
de permettre d'imiter le plus possible le déroulement de
réunions de travail classiques. Cependant, dans la
première version de ce projet, les interventions des
participants se feront en mode mono-média seulement
(i.e. échanges en forme textuelle).
 Le serveur devra permettre de planifier et de gérer le
déroulement de plusieurs réunions simultanées. Des
programmes clients existeront dans l'avenir pour
plusieurs plate-formes (Mac, Windows, Unix) afin de
permettre à des personnes désirant organiser des
réunions virtuelles ou y participer de dialoguer avec le
serveur en utilisant un protocole ad hoc développé au
dessus de IP.


Après s'être connecté au serveur (à l'aide d'un nom de login et d'un mot de
passe mémorisé par le système), une personne a la possibilité de planifier des
réunions virtuelles (choix d'un nom, définition du sujet, date de début et durée
prévue, ordre du jour), de consulter les détails d'organisation d'une réunion, de
les modifier (seulement l'organisateur), d'ouvrir et de clôturer une réunion
(seulement l'animateur), d'entrer (virtuellement) dans une réunion
précédemment ouverte, et d'en sortir. En cours de réunion, un participant peut
demander à prendre la parole. Quand elle lui est accordée, il peut entrer le texte
d'une intervention qui sera transmise en ``temps-réel'' par le serveur à tous les
participants de la réunion.
 Plusieurs sortes de réunions doivent pouvoir être organisables :
– Réunions standards, avec un organisateur qui se charge de la planification de la
réunion et désigne un animateur chargé de choisir les intervenants successifs parmi
ceux qui demandent la parole.
– Réunions privés, qui sont des réunions standards dont l'accès est réservé à un
groupe de personnes défini par l'organisateur
– Réunions démocratiques, qui sont planifiées comme des réunions standards, mais où
les intervenants successifs sont choisis automatiquement par le serveur sur la base
d'une politique premier demandeur-premier servi.
27