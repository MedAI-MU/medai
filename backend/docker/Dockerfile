FROM node:24-alpine AS builder

SHELL ["/bin/sh", "-c", "-o", "pipefail"]

ENV APK_CACHE_DIR="/medai/.cache/apk-backend-builder" \
    APK_SYMLINK_DIR="/etc/apk/cache" \
    NPM_CACHE_DIR="/medai/.cache/npm" \
    PNPM_HOME="/pnpm"

ENV NPM_CONFIG_RETRY=5 \
    NPM_CONFIG_TIMEOUT=30000 \
    PATH="$PNPM_HOME:$PATH"

RUN mkdir -p ${APK_CACHE_DIR} \
    && ln -s ${APK_CACHE_DIR} ${APK_SYMLINK_DIR}

RUN --mount=type=cache,target=${APK_CACHE_DIR} \
    apk update && apk upgrade \
    && addgroup -S medai \
    && adduser -S -h /medai -G medai medai \
    && chown -R medai:medai /medai

RUN --mount=type=cache,target=${NPM_CACHE_DIR} \
    npm install -g pnpm --cache ${NPM_CACHE_DIR}

WORKDIR /medai/

COPY --chmod=444 --chown=root:root package.json pnpm-lock.yaml pnpm-workspace.yaml ./

RUN --mount=type=cache,id=pnpm,target=/pnpm/store \
    pnpm install --frozen-lockfile

COPY --chown=root:root src src
COPY --chown=root:root nest-cli.json tsconfig.json tsconfig.build.json typeorm.data-source.ts ./

RUN pnpm run build

FROM node:24-alpine AS staging

SHELL ["/bin/sh", "-c", "-o", "pipefail"]

ENV APK_CACHE_DIR="/medai/.cache/apk-backend" \
    APK_SYMLINK_DIR="/etc/apk/cache" \
    NODE_ENV=production

RUN mkdir -p ${APK_CACHE_DIR} \
    && ln -s ${APK_CACHE_DIR} ${APK_SYMLINK_DIR}

RUN --mount=type=cache,target=${APK_CACHE_DIR} \
    apk update && apk upgrade \
    && apk add --no-cache dumb-init postgresql-client ffmpeg \
    && addgroup -S medai \
    && adduser -S -h /medai -G medai medai \
    && chown -R medai:medai /medai

WORKDIR /medai/

COPY --from=builder --chown=medai:medai /medai/dist ./dist
COPY --from=builder --chown=medai:medai /medai/node_modules ./node_modules
COPY --chmod=444 --chown=medai:medai package.json ./

EXPOSE 8000

USER medai

CMD ["node", "dist/src/main"]
