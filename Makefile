

.PHONY: run # Runs the application artifact
run:
	java -jar target/urlshortener-1.0.jar server config.yml

.PHONY: build # Builds the application artifact
build:
	mvn clean package -DskipTests

.PHONY: benchmark # Runs JMH benchmarks referenced by the "JmhRunner" class
benchmark:
	mvn clean test -Dtest="JmhRunner"

.PHONY: docker # Builds the application artifact and the docker image
docker:
	$(MAKE) build
	docker build -t urlshortener:latest .

.PHONY: test # Runs the application unit tests
test:
	mvn clean test

.PHONY: infra # Runs the infrastructure dependencies required by the application
infra:
	docker-compose up

.PHONY: help # Generate list of targets with descriptions
help:
	@grep '^.PHONY: .* #' Makefile | sed 's/\.PHONY: \(.*\) # \(.*\)/\1 \2/' | expand -t20