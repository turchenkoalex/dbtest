VERSION=$(shell git rev-parse --abbrev-ref HEAD)
IMAGE="turchenkoalex/dbtest:$(VERSION)"
MANIFEST_NAME="dbtest:$(VERSION)"
BUILDX_BUILDER="dbtest-builder"
BUILDX_BUILDER_STATUS=$(shell docker buildx inspect $(BUILDX_BUILDER) > /dev/null 2>&1; echo $$?)

.PHONY: all build

all: build

build:
ifeq ($(BUILDX_BUILDER_STATUS),1)
	docker buildx create --name $(BUILDX_BUILDER)
endif
	docker buildx use $(BUILDX_BUILDER)
	docker buildx inspect --bootstrap
	docker buildx build --push --pull --tag $(IMAGE) --platform=linux/amd64,linux/arm64 .
	echo "New image tag: $(IMAGE)"
	docker buildx rm $(BUILDX_BUILDER)
