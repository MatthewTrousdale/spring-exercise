# Getting Started

### Reference Documentation:

## Assumptions:

I assumed looking at the requirements it was always one company returned. So I hardcoded it.
It's easy to change to multiple companies but I think the search requirement was a single.

## Running the app

The app is reactive. However I've written many blocking apps also

Run unit test, excluding Spring integration tests via - **mvn surefire:test**

Run IT test via - **mvn failsafe:integration-test**

It starts mongodb and mock-server with expectations matching the real response for
BBC LIMITED to run IT tests against.

Running app first run something like, for your OS so mongo is available at default port:  

**docker run --name mongodb -p 27017:27017 -d mongodb/mongodb-community-server:5.0-ubuntu2004**

`curl --location 'http://localhost:8080/proxy' \
--header 'Accept: application/json' \
--header 'Content-Type: application/json' \
--data '{
"companyName" : "BBC LIMITED",
"companyNumber" : "06500244"
}'`

