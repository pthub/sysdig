# sysdig
monitor sysdig output and send an email

## article
https://www.linkedin.com/pulse/intrusion-detection-cloud-pradeep-thomas?trk=pulse_spock-articles

## building it

mvn clean package

Creates a zip file

## running it

### Config file

There is a sample config file under source main resources

### Command

Example:

java -jar monitor-1.0.jar /usr/local/monitor/monitor.conf


## Process

1. Execute one or more sysdig commands and pipe the output to a file/s
2. Change the config file to include the sysdig output directory
3. Execute the java command to monitor the directory and send emails  