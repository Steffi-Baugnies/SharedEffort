# Use an official Python runtime as a parent image
FROM python:3

LABEL "easyRepas.mantainer"="Scorpio Interactive <info@scorpio-interactive.com>"
LABEL "easyRepas.description"="API's of the easyRepas Project"

# Set the working directory to /app
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY ./app/requirements.txt /app

# Install any needed packages specified in requirements.txt
RUN pip install -r requirements.txt \
&& rm /app/requirements.txt

# Define environment variable
ENV Name host
ENV Name user
ENV Name password
ENV Name database

COPY ./app/api.py /app

# Run app.py when the container launches
CMD ["python", "api.py"]
