import requests
from http.server import HTTPServer, BaseHTTPRequestHandler
import socketserver
import json
import RPi.GPIO as GPIO
import time
import unittest
import adc
from threading import Thread
from RSA_light_iot import RSA_Light
import ssl

GPIO.setmode(GPIO.BOARD)
# bouton de sélection
GPIO.setup(11, GPIO.IN, pull_up_down = GPIO.PUD_UP)
# bouton de confirmation
GPIO.setup(13, GPIO.IN, pull_up_down = GPIO.PUD_UP )
# LED 1
GPIO.setup(3, GPIO.OUT)
# LED 2
GPIO.setup(5, GPIO.OUT)
# LED analogique
GPIO.setup(12, GPIO.OUT)
# alarme
GPIO.setup(40, GPIO.OUT)

ma_MLI = GPIO.PWM(12, 60)
ma_MLI.start(0)

# MLI de l'alarme
alarme_MLI = GPIO.PWM(40, 400)

GPIO.output(3, GPIO.LOW)
GPIO.output(5, GPIO.LOW)

entree = GPIO.input(11)
entree2 = GPIO.input(13)

# Temps que le chien est dehors
Temps = 0
# Heure que la sonnerie va sonner
heure = 0
# heure que l'application a déterminé
heure2 = -1
# valeur adc avant des modifications
valAvant = 0.0

dans = 0

chaque = 0


# fait a l'aide de la partie 5.4 Sécuriser les échanges de données par Maryse Mongeau
def chiffrer(donnee, cle_publique):
    crypt = []
    for c in str(donnee):
        asc = ord(c)
        cryptogramme = rsa.rsalight_encrypt(asc, cle_publique)
        crypt.append(cryptogramme)
    return crypt
def dechiffrer(cle_privee, cryptogramme):
    donnee = []
    for c in str(cryptogramme):
        msg = rsa.rsalight_decrypt(c, cle_privee)
        donnee.append(chr(msg))
    message = ''.join(message)
    return message

rsa = RSA_Light(32)
cle_publique, cle_privee = rsa.rsalight_keygen()

class MyHttpRequestHandler(BaseHTTPRequestHandler):
    #rsa = RSA_Light(32)
    #cle_publique = rsa.rsalight_keygen()
    def do_POST(self):
        global Temps
        global heure
        global heure2
        global valAvant
        global dans
        global chaque
        longueur = int(self.headers['Content-Length'])
        message = self.rfile.read(longueur).decode("utf-8")
        donnees_json = json.loads(message)
        
        #cle = donnees_json['cle']
        
        # recois le temps de l'utilisateur
        if (donnees_json['Temps'] != -1):
            #Temps = dechiffrer(cle, donnees_json['Temps'])
            Temps = donnees_json['Temps']
            changeTemps(Temps)
        # recois l'heure de l'utilisateur
        if (donnees_json['Heure'] != -1):
            #heure = dechiffrerr(cle, donnees_json['Heure'])
            heure2 = donnees_json['Heure']
            # pourcentage pour mettre 24 en pourcentage
            pourcentage = 100/24
            # change la force
            ma_MLI.ChangeDutyCycle(heure2 * pourcentage)
            
        if (donnees_json['Dans'] != -1):
            dans = donnees_json['Dans']
        
        if (donnees_json['Chaque'] != -1):
            chaque = donnees_json['Chaque']
            
        # fait les variables dans et chaque
            
        # MyHttpRequestHandler.valeur = donnees_json['valeur']
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()
        self.wfile.write(bytes("Message recu", "utf-8"))
        return
    
    def do_GET(self):
        self.send_response(200)
        self.send_header("Content-type", "application/json")
        self.end_headers()
        #self.wfile.write(bytes(json.dumps({'Heure' : chiffrer(heure, cle_privee), 'Temps' : chiffrer(Temps, cle_privee), 'cle' : cle_publique}), "utf8"))
        dictionary = {"Heure":heure, "Temps":Temps}
        self.wfile.write(bytes(json.dumps(dictionary), "utf8"))
        print(json.dumps(dictionary))
        self.wfile.flush()
        return
    
# Creer et demarrer l'objet serveur
mon_handler = MyHttpRequestHandler
mon_serveur = HTTPServer(('', 8080), mon_handler)
#mon_serveur.socket = ssl.wrap_socket (mon_serveur.socket, keyfile="cle.pem", certfile="certificat.pem", server_side=True)

# fonction qui change le temps et modifie les LED
def changeTemps(temps):
    
    if (temps == 0):
        GPIO.output(3, GPIO.LOW)
        GPIO.output(5, GPIO.LOW)
    
    elif (temps == 30):
        GPIO.output(3, GPIO.HIGH)
        GPIO.output(5, GPIO.LOW)
        
    elif (temps == 60):
        GPIO.output(3, GPIO.LOW)
        GPIO.output(5, GPIO.HIGH)
        
    elif (temps == 90):
        GPIO.output(3, GPIO.HIGH)
        GPIO.output(5, GPIO.HIGH)
    
    print("Temps sélectionné: " + str(temps) + " minutes")

# fonction qui commence le décompte pour le temps
# ne va pas fonctionner avec 1h30 mais plutot en secondes pour sauver du temps
def countDownTemps(Temps, Dans, Chaque):
    GPIO.output(3, GPIO.LOW)
    GPIO.output(5, GPIO.LOW)
    
    time.sleep(Dans)
    print("ALARME DANS")
    for i in range(5):
        time.sleep(Temps)
        print("ALARME TEMPS")
        alarme_MLI.start(50)
        time.sleep(2)
        alarme_MLI.stop()
        time.sleep(Chaque)
        print("ALARME CHAQUE")

