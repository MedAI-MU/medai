export function validateSchema(schema, data) {
  const validation = schema.safeParse(data);

  // 1) No Errors
  if (validation.success) return { success: true };

  // 2) Errors found

  // unify error formate => {email: "error"}
  const schemaErrors = validation.error.flatten().fieldErrors;
  const fieldErrors = {};

  // gets the first error message in each field
  for (const field in schemaErrors) {
    if (!fieldErrors[field]) fieldErrors[field] = schemaErrors[field][0];
  }

  return {
    success: false,
    fieldErrors,
  };
}
