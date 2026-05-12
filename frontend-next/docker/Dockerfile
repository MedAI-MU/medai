FROM node:24-alpine AS builder

SHELL ["/bin/sh", "-c", "-o", "pipefail"]

ENV APK_CACHE_DIR="/medai/.cache/apk-backend-builder" \
    APK_SYMLINK_DIR="/etc/apk/cache" \
    NPM_CACHE_DIR="/medai/.cache/npm"

ENV NPM_CONFIG_RETRY=5 \
    NPM_CONFIG_TIMEOUT=30000 \
    NPM_CONFIG_CACHE="$NPM_CACHE_DIR"

RUN mkdir -p ${APK_CACHE_DIR} \
    && ln -s ${APK_CACHE_DIR} ${APK_SYMLINK_DIR}

RUN --mount=type=cache,target=${APK_CACHE_DIR} \
    apk update && apk upgrade \
    && addgroup -S medai \
    && adduser -S -h /medai -G medai medai \
    && chown -R medai:medai /medai

WORKDIR /medai/

COPY --chmod=444 --chown=root:root package.json package-lock.json ./

RUN --mount=type=cache,id=npm,target=${NPM_CACHE_DIR} \
    npm ci

COPY --chown=root:root . .

ARG NEXT_PUBLIC_API_BASE_URL

RUN NEXT_PUBLIC_API_BASE_URL=$NEXT_PUBLIC_API_BASE_URL npm run build

FROM node:24-alpine AS final

SHELL ["/bin/sh", "-c", "-o", "pipefail"]
ENV APK_CACHE_DIR="/medai/.cache/apk-backend" \
    APK_SYMLINK_DIR="/etc/apk/cache" \
    NODE_ENV=production

RUN mkdir -p ${APK_CACHE_DIR} \
    && ln -s ${APK_CACHE_DIR} ${APK_SYMLINK_DIR}

RUN --mount=type=cache,target=${APK_CACHE_DIR} \
    apk update && apk upgrade \
    && addgroup -S medai \
    && adduser -S -h /medai -G medai medai \
    && chown -R medai:medai /medai

WORKDIR /medai/

COPY --from=builder /medai/public ./public
COPY --from=builder --chown=medai:medai /medai/.next/standalone ./
COPY --from=builder --chown=medai:medai /medai/.next/static    ./.next/static

USER medai
EXPOSE 3000


CMD [ "node", "server.js" ]
