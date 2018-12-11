.DEFAULT_GOAL := common-build

common-build:
	./gradlew -Penv=dev -x test clean build
	@echo "Done building."

common-build-docker-img:
	./gradlew -Penv=dev -PdockerUser=$(dockerUser) -PdockerPwd=$(dockerPwd) -x test buildDockerImage
	@echo "Done docker image."
# make dockerUser=user dockerPwd=pwd common-build-docker-img
common-build-docker-push:
	./gradlew -Penv=dev -PdockerUser=$(dockerUser) -PdockerPwd=$(dockerPwd) -x test pushImage
	@echo "Done docker push."