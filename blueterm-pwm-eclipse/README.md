blueterm-pwm
============

Testing android library for Vx600 terminals with master from Monet.

CZ: Aplikace zatim neni urcena do produkce!!! Vzniklo to jako demonstrace ze umime pouzit verifoni knihovnu adk-pwm. 

* Aplikace zere moc vykonu kvuli mnoha threadum se sleepem. Opravim mozna v budoucnu, kdyz bude zajem to pouzivat.
* Puvodni zamer knihovny bluterm bylo komunikace s ingenico terminaly primo pres bluetooth, takze se tam vyskytuji casti kodu, ktere zdanlive nemaji smysl, protoze puvodne slouzili k inicializaci bluettoth, coz nyni dela verifoni knihovna.
* Synchronizace vsech threadu je pres tridu MessageThread
* Zasadnim problemem bylo, ze moneti master komunikuje asynchrone, zatico verifonni knihovna se snazi byt synchronni, takze zakladni myslenka je takova, ze vytvorim spojeni s terminalem, poslu jednoduchy command s ip a portem tcp serveru, ktery vytvorim v androidu, a na nem asynchronne komunikujeme monetim protokolem MNSP uvnitr ktereho je SPDH.