# fonction qui commence le décompte pour l'heure
def countDownHeure(heure):
    print("ALARME HEURE COMMENCE")
    ma_MLI.ChangeDutyCycle(0)
    # https://www.programiz.com/python-programming/datetime/current-time
    temps = time.localtime()
    Heure = temps.tm_hour
    
    # Les heures vont etre en secondes pour sauver du temps
    heureObtenu = heure - Heure
    
    # si l'heure devient plus petit que 0
    # signifie que l'alarme sera demain
    if (heureObtenu < 0):
        # formule pour calculer combien de temps vers le lendemain
        # si l'heure a déja passé
        # ex : alarme pour 10 mais il est 14
        heureObtenu = (heureObtenu * -1) + 24
    print(heureObtenu)
    # 1h = 1sec
    time.sleep(heureObtenu)
    print("ALARME HEURE")
    alarme_MLI.start(50)
    time.sleep(2)
    alarme_MLI.stop()
    
# fonction qui permet de changer l'heure dans le bon format
def changerHeure(val):
    # 1440 minutes dans 24 heures
    # Pour faire le cacule
    pourcentage = 14.4
    Valeur = val * 20
    # https://stackoverflow.com/questions/20457038/how-to-round-to-2-decimals-with-python
    heure = round((pourcentage * Valeur)/60)
    return heure

# la boucle qui contient les changements d'heure et de temps
def bouclePrincipale():
    global Temps
    global heure
    global heure2
    global valAvant
    global dans
    global chaque
    while True:
        
        # les variables qui contient les entrées de les boutons
        entree = GPIO.input(11)
        entree2 = GPIO.input(13)
        print(entree)
        print(entree2)
        
        print(dans)
        print(chaque)
        
        time.sleep(2)
        # Lorsque le boutton est pesé
        if (entree == 0):
            Temps += 30
            # si le temps est plus que 90 minutes, il recommence
            if (Temps > 90):
                Temps = 0
            changeTemps(Temps)
            time.sleep(2)
        
        # valeur du potentiometre
        val = adc.get_adc(0)
        
        # si l'heure sur l'objet a changé
        if (valAvant != val):
            # la nouvelle valeur est assigné car il a eu un changement
            valAvant = val
            heure = changerHeure(val)
            ma_MLI.ChangeDutyCycle(val * 20)
        # si l'heure envoyé par le client est changé
        if (heure2 != -1):
            heure = heure2
            heure2 = -1
        
        
        print("Heure choisi: " + str(heure))
        
        # si le bouton de confirmation est pesé
        if (entree2 == 0):
            # Thread des countdown
            countDownT_tread = Thread(target=countDownTemps, args=(Temps, dans, chaque))
            countDownH_tread = Thread(target=countDownHeure, args=(heure,))
            countDownT_tread.start()
            countDownH_tread.start()
            countDownT_tread.join()
            countDownT_tread.join()
            
            # tout les données sont reset
            Temps = 0
            heure = 0
            dans = 0
            chaque = 0
            valAvant = 0


class TestGPIO(unittest.TestCase):
    def testTemps(self):
        # si le bouton de sélection est pesé
        if (entree == 1):
            # si le temps sélectionné est 30 min
            if (Temps == 30):
                self.assertEqual(GPIO.input(3), GPIO.HIGH)
                self.assertEqual(GPIO.input(5), GPIO.LOW)
            # si le temps est 60 min
            elif (Temps == 60):
                self.assertEqual(GPIO.input(3), GPIO.LOW)
                self.assertEqual(GPIO.input(5), GPIO.HIGH)
            # si le temps est 90 min
            elif (Temps == 90):
                self.assertEqual(GPIO.input(3), GPIO.HIGH)
                self.assertEqual(GPIO.input(5), GPIO.HIGH)
            # si le temps est 0 min
            elif (Temps == 0):
                self.assertEqual(GPIO.input(3), GPIO.LOW)
                self.assertEqual(GPIO.input(5), GPIO.LOW)
            
    def testSelectionHeure(self):
        val = adc.get_adc(0)
        if (val == 2.5):
            self.assertEqual(ma_MLI.duty_cycle, 50)
      
    # pas trouvé comment assertEqual un duty cycle ou Htz
    #def tempsEcoule(self):
        #if (entree2 == 1):
            #time.sleep(Temps)
            #self.assertEqual(alarme_MLI.duty_cycle,
        
    #def heureVenu(self):
        #if (entree2 == 1):
            

def run():
    print("Serveur en marche")
    mon_serveur.serve_forever()
        
def runTests():
    while True:
        suite = unittest.TestLoader().loadTestsFromTestCase(TestGPIO)
        unittest.TextTestRunner().run(suite)
        time.sleep(5)


if __name__ == '__main__':
    # thread pour le serveur
    control_tread = Thread(target=run, daemon=True)
    control_tread.start()
    time.sleep(0.1)
    
    # thread pour la boucle principale
    mainThread = Thread(target=bouclePrincipale)
    mainThread.start()
    time.sleep(0.1)
    
    # thread pour les tests
    testThread = Thread(target=runTests, daemon=True)
    testThread.start()
    
    mainThread.join()

