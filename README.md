# UrlShortener
Web Application for shortening URLs. Supports two actions:
1. POST /url. Create a shortened URL (`urlId`) that can be used to get redirected to the original longer URL. 
2. GET /{urlId}. Get redirect based on a `urlId` which returns a http redirect to the `originalUrl` associated with the `urlId`

Example in `curl`:
- Create: `curl localhost:8080/url --header "Content-Type: application/json" --data '{"originalUrl":"http://www.google.com"}'`
  - Returns: `{"traceId":"66ae593814044","urlMapping":{"urlId":"BC96z","originalUrl":"http://www.google.com"}}`
- Get: `curl localhost:8080/BC96z`
  - Returns: `HTTP/1.1, 303, See Other, Location: http://www.google.com`

Design decisions
---
URL shortening: Hashing `originalUrl` with `MurmurHash3` to turn the `originalUrl` into a deterministic number. Use the number to pick alphanumeric characters for the shortened url (`urlId`).
* Hashing because it does not require an external network call or extra infrastructure like a centralized counter / id assigner (Zookeeper) would. It makes the system more scalable and there's one less piece of infrastructure that can break and has to be maintained. 
* MurmurHash3 is used for consistent hashing in Cassandra and in Couchbase, so it's battle tested for similar use cases.

Hash collision resolution: If the generated `urlId` is already present in the database, It's checked for duplication and returned as-is if the stored `originalUrl` matches the one being inserted. Although rare, different `originalUrl` can hash into the same `urlId`. When that happens, a deterministic padding is added to rehash the `originalUrl` and this process is repeated until the `urlId` is unique or an existing duplicate is found. This approach is based on the [Open Adressing](https://en.wikipedia.org/wiki/Open_addressing) collision resolution method often found in hashmaps.

Data storage: Picked Cassandra because it's a very performant, scalable and reliable database if your use case fits within it's strengths. I'd however consider alternatives (Persistent Redis / ACID+Redis cache) since inserting url mappings relies on `INSERT IF NOT EXISTS` for avoiding race conditions and this a slow operation in Cassandra (requires Paxos). Given that the Read/Write ratio of the application is probably more than a 9/1 read/write skew I'll eat the performance loss on writes to avoid having to maintain a caching layer for reads.

How to start the UrlShortener application
---

1. Run `make infra` to run infrastructure dependencies using docker (Cassandra)
1. Run `make build` to build the application artifact
1. Run `make run` to run the application artifact
1. The application should now be running on `http://localhost:8080` and you should be able to test it using the `curl` commands from the above example.

Other notable commands
---
* `make help` lists all available make commands and their help text
* `make benchmark` runs the JMH benchmarks and writes the results to file (benchmark-results/jmh-reports/).
* `make test` runs all application tests (requires docker for `testcontainer` integration tests)
* `make docker` packages the application into a docker container
* `locust` runs load tests based on `locustfile.py` (application must be running)
