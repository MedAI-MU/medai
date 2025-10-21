run:
	@COMPOSE_BAKE=true DOCKER_BUILDKIT=1 \
	docker compose --project-name medai-local -f docker-compose/local.yaml --env-file backend/.env up --build --remove-orphans

check-backend:
	@pre-commit run --all-files

test-backend:
	@DOCKER_BUILDKIT=1 \
	docker build -f backend/docker/Dockerfile.test -t medai-backend-test backend
	@docker run --env-file backend/.env.example --rm medai-backend-test pytest
