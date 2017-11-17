# BellChoir
Lab 2 from CS-410 Operating Systems

This program plays a song written in a text file. Each note is played by a different thread(player).  The program first reads in the data, then validates it to make sure that the requested notes and note lengths exist. If any of the notes or note lengths do not exist, then the program will not play the song. Once the song has been validated, threads are started for each hand bell note so each player has one note. 
When the note wants to be played, it must first receive a mutex to make sure that only one note is playing at a time. The note will then play and the mutex will be returned. When the song ends the threads are stopped and the program completes.

This program can handle whole, half, quarter, and eigth notes and can play the chromatic scale from A4 to B5 but no flats. 
