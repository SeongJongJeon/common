.DEFAULT_GOAL := common-build

common-build:
	./gradlew -Penv=dev -x test
	@echo "Done building."