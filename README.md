# WSO2 API Gateway Client

Detta är en exempel-applikation vars syfte är att hjälpa utvecklare att bygga klienter för att göra anrop mot API:er som exponeras i Sundsvalls kommuns API Gateway - **WSO2**.

Detta innefattar även hantering av Oauth2-tokens. 
Vi använder oss av Oauth2's grant type: [Client credentials](https://oauth.net/2/grant-types/client-credentials/  )

## Instruktioner
För att komma igång med utvecklingen av din klient så kan du börja med att klona ned detta repo:
```
git clone https://github.com/Sundsvallskommun/wso2-api-gateway-java-client.git 
```

Nu är det dags att använda den OpenAPI-specifikation som gäller för det API du vill anropa. 
Denna kan du hitta i WSO2 Devportal: 
```
API > Overview > Gateway Environments > External Production and Sandbox > Swagger
```

Byt ut den existerande openapi.json till din egna:
```
src/main/resources/openapi.json
```
För att det inte ska uppstå problem vid kompileringen så behöver du gå in i ```ExampleResource.java``` och kommentera bort den befintliga koden. Koden använder klasser som med största sannolikhet kommer förvinna när du genererar upp nya klasser.

För att kompilera och generera upp alla nödvändiga filer kör du:
```
./mvnw clean compile
```

Nu kan du öppna din IDE och se de genererade klasserna under:
```
target/generated-sources/openapi/src/gen/java/main/org/openapitools/client
```

### REST-Anropen
I klassen ```ExampleResource.java``` har jag skapat upp två exempel på anrop. Dessa anrop är mot API:t ```Feedbacksettings``` så du behöver ändra metoderna för att det ska fungera mot just det API du avser anropa.

Exempel i koden:
- GET   /settings -> ```getSettings()```
- POST  /settings -> ```postSettings()```

### Oauth2

I samma klass ligger även metoden ```renewAccessToken()``` som används för att hämta en ny access token. Du kan se exempel på hur denna metod används i de olika metoderna.

Du behöver lägga till **client id** och **client secret** i dina miljövariabler. Döp dem till något lämpligt, förslagsvis enligt:
- \<APPLIKATIONSNAMN\>_CLIENT_ID
- \<APPLIKATIONSNAMN\>_CLIENT_SECRET

När du har anpassat anropen till de metoder som har genererats upp för det API du avser anropa så kan du starta applikationen genom att köra kommandot:
```
./mvnw clean compile quarkus:dev -Dclient.id=${<APPLIKATIONSNAMN>_CLIENT_ID} -Dclient.secret=${<APPLIKATIONSNAMN>_CLIENT_SECRET}
```

### Done!

Nu kan du öppna http://localhost:8080/q/swagger-ui/ och testa dina anrop.
