

Music-Moves

Progetto ESP1314 Ehi Team  

Membri progetto: Andrea Scarmagnan, Emanuele Fabian, Giovanni Zampieri  

*DEFAULT*  

•	Creazione grafica delle 5 UI con pulsanti, liste e seekbar  
	o	UI1 - Fatta  
	o	UI2 - Fatta  
	o	UI3 - Fatta  
	o	UI4 - Fatta  
	o	UI5 - Fatta  
•	Interconnessione delle 5 UI, gestione longpress, tasto back e menù:  Fatto  
•	Implementazione database sqlite:  Fatto  
•	Acquisizione dati accelerometro:  Fatto  
•	Implementazione della funzione per la rieleaborazione dei dati:  Fatto  
•	Riproduzione dei file audio e calibrazione suoni:  Fatto  
•	Implementazione delle preferenze e salvataggio dello stato  

Cose ancora da fare DEFAULT  
•	Salvare lo stato  
	o	UI3: salvare stato testo AlertDialog  
	o	UI2, UI4, UI5: onBackPressed da mettere a posto (controllare AndroidManifest.xml)  
•	Sistemazioni aggiuntive  
	o	Service per la registrazione  
	
*PLUS*  
•	Implementazione Volume  
•	Implementazione Velocità  
•	Implementazione Delay  
•	Implementazione Echo  
•	Associazione alle diverse gesture  
	o	Volume: associato a Drag (verticale).  
	o	Cambio di velocita': associato a Drag (orizzontale).  
	o	Delay: attivabile e disattivabile tramite Double Touch.  
	o	Echo: associato ad ogni singolo Touch.  
•	Implementazione delle preferenze  
	o	SeekBar ritardo Delay  
	o	SeekBar ritardo Echo  
	o	SeekBar intensita' Delay  
	o	SeekBar intensita' Echo  
	
DESCRIZIONE COMPLETA PLUS  
  Inseriamo come progetto plus la possibilita' in real time di applicare
  effetti sonori alla traccia in riproduzione. 
  
  Associamo a 4 diverse gesture i seguenti 4 effetti:
  
  •	Volume: associato a Drag (verticale).
  •	Cambio di velocita': associato a Drag (orizzontale). 
  •	Delay: attivabile e disattivabile tramite Double Touch. 
  •	Echo: associato ad ogni singolo Touch. 
  
  Viene modificata la UI#4 nel seguente modo: un'area dello schermo,
  come da figura allegata, viene riservata ai comandi touch, sopra elencati, 
  per gli effetti in real time. 
 
  Viene modificata anche la UI#5 dove inseriamo 4 SeekBar per gestire ritardo
  e l'intensita' di Delay ed Echo. 
  
  Invece le variazioni di volume e cambio di velocita' dipenderanno 
  dall'intensita' del gesto. 
  
  L'echo viene realizzato digitalmente registrando e poi riproducendo 
  piu' volte un suono con intensita' e durata calanti. 
  Quindi alla pressione del touch screen viene preso un frammento 
  (di lunghezza prefissata) della traccia e viene sovrapposto piu' volte, 
  con leggero ritardo, alla riproduzione della traccia principale 
  fino a scomparire del tutto. 
  
  Il delay viene realizzato sovrapponendo alla traccia principale, 
  la traccia stessa con il ritardo scelto tramite SeekBar. 
  Per realizzare gli effetti Delay ed Echo utilizzeremo la classe 
  EnvironmentalReverb della libreria Android (link). 
  
  Per modificare il volume varieremo l'ampiezza del campione audio, 
  mentre per la velocita' di riproduzione modificheremo il sample rate 
  utilizzando la classe AudioTrack.
  