run:
	@COMPOSE_BAKE=true DOCKER_BUILDKIT=1 \
	docker compose --project-name medai-local -f docker-compose/local.yaml --env-file backend/.env up --build --remove-orphans -d
	@echo "Running migrations..."
	@pnpm --prefix ./backend run migration:run
	@echo "Showing logs..."
	@docker compose --project-name medai-local -f docker-compose/local.yaml --env-file backend/.env logs -f backend

migrate:
	@echo "Running migrations..."
	@pnpm --prefix ./backend run migration:run

generate-migration:
	@echo "Generating new migration..."
	@pnpm --prefix ./backend run migration:generate

pre-commit:
	@pre-commit run --all-files

format-backend:
	@pnpm --prefix ./backend run format

format-frontend:
	@pnpm --prefix ./frontend run format

format:
	@$(MAKE) format-backend
	@$(MAKE) format-frontend

lint-backend:
	@pnpm --prefix ./backend run lint

lint-frontend:
	@pnpm --prefix ./frontend run lint

lint:
	@$(MAKE) lint-backend
	@$(MAKE) lint-frontend

test-backend:
	@DOCKER_BUILDKIT=1 \
	docker build -f backend/docker/Dockerfile.test -t medai-backend-test backend
	@docker run --env-file backend/.env.example --rm medai-backend-test pnpm test:cov

stop:
	@docker compose --project-name medai-local -f docker-compose/local.yaml stop
