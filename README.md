# send-letter-performance-tests

## Running
```bash
export BASE_URL=https://send-letter-producer-demo.service.core-compute-demo.internal/
export S2S_URL=https://rpe-service-auth-provider-demo.service.core-compute-demo.internal/
export PROXY_HOST=proxyout.reform.hmcts.net
export SERVICE_NAME=divorce
export SERVICE_PASS=getfromvault
export PROXY_PORT=8080
./gradlew gatlingRun-uk.gov.hmcts.reform.sendletter.MainSimulation
```
