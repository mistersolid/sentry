FROM ubuntu:latest
LABEL authors="solid"

ENTRYPOINT ["top", "-b"]