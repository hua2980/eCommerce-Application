# Pull the "tomcat" image. The community maintains this image.
FROM tomcat
# Copy all files present in the current folder to the "/usr/local/tomcat/webapps"  folder
COPY ./*.* /usr/local/tomcat/webapps