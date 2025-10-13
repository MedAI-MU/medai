run:
	@COMPOSE_BAKE=true DOCKER_BUILDKIT=1 \
	docker compose --project-name medai-local -f docker-compose/local.yaml up --build --remove-orphans 
