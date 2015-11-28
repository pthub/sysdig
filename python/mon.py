#!/usr/bin/python
import os
import time
import smtplib
from subprocess import call
import sys
def send_email(IP):
    #add the sender email address
    mailersender = ""
    #add the sender password
    mailerpass = ""
    #add the receiver email address
    mailerreceiver =  ""

    # Prepare actual message
    message = """\From: %s\nTo: %s\nSubject: %s-%s\n\n
    """ % (mailersender, ", ".join(mailerreceiver), 'event-', IP)
    try:
        server = smtplib.SMTP("smtp.gmail.com", 587)
        server.ehlo()
        server.starttls()
        server.login(mailersender, mailerpass)
        server.sendmail(mailersender, mailerreceiver, message)
        server.close()
        print 'successfully sent the mail'
    except:
        print "failed to send mail"

call(["ufw", "allow", "from", sys.argv[1], "to", "any", "port", "22"])
send_email(sys.argv[1])
