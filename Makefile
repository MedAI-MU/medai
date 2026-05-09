run:
	@echo "Cleaning up node modules volume..."
	@docker container rm medai-backend -f || true
	@docker volume rm medai-local_backend-node_modules || true
	@echo "Starting MedAI local development environment..."
	@COMPOSE_BAKE=true DOCKER_BUILDKIT=1 \
	docker compose --project-name medai-local -f docker-compose/local.yaml --env-file backend/.env up --build --remove-orphans -d
	@$(MAKE) migrate
	@$(MAKE) seed 2> /dev/null
	@echo "Showing logs..."
	@docker compose --project-name medai-local -f docker-compose/local.yaml --env-file backend/.env logs -f backend

clean-db:
	@docker container rm medai-db -f 2> /dev/null
	@docker volume rm medai-local_postgres_data -f

migrate:
	@echo "Running migrations..."
	@pnpm --prefix ./backend run migration:run

generate-migration:
	@echo "Generating new migration..."
	@pnpm --prefix ./backend run migration:generate

create-migration:
	@echo "Creating migration..."
	@pnpm --prefix ./backend run migration:create

seed:
	@echo "Running seeders..."
	@pnpm --prefix ./backend run seed

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
