# Getting Started

### Reference Documentation:

Run unit test, excluding Spring integration tests via - 
**mvn surefire:test**

Run IT test via - 
**mvn failsafe:integration-test**

It starts mongodb and mock-server to run IT test against.

## Shortfalls:

Running app first run something like:  

**docker run --name mongodb -p 27017:27017 -d mongodb/mongodb-community-server:5.0-ubuntu2004**


`curl --location 'http://localhost:8080/proxy' \
--header 'Accept: application/json' \
--header 'Content-Type: application/json' \
--data '{
"companyName" : "BBC LIMITED",
"companyNumber" : "06500244"
}'`

Due to full-time work and family obligations. Coupled with the fact I was told to get it in by Friday
and was unable to even start until the weekend. I've not included some things that I could add if given 
a little more time.

**Searching on anything other that CompanyNumber.
Saving to MongoDb to return, rather than call the True Api.
More test should be added. I tested the main functions but not any other cases.**

I will happily add if it's not an immediate NO :)


