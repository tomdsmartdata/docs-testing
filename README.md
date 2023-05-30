# Docs Testing
This is a short-lived repository that is used for comparing content from the old https://docs.enosix.io site exists
in the new content. It has a script that can be run manually to generate a list of the URLs referenced in
the site (not just a site map). This list of URLs in a text file can then be fed
into an automated Selenium test (using the Selenide library for fluent assertions and 
Allure for test reporting).

# Dependencies
1. Java 17
2. Maven 3.x
3. Google Chrome

# Running this repository
1. Change directory into **src/test/resources**  
    `cd src/test/resources`
2. The first piece needed is the urls for the original site in a text file (*docs-enosix-io.txt*)  
    `./fetchurls.sh -d https://docs.enosix.io/ -l . -f docs-enosix-io -n`
3. Change directory back to project root
    `cd ../../..`
4. Execute the tests with a Maven test command
    `mvn test`

## Interpret results
If the `mvn test` command completes successfully, then every URL can be found. If it fails, then the
list of URLs reported in the test failure received a non-200 status code response.  
For every failed URL, a screenshot can be found in the `build/reports/tests/screenshots` directory.  Screenshots
ending in **docs.enosix.io** are the original documentation site for comparison.