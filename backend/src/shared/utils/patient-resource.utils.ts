import type { ObjectLiteral } from 'typeorm';

export function getNested(resource: ObjectLiteral, path: string): unknown {
  const keys = path.split('.');
  let value: unknown = resource;
  for (const key of keys) {
    if (typeof value !== 'object' || value === null) return undefined;
    value = (value as Record<string, unknown>)[key];
  }
  return value;
}
