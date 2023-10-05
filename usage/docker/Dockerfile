FROM ubuntu:18.04

RUN apt-get update \
    && apt-get install -y curl wget unzip apt-transport-https ca-certificates gnupg-agent software-properties-common maven gradle jq \
    && curl -sL https://deb.nodesource.com/setup_12.x | bash - \
    && apt-get install -y nodejs \
    && mvn -version \
    && gradle -v \
    && jq --version \
    && npm --version
